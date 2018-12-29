package com.kinespherept.screen.therapist

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.SpringConfig
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitStatus
import com.kinespherept.model.navigation.SceneDefinition
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
class VerifyPatientVisitWithTreatmentController {

    @MVCMember @Nonnull VerifyPatientVisitWithTreatmentModel model
    @MVCMember @Nonnull VerifyPatientVisitWithTreatmentView view

    @GriffonAutowire FillOutPatientVisitWithTreatmentController fillOutPatientVisitWithTreatmentController

    @SpringAutowire EmployeeSession employeeSession
    @SpringAutowire LookupDataService lookupDataService
    //@SpringAutowire SceneManager sceneManager
    @SpringAutowire VisitService visitService


    @PostConstruct
    void init() {
        SpringConfig.autowire(this)
    }


    void prepareForm(Visit visit) {
        log.info "prepareForm(${visit})"

        model.visit = visit
        model.visitDate = visit.visitDate.toString()
        model.patientName = visit.patient.displayableName
        model.patientType = visit.patientType.patientTypeName
        model.insuranceType = visit.insuranceType.insuranceTypeName
        model.visitType = visit.visitType.visitTypeName
        model.notes = visit.notes

        // prep the diagnosis count and UI list
        model.diagnosisCount = visit.visitDiagnoses.size()
        model.diagnosisRows.clear()
        visit.visitDiagnoses.forEach({ vd ->
            model.diagnosisRows << view.buildDiagnosisRow(lookupDataService.findDiagnosisById(vd.diagnosisId).nameForDisplay)
        })
        view.diagnosisRows.children.clear()
        view.diagnosisRows.children.addAll(model.diagnosisRows)

        // prep the treatment count and UI list
        log.info "prepareForm() :: visit.visitTreatments=${visit.visitTreatments}"
        model.treatmentCount = visit.visitTreatments.collect { it.treatmentQuantity }.sum()
        model.treatmentRows.clear()
        visit.visitTreatments.forEach( { vt ->
            model.treatmentRows << view.buildTreatmentRow(lookupDataService.findTreatmentById(vt.treatmentId).displayableName,
                    vt.treatmentQuantity)
        })
        view.treatmentRows.children.clear()
        view.treatmentRows.children.addAll(model.treatmentRows)
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void returnToEdit() {
        log.info "returnToEdit() :: model.visit=${model.visit}"
        fillOutPatientVisitWithTreatmentController.prepareForm(false)
        fillOutPatientVisitWithTreatmentController.setVisitAndDisplay(model.visit)
        SceneManager.changeTheScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_WITH_TREATMENT)
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void saveAndComplete() {
        log.info "saveAndComplete():: model.visit=${model.visit}"

        // save the visit
        visitService.saveVisitReplaceDiagnosesAndTreatments(model.visit,
                VisitStatus.SEEN_BY_THERAPIST, employeeSession.getEmployee()
        )

        // return to the therapist view page
        fillOutPatientVisitWithTreatmentController.prepareForm(false)
        SceneManager.changeTheScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_WITH_TREATMENT)
    }

}