package com.kinespherept.screen.therapist

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.component.NavigationController
import com.kinespherept.component.SelectDiagnosisController
import com.kinespherept.component.SelectTreatmentController
import com.kinespherept.config.CommonProperties
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.NavigationSection
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Diagnosis
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitDiagnosis
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
class FillOutPatientVisitWithTreatmentController {

    @MVCMember @Nonnull FillOutPatientVisitWithTreatmentModel model
    @MVCMember @Nonnull FillOutPatientVisitWithTreatmentView view

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire EmployeeSession employeeSession
    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire PatientService patientService
    @SpringAutowire VisitService visitService
    //@SpringAutowire SceneManager sceneManager


    @GriffonAutowire FillOutPatientVisitNoTreatmentController fillOutPatientVisitNoTreatmentController
    @GriffonAutowire NavigationController navigationController
    @GriffonAutowire SelectDiagnosisController selectDiagnosisController
    @GriffonAutowire SelectPatientVisitController selectPatientVisitController
    @GriffonAutowire SelectTreatmentController selectTreatmentController
    @GriffonAutowire VerifyPatientVisitWithTreatmentController verifyPatientVisitWithTreatmentController

    // this flag signifies we are in the prepareForm() or setVisitAndDisplay() method.  this is needed/useful because that method
    // sets a couple of model fields that have onChange() events attached to them, and this flag will tell
    // those methods not to take any action that would otherwise be taken if a user triggered the onChange() event.
    boolean preparingForm = false

    // same deal as above, just for clearing the form
    boolean clearingForm = false

