package com.kinespherept.service

import com.kinespherept.dao.repository.PatientRepository
import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.Patient
import com.kinespherept.model.core.PatientType
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

class PatientServiceSpec extends Specification {

    // the class under test
    PatientService patientService

    // mocks that make the world go 'round
    PatientRepository patientRepository
    LookupDataService lookupDataService

    void setup() {
        patientRepository = Mock()
        lookupDataService = Mock()
        patientService = new PatientService(patientRepository, lookupDataService)
    }


    @Unroll
    def 'test searchPatients(#search)'() {
        given:
        patientService.patients = getSearchablePatientList()

        expect:
        List<Patient> filterPatients = patientService.searchPatients(search)
        println "filterPatients.ids=${filterPatients.stream().map({ p -> p.patientId}).collect(Collectors.toList())}"
        filterPatients.stream().map({ p -> p.patientId}).collect(Collectors.toList()) == idList

        where:
        search  | idList
        ''      | []
        'a'     | [1, 7, 8, 9, 10, 14]
        'b'     | [2, 7, 8, 9, 10, 3, 11, 12]
        'c'     | [8, 9, 10, 11, 12, 4, 13, 14]
        'd'     | [9, 10, 12, 13, 5]
        'e'     | [10, 13, 6, 14]
        'ab'    | [7, 8, 9, 10]
        'cd'    | [9, 10, 12, 13]
        'f'     | [1, 2, 7, 8, 9, 10, 14]
        'h'     | [8, 9, 10, 11, 12, 4, 13, 14]
        'hf'    | [14]
    }


    List<Patient> getSearchablePatientList() {
        [
                new Patient(patientId: 1, firstName: 'a', lastName: 'f'),
                new Patient(patientId: 2, firstName: 'b', lastName: 'f'),
                new Patient(patientId: 3, firstName: 'b', lastName: 'g'),
                new Patient(patientId: 4, firstName: 'c', lastName: 'h'),
                new Patient(patientId: 5, firstName: 'd', lastName: 'i'),
                new Patient(patientId: 6, firstName: 'e', lastName: 'j'),
                new Patient(patientId: 7, firstName: 'ab', lastName: 'fg'),
                new Patient(patientId: 8, firstName: 'abc', lastName: 'fgh'),
                new Patient(patientId: 9, firstName: 'abcd', lastName: 'fghi'),
                new Patient(patientId: 10, firstName: 'abcde', lastName: 'fghij'),
                new Patient(patientId: 11, firstName: 'bc', lastName: 'gh'),
                new Patient(patientId: 12, firstName: 'bcd', lastName: 'ghi'),
                new Patient(patientId: 13, firstName: 'cde', lastName: 'hij'),
                new Patient(patientId: 14, firstName: 'eca', lastName: 'jhf')
        ]
    }


    def 'test hookupLookupData()'() {
        given:
        patientService.patients = getSimplePatientList()
        List<InsuranceType> insuranceTypes = [
                new InsuranceType(1),
                new InsuranceType(2),
                new InsuranceType(3)
        ]
        List<PatientType> patientTypes = [
                new PatientType(1),
                new PatientType(2),
                new PatientType(3),
        ]

        when:
        patientService.hookupLookupData()

        then:
        2 * lookupDataService.getInsuranceTypes() >> insuranceTypes
        2 * lookupDataService.getPatientTypes() >> patientTypes
        patientService.patients[0].insuranceType.insuranceTypeId == 2
        patientService.patients[0].patientType.patientTypeId == 1
        patientService.patients[1].insuranceType.insuranceTypeId == 3
        patientService.patients[1].patientType.patientTypeId == 2
        patientService.patients[2].insuranceType == null
        patientService.patients[2].patientType == null
    }



    List<Patient> getSimplePatientList() {
        [
                new Patient(patientId: 1, firstName: 'aaa', lastName: 'bbb', patientTypeId: 1, insuranceTypeId: 2),
                new Patient(patientId: 2, firstName: 'ccc', lastName: 'ddd', patientTypeId: 2, insuranceTypeId: 3),
                new Patient(patientId: 3, firstName: 'eee', lastName: 'fff')
        ]
    }


}
