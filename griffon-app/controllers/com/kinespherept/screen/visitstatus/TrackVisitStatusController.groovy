package com.kinespherept.screen.visitstatus

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.component.NavigationController
import com.kinespherept.config.CommonProperties
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.NavigationSection
import com.kinespherept.enums.SearchType
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.EmployeeRole
import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitStatus
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.EmployeeService
import com.kinespherept.service.LookupDataService
import com.kinespherept.service.PatientService
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
class TrackVisitStatusController {

    @MVCMember @Nonnull TrackVisitStatusModel model
    @MVCMember @Nonnull TrackVisitStatusView view


    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire EmployeeService employeeService
    @SpringAutowire EmployeeSession employeeSession
    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire PatientService patientService
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
        String patientSearch = model.patientSearchProperty.getValue()

        // prepare the status-type visit statuses (all the standard ones)
        model.statusTypeVisitStatuses.clear()
        model.statusTypeVisitStatuses.addAll(VisitStatus.values().collect{ it.text })

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

        navigationController.prepareForm(view.navigationPane, NavigationSection.STATUS_TRACKER)

        model.patients.clear()
        model.patients << TrackVisitStatusModel.PATIENT_SEARCH
        model.patientsChoice = TrackVisitStatusModel.PATIENT_SEARCH

        // prepare the patient-type visit statuses (all standard, plus 'ALL')
        model.patientTypeVisitStatuses.clear()
        model.patientTypeVisitStatuses << TrackVisitStatusModel.ALL
        model.patientTypeVisitStatuses.addAll(VisitStatus.values().collect{ it.text })


        model.changeToStatuses.clear()
        model.changeToStatuses << TrackVisitStatusView.SELECT_STATUS
        model.changeToStatuses.addAll(VisitStatus.values().collect{ it.text })
        model.changeToStatusesChoice = TrackVisitStatusView.SELECT_STATUS

        log.debug "prepareForm() :: searchType=${model.searchType}"

        runInsideUISync {
            view.prepareSearchFilterRow(model.searchType)
        }

        // reload the search results with the current criteria
        if(model.searchType == SearchType.STATUS) {

            // kick off the search again
            loadVisitDataByStatusWithDistraction()
        } else {
            // restore the patient based choices
            model.patientSearchProperty.setValue(patientSearch)

            changePatientFilterInternal()

            // reset this flag - the call above cleared it..
            preparingForm = true

            if(model.selectedPatient) {
                loadVisitDataByPatientWithDistraction()
            }
        }

