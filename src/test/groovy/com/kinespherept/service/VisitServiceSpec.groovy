package com.kinespherept.service

import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.Patient
import com.kinespherept.model.core.PatientType
import com.kinespherept.model.core.Visit
import com.kinespherept.dao.VisitDao
import com.kinespherept.dao.repository.VisitDiagnosisRepository
import com.kinespherept.dao.repository.VisitRepository
import com.kinespherept.dao.repository.VisitStatusChangeRepository
import com.kinespherept.dao.repository.VisitTreatmentRepository
import com.kinespherept.model.core.VisitType
import spock.lang.Specification

import java.time.LocalDate

class VisitServiceSpec extends Specification {

    // the class under test
    VisitService service

    // supporting objects
    EmployeeService employeeService
    LookupDataService lookupDataService
    PatientService patientService
    VisitDao visitDao
    VisitDiagnosisRepository visitDiagnosisRepository
    VisitRepository visitRepository
    VisitStatusChangeRepository visitStatusChangeRepository
    VisitTreatmentRepository visitTreatmentRepository


    void setup() {
        employeeService = Mock()
        lookupDataService = Mock()
        patientService = Mock()
        visitDao = Mock()
        visitDiagnosisRepository = Mock()
        visitRepository = Mock()
        visitStatusChangeRepository = Mock()
        visitTreatmentRepository = Mock()

        service = Spy(constructorArgs: [
                employeeService,
                lookupDataService,
                patientService,
                visitDao,
                visitDiagnosisRepository,
                visitRepository,
                visitStatusChangeRepository,
                visitTreatmentRepository
        ])
    }


    void 'test visitGetsVisitNumber()'() {
        expect:
        service.visitGetsVisitNumber(visit) == flag

        where:
        flag  | visit
        true  | new Visit(visitType: INITIAL, patientType: DANCER, insuranceType: BCBS)
        false | new Visit(visitType: CANCEL, patientType: DANCER, insuranceType: BCBS)
        false | new Visit(visitType: INITIAL, patientType: CLIENT, insuranceType: BCBS)
        false | new Visit(visitType: INITIAL, patientType: DANCER, insuranceType: CASH)
        false | new Visit(visitType: INITIAL, patientType: DANCER, insuranceType: CASH_NO_BILL)
    }



    void 'test setVisitNumbers() with all valid visits and no duplicate dates'() {
        given:
        List<Visit> patientVisits = [
                new Visit(visitId: 1, patient: PATIENT, visitType: INITIAL, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(10)),
                new Visit(visitId: 2, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(7)),
                new Visit(visitId: 3, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(4)),
                new Visit(visitId: 4, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(1))
        ]

        when:
        service.setVisitNumbers(patientVisits[1])

        then:
        1 * visitRepository.findByPatientIdOrderByVisitDateAsc(1) >> patientVisits
        1 * visitRepository.saveAll( { List<Visit> visits ->
            visits.size() == 4 && visits[0].visitId == 1  && visits[0].visitNumber == 1&&
                    visits[1].visitId == 2 && visits[1].visitNumber == 2 &&
                    visits[2].visitId == 3 && visits[2].visitNumber == 3 &&
                    visits[3].visitId == 4 && visits[3].visitNumber == 4
        })
    }

    void 'test setVisitNumbers() with some non-numbered visits and no duplicate dates'() {
        given:
        List<Visit> patientVisits = [
                new Visit(visitId: 1, patient: PATIENT, visitType: INITIAL, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(10)),
                new Visit(visitId: 2, patient: PATIENT, visitType: CANCEL, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(7)),
                new Visit(visitId: 3, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(4)),
                new Visit(visitId: 4, patient: PATIENT, visitType: CANCEL, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(1))
        ]

        when:
        service.setVisitNumbers(patientVisits[1])

        then:
        1 * visitRepository.findByPatientIdOrderByVisitDateAsc(1) >> patientVisits
        1 * visitRepository.saveAll( { List<Visit> visits ->
            visits.size() == 2 && visits[0].visitId == 1  && visits[0].visitNumber == 1&&
                    visits[1].visitId == 3 && visits[1].visitNumber == 2
        })
    }

    void 'test setVisitNumbers() with all numbered visits and some duplicate dates'() {
        given:
        List<Visit> patientVisits = [
                new Visit(visitId: 1, patient: PATIENT, visitType: INITIAL, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(10)),
                new Visit(visitId: 2, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(7)),
                new Visit(visitId: 3, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(4)),
                new Visit(visitId: 4, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(7)),
                new Visit(visitId: 5, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(4)),
                new Visit(visitId: 6, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(1))
        ]

        when:
        service.setVisitNumbers(patientVisits[1])

        then:
        1 * visitRepository.findByPatientIdOrderByVisitDateAsc(1) >> patientVisits
        1 * visitRepository.saveAll( { List<Visit> visits ->
            visits.size() == 6 && visits[0].visitId == 1 && visits[0].visitNumber == 1 &&
                    visits[1].visitId == 2 && visits[1].visitNumber == 2 &&
                    visits[2].visitId == 4 && visits[2].visitNumber == 2 &&
                    visits[3].visitId == 3 && visits[3].visitNumber == 3 &&
                    visits[4].visitId == 5 && visits[4].visitNumber == 3 &&
                    visits[5].visitId == 6 && visits[5].visitNumber == 4
        })
    }

    void 'test setVisitNumbers() with all numbered visits and mostly preset visit numbers'() {
        given:
        List<Visit> patientVisits = [
                new Visit(visitId: 1, patient: PATIENT, visitType: INITIAL, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(10), visitNumber: 1),
                new Visit(visitId: 2, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(8), visitNumber: 2),
                new Visit(visitId: 3, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(6), visitNumber: 3),
                new Visit(visitId: 4, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(5), visitNumber: 4),
                new Visit(visitId: 5, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(3), visitNumber: 5),
                new Visit(visitId: 6, patient: PATIENT, visitType: FOLLOW_UP, patientType: DANCER, insuranceType: BCBS,
                        visitDate: LocalDate.now().minusDays(1), visitNumber: 0)
        ]

        when:
        service.setVisitNumbers(patientVisits[5])

        then:
        1 * visitRepository.findByPatientIdOrderByVisitDateAsc(1) >> patientVisits
        1 * visitRepository.saveAll( { List<Visit> visits ->
            visits.size() == 1 && visits[0].visitId == 6 && visits[0].visitNumber == 6
        })
    }


    //
    // test data
    //

    static VisitType INITIAL = new VisitType(visitTypeName: 'Initial')
    static VisitType FOLLOW_UP = new VisitType(visitTypeName: 'Follow up')
    static VisitType CANCEL = new VisitType(visitTypeName: 'Cancel/No Show')

    static PatientType DANCER = new PatientType(patientTypeName: 'Dancer')
    static PatientType CLIENT = new PatientType(patientTypeName: 'Client')

    static InsuranceType BCBS = new InsuranceType(insuranceTypeName: 'BCBS')
    static InsuranceType CASH = new InsuranceType(insuranceTypeName: 'Cash')
    static InsuranceType CASH_NO_BILL = new InsuranceType(insuranceTypeName: 'Cash-Don\'t bill')

    static Patient PATIENT = new Patient(patientId: 1)


}
