package com.kinespherept.screen.therapist

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.component.NavigationController
import com.kinespherept.config.CommonProperties
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.NavigationSection
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitStatus
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.LookupDataService
import com.kinespherept.service.PatientService
import com.kinespherept.service.VisitService
import com.kinespherept.session.EmployeeSession
import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import groovy.util.logging.Slf4j

import javax.annotation.Nonnull
import javax.annotation.PostConstruct
import java.time.LocalDate
import java.util.stream.Collectors


@ArtifactProviderFor(GriffonController)
@Slf4j
class SelectPatientVisitController {

    @MVCMember @Nonnull SelectPatientVisitModel model
    @MVCMember @Nonnull SelectPatientVisitView view

    @SpringAutowire EmployeeSession employeeSession
    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire PatientService patientService
    @SpringAutowire VisitService visitService
    @SpringAutowire CommonProperties commonProperties

    @GriffonAutowire NavigationController navigationController
    @GriffonAutowire FillOutPatientVisitWithTreatmentController fillOutPatientVisitWithTreatmentController
    @GriffonAutowire FillOutPatientVisitNoTreatmentController fillOutPatientVisitNoTreatmentController

    // this flag signifies we are in the prepareForm() or setVisitAndDisplay() method.  this is needed/useful because that method
    // sets a couple of model fields that have onChange() events attached to them, and this flag will tell
    // those methods not to take any action that would otherwise be taken if a user triggered the onChange() event.
    boolean preparingForm = false

    @PostConstruct
    void init() {
        log.debug "init()"
        SpringConfig.autowire(this)
    }


    void prepareForm(LocalDate visitDate, boolean showAllVisits) {
        model.visitDate = visitDate
        prepareForm(false, showAllVisits)
    }


    void prepareForm(boolean resetVisitDate = true, boolean showAllVisits = false) {

        preparingForm = true

        log.debug "prepareForm() :: employeeSession.employee=${employeeSession.employee}"

        navigationController.prepareForm(view.navigationPane, NavigationSection.THERAPIST)

        if(resetVisitDate) {
            model.visitDate = LocalDate.now()
        }
        model.showAllVisits = showAllVisits
        view.setToggle(model.showAllVisits)

        refreshVisitors()

        preparingForm = false
    }


    void refreshVisitors() {
        log.info "refreshVisitors() :: showAllVisits=${model.showAllVisits}; ${model.visitDate}; ${employeeSession.employee})"

        model.visitors.clear()
        model.visits.clear()

        // prepare the list of visits - grab the full list from the visitService..
        model.visits.addAll(visitService.findVisitsByDateAndTherapist(model.visitDate, employeeSession.employee)
                .stream()
                // .. and filter down based on showAllVisits
                .filter{ v ->  model.showAllVisits ?: v.visitStatus == VisitStatus.VISIT_CREATED }
                .collect(Collectors.toList())
        )

        // for each entry in the visit list, populate the name for the list view
        model.visitors.addAll(
                model.visits.stream()
                    .map{ v -> patientService.findById(v.patientId).displayableName }
                    .collect(Collectors.toList()))
    }


    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void loadVisit() {
        if(view.visitors.selectionModel.selectedIndex > -1) {
            Visit visit = model.visits[view.visitors.selectionModel.selectedIndex]

            if(visitService.visitRequiresTreatment(visit)) {
                fillOutPatientVisitWithTreatmentController.transitionToTreatments(visit, model.showAllVisits)
                SceneManager.changeTheScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_WITH_TREATMENT)
            } else {
                fillOutPatientVisitNoTreatmentController.transitionFromTreatments(visit, model.showAllVisits)
                SceneManager.changeTheScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_NO_TREATMENT)
            }
        } else {
            log.debug "loadVisit() :: no visit selected"
        }
    }


    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void showPendingVisits() {
        log.debug "showPendingVisits()"
        if(model.showAllVisits) {
            model.showAllVisits = false
            view.setToggle(model.showAllVisits)
            refreshVisitors()
        }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void showAllVisits() {
        log.debug "showAllVisits()"
        if(!model.showAllVisits) {
            model.showAllVisits = true
            view.setToggle(model.showAllVisits)
            refreshVisitors()
        }
    }

}
