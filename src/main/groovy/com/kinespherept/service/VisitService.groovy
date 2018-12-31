package com.kinespherept.service

import com.kinespherept.dao.VisitDao
import com.kinespherept.dao.repository.VisitDiagnosisRepository
import com.kinespherept.dao.repository.VisitRepository
import com.kinespherept.dao.repository.VisitStatusChangeRepository
import com.kinespherept.dao.repository.VisitTreatmentRepository
import com.kinespherept.model.core.Diagnosis
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.Patient
import com.kinespherept.model.core.PatientType
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitStatus
import com.kinespherept.model.core.VisitStatusChange
import com.kinespherept.model.core.VisitType
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.stream.Collectors


@Service
@Slf4j
@Transactional
class VisitService {

    EmployeeService employeeService
    LookupDataService lookupDataService
    PatientService patientService
    VisitDao visitDao
    VisitDiagnosisRepository visitDiagnosisRepository
    VisitRepository visitRepository
    VisitStatusChangeRepository visitStatusChangeRepository
    VisitTreatmentRepository visitTreatmentRepository



    VisitService(EmployeeService employeeService,
            LookupDataService lookupDataService, PatientService patientService,
            VisitDao visitDao, VisitRepository visitRepository,
            VisitDiagnosisRepository visitDiagnosisRepository,
            VisitStatusChangeRepository visitStatusChangeRepository,
            VisitTreatmentRepository visitTreatmentRepository) {
        this.employeeService = employeeService
        this.lookupDataService = lookupDataService
        this.patientService = patientService
        this.visitDao = visitDao
        this.visitDiagnosisRepository = visitDiagnosisRepository
        this.visitRepository = visitRepository
        this.visitStatusChangeRepository = visitStatusChangeRepository
        this.visitTreatmentRepository = visitTreatmentRepository
    }




    List<Visit> findVisitsByDateAndTherapist(LocalDate visitDate, Employee therapist) {
        List<Visit> visits = visitRepository.findByVisitDateAndTherapistId(visitDate, therapist.employeeId)
        visits.forEach({ visit ->
            populateLookupData(visit, true)
        })
        visits
    }


    List<Visit> findVisitsForPatient(Patient patient) {
        List<Visit> visits = visitRepository.findByPatientIdOrderByVisitDateAsc(patient.patientId)
        visits.forEach({ visit ->
            populateLookupData(visit, true)
            visit.patient = patient
        })
        visits
    }

    List<Visit> findVisitsForPatientOnDate(Patient patient, LocalDate localDate) {
        List<Visit> visits = visitRepository.findByPatientIdAndVisitDateOrderByVisitIdAsc(patient.patientId, localDate)
        visits.forEach({ visit ->
            populateLookupData(visit, true)
            visit.patient = patient
        })
        visits
    }


    void populateLookupData(Visit visit, boolean loadDxAndTx) {
        visit.therapist = employeeService.findById(visit.therapistId)
        visit.patient = patientService.findById(visit.patientId)
        visit.insuranceType = lookupDataService.findInsuranceTypeById(visit.insuranceTypeId)
        visit.patientType = lookupDataService.findPatientTypeById(visit.patientTypeId)
        visit.visitType = lookupDataService.findVisitTypeById(visit.visitTypeId)
        if(loadDxAndTx) {
            visit.visitDiagnoses = visitDiagnosisRepository.getByVisitId(visit.visitId)
            visit.visitTreatments = visitTreatmentRepository.getByVisitId(visit.visitId)
        }
    }

    /**
     * Returns true if the patient has visited before.
     */
    boolean patientHasVisitedBefore(Patient patient) {
        visitRepository.countByPatientId(patient.patientId) > 0
    }

    int getSavedVisitCount(Patient patient) {
        visitRepository.countByPatientId(patient.patientId)
    }


    void deleteVisit(Visit visit) {
        visitStatusChangeRepository.deleteByVisitId(visit.visitId)
        visitRepository.delete(visit)
    }