        // declare that we are done preparing the form
        preparingForm = false
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
            if(model.searchType == SearchType.STATUS) {
                loadVisitDataByStatusWithDistraction()
            } else {
                loadVisitDataByPatientWithDistraction()
            }
        }
    }

    void selectToDate() {
        log.debug "selectToDate() :: toDate=${model.toDate}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            if(model.searchType == SearchType.STATUS) {
                loadVisitDataByStatusWithDistraction()
            } else {
                loadVisitDataByPatientWithDistraction()
            }
        }
    }

    void changeStatusTypeVisitStatus(String status) {
        log.debug "changeStatusTypeVisitStatus(${status})"
        if(status != null) {
            model.statusTypeVisitStatusesChoice = status
        }
        if(!preparingForm) {
            loadVisitDataByStatusWithDistraction()
        }
    }

    void changeInsuranceType(String insuranceType) {
        log.debug "changeInsuranceType(${insuranceType})"
        if (insuranceType != null) {
            model.insuranceTypesChoice = insuranceType
        }
        if(!preparingForm) {
            loadVisitDataByStatusWithDistraction()
        }
    }

    void changeTherapist(String therapist) {
        log.debug "changeTherapist(${therapist}) "
        if(therapist != null) {
            model.therapistsChoice = therapist
        }
        if(!preparingForm) {
            loadVisitDataByStatusWithDistraction()
        }
    }

    void changePatientFilter() {
        log.debug "changePatientFilter() :: patientSearchProperty=${model.patientSearchProperty.getValue()}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            changePatientFilterInternal()
        }
    }


    void changePatientTypeVisitStatus(String statusChoice) {
        log.debug "changePatientTypeVisitStatus(${statusChoice}) :: preparingForm=${preparingForm}"
        if(statusChoice != null) {
            model.patientTypeVisitStatusesChoice = statusChoice
        }
        if(!preparingForm) {
            loadVisitDataByPatientWithDistraction()
        }
    }



    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void selectPatientSearch() {
        log.debug "selectPatientSearch()"
        if(model.searchType != SearchType.PATIENT) {
            model.searchType = SearchType.PATIENT

            preparingForm = true

            // backup the from/to dates for the status search, then..
            model.statusFromDate = model.fromDate
            model.statusToDate = model.toDate
            // ..restore the patient dates
            model.fromDate = model.patientFromDate
            model.toDate = model.patientToDate

            preparingForm = false

            view.prepareSearchFilterRow(SearchType.PATIENT)
            log.debug "just set searchType to PATIENT"

            if(model.selectedPatient != null) {
                // execute the search (by patient)
                loadVisitDataByPatientWithDistraction()
            }

        }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void selectStatusSearch() {
        log.debug "selectStatusSearch()"
        if(model.searchType != SearchType.STATUS) {
            model.searchType = SearchType.STATUS

            preparingForm = true

            // backup the from/to dates for the status search, then..
            model.patientFromDate = model.fromDate
            model.patientToDate = model.toDate
            // ..restore the patient dates
            model.fromDate = model.statusFromDate
            model.toDate = model.statusToDate

            preparingForm = false

            view.prepareSearchFilterRow(SearchType.STATUS)
            log.debug "just set searchType to STATUS"

            // execute the search (by status)
            loadVisitDataByStatusWithDistraction()
        }
    }



    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void changePatientFilterInternal() {
        log.debug "changePatientFilterInternal() :: patientSearch=${model.patientSearchProperty.getValue()}"
        model.filteredPatients = patientService.searchPatients(model.patientSearchProperty.getValue())

        preparingForm = true
        model.patients.clear()
        model.filteredPatientCount = "Found ${model.filteredPatients.size()} results..".toString()
        model.patients << model.filteredPatientCount

        model.filteredPatients.each {
            model.patients << it.lastNameFirst
        }

        model.patientsChoice = model.filteredPatientCount
        view.resetFilteredPatients()
        preparingForm = false
    }

    void selectPatient(String patientsChoice) {
        log.debug "selectPatient(${patientsChoice}) :: preparingForm=${preparingForm}"
        model.patientsChoice = patientsChoice
        if(!preparingForm) {
            if(model.patientsChoice != model.filteredPatientCount) {
                model.selectedPatient = patientService.findByLastNameFirst(model.patientsChoice)

                log.debug "selectPatient() :: can now search for patient results for [${model.selectedPatient}]"

                // we have changed the patient selection, so reset the "fromDate"
                preparingForm = true
                model.fromDate = null
                preparingForm = false

                loadVisitDataByPatientWithDistraction()
            }
        }
    }

    /**
     * Builds and returns a string that will be displayed to the user that describes the search
     * filter criteria; ie - the criteria used for the currently displayed search results.
     */
    String prepareSearchFiltersForDisplay() {
        String searchFilters = ''
        if(model.searchType == SearchType.STATUS) {
            searchFilters = "Status: [${model.statusTypeVisitStatusesChoice}]    Insurance: [${model.insuranceTypesChoice}]    Therapist: [${model.therapistsChoice}]"
            if(model.fromDate) {
                searchFilters += "    Date range: [${commonProperties.dateFormatter.format(model.fromDate)}] to [${commonProperties.dateFormatter.format(model.toDate)}]"
            } else {
                searchFilters += "    On Date: [${commonProperties.dateFormatter.format(model.toDate)}]"
            }
        } else if(model.searchType == SearchType.PATIENT) {
            searchFilters = "Patient: [${model.selectedPatient.displayableName}]"
            if(model.patientTypeVisitStatusesChoice != TrackVisitStatusModel.ALL) {
                searchFilters += "    Status: [${model.patientTypeVisitStatusesChoice}]"
            }
            if(model.fromDate) {
                searchFilters += "    Date range: [${commonProperties.dateFormatter.format(model.fromDate)}] to [${commonProperties.dateFormatter.format(model.toDate)}]"
            } else {
                searchFilters += "    On Date: [${commonProperties.dateFormatter.format(model.toDate)}]"
            }
        }
        searchFilters
    }


    /**
     * Template method handles managing the UI while searching for results...
     * @param prepareHeaders A Closure that prepares the column header labels
     * @param findVisitData A Closure that returns a List<Visit>
     * @param buildResultRow A Closure that takes these parameters: { Visit entry, int i },
     *      and calls the appropriate view method to build the right row result
     */
    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void loadVisitDataWithDistraction(Closure prepareHeaders, Closure findVisitData, Closure buildResultRow) {
        runInsideUISync {
            view.showSpinner(true)

            String searchFilters = prepareSearchFiltersForDisplay()

            prepareHeaders()

            // clear the current result set and message count
            view.visitResultsGridPane.children.clear()
            model.resultsMessage = ''

            runOutsideUI {
                List<Visit> visits = findVisitData()

                // regen the search filter string for a patient search after the search is complete
                if(model.searchType == SearchType.PATIENT) {
                    searchFilters = prepareSearchFiltersForDisplay()
                }

                runInsideUISync {
                    // clear the results again.  this fixes a display bug that occurs when the user
                    // starts a 2nd (or more) search before a previous search returns results
                    view.visitResultsGridPane.children.clear()

                    // reset the model elements
                    model.visits.clear()
                    model.visits.addAll(visits)     // reset Visits list to match this result set
                    model.statusChoiceBoxes.clear() // clear the status ChoiceBox list so we can rebuilt it
                    model.visitCheckBoxes.clear()   // clear the CheckBox list so we can rebuild it

                    // allow options based on result set size
                    view.allowAffectAllRowsButtons(visits.size() > 0)     // select/unselect buttons
                    view.allowMultiRowUpdate(false)                 // disable to start with

                    // reset the 'multi-update status select' drop-down
                    model.changeToStatusesChoice = TrackVisitStatusView.SELECT_STATUS

                    // rebuild the results
                    model.resultsMessage = "Found ${visits.size()} result${visits.size() == 1 ? '' : 's'}.  Filters:   ${searchFilters}"
                    visits.eachWithIndex{ Visit entry, int i -> buildResultRow(entry, i) }
                    view.showSpinner(false)
                }
            }
        }
    }

    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void loadVisitDataByPatientWithDistraction() {
        model.searchType = SearchType.PATIENT
        loadVisitDataWithDistraction(
                { view.prepareGridsByPatient() },
                { loadVisitDataByPatient() },
                { Visit entry, int i -> view.buildResultRowByPatient(entry, i) }
        )
    }


    List<Visit> loadVisitDataByPatient() {
        List<Visit> visits = visitService.findVisitsByPatient(model.selectedPatient,
                VisitStatus.findFromText(model.patientTypeVisitStatusesChoice),
                model.fromDate, model.toDate)

        // see if the "fromDate" is null...
        if(!model.fromDate) {
            // special case - set the "fromDate" to be the earliest date found in the result set
            if(visits.size() > 0) {
                preparingForm = true
                model.fromDate = visits[0].visitDate
                preparingForm = false
            }
        }

        visits
    }

    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void loadVisitDataByStatusWithDistraction() {
        model.searchType = SearchType.STATUS
        loadVisitDataWithDistraction(
                { view.prepareGridsByStatus() },
                { loadVisitDataByStatus() },
                { Visit entry, int i -> view.buildResultRowByStatus(entry, i) }
        )
    }


    List<Visit> loadVisitDataByStatus() {
        log.debug "loadVisitDataByStatus(visitStatus=${model.statusTypeVisitStatusesChoice}; fromDate=${model.fromDate}; toDate=${model.toDate}; insuranceType=${model.insuranceTypesChoice}; therapist=${model.therapistsChoice})"

        // convert VisitStatus, InsuranceType and Therapist into proper objects

        VisitStatus visitStatus = VisitStatus.findFromText(model.statusTypeVisitStatusesChoice)
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


    /**
     * This is called when a user presses the "Change Status" button after selecting a status to change to
     */
    void changeVisitStatus(Visit visit, VisitStatus visitStatus) {
        log.debug "changeVisitStatus(${visit}, ${visitStatus})"

        // reset the error message
        model.errorMessage = ''

        visitService.saveVisitWithStatusChange(visit, visitStatus, employeeSession.employee)
        // after making the change, refresh the visit data
        if(model.searchType == SearchType.STATUS) {
            loadVisitDataByStatusWithDistraction()
        } else {
            loadVisitDataByPatientWithDistraction()
        }
    }

    void viewVisitDetails(Visit visit) {
        log.debug "viewVisitDetails(${visit}), visitRequiresTreatment=${visitService.visitRequiresTreatment(visit)}"

        // decide what 'view-visit-details' page to switch to : [With|No]Treatment
        if(visitService.visitRequiresTreatment(visit)) {
            viewVisitDetailsWithTreatmentController.prepareForm(visit)
            SceneManager.changeTheScene(SceneDefinition.VIEW_VISIT_DETAILS_WITH_TREATMENT)
        } else {
            viewVisitDetailsNoTreatmentController.prepareForm(visit)
            SceneManager.changeTheScene(SceneDefinition.VIEW_VISIT_DETAILS_NO_TREATMENT)
        }
    }

    /**
     * Called when the "Clear" button next to the patient search field is pressed
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void clearPatientSearch() {
        preparingForm = true

        model.patientSearchProperty.setValue('')
        model.patients.clear()
        model.filteredPatients.clear()
        model.selectedPatient = null
        model.patients << TrackVisitStatusModel.PATIENT_SEARCH
        model.patientsChoice = TrackVisitStatusModel.PATIENT_SEARCH
        view.prepareSearchFilterRow(model.searchType)

        view.visitResultsGridPane.children.clear()
        model.resultsMessage = ''
        preparingForm = false
    }

    /**
     * Called when any checkboxes are selected or unselected; performs the work of enabling and
     * disabling various components to ensure that the user only has intuitive options that will
     * prevent both confusion & problematic actions from being performs.
     */
    void rowSelectStatusChanged() {
        // reset the error message
        model.errorMessage = ''

        // what happens depends on how many boxes are checked now...
        int selectCount = model.visitCheckBoxes.findAll { it.selected }.size()

        // if 0 selected..
        if(selectCount == 0) {
            // - enable choice/button for all single row updates
            // - disable choice/button for multi-select update
            model.statusChoiceBoxes.each { it.disable = false }
            model.updateStatusButtons.each { it.disable = false}
            view.allowMultiRowUpdate(false)
        }

        // if only 1 selected..
        if(selectCount == 1) {
            // - enable choice/button for row
            // - disable choice/button for all other rows (single row update)
            // - disable choice/button for multi-select update
            String visitId = model.visitCheckBoxes.find { it.selected }.id
            model.statusChoiceBoxes.each {
                it.disable = it.id != visitId ?: false
            }
            model.updateStatusButtons.each {
                it.disable = it.id != visitId ?: false
            }
            view.allowMultiRowUpdate(false)
        }

        // if more than 1 selected..
        if(selectCount > 1) {
            // - disable choice/button for all other rows (single row update)
            // - enable choice/button for multi-select update
            model.statusChoiceBoxes.each { it.disable = true }
            model.updateStatusButtons.each { it.disable = true }
            view.allowMultiRowUpdate(true)
        }
    }



    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void doMultiSelectUpdateStatus() {
        // reset the error message
        model.errorMessage = ''

        VisitStatus newStatus = VisitStatus.findFromText(model.changeToStatusesChoice)

        List<Visit> visitsToUpdate =
                model.visitCheckBoxes.stream()
                        // filter by CheckBox.selected..
                        .filter { cb -> cb.selected }
                        // map the CheckBox entry to the corresponding Visit
                        .map { cb ->  model.visits.find { it.visitId == Long.valueOf(cb.id) } }
                        .collect(Collectors.toList())

        visitsToUpdate.each {
            visitService.saveVisitWithStatusChange(it, newStatus, employeeSession.employee)
        }

        // after making the change, refresh the visit data
        if(model.searchType == SearchType.STATUS) {
            loadVisitDataByStatusWithDistraction()
        } else {
            loadVisitDataByPatientWithDistraction()
        }

    }

    /**
     * Called when the "Select All Entries" button is pressed
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void selectAllEntries() {
        log.debug "selectAllEntries(), model.visitCheckBoxes.size=${model.visitCheckBoxes.size}"
        model.visitCheckBoxes.each { it.selected = true }
        rowSelectStatusChanged()
    }

    /**
     * Called when the "Unselect All Entries" button is pressed
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void unselectAllEntries() {
        log.debug "unselectAllEntries(), model.visitCheckBoxes.size=${model.visitCheckBoxes.size}"
        model.visitCheckBoxes.each { it.selected = false }
        rowSelectStatusChanged()
    }

}