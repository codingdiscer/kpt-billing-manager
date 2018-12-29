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
class VerifyPatientVisitNoTreatmentController {

    @MVCMember @Nonnull VerifyPatientVisitNoTreatmentModel model
    @MVCMember @Nonnull VerifyPatientVisitNoTreatmentView view

    @GriffonAutowire FillOutPatientVisitNoTreatmentController fillOutPatientVisitNoTreatmentController

    @SpringAutowire EmployeeSession employeeSession
    @SpringAutowire LookupDataService lookupDataService
    //@SpringAutowire SceneManager sceneManager
    @SpringAutowire VisitService visitService


    @PostConstruct
    void init() {
        log.debug "init()"
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
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void returnToEdit() {
        log.info "returnToEdit() :: model.visit=${model.visit}"
        fillOutPatientVisitNoTreatmentController.prepareForm(false)
        fillOutPatientVisitNoTreatmentController.setVisitAndDisplay(model.visit)
        SceneManager.changeTheScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_NO_TREATMENT)
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void saveAndComplete() {
        log.info "saveAndComplete():: model.visit=${model.visit}"

        // clear out the diagnoses and treatments
        model.visit.visitDiagnoses.clear()
        model.visit.visitTreatments.clear()

        // save the visit
        visitService.saveVisitReplaceDiagnosesAndTreatments(model.visit,
                VisitStatus.SEEN_BY_THERAPIST, employeeSession.getEmployee()
        )

        // return to the therapist view page
        fillOutPatientVisitNoTreatmentController.prepareForm(false)
        SceneManager.changeTheScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_NO_TREATMENT)
    }
}