    Visit saveVisitWithStatusChange(Visit visit, VisitStatus visitStatus, Employee employee) {
        // save the visit status
        visit.visitStatus = visitStatus
        visitRepository.save(visit)

        // create the visit change record and add it to the visit
        visit.visitStatusChanges << visitStatusChangeRepository.save(
                new VisitStatusChange(
                visitStatus: visitStatus,
                visitId: visit.visitId,
                changed: LocalDateTime.now(),
                employeeId: employee.employeeId
        ))

        visit
    }

    /**
     *
     */
    Visit saveVisitReplaceDiagnosesAndTreatments(Visit visit, VisitStatus visitStatus, Employee employee) {
        saveVisitWithStatusChange(visit, visitStatus, employee)

        log.debug "saveVisitReplaceDiagnosesAndTreatments() :: after call to saveVisitWithStatusChange(), visit.visitId=${visit.visitId}"

        // diagnsoses
        visitDiagnosisRepository.deleteByVisitId(visit.visitId)
        visit.visitDiagnoses.each { vd ->
            // some VDs might not have the visitId set...
            vd.visitId = visit.visitId
            visitDiagnosisRepository.save(vd)
        }

        // treatments
        visitTreatmentRepository.deleteByVisitId(visit.visitId)
        visit.visitTreatments.each { vt -> visitTreatmentRepository.save(vt) }

        visit
    }


    /**
     * @deprecated
     */
    @Deprecated
    int getVisitNumber(List<Visit> allVisits, Visit visit) {
        int visitNumber = 1
        allVisits.eachWithIndex{ Visit entry, int i ->
            if(entry.visitId == visit.visitId) {
                visitNumber = i + 1
                return  // this breaks out of the eachWithIndex{} closure
            }
        }
        visitNumber
    }


    /**
     * Returns the numeric sequential order of this visit in relation to all visits from
     * this patient.  The visits are sorted by the visit.date.
     *
     * @deprecated
     */
    @Deprecated
    int getVisitNumber(Visit visit) {
        getVisitNumber(visitRepository.findByPatientIdOrderByVisitDateAsc(visit.patient.patientId), visit)
    }


    /**
     * Given a visit, this method returns the visit that occurred just previous to it (as sorted by visit.date).
     */
    Visit getPreviousVisit(Visit visit) {
        List<Visit> visits = visitRepository.findByPatientIdOrderByVisitDateAsc(visit.patient.patientId)
        Visit previousVisit = null
        visits.eachWithIndex { Visit entry, int index ->
            if(entry.visitId == visit.visitId && index > 0) {
                previousVisit = visits.get(index - 1)
            }
        }
        if(previousVisit) {
            // load up all the goodies for the visit if we found one
            populateLookupData(previousVisit, true)
        }
        previousVisit
    }

    /**
     * Returns true if the two given visits have the exact same set of diagnoses, or false otherwise.
     */
    boolean visitsHaveSameDiganoses(Visit visit1, Visit visit2) {
        // first do a simple size check
        if(visit1.visitDiagnoses.size() != visit2.visitDiagnoses.size()) {
            return false
        }
        boolean foundMismatch = false
        // in this loop, we'll look for a mismatch
        visit1.visitDiagnoses.each { vd ->
            if(visit2.visitDiagnoses.find { it.diagnosisId == vd.diagnosisId } == null) {
                foundMismatch = true
                return  // this breaks out of the each{} closure
            }
        }
        // see if we found a mismatch....
        !foundMismatch
    }


