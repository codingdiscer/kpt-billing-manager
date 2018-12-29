package com.kinespherept.service

import com.kinespherept.dao.LookupDao
import com.kinespherept.dao.VisitDao
import com.kinespherept.dao.repository.DiagnosisRepository
import com.kinespherept.dao.repository.DiagnosisTypeRepository
import com.kinespherept.dao.repository.InsuranceTypeRepository
import com.kinespherept.dao.repository.PatientTypeRepository
import com.kinespherept.dao.repository.TreatmentRepository
import com.kinespherept.dao.repository.VisitTypeRepository
import com.kinespherept.model.core.Diagnosis
import com.kinespherept.model.core.DiagnosisType
import com.kinespherept.model.core.PatientType
import spock.lang.Specification

class LookupDataServiceSpec extends Specification {

    // the class under test
    LookupDataService lookupDataService

    // supporting components
    DiagnosisRepository diagnosisRepository
    DiagnosisTypeRepository diagnosisTypeRepository
    InsuranceTypeRepository insuranceTypeRepository
    PatientTypeRepository patientTypeRepository
    TreatmentRepository treatmentRepository
    VisitTypeRepository visitTypeRepository
    LookupDao lookupDao
    VisitDao visitDao

    void setup() {
        diagnosisRepository = Mock()
        diagnosisTypeRepository = Mock()
        insuranceTypeRepository = Mock()
        patientTypeRepository = Mock()
        treatmentRepository = Mock()
        visitTypeRepository = Mock()
        lookupDao = Mock()
        visitDao = Mock()

        lookupDataService = new LookupDataService(diagnosisRepository,
                diagnosisTypeRepository,
                insuranceTypeRepository, patientTypeRepository,
                treatmentRepository, visitTypeRepository,
                lookupDao, visitDao
        )
    }


    def 'test searchDiagnoses()'() {
        given:
        List<DiagnosisType> diagnosisTypeList = [
                new DiagnosisType(diagnosisTypeId: 1, diagnosisTypeName: 'typeA', displayOrder: 1),
                new DiagnosisType(diagnosisTypeId: 2, diagnosisTypeName: 'typeB', displayOrder: 2),
                new DiagnosisType(diagnosisTypeId: 3, diagnosisTypeName: 'typeC', displayOrder: 3),
                new DiagnosisType(diagnosisTypeId: 4, diagnosisTypeName: 'typeD', displayOrder: 4),
        ]

        List<Diagnosis> diagnosisList = [
                new Diagnosis(diagnosisId: 1, diagnosisTypeId: 1, diagnosisName: 'nameA', displayOrder: 1, diagnosisCode: 'codeA', diagnosisType: diagnosisTypeList[0]),
                new Diagnosis(diagnosisId: 2, diagnosisTypeId: 1, diagnosisName: 'nameB', displayOrder: 2, diagnosisCode: 'codeB', diagnosisType: diagnosisTypeList[0]),

                new Diagnosis(diagnosisId: 3, diagnosisTypeId: 2, diagnosisName: 'nameC', displayOrder: 1, diagnosisCode: 'codeC', diagnosisType: diagnosisTypeList[1]),
                new Diagnosis(diagnosisId: 4, diagnosisTypeId: 2, diagnosisName: 'nameD', displayOrder: 2, diagnosisCode: 'codeD', diagnosisType: diagnosisTypeList[1]),

                new Diagnosis(diagnosisId: 5, diagnosisTypeId: 4, diagnosisName: 'nameE', displayOrder: 2, diagnosisCode: 'codeE', diagnosisType: diagnosisTypeList[3]),
                new Diagnosis(diagnosisId: 6, diagnosisTypeId: 4, diagnosisName: 'nameF', displayOrder: 1, diagnosisCode: 'codeF', diagnosisType: diagnosisTypeList[3]),

                new Diagnosis(diagnosisId: 7, diagnosisTypeId: 3, diagnosisName: 'nameG', displayOrder: 2, diagnosisCode: 'codeG', diagnosisType: diagnosisTypeList[2]),
                new Diagnosis(diagnosisId: 8, diagnosisTypeId: 3, diagnosisName: 'nameH', displayOrder: 1, diagnosisCode: 'codeH', diagnosisType: diagnosisTypeList[2]),
                new Diagnosis(diagnosisId: 9, diagnosisTypeId: 3, diagnosisName: 'nameI', displayOrder: 3, diagnosisCode: 'codeI', diagnosisType: diagnosisTypeList[2]),
        ]


        lookupDataService.diagnosisTypes = diagnosisTypeList
        lookupDataService.diagnoses = diagnosisList

        when:
        List<Diagnosis> outList = lookupDataService.searchDiagnoses('type')

        then:
        outList.size() == 9
        outList[0].diagnosisId == 1
        outList[1].diagnosisId == 2
        outList[2].diagnosisId == 3
        outList[3].diagnosisId == 4
        outList[4].diagnosisId == 8
        outList[5].diagnosisId == 7
        outList[6].diagnosisId == 9
        outList[7].diagnosisId == 6
        outList[8].diagnosisId == 5
    }



    def 'test findPatientTypeByName()'() {
        given:
        PatientType pt1 = new PatientType(patientTypeId: 1, patientTypeName: 'a')
        PatientType pt2 = new PatientType(patientTypeId: 2, patientTypeName: 'b')
        lookupDataService.patientTypes.addAll([pt1, pt2])


        expect:
        PatientType result = lookupDataService.findPatientTypeByName(name)
        if(result != null) {
            assert result.patientTypeId == expectedId
        }

        where:
        name | expectedId
        'a'  | 1
        'b'  | 2
        'c'  | null
        null | null
    }



}