    @PostConstruct
    void init() {
        log.info "init()"
        SpringConfig.autowire(this)
        log.debug "employeeSession=${employeeSession}"
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
        log.debug "refreshVisitors() :: showAllVisits=${model.showAllVisits}; ${model.visitDate}; ${employeeSession.employee})"

        model.visitors.clear()
        model.visits = visitService.findVisitsByDateAndTherapist(
                model.visitDate, employeeSession.employee)
        model.visits?.each { Visit v ->
            model.visitors.addAll(
                    patientService.patients.stream()
                            .filter({ p -> p.patientId == v.patientId })
                            .filter({ p -> model.showAllVisits ?: v.visitStatus == VisitStatus.VISIT_CREATED })
                            .map({ p -> p.getDisplayableName() })
                            .collect(Collectors.toList())
            )
        }

        // now clear the form
        clearForm()
    }



    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void clearForm() {
        clearingForm = true

        log.debug "clearForm() :: selectDiagnosisController.hashcode=${selectDiagnosisController.hashCode()}, view.anchorPane.hashcode=${view.diagnosisSelector.hashCode()}"

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
                // make sure all the visits for this patient are loaded
                visit.patient.visits = visitService.findVisitsForPatient(visit.patient)

                // load the visit for the selected patient
                setVisitAndDisplay(visit)
            } else {
                fillOutPatientVisitNoTreatmentController.transitionFromTreatments(visit)
                SceneManager.changeTheScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_NO_TREATMENT)
            }
        } else {
            log.debug "loadVisit() :: no visit selected"
        }
    }

    void setVisitAndDisplay(Visit visit) {
        preparingForm = true

        model.errorMessage = ''

        model.selectedVisit = visit
        log.debug "setVisitAndDisplay() :: selectedVisit=${model.selectedVisit}"
        log.debug "setVisitAndDisplay() :: selectedVisit.visitStatus=${model.selectedVisit.visitStatus}"
        log.debug "setVisitAndDisplay() :: selectedVisit.visitTreatments=${model.selectedVisit.visitTreatments}"
        log.debug "setVisitAndDisplay() :: selectedVisit.visitDiagnoses=${model.selectedVisit.visitDiagnoses}"
        log.debug "setVisitAndDisplay() :: selectedVisit.patient=${model.selectedVisit.patient}"
        log.info "setVisitAndDisplay() :: selectedVisit.patient.visits=${model.selectedVisit.patient.visits}"
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

        selectDiagnosisController.prepareForm(view.diagnosisSelector,
                model.selectedVisit.visitDiagnoses.stream().map( { vd -> lookupDataService.findDiagnosisById(vd.diagnosisId) }) .collect(Collectors.toList()),
                commonProperties.maxDiagnosisForPatientVisit)
        selectTreatmentController.prepareForm(view.treatmentSelector, model.selectedVisit.visitTreatments)

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
        log.debug "verifyAndComplete(), diagnosisCount=${selectDiagnosisController.getSelectedDiagnoses().size()}, treatmentCount=${selectTreatmentController.getTreatmentCount()}"

        // make sure diagnoses & treatments have been selected
        if(selectDiagnosisController.getSelectedDiagnoses().size() == 0) {
            if(selectTreatmentController.getTreatmentCount() == 0) {
                model.errorMessage = 'Select diagnoses and treatments.'
                return
            } else {
                model.errorMessage = 'Select diagnoses.'
                return
            }
        } else {
            if(selectTreatmentController.getTreatmentCount() == 0) {
                model.errorMessage = 'Select treatments.'
                return
            }
        }

        // make sure the correct amount of diagnoses have been selected
        if(selectDiagnosisController.getSelectedDiagnoses().size() > commonProperties.maxDiagnosisForPatientVisit) {
            model.errorMessage = "Limited to ${commonProperties.maxDiagnosisForPatientVisit} diagnoses."
            return
        }

        // if we got this far, then diagnoses and treatments have been selected
        log.debug "verifyAndComplete(), selectedDiagnoses=${selectDiagnosisController.getSelectedDiagnoses()}"
        log.debug "verifyAndComplete(), selectedTreatments=${selectTreatmentController.getSelectedTreatments(model.selectedVisit.visitId)}"

        updateDiagnosesAndTreatmentsToVisit(model.selectedVisit)

        // load the rest of the fields into the visit info
        model.selectedVisit.patientType = lookupDataService.findPatientTypeByName(model.patientTypesChoice)
        model.selectedVisit.patientTypeId = model.selectedVisit.patientType.patientTypeId
        model.selectedVisit.insuranceType = lookupDataService.findInsuranceTypeByName(model.insuranceTypesChoice)
        model.selectedVisit.insuranceTypeId = model.selectedVisit.insuranceType.insuranceTypeId
        model.selectedVisit.visitType = lookupDataService.findVisitTypeByName(model.visitTypesChoice)
        model.selectedVisit.visitTypeId = model.selectedVisit.visitType.visitTypeId
        model.selectedVisit.notes = model.notes

        // go to the new scene to verify the info
        verifyPatientVisitWithTreatmentController.prepareForm(model.selectedVisit)
        SceneManager.changeTheScene(SceneDefinition.VERIFY_PATIENT_VISIT_WITH_TREATMENT)
    }

    void updateDiagnosesAndTreatmentsToVisit(Visit visit) {
        visit.visitDiagnoses.clear()
        visit.visitDiagnoses = selectDiagnosisController.getSelectedDiagnoses().collect {
            Diagnosis d -> new VisitDiagnosis(visitId: visit.visitId, diagnosisId: d.diagnosisId)
        }

        visit.visitTreatments.clear()
        visit.visitTreatments = selectTreatmentController.getSelectedTreatments(visit.visitId)
    }


    void transitionToTreatments(Visit visit, boolean showAllVisits) {
        transitionToTreatments(visit, visit.patientType.patientTypeName,
                visit.insuranceType.insuranceTypeName, visit.visitType.visitTypeName, visit.notes, showAllVisits)
    }

    void transitionToTreatments(Visit visit, String patientTypesChoice, String insuranceTypesChoice,
                                String visitTypesChoice, String notes, boolean showAllVisits)
    {
        model.visitDate = visit.visitDate
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
        log.info "checkTreatmentTypeChange() :: patientType=${model.patientTypesChoice}; preparingForm=${preparingForm}; clearingForm=${clearingForm}"

        // make sure we aren't preparing the form or clearing the form (as those cause false 'change positives')
        if(!preparingForm && !clearingForm) {
            if(!visitService.visitRequiresTreatment(model.patientTypesChoice,
                    model.insuranceTypesChoice, model.visitTypesChoice)) {
                log.debug "..need to change scenes here..."
                fillOutPatientVisitNoTreatmentController.transitionFromTreatments(model.selectedVisit,
                        model.patientTypesChoice, model.insuranceTypesChoice, model.visitTypesChoice, model.notes, model.showAllVisits)
                SceneManager.changeTheScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_NO_TREATMENT)
            }
        }
    }


}