    /**
     * The one query to rule them all...
     *
     * This method serves the visitstatus form, which has 5 different search criteria:
     *  visitStatus, fromDate, toDate, insuranceType and therapist
     *
     *  where:
     *      - visitStatus is never null
     *      - fromDate is never null
     *      - toDate can be null (which means only search by the fromDate)
     *      - insuranceType can be null (null means search by all insurance, non-null means search by the specified insurance
     *      - therapist can be null (null means search by all therapist, non-null means search by the specified therapist
     *
     */
    List<Visit> findVisitsByStatus(VisitStatus visitStatus, LocalDate fromDate, LocalDate toDate,
                                   InsuranceType insuranceType, Employee therapist) {

        List<Visit> visits = null

        if(!visitStatus || !toDate) {
            throw new IllegalArgumentException('VisitStatus and toDate are required parameters to findVisitsByStatus()')
        }

        if(!fromDate) {
            // only search by the fromDate
            if(!insuranceType) {
                if(!therapist) {
                    // insurance type and therapist are null
                    log.info 'findByVisitStatusIdAndVisitDate()'
                    visits = visitRepository.findByVisitStatusAndVisitDate(visitStatus, toDate)
                } else {
                    // therapist is not null
                    log.info 'findByVisitStatusIdAndVisitDateAndTherapistId()'
                    visits =  visitRepository.findByVisitStatusAndVisitDateAndTherapistId(visitStatus, toDate, therapist.employeeId)
                }
            } else if(!therapist) {
                // insuranceType is not null
                log.info 'findByVisitStatusIdAndVisitDateAndInsuranceTypeId()'
                visits =  visitRepository.findByVisitStatusAndVisitDateAndInsuranceTypeId(visitStatus, toDate, insuranceType.insuranceTypeId)
            } else {
                // insuranceType and therapist are not null
                log.info 'findByVisitStatusIdAndVisitDateAndInsuranceTypeIdAndTherapistId()'
                visits =  visitRepository.findByVisitStatusAndVisitDateAndInsuranceTypeIdAndTherapistId(visitStatus, toDate, insuranceType.insuranceTypeId, therapist.employeeId)
            }
        } else {
            // search by the fromDate & toDate
            if (!insuranceType) {
                if (!therapist) {
                    // insurance type and therapist are null
                    log.info 'findByVisitStatusAndFromDateAndToDate()'
                    visits = visitRepository.findByVisitStatusAndFromDateAndToDate(visitStatus, fromDate, toDate)
                } else {
                    // therapist is not null
                    log.info 'findByVisitStatusAndFromDateAndToDateAndTherapistId()'
                    visits = visitRepository.findByVisitStatusAndFromDateAndToDateAndTherapistId(visitStatus, fromDate, toDate, therapist.employeeId)
                }
            } else if (!therapist) {
                // insuranceType is not null
                log.info 'findByVisitStatusAndFromDateAndToDateAndInsuranceTypeId()'
                visits = visitRepository.findByVisitStatusAndFromDateAndToDateAndInsuranceTypeId(visitStatus, fromDate, toDate, insuranceType.insuranceTypeId)
            } else {
                // insuranceType and therapist are not null
                log.info 'findByVisitStatusAndFromDateAndToDateAndInsuranceTypeIdAndTherapistId()'
                visits = visitRepository.findByVisitStatusAndFromDateAndToDateAndInsuranceTypeIdAndTherapistId(visitStatus, fromDate, toDate, insuranceType.insuranceTypeId, therapist.employeeId)
            }
        }

        // see if we got any results
        if(visits) {
            // yup, so load up all the useful lookup info
            visits.each{ populateLookupData(it, true) }
        }

        visits
    }


    /**
     * returns true if the given combo of patientType, insuranceType and visitType requires treatments
     */
    boolean visitRequiresTreatment(String patientTypesChoice, String insuranceTypesChoice, String visitTypesChoice) {
        if(patientTypesChoice == 'Client' || insuranceTypesChoice == 'Cash-Don\'t bill' || visitTypesChoice == 'Cancel/No Show') {
            return false
        }
        return true
    }

    /**
     * returns true if the given combo of patientType, insuranceType and visitType requires treatments
     */
    boolean visitRequiresTreatment(PatientType patientType, InsuranceType insuranceType, VisitType visitType) {
        if(patientType.patientTypeName == 'Client' || insuranceType.insuranceTypeName == 'Cash-Don\'t bill' ||
                visitType.visitTypeName == 'Cancel/No Show')
        {
            return false
        }
        return true
    }

    /**
     * returns true if the given visit is such (combo of patientType, insuranceType and visitType) requires treatments
     */
    boolean visitRequiresTreatment(Visit visit) {
        return visitRequiresTreatment(visit.patientType, visit.insuranceType, visit.visitType)
    }



    List<Diagnosis> getAllTrackedDiagnoses(Patient patient) {
        visitDao.getRecentPatientDiagnoses(patient.patientId, LocalDate.MIN).stream()
                .map({ dId -> lookupDataService.findDiagnosisById(dId) } )
                .collect(Collectors.toList())
    }

}
