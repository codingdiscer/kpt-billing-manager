package com.kinespherept.screen.patient

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.Mutation
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.EmployeeRole
import com.kinespherept.model.core.Patient
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitDiagnosis
import com.kinespherept.model.core.VisitStatus
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.EmployeeService
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
import java.time.LocalDate
import java.util.stream.Collectors

@ArtifactProviderFor(GriffonController)
@Slf4j
class SetupVisitController {

    @MVCMember @Nonnull SetupVisitModel model
    @MVCMember @Nonnull SetupVisitView view

    //@SpringAutowire SceneManager sceneManager
    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire EmployeeService employeeService
    @SpringAutowire VisitService visitService
    @SpringAutowire EmployeeSession employeeSession

    @GriffonAutowire SchedulePatientsController schedulePatientsController

    @PostConstruct
    void init() {
        SpringConfig.autowire(this)
    }

    /**
     * The shortened version - called from the AdministerPatients screen.
     * We likely won't know the preferred therapist or date, so figure those out
     */
    void prepareForm(Patient patient) {
        // take whatever was previously selected..
        String therapistFullName = model.therapistsChoice
        if(!therapistFullName) {
            // ..otherwise, just choose the first therapist from the known list
            therapistFullName = employeeService.findByRoleExplicit(EmployeeRole.THERAPIST)[0].fullname
        }

        prepareForm(patient, therapistFullName, model.visitDate, null)
    }

    /**
     * Prepares the full form to being scheduling a patient
     */
    void prepareForm(Patient patient, String therapistFullName, LocalDate visitDate, Visit visit) {
        // set aside the patient
        model.patient = patient
        model.patientName = patient.getDisplayableName()

        model.visitDate = visitDate

        model.patientTypes.clear()
        lookupDataService.patientTypes.each { pt -> model.patientTypes << pt.patientTypeName }

        model.visitTypes.clear()
        lookupDataService.visitTypes.each { vt -> model.visitTypes << vt.visitTypeName }

        model.insuranceTypes.clear()
        lookupDataService.insuranceTypes.each { it -> model.insuranceTypes << it.insuranceTypeName }

        model.therapists.clear()
        employeeService.findByRoleExplicit(EmployeeRole.THERAPIST).each { th -> model.therapists << th.fullname }
        model.therapistsChoice = therapistFullName

        model.visit = visit

        if(visit) {
            model.mutation = Mutation.UPDATE
            model.patientTypesChoice = visit.patientType.patientTypeName
            model.insuranceTypesChoice = visit.insuranceType.insuranceTypeName
            model.visitTypesChoice = visit.visitType.visitTypeName
            model.notes = visit.notes
        } else {
            model.mutation = Mutation.CREATE
            model.patientTypesChoice = patient.patientType.patientTypeName
            model.insuranceTypesChoice = patient.insuranceType.insuranceTypeName

            log.debug "prepareForm() :: patient.visit.size=${patient.visits.size()}"
            if(patient.visits.size() == 0) {
                // see if this patient has ever been here before...
                if(visitService.patientHasVisitedBefore(patient)) {
                    log.debug "prepareForm() :: ..the patient has visited before.."
                    // default the visit types to 'follow up' (which is the second visit type entry)
                    model.visitTypesChoice = lookupDataService.visitTypes[1].visitTypeName
                } else {
                    log.debug "prepareForm() :: ..the patient has NOT visited before.."
                    // default the visit types to 'initial' (which is the first visit type entry)
                    model.visitTypesChoice = lookupDataService.visitTypes[0].visitTypeName
                }
            } else {
                // default the visit types to 'follow up' (which is the second visit type entry)
                model.visitTypesChoice = lookupDataService.visitTypes[1].visitTypeName
            }

            model.notes = ''
        }
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void setupVisit() {
        log.info "setupVisit() :: visitDate.dateNow=${model.visitDate}, patientTypesChoice=${model.patientTypesChoice}, visitTypesChoice=${model.visitTypesChoice}, therapists=${model.therapists}, therapistsChoice=${model.therapistsChoice}, notes=${model.notes},"

        // see if we are creating or updating...
        if(model.mutation == Mutation.CREATE) {

            // create the visit entry
            model.patient.visits <<  visitService.saveVisitReplaceDiagnosesAndTreatments(new Visit(
                    visitDate: model.visitDate,
                    patientId: model.patient.patientId,
                    patientTypeId: lookupDataService.findPatientTypeByName(model.patientTypesChoice)?.patientTypeId,
                    insuranceTypeId: lookupDataService.findInsuranceTypeByName(model.insuranceTypesChoice)?.insuranceTypeId,
                    visitTypeId: lookupDataService.findVisitTypeByName(model.visitTypesChoice)?.visitTypeId,
                    therapistId: employeeService.employees.find { it.fullname == model.therapistsChoice }?.employeeId,
                    visitStatus: VisitStatus.VISIT_CREATED,
                    notes: model.notes,
                    visitNumber: getVisitNumber(),

                    // map the patient's diagnoses into VisitDiagnosis objects
                    visitDiagnoses: visitService.getAllTrackedDiagnoses(model.patient).stream()
                            .map({ d -> new VisitDiagnosis(diagnosisId: d.diagnosisId) })
                            .collect(Collectors.toList())


                    ),
                    VisitStatus.VISIT_CREATED,
                    employeeSession.employee
            )

        } else if(model.mutation == Mutation.UPDATE) {
            model.visit.visitDate = model.visitDate
            model.visit.patientId =  model.patient.patientId
            model.visit.patientTypeId = lookupDataService.findPatientTypeByName(model.patientTypesChoice)?.patientTypeId
            model.visit.insuranceTypeId = lookupDataService.findInsuranceTypeByName(model.insuranceTypesChoice)?.insuranceTypeId
            model.visit.visitTypeId = lookupDataService.findVisitTypeByName(model.visitTypesChoice)?.visitTypeId
            model.visit.therapistId = employeeService.findByFullname(model.therapistsChoice)?.employeeId
            model.visit.notes = model.notes

            log.info("setupVisit() :: saving visit=${model.visit}")

            visitService.saveVisitWithStatusChange(model.visit, VisitStatus.VISIT_CREATED, employeeSession.employee)
        }

        schedulePatientsController.refreshVisitors()

        // we are done here - go back to the patient view
        SceneManager.changeTheScene(SceneDefinition.SCHEDULE_PATIENTS)
    }


    int getVisitNumber() {
        // see if a visit has already been setup for this patient on this date
        List<Visit> sameDateVisits = visitService.findVisitsForPatientOnDate(model.patient, model.visitDate)

        if(sameDateVisits.size() > 0) {
            return sameDateVisits[0].visitNumber
        }


        // if we got this far, then no same-day visits (for this date)

        // next, grab all the visits for the patient..
        List<Visit> allVisits = visitService.findVisitsForPatient(model.patient)
        if(allVisits.size() > 0) {
            // increment the visit number from the last visit
            return allVisits[allVisits.size() - 1].visitNumber + 1
        }

        // if we got this far, then we haven't seen this patient yet.  start with visit number 1.
        return 1


        // if we got this far, then it is a new visit on this date, so take the next number in line
        //NO - bug here - it blindly takes the next visit number, but that fails when more than one visit coccurs on a date
        //visitService.getSavedVisitCount(model.patient) + 1
    }



    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void cancel() {
        SceneManager.changeTheScene(SceneDefinition.SCHEDULE_PATIENTS)
    }



}