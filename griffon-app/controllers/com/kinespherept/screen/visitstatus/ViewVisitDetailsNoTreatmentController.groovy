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
class ViewVisitDetailsNoTreatmentController {

    @MVCMember @Nonnull ViewVisitDetailsNoTreatmentModel model
    @MVCMember @Nonnull ViewVisitDetailsNoTreatmentView view

    @GriffonAutowire TrackVisitStatusController trackVisitStatusController
    @GriffonAutowire EditVisitDetailsNoTreatmentController editVisitDetailsNoTreatmentController

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire EmployeeService employeeService
    @SpringAutowire LookupDataService lookupDataService
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
        editVisitDetailsNoTreatmentController.prepareForm(model.visit)
        SceneManager.changeTheScene(SceneDefinition.EDIT_VISIT_DETAILS_NO_TREATMENT)
    }

}
