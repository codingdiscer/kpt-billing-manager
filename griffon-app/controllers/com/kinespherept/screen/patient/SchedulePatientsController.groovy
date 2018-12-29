package com.kinespherept.screen.patient

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.component.NavigationController
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.Mutation
import com.kinespherept.enums.NavigationSection
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.EmployeeRole
import com.kinespherept.model.core.Patient
import com.kinespherept.model.core.Visit
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.EmployeeService
import com.kinespherept.service.PatientService
import com.kinespherept.service.VisitService
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import groovy.util.logging.Slf4j
import org.springframework.util.StringUtils

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonController)
@Slf4j
class SchedulePatientsController {


    @MVCMember @Nonnull SchedulePatientsModel model
    @MVCMember @Nonnull SchedulePatientsView view

    @SpringAutowire PatientService patientService
    @SpringAutowire EmployeeService employeeService
    @SpringAutowire VisitService visitService
    //@SpringAutowire SceneManager sceneManager

    @GriffonAutowire SetupPatientController setupPatientController
    @GriffonAutowire SetupVisitController setupVisitController
    @GriffonAutowire NavigationController navigationController

    boolean preparingForm = false


    @PostConstruct
    void init() {
        SpringConfig.autowire(this)
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void prepareForm() {
        log.info "prepareForm() :: view.application=${view.application}"
        preparingForm = true

        // clear all the 'selected' form fields
        model.searchFilter = ''
//        model.patientName = ''
//        model.insuranceTypeName = ''
//        model.patientTypeName = ''
//        model.notes = ''

        // setup the names in the name list
        populatePatientResults(patientService.patients)

        // prepare the employees dropdown
        List<Employee> thps = employeeService.findByRoleExplicit(EmployeeRole.THERAPIST)
        model.therapists.clear()
        thps.each { model.therapists << it.fullname }
        model.therapistsChoice = thps[0].fullname

        // setup the navigationPane pane
        navigationController.prepareForm(view.navigationPane, NavigationSection.SCHEDULER)

        // load the daily schedule
        loadVisitors()

        preparingForm = false
    }

    void populatePatientResults(List<Patient> patients) {
        view.patientResults.items.clear()
        patients.forEach({ p ->
            view.patientResults.items << view.buildPatientSearchResult(p)})
    }


    /**
     * Called by the 'x' (clear) button on the UI to wipe out the search filter value
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void clearFilter() {
        model.searchFilter = ''
        populatePatientResults(patientService.patients)
    }

    /**
     * Called by the 'Add Patient' button on the UI to go to the 'add patient' scene
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void addPatient() {
        log.debug "addPatient() :: view.application=${view.application}"
        setupPatientController.prepareForm(Mutation.CREATE, null)
        SceneManager.changeTheScene(SceneDefinition.SETUP_PATIENT)
    }


//    /**
//     * Called by the 'Edit Patient' button on the UI to go to the 'edit patient' scene
//     */
//    @ControllerAction
//    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
//    void editPatient() {
//        log.debug "editPatient() :: model.patient=${model.patient}"
//
//        if(model.patient) {
//            //patientSetupController.clearForm()
//            setupPatientController.prepareForm(Mutation.UPDATE, model.patient)
//            SceneManager.changeTheScene(SceneDefinition.SETUP_PATIENT)
//        }
//
//    }
//
//    /**
//     * Called by the 'Setup New Visit' button on the UI to go to the 'setup visit' scene
//     */
//    @ControllerAction
//    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
//    void setupVisit() {
//        log.debug "setupVisit() :: model.patient=${model.patient}"
//        if(model.patient) {
//            setupVisitController.prepareForm(model.patient, model.therapistsChoice, model.visitDate, null)
//            SceneManager.changeTheScene(SceneDefinition.SETUP_VISIT)
//        }
//    }


    /**
     * Called by the 'Cancel visit' button on the UI to remove a visit
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void deleteVisit() {
        // the property to explore more...
        log.debug "deleteVisit() :: view.visitors.selectedIndex=${view.visitors.selectionModel.selectedIndex}"

        // make sure something is selected (-1 = nothing selected; >= 0 means something is selected)
        if(view.visitors.selectionModel.selectedIndex > -1) {
            log.debug "deleteVisit() :: selectedVisitor=${model.visitors[view.visitors.selectionModel.selectedIndex]}"
            log.debug "deleteVisit() :: selectedVisit=${model.visits[view.visitors.selectionModel.selectedIndex]}"

            Visit deletedVisit = model.visits[view.visitors.selectionModel.selectedIndex]

            // remove this visit altogether
            visitService.deleteVisit(deletedVisit)

            log.debug "deleteVisit() ::  pre.patient.visits.size=${patientService.patients.find { it.patientId == deletedVisit.patientId }.visits.size()}"
            patientService.patients.find { it.patientId == deletedVisit.patientId }.visits.remove(deletedVisit)
            log.debug "deleteVisit() :: post.patient.visits.size=${patientService.patients.find { it.patientId == deletedVisit.patientId }.visits.size()}"

            refreshVisitors()
        }
    }

    /**
     * Called by the 'Edit visit' button on the UI to edit a visit
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void editVisit() {
        log.debug "editVisit() :: view.visitors.selectedIndex=${view.visitors.selectionModel.selectedIndex}"

        // make sure something is selected (-1 = nothing selected; >= 0 means something is selected)
        if(view.visitors.selectionModel.selectedIndex > -1) {
            log.debug "editVisit() :: selectedVisitor=${model.visitors[view.visitors.selectionModel.selectedIndex]}"
            log.debug "editVisit() :: selectedVisit=${model.visits[view.visitors.selectionModel.selectedIndex]}"

            Visit editedVisit = model.visits[view.visitors.selectionModel.selectedIndex]
            log.debug "editVisit() :: visit.patient=${editedVisit.patient}"
            log.debug "editVisit() :: visit.therapist=${editedVisit.therapist}"
            log.debug "editVisit() :: model.visitDate=${model.visitDate}"

            setupVisitController.prepareForm(editedVisit.patient, editedVisit.therapist.fullname, model.visitDate, editedVisit)
            SceneManager.changeTheScene(SceneDefinition.SETUP_VISIT)
        }
    }


    /**
     * Called when the text field holding the search filter is changed - this method makes calls to
     * update the search result UI element.
     * @param searchFilter The new search value
     */
    void applyFilter(String searchFilter) {

        log.debug "applyFilter(${searchFilter}), preparingForm=${preparingForm}"
        if(!preparingForm) {
            if (StringUtils.isEmpty(searchFilter)) {
                clearFilter()
            } else {
                view.patientResults.items.clear()
                populatePatientResults(patientService.searchPatients(searchFilter))
            }
        }
    }

    /**
     * Called when a patient is selected from the search results.  Makes calls to schedule a new visit
     */
    void selectPatient(Patient patient) {
        log.debug "selectPatient(${patient})"
//        model.patient = patient
//        model.patientName = patient.getDisplayableName()
//        model.patientTypeName = patient.patientType?.patientTypeName
//        model.insuranceTypeName = patient.insuranceType?.insuranceTypeName
//        //model.diagnosisList.clear()
//        //model.diagnosisList.addAll(patient.diagnoses.collect { it.diagnosisName })
//        model.notes = patient.notes

        setupVisitController.prepareForm(patient, model.therapistsChoice, model.visitDate, null)
        SceneManager.changeTheScene(SceneDefinition.SETUP_VISIT)
    }


    void refreshVisitors() {
        log.debug "refreshVisitors(${model.visitDate}, ${employeeService.findByFullname(model.therapistsChoice)}), preparingForm=${preparingForm}"
        if(!preparingForm) {
            loadVisitors()
        }
    }

    /**
     * Loads up the visitors list based on the currently selected therapist and date
     */
    void loadVisitors() {
        model.visitors.clear()
        model.visits = visitService.findVisitsByDateAndTherapist(
                model.visitDate, employeeService.findByFullname(model.therapistsChoice))
        model.visits?.each { Visit v ->
            model.visitors << patientService.patients.find { it.patientId == v.patientId }.getDisplayableName()
        }
    }
}