package com.kinespherept.screen.visitstatus

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.component.NavigationController
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.NavigationSection
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.EmployeeRole
import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitStatus
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.EmployeeService
import com.kinespherept.service.LookupDataService
import com.kinespherept.service.VisitService
import com.kinespherept.session.EmployeeSession
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import groovy.util.logging.Slf4j

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonController)
@Slf4j
class TrackVisitStatusController {

    @MVCMember @Nonnull TrackVisitStatusModel model
    @MVCMember @Nonnull TrackVisitStatusView view


    @SpringAutowire EmployeeService employeeService
    @SpringAutowire EmployeeSession employeeSession
    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire VisitService visitService

    @GriffonAutowire NavigationController navigationController
    @GriffonAutowire ViewVisitDetailsNoTreatmentController viewVisitDetailsNoTreatmentController
    @GriffonAutowire ViewVisitDetailsWithTreatmentController viewVisitDetailsWithTreatmentController

    // this flag signifies we are in the prepareForm() method.  this is needed/useful because that method
    // sets a couple of model fields that have onChange() events attached to them, and this flag will tell
    // those methods not to take any action that would otherwise be taken if a user triggered the onChange() event.
    boolean preparingForm = false


    @PostConstruct
    void init() {
        SpringConfig.autowire(this)
    }


    void prepareForm() {

        // declare that we are preparing the form
        preparingForm = true

        // prepare the visit statuses (a selected list)
        model.visitStatuses.clear()
        model.visitStatuses << VisitStatus.VISIT_CREATED.text
        model.visitStatuses << VisitStatus.SEEN_BY_THERAPIST.text
        model.visitStatuses << VisitStatus.PREPARED_FOR_BILLING.text
        model.visitStatuses << VisitStatus.BILLED_TO_INSURANCE.text
        model.visitStatuses << VisitStatus.REMITTANCE_ENTERED.text
        model.visitStatuses << VisitStatus.AWAITING_SECONDARY.text
        model.visitStatuses << VisitStatus.BILL_SENT_TO_PATIENT.text
        model.visitStatuses << VisitStatus.PAID_IN_FULL.text
        model.visitStatusesChoice = VisitStatus.SEEN_BY_THERAPIST.text

        // prepare the insurance and therapist lists
        model.insuranceTypes.clear()
        model.insuranceTypes << TrackVisitStatusModel.ALL
        model.insuranceTypesChoice = TrackVisitStatusModel.ALL
        lookupDataService.insuranceTypes.each {
            model.insuranceTypes << it.insuranceTypeName
        }

        model.therapists.clear()
        model.therapists << TrackVisitStatusModel.ALL
        model.therapistsChoice = TrackVisitStatusModel.ALL
        employeeService.findByRoleExplicit(EmployeeRole.THERAPIST).each {
            model.therapists << it.fullname
        }

        navigationController.prepareForm(view.navigationPane, NavigationSection.STATUS_TRACKER)

        // declare that we are done preparing the form
        preparingForm = false

        loadVisitData()
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void clearToDate() {
        log.debug "[clearToDate] button pressed "
        model.toDate = null
    }

    void selectFromDate() {
        log.debug "selectFromDate() :: fromDate=${model.fromDate}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            loadVisitData()
        }
    }

    void selectToDate() {
        log.debug "selectToDate() :: toDate=${model.toDate}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            loadVisitData()
        }
    }

    void changeVisitStatus() {
        log.debug "changeVisitStatus() :: visitStatus=${model.visitStatusesChoice}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            loadVisitData()
        }
    }

    void changeInsuranceType() {
        log.debug "changeInsuranceType() :: insuranceType=${model.insuranceTypesChoice}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            loadVisitData()
        }
    }

    void changeTherapist() {
        log.debug "changeTherapist() :: therapist=${model.therapistsChoice}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            loadVisitData()
        }
    }


    void loadVisitData() {
        log.info "loadVisitData(visitStatus=${model.visitStatusesChoice}; fromDate=${model.fromDate}; toDate=${model.toDate}; insuranceType=${model.insuranceTypesChoice}; therapist=${model.therapistsChoice})"

        // convert VisitStatus, InsuranceType and Therapist into proper objects

        VisitStatus visitStatus = VisitStatus.findFromText(model.visitStatusesChoice)
        InsuranceType insuranceType = null
        Employee therapist = null

        if(model.insuranceTypesChoice != TrackVisitStatusModel.ALL) {
            insuranceType = lookupDataService.findInsuranceTypeByName(model.insuranceTypesChoice)
        }

        if(model.therapistsChoice != TrackVisitStatusModel.ALL) {
            therapist = employeeService.findByFullname(model.therapistsChoice)
        }

        List<Visit> visits = visitService.findVisitsByStatus(visitStatus, model.fromDate, model.toDate, insuranceType, therapist)

        log.info "found ${visits?.size()} visits that match criteria : ${visits}"

        // clear the children, and rebuild the results
        view.visitResultsGridPane.children.clear()

        visits.eachWithIndex{ Visit entry, int i -> view.buildResultRow(entry, visitStatus, i)   }
    }


    void changeVisitStatus(Visit visit, VisitStatus visitStatus) {
        log.info "changeVisitStatus(${visit}, ${visitStatus})"
        visitService.saveVisitWithStatusChange(visit, visitStatus, employeeSession.employee)
        // after making the change, refresh the visit data
        loadVisitData()
    }

    void viewVisitDetails(Visit visit) {
        log.info "viewVisitDetails(${visit}), visitRequiresTreatment=${visitService.visitRequiresTreatment(visit)}"

        // decide what 'view-visit-details' page to switch to : [With|No]Treatment
        if(visitService.visitRequiresTreatment(visit)) {
            viewVisitDetailsWithTreatmentController.prepareForm(visit)
            SceneManager.changeTheScene(SceneDefinition.VIEW_VISIT_DETAILS_WITH_TREATMENT)
        } else {
            viewVisitDetailsNoTreatmentController.prepareForm(visit)
            SceneManager.changeTheScene(SceneDefinition.VIEW_VISIT_DETAILS_NO_TREATMENT)
        }
    }





}