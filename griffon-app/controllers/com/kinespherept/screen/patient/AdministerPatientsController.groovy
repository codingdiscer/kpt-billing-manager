package com.kinespherept.screen.patient

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.component.NavigationController
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.Mutation
import com.kinespherept.enums.NavigationSection
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Patient
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.PatientService
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
class AdministerPatientsController {

    @MVCMember @Nonnull AdministerPatientsModel model
    @MVCMember @Nonnull AdministerPatientsView view

    @GriffonAutowire NavigationController navigationController
    @GriffonAutowire SetupPatientController setupPatientController
    @GriffonAutowire SetupVisitController setupVisitController

    @SpringAutowire PatientService patientService

    boolean preparingForm = false


    @PostConstruct
    void init() {
        log.debug "init()"
        SpringConfig.autowire(this)
    }


    void prepareForm() {
        log.info "prepareForm() :: view.application=${view.application}"
        preparingForm = true

        // clear all the 'selected' form fields
        model.searchFilter = ''
        model.patientName = ''
        model.insuranceTypeName = ''
        model.patientTypeName = ''
        model.notes = ''

        // setup the names in the name list
        populatePatientResults(patientService.patients)

        // disable the buttons on the form
        view.disableButtons()

        // setup the navigationPane pane
        navigationController.prepareForm(view.navigationPane, NavigationSection.PATIENTS)

        preparingForm = false
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
                view.patientSearchResults.items.clear()
                populatePatientResults(patientService.searchPatients(searchFilter))
            }
        }
    }

    void populatePatientResults(List<Patient> patients) {
        view.patientSearchResults.items.clear()

        patients.sort { p1, p2 ->
                // first level, compare lastname
                if(p1.lastName != p2.lastName) {
                    return p1.lastName <=> p2.lastName
                }
                return p1.firstName <=> p2.firstName
        }

        patients.forEach({ p ->
            view.patientSearchResults.items << view.buildPatientSearchResult(p)})
    }

    /**
     * Called when a patient is selected from the search results.  Makes calls to load the patient
     * info into the UI
     * @param patient The patient info to load
     */
    void selectPatient(Patient patient) {
        log.debug "onPatientSelect(${patient})"
        model.patient = patient
        model.patientName = patient.getDisplayableName()
        model.patientTypeName = patient.patientType?.patientTypeName
        model.insuranceTypeName = patient.insuranceType?.insuranceTypeName
        model.notes = patient.notes

        // enable the buttons on the form
        view.enableButtons()
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


    /**
     * Called by the 'Edit Patient' button on the UI to go to the 'edit patient' scene
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void editPatient() {
        log.debug "editPatient() :: model.patient=${model.patient}"

        if(model.patient) {
            //patientSetupController.clearForm()
            setupPatientController.prepareForm(Mutation.UPDATE, model.patient)
            SceneManager.changeTheScene(SceneDefinition.SETUP_PATIENT)
        }

    }

    /**
     * Called by the 'Setup New Visit' button on the UI to go to the 'setup visit' scene
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void setupVisit() {
        log.debug "setupVisit() :: model.patient=${model.patient}"
        if(model.patient) {
            setupVisitController.prepareForm(model.patient)
            SceneManager.changeTheScene(SceneDefinition.SETUP_VISIT)
        }
    }


}
