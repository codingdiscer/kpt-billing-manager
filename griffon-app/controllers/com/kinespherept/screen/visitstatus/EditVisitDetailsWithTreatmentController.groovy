package com.kinespherept.screen.visitstatus

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.component.SelectDiagnosisController
import com.kinespherept.component.SelectTreatmentController
import com.kinespherept.config.CommonProperties
import com.kinespherept.config.SpringConfig
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Diagnosis
import com.kinespherept.model.core.EmployeeRole
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitDiagnosis
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
class EditVisitDetailsWithTreatmentController {

    @MVCMember @Nonnull EditVisitDetailsWithTreatmentModel model
    @MVCMember @Nonnull EditVisitDetailsWithTreatmentView view

    @GriffonAutowire EditVisitDetailsNoTreatmentController editVisitDetailsNoTreatmentController
    @GriffonAutowire SelectDiagnosisController selectDiagnosisController
    @GriffonAutowire SelectTreatmentController selectTreatmentController
    @GriffonAutowire ViewVisitDetailsWithTreatmentController viewVisitDetailsWithTreatmentController

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire EmployeeService employeeService
    @SpringAutowire EmployeeSession employeeSession
    @SpringAutowire LookupDataService lookupDataService
    //@SpringAutowire SceneManager sceneManager
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

        model.notes = visit.notes
        model.errorMessage = ''


        // prepare the diagnosis and treatment components with empty presets
        selectDiagnosisController.prepareForm(view.diagnosisSelector, [], commonProperties.maxDiagnosisForPatientVisit)
        selectTreatmentController.prepareForm(view.treatmentSelector, [])

        selectDiagnosisController.prepareForm(view.diagnosisSelector,
                visit.visitDiagnoses.stream().map( { vd -> lookupDataService.findDiagnosisById(vd.diagnosisId) }) .collect(Collectors.toList()),
                commonProperties.maxDiagnosisForPatientVisit)
        selectTreatmentController.prepareForm(view.treatmentSelector, visit.visitTreatments)


        preparingForm = false

    }

    void checkTreatmentTypeChange() {
        log.info "checkPatientTypeChange() :: patientType=${model.patientTypesChoice}; insuranceType=${model.insuranceTypesChoice}; preparingForm=${preparingForm};"

        // make sure we aren't preparing the form (as that causes false 'change positives')
        if(!preparingForm) {
            if(!visitService.visitRequiresTreatment(model.patientTypesChoice,
                model.insuranceTypesChoice, model.visitTypesChoice)) {
                log.info "checkPatientTypeChange() ::  ..need to change scenes to noTreatment here..."
                editVisitDetailsNoTreatmentController.transitionFromTreatments(model.visit,
                        model.patientTypesChoice, model.insuranceTypesChoice, model.visitTypesChoice, model.notes)
                SceneManager.changeTheScene(SceneDefinition.EDIT_VISIT_DETAILS_NO_TREATMENT)
            }
        }
    }


//    void transitionToTreatments(Visit visit) {
//        transitionToTreatments(visit, visit.patientType.patientTypeName,
//                visit.insuranceType.insuranceTypeName, visit.visitType.visitTypeName, visit.notes)
//    }

    void transitionToTreatments(Visit visit, String patientTypesChoice, String insuranceTypesChoice, String visitTypesChoice, String notes) {
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
        viewVisitDetailsWithTreatmentController.prepareForm(model.visit)
        SceneManager.changeTheScene(SceneDefinition.VIEW_VISIT_DETAILS_WITH_TREATMENT)
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void saveChangesAndContinue() {
        log.info "saveChangesAndContinue()"

        if(selectDiagnosisController.getSelectedDiagnoses().size() == 0) {
            if(selectTreatmentController.getTreatmentCount() == 0) {
                model.errorMessage = 'Select diagnoses and treatments.'
                return
            } else {
                model.errorMessage = 'Select diagnoses.'
                return
            }
        } else {
            if(selectTreatmentController.getTreatmentCount() == 0) {
                model.errorMessage = 'Select treatments.'
                return
            }
        }

        if(selectDiagnosisController.getSelectedDiagnoses().size() > commonProperties.maxDiagnosisForPatientVisit) {
            model.errorMessage = "Limited to ${commonProperties.maxDiagnosisForPatientVisit} diagnoses."
            return
        }



        Visit visit = model.visit

        // save updates for any mutable fields
        visit.patientType = lookupDataService.findPatientTypeByName(model.patientTypesChoice)
        visit.patientTypeId = visit.patientType.patientTypeId
        visit.insuranceType = lookupDataService.findInsuranceTypeByName(model.insuranceTypesChoice)
        visit.insuranceTypeId = visit.insuranceType.insuranceTypeId
        visit.visitType = lookupDataService.findVisitTypeByName(model.visitTypesChoice)
        visit.visitTypeId = visit.visitType.visitTypeId
        visit.notes = model.notes

        // grab the diagnoses and treatments from the other components
        visit.visitDiagnoses.clear()
        visit.visitDiagnoses = selectDiagnosisController.getSelectedDiagnoses().collect {
            Diagnosis d -> new VisitDiagnosis(visitId: visit.visitId, diagnosisId: d.diagnosisId)
        }

        visit.visitTreatments.clear()
        visit.visitTreatments = selectTreatmentController.getSelectedTreatments(visit.visitId)

        // track that the save occurred.  we aren't actually updating the visit status
        visitService.saveVisitReplaceDiagnosesAndTreatments(visit, visit.visitStatus, employeeSession.employee)
        // TODO - do a better job here of tracking what changed

        // done here, so bounce us back to the 'view visit details' page
        viewVisitDetailsWithTreatmentController.prepareForm(model.visit)
        SceneManager.changeTheScene(SceneDefinition.VIEW_VISIT_DETAILS_WITH_TREATMENT)
    }

}
