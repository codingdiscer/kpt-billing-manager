package com.kinespherept.screen.visitstatus

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.config.SpringConfig
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.EmployeeRole
import com.kinespherept.model.core.Visit
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
import java.util.stream.Collectors


@ArtifactProviderFor(GriffonController)
@Slf4j
class EditVisitDetailsNoTreatmentController {

    @MVCMember @Nonnull EditVisitDetailsNoTreatmentModel model
    @MVCMember @Nonnull EditVisitDetailsNoTreatmentView view

    @GriffonAutowire EditVisitDetailsWithTreatmentController editVisitDetailsWithTreatmentController
    @GriffonAutowire ViewVisitDetailsNoTreatmentController viewVisitDetailsNoTreatmentController

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire EmployeeService employeeService
    @SpringAutowire EmployeeSession employeeSession
    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire VisitService visitService

    boolean preparingForm = false

    @PostConstruct
    void init() {
        log.debug "init()"
        SpringConfig.autowire(this)
    }



    void prepareForm(Visit visit) {
        log.info "prepareForm(${visit})"
        preparingForm = true

        model.visit = visit
        model.visitDate = visit.visitDate.format(commonProperties.dateFormatter)
        model.visitNumber = visit.visitNumber
        model.patientName = visit.patient.displayableName

        model.patientTypes.clear()
        model.patientTypes.addAll(lookupDataService.patientTypes.stream().map({ pt -> pt.patientTypeName }).collect(Collectors.toList()))
        model.patientTypesChoice = model.visit.patientType.patientTypeName

        model.insuranceTypes.clear()
        model.insuranceTypes.addAll(lookupDataService.insuranceTypes.stream().map({ it -> it.insuranceTypeName }).collect(Collectors.toList()))
        model.insuranceTypesChoice = model.visit.insuranceType.insuranceTypeName

        model.visitTypes.clear()
        model.visitTypes.addAll(lookupDataService.visitTypes.stream().map( { vt -> vt.visitTypeName }).collect(Collectors.toList()))
        model.visitTypesChoice = model.visit.visitType.visitTypeName

        model.therapists.clear()
        model.therapists.addAll(employeeService.findByRoleExplicit(EmployeeRole.THERAPIST).stream().map( { e -> e.fullname }).collect(Collectors.toList()))
        model.therapistsChoice = model.visit.therapist.fullname

        model.visitNumber = String.valueOf(model.visit.visitNumber)
        model.notes = visit.notes
        preparingForm = false
    }

    void checkTreatmentTypeChange() {
        log.info "checkPatientTypeChange() :: patientType=${model.patientTypesChoice}; preparingForm=${preparingForm};"

        // make sure we aren't preparing the form (as that causes false 'change positives')
        if(!preparingForm && visitService.visitRequiresTreatment(model.patientTypesChoice,
                model.insuranceTypesChoice, model.visitTypesChoice))
        {
            log.info "checkPatientTypeChange() ::  ..need to change scenes here..."
            editVisitDetailsWithTreatmentController.transitionToTreatments(model.visit,
                    model.patientTypesChoice, model.insuranceTypesChoice, model.visitTypesChoice, model.notes)
            SceneManager.changeTheScene(SceneDefinition.EDIT_VISIT_DETAILS_WITH_TREATMENT)
        }
    }

//    void transitionFromTreatments(Visit visit) {
//        transitionFromTreatments(visit, visit.patientType.patientTypeName,
//                visit.insuranceType.insuranceTypeName, visit.visitType.visitTypeName, visit.notes)
//    }


    void transitionFromTreatments(Visit visit, String patientTypesChoice, String insuranceTypesChoice, String visitTypesChoice, String notes) {
        prepareForm(visit)
        preparingForm = true
        model.patientTypesChoice = patientTypesChoice
        model.insuranceTypesChoice = insuranceTypesChoice
        model.visitTypesChoice = visitTypesChoice
        model.notes = notes
        preparingForm = false
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void cancelAndIgnoreChanges() {
        log.info "cancelAndIgnoreChanges()"
        viewVisitDetailsNoTreatmentController.prepareForm(model.visit)
        SceneManager.changeTheScene(SceneDefinition.VIEW_VISIT_DETAILS_NO_TREATMENT)
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void saveChangesAndContinue() {
        log.info "saveChangesAndContinue()"

        Visit visit = model.visit

        // save updates for any mutable fields
        visit.patientType = lookupDataService.findPatientTypeByName(model.patientTypesChoice)
        visit.patientTypeId = visit.patientType.patientTypeId
        visit.insuranceType = lookupDataService.findInsuranceTypeByName(model.insuranceTypesChoice)
        visit.insuranceTypeId = visit.insuranceType.insuranceTypeId
        visit.visitType = lookupDataService.findVisitTypeByName(model.visitTypesChoice)
        visit.visitTypeId = visit.visitType.visitTypeId
        visit.notes = model.notes
        // clear out the diagnoses and treatments
        visit.visitDiagnoses.clear()
        visit.visitTreatments.clear()

        // track that the save occurred.  we aren't actually updating the visit status
        visitService.saveVisitReplaceDiagnosesAndTreatments(visit, visit.visitStatus, employeeSession.employee)
        // TODO - do a better job here of tracking what changed

        // done here, so bounce us back to the 'view visit details' page
        viewVisitDetailsNoTreatmentController.prepareForm(model.visit)
        SceneManager.changeTheScene(SceneDefinition.VIEW_VISIT_DETAILS_NO_TREATMENT)
    }

}
