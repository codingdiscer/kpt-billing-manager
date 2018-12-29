package com.kinespherept.screen.visitstatus

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.config.SpringConfig
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Visit
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.EmployeeService
import com.kinespherept.service.LookupDataService
import com.kinespherept.service.VisitService
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
class ViewVisitDetailsWithTreatmentController {

    @MVCMember @Nonnull ViewVisitDetailsWithTreatmentModel model
    @MVCMember @Nonnull ViewVisitDetailsWithTreatmentView view

    @GriffonAutowire TrackVisitStatusController trackVisitStatusController
    @GriffonAutowire EditVisitDetailsWithTreatmentController editVisitDetailsWithTreatmentController

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire EmployeeService employeeService
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
        model.visitDate = visit.visitDate.format(commonProperties.dateFormatter)
        //model.visitNumber = visitService.getVisitNumber(visit)
        model.visitNumber = visit.visitNumber
        model.patientName = visit.patient.displayableName
        model.patientType = visit.patientType.patientTypeName
        model.insuranceType = visit.insuranceType.insuranceTypeName
        model.visitType = visit.visitType.visitTypeName
        model.therapist = employeeService.findById(visit.therapistId)?.fullname
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
    void returnToStatusTracker() {
        log.info "returnToStatusTracker()"
        trackVisitStatusController.prepareForm()
        SceneManager.changeTheScene(SceneDefinition.TRACK_VISIT_STATUS)
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void editVisitDetails() {
        log.info "editVisitDetails()"
        editVisitDetailsWithTreatmentController.prepareForm(model.visit)
        SceneManager.changeTheScene(SceneDefinition.EDIT_VISIT_DETAILS_WITH_TREATMENT)
    }

}
