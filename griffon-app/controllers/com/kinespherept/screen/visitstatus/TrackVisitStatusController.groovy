package com.kinespherept.screen.visitstatus

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.component.NavigationController
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.NavigationSection
import com.kinespherept.enums.SearchType
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.EmployeeRole
import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.Patient
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

@ArtifactProviderFor(GriffonController)
@Slf4j
class TrackVisitStatusController {

    @MVCMember @Nonnull TrackVisitStatusModel model
    @MVCMember @Nonnull TrackVisitStatusView view


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
        String visitStatusesChoice = model.visitStatusesChoice
        String insuranceTypesChoice = model.insuranceTypesChoice
        String therapistsChoice = model.therapistsChoice
        String patientSearch = model.patientSearch
        String patientsChoice = model.patientsChoice

        // prepare the visit statuses (a selected list)
        model.visitStatuses.clear()
        model.visitStatuses.addAll(VisitStatus.values().collect{ it.text })

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


        log.info "prepareForm() :: lastSearchType=${model.lastSearchType}"

        // reload the search results with the current criteria
        if(model.lastSearchType == SearchType.STATUS) {
            // restore the status based choices
            model.visitStatusesChoice = visitStatusesChoice
            model.insuranceTypesChoice = insuranceTypesChoice
            model.therapistsChoice = therapistsChoice

            // kick off the search again
            loadVisitDataByStatusWithDistraction()
        } else {
            // restore the patient based choices
            model.patientSearch = patientSearch

            changePatientFilterInternal()

            // reset this flag - the call above cleared it..
            preparingForm = true

            model.patientsChoice = patientsChoice

            // reset the status based choices
            model.visitStatusesChoice = VisitStatus.VISIT_CREATED.text
            model.insuranceTypesChoice = TrackVisitStatusModel.ALL
            model.therapistsChoice = TrackVisitStatusModel.ALL

            loadVisitDataByPatientWithDistraction()
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
            if(model.lastSearchType == SearchType.STATUS) {
                loadVisitDataByStatusWithDistraction()
            } else {
                loadVisitDataByPatientWithDistraction()
            }
        }
    }

    void selectToDate() {
        log.debug "selectToDate() :: toDate=${model.toDate}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            if(model.lastSearchType == SearchType.STATUS) {
                loadVisitDataByStatusWithDistraction()
            } else {
                loadVisitDataByPatientWithDistraction()
            }
        }
    }

    void changeVisitStatus() {
        log.debug "changeVisitStatus() :: visitStatus=${model.visitStatusesChoice}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            clearPatientSearch()
            loadVisitDataByStatusWithDistraction()
        }
    }

    void changeInsuranceType() {
        log.debug "changeInsuranceType() :: insuranceType=${model.insuranceTypesChoice}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            clearPatientSearch()
            loadVisitDataByStatusWithDistraction()
        }
    }

    void changeTherapist() {
        log.debug "changeTherapist() :: therapist=${model.therapistsChoice}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            clearPatientSearch()
            loadVisitDataByStatusWithDistraction()
        }
    }

    void changePatientFilter() {
        log.info "changePatientFilter() :: patientSearch=${model.patientSearch}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            changePatientFilterInternal()
        }
    }


    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void changePatientFilterInternal() {
        log.info "changePatientFilterInternal() :: patientSearch=${model.patientSearch}"
        model.filteredPatients = patientService.searchPatients(model.patientSearch)
        log.info "changePatientFilterInternal() :: ..found ${model.filteredPatients.size()} results"

        runInsideUISync {
            preparingForm = true
            model.patients.clear()
            model.filteredPatientCount = "Found ${model.filteredPatients.size()} results..".toString()
            model.patients << model.filteredPatientCount
            model.filteredPatients.each {
                model.patients << it.lastNameFirst
            }
            model.patientsChoice = model.filteredPatientCount
            preparingForm = false
        }
    }

    void selectPatient() {
        log.info "selectPatient() :: patientsChoice=${model.patientsChoice}; preparingForm=${preparingForm}"
        if(!preparingForm) {
            if(model.patientsChoice != model.filteredPatientCount) {
                Patient patient = patientService.findByLastNameFirst(model.patientsChoice)

                log.info "selectPatient() :: can now search for patient results for [${patient}]"

                loadVisitDataByPatientWithDistraction()
            }
        }
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

            prepareHeaders()

            // clear the current result set and message count
            view.visitResultsGridPane.children.clear()
            model.resultCountMessage = ''

            runOutsideUI {
                List<Visit> visits = findVisitData()

                runInsideUISync {
                    // rebuild the results
                    model.resultCountMessage = "Found ${visits.size()} result${visits.size() == 1 ? '' : 's'}."
                    visits.eachWithIndex{ Visit entry, int i -> buildResultRow(entry, i) }
                    view.showSpinner(false)
                }
            }
        }
    }

    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void loadVisitDataByPatientWithDistraction() {
        model.lastSearchType = SearchType.PATIENT
        loadVisitDataWithDistraction(
                { view.prepareGridsByPatient() },
                { loadVisitDataByPatient() },
                { Visit entry, int i -> view.buildResultRowByPatient(entry, i) }
        )
    }


    List<Visit> loadVisitDataByPatient() {
        visitService.findVisitsByPatient(patientService.findByLastNameFirst(model.patientsChoice),
                model.fromDate, model.toDate)
    }

    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void loadVisitDataByStatusWithDistraction() {
        model.lastSearchType = SearchType.STATUS
        loadVisitDataWithDistraction(
                { view.prepareGridsByStatus() },
                { loadVisitDataByStatus() },
                { Visit entry, int i -> view.buildResultRowByStatus(entry, i) }
        )
    }


    List<Visit> loadVisitDataByStatus() {
        log.info "loadVisitDataByStatus(visitStatus=${model.visitStatusesChoice}; fromDate=${model.fromDate}; toDate=${model.toDate}; insuranceType=${model.insuranceTypesChoice}; therapist=${model.therapistsChoice})"

        // convert VisitStatus, InsuranceType and Therapist into proper objects

        VisitStatus visitStatus = VisitStatus.findFromText(model.visitStatusesChoice)
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


    void changeVisitStatus(Visit visit, VisitStatus visitStatus) {
        log.info "changeVisitStatus(${visit}, ${visitStatus})"
        visitService.saveVisitWithStatusChange(visit, visitStatus, employeeSession.employee)
        // after making the change, refresh the visit data
        if(model.lastSearchType == SearchType.STATUS) {
            loadVisitDataByStatusWithDistraction()
        } else {
            loadVisitDataByPatientWithDistraction()
        }
    }

    void viewVisitDetails(Visit visit) {
        log.info "viewVisitDetails(${visit}), visitRequiresTreatment=${visitService.visitRequiresTreatment(visit)}"

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
        model.patientSearch = ''
        model.patients.clear()
        model.patients << TrackVisitStatusModel.PATIENT_SEARCH
        model.patientsChoice = TrackVisitStatusModel.PATIENT_SEARCH
        view.visitResultsGridPane.children.clear()
        preparingForm = false
    }

}