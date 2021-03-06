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
class FillOutPatientVisitNoTreatmentController {

    @MVCMember @Nonnull FillOutPatientVisitNoTreatmentModel model
    @MVCMember @Nonnull FillOutPatientVisitNoTreatmentView view

    @SpringAutowire EmployeeSession employeeSession
    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire PatientService patientService
    @SpringAutowire VisitService visitService
    @SpringAutowire CommonProperties commonProperties

    @GriffonAutowire FillOutPatientVisitWithTreatmentController fillOutPatientVisitWithTreatmentController
    @GriffonAutowire NavigationController navigationController
    @GriffonAutowire SelectPatientVisitController selectPatientVisitController
    @GriffonAutowire VerifyPatientVisitNoTreatmentController verifyPatientVisitNoTreatmentController

    // this flag signifies we are in the prepareForm() or setVisitAndDisplay() method.  this is needed/useful because that method
    // sets a couple of model fields that have onChange() events attached to them, and this flag will tell
    // those methods not to take any action that would otherwise be taken if a user triggered the onChange() event.
    boolean preparingForm = false

    // same deal as above, just for clearing the form
    boolean clearingForm = false

    @PostConstruct
    void init() {
        log.debug "init()"
        SpringConfig.autowire(this)
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

    /**
     * Called when the date selection in the date picker is changed.  just toss back
     * to the visit selection screen.
     */
    void changeDate() {
        selectPatientVisitController.prepareForm(model.visitDate, model.showAllVisits)
        SceneManager.changeTheScene(SceneDefinition.SELECT_PATIENT_VISIT)
    }


    void refreshVisitors() {
        log.debug "refreshVisitors() :: showAllVisits=${model.showAllVisits}; ${model.visitDate}; ${employeeSession.employee})"

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
    void clearForm() {
        log.debug "clearForm()"
        clearingForm = true
        model.selectedVisit = null

        selectPatientVisitController.prepareForm(false, model.showAllVisits)
        SceneManager.changeTheScene(SceneDefinition.SELECT_PATIENT_VISIT)
        clearingForm = false
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void loadVisit() {
        if(view.visitors.selectionModel.selectedIndex > -1) {
            Visit visit = model.visits[view.visitors.selectionModel.selectedIndex]

            if(visitService.visitRequiresTreatment(visit)) {
                fillOutPatientVisitWithTreatmentController.transitionToTreatments(visit, model.showAllVisits)
                SceneManager.changeTheScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_WITH_TREATMENT)
            } else {
                // load the visit for the selected patient
                setVisitAndDisplay(visit)
            }
        } else {
            log.debug "loadVisit() :: no visit selected"
        }
    }

    void setVisitAndDisplay(Visit visit) {
        preparingForm = true

        model.selectedVisit = visit
        log.debug "setVisitAndDisplay() :: selectedVisit=${model.selectedVisit}"
        log.debug "setVisitAndDisplay() :: selectedVisit.visitStatus=${model.selectedVisit.visitStatus}"
        log.debug "setVisitAndDisplay() :: selectedVisit.visitTreatments=${model.selectedVisit.visitTreatments}"
        log.debug "setVisitAndDisplay() :: selectedVisit.visitDiagnoses=${model.selectedVisit.visitDiagnoses}"
        log.debug "setVisitAndDisplay() :: selectedVisit.patient=${model.selectedVisit.patient}"
        log.debug "setVisitAndDisplay() :: selectedVisit.patientType=${model.selectedVisit.patientType}"
        log.debug "setVisitAndDisplay() :: selectedVisit.notes=${model.selectedVisit.notes}"

        model.patientName = model.selectedVisit.patient.displayableName

        model.visitDate = model.selectedVisit.visitDate

        model.patientTypes.clear()
        model.patientTypes.addAll(lookupDataService.patientTypes.stream().map({ pt -> pt.patientTypeName }).collect(Collectors.toList()))
        model.patientTypesChoice = model.selectedVisit.patientType.patientTypeName

        model.insuranceTypes.clear()
        model.insuranceTypes.addAll(lookupDataService.insuranceTypes.stream().map({ it -> it.insuranceTypeName }).collect(Collectors.toList()))
        model.insuranceTypesChoice = model.selectedVisit.insuranceType.insuranceTypeName

        model.visitTypes.clear()
        model.visitTypes.addAll(lookupDataService.visitTypes.stream().map( { vt -> vt.visitTypeName }).collect(Collectors.toList()))
        model.visitTypesChoice = model.selectedVisit.visitType.visitTypeName

        model.visitNumber = String.valueOf(model.selectedVisit.visitNumber)
        model.notes = model.selectedVisit.notes

        preparingForm = false
    }


    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void showPendingVisits() {
        log.debug "showPendingVisits()"
        if(model.showAllVisits) {
            model.showAllVisits = false
            refreshVisitors()
        }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void showAllVisits() {
        log.debug "showAllVisits()"
        if(!model.showAllVisits) {
            model.showAllVisits = true
            refreshVisitors()
        }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void verifyAndComplete() {
        log.info "verifyAndComplete()"

        // clear any diagnoses and treatments
        model.selectedVisit.visitDiagnoses.clear()
        model.selectedVisit.visitTreatments.clear()

        // load the rest of the fields into the visit info
        model.selectedVisit.patientType = lookupDataService.findPatientTypeByName(model.patientTypesChoice)
        model.selectedVisit.patientTypeId = model.selectedVisit.patientType.patientTypeId
        model.selectedVisit.insuranceType = lookupDataService.findInsuranceTypeByName(model.insuranceTypesChoice)
        model.selectedVisit.insuranceTypeId = model.selectedVisit.insuranceType.insuranceTypeId
        model.selectedVisit.visitType = lookupDataService.findVisitTypeByName(model.visitTypesChoice)
        model.selectedVisit.visitTypeId = model.selectedVisit.visitType.visitTypeId
        model.selectedVisit.notes = model.notes

        // go to the new scene to verify the info
        verifyPatientVisitNoTreatmentController.prepareForm(model.selectedVisit)
        SceneManager.changeTheScene(SceneDefinition.VERIFY_PATIENT_VISIT_NO_TREATMENT)
    }

    void transitionFromTreatments(Visit visit, boolean showAllVisits) {
        transitionFromTreatments(visit, visit.patientType.patientTypeName, visit.insuranceType.insuranceTypeName,
                visit.visitType.visitTypeName, visit.notes, showAllVisits)
    }


    void transitionFromTreatments(Visit visit, String patientTypesChoice, String insuranceTypesChoice,
                                  String visitTypesChoice, String notes, boolean showAllVisits)
    {
        prepareForm(false, showAllVisits)
        setVisitAndDisplay(visit)

        preparingForm = true
        model.patientTypesChoice = patientTypesChoice
        model.insuranceTypesChoice = insuranceTypesChoice
        model.visitTypesChoice = visitTypesChoice
        model.notes = notes
        preparingForm = false
    }


    void checkTreatmentTypeChange() {
        log.debug "checkPatientTypeChange() :: patientType=${model.patientTypesChoice}; preparingForm=${preparingForm}; clearingForm=${clearingForm}"

        // make sure we aren't preparing the form or clearing the form (as those cause false 'change positives')
        if(!preparingForm && !clearingForm) {
            if(visitService.visitRequiresTreatment(model.patientTypesChoice,
                model.insuranceTypesChoice, model.visitTypesChoice))
            {
                log.info "..need to change scenes here..."
                fillOutPatientVisitWithTreatmentController.transitionToTreatments(model.selectedVisit,
                        model.patientTypesChoice, model.insuranceTypesChoice, model.visitTypesChoice, model.notes,
                        model.showAllVisits)
                SceneManager.changeTheScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_WITH_TREATMENT)
            }
        }
    }



}
