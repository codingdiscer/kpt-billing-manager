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

        // set aside the drop-down selections
        String visitStatusesChoice = model.visitStatusesChoice
        String insuranceTypesChoice = model.insuranceTypesChoice
        String therapistsChoice = model.therapistsChoice

        // prepare the visit statuses (a selected list)
        model.visitStatuses.clear()
        model.visitStatuses.addAll(VisitStatus.values().collect{ it.text })

        // prepare the insurance and therapist lists
        model.insuranceTypes.clear()
        model.insuranceTypes << TrackVisitStatusModel.ALL
        lookupDataService.insuranceTypes.each {
            model.insuranceTypes << it.insuranceTypeName
        }

        model.therapists.clear()
        model.therapists << TrackVisitStatusModel.ALL
        employeeService.findByRoleExplicit(EmployeeRole.THERAPIST).each {
            model.therapists << it.fullname
        }

        // restore the choices
        model.visitStatusesChoice = visitStatusesChoice
        model.insuranceTypesChoice = insuranceTypesChoice
        model.therapistsChoice = therapistsChoice

        navigationController.prepareForm(view.navigationPane, NavigationSection.STATUS_TRACKER)

        // declare that we are done preparing the form
        preparingForm = false

        // reload the form with the current criteria
        loadVisitDataByStatusWithDistraction()
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void clearFromDate() {
        log.debug "[clearFromDate] button pressed "
        model.fromDate = null
    }

    void selectFromDate() {
        log.debug "selectFromDate() :: fromDate=${model.fromDate}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            loadVisitDataByStatusWithDistraction()
        }
    }

    void selectToDate() {
        log.debug "selectToDate() :: toDate=${model.toDate}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            loadVisitDataByStatusWithDistraction()
        }
    }

    void changeVisitStatus() {
        log.debug "changeVisitStatus() :: visitStatus=${model.visitStatusesChoice}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            loadVisitDataByStatusWithDistraction()
        }
    }

    void changeInsuranceType() {
        log.debug "changeInsuranceType() :: insuranceType=${model.insuranceTypesChoice}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            loadVisitDataByStatusWithDistraction()
        }
    }

    void changeTherapist() {
        log.debug "changeTherapist() :: therapist=${model.therapistsChoice}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            loadVisitDataByStatusWithDistraction()
        }
    }

//    void selectPatient() {
//        log.info "selectPatient() :: patientsChoice=${model.patientsChoice}; preparingForm=${preparingForm}"
//        if(!preparingForm) {
//            loadVisitDataByPatientWithDistraction()
//        }
//    }




    /**
     * Template method handles managing the UI while searching for results...
     * @param findVisitData A Closure that returns a List<Visit>
     * @param buildResultRow A Closure that takes these parameters: { Visit entry, int i },
     *      and calls the appropriate view method to build the right row result
     */
    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void loadVisitDataWithDistraction(Closure findVisitData, Closure buildResultRow) {
        runInsideUISync {
            view.showSpinner(true)

            // clear the current result set and message count
            view.visitResultsGridPane.children.clear()
            model.resultCountMessage = ''

            runOutsideUI {
                List<Visit> visits = findVisitData()

                runInsideUISync {
                    // rebuild the results
                    model.resultCountMessage = "Found ${visits.size()} result${visits.size() == 1 ? '' : 's'}."
                    visits.eachWithIndex{ Visit entry, int i -> buildResultRow(entry, i) }
                    view.showSpinner(false)
                }
            }
        }
    }



//    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
//    void loadVisitDataByPatientWithDistraction() {
//        runInsideUIAsync {
//            view.showSpinner(true)
//        }
//    }
//
//
//    List<Visit> loadVisitDataByPatient() {
//    }

    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void loadVisitDataByStatusWithDistraction() {
        loadVisitDataWithDistraction(
                { loadVisitDataByStatus() },
                { Visit entry, int i -> view.buildResultRowByStatus(entry, i) }
        )
    }


    List<Visit> loadVisitDataByStatus() {
        log.info "loadVisitDataByStatus(visitStatus=${model.visitStatusesChoice}; fromDate=${model.fromDate}; toDate=${model.toDate}; insuranceType=${model.insuranceTypesChoice}; therapist=${model.therapistsChoice})"

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

        visitService.findVisitsByStatus(visitStatus, model.fromDate, model.toDate, insuranceType, therapist)
    }


    void changeVisitStatus(Visit visit, VisitStatus visitStatus) {
        log.info "changeVisitStatus(${visit}, ${visitStatus})"
        visitService.saveVisitWithStatusChange(visit, visitStatus, employeeSession.employee)
        // after making the change, refresh the visit data
        //loadVisitDataByStatus()
        loadVisitDataByStatusWithDistraction()
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