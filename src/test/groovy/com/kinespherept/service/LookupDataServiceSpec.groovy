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
import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.PatientType
import com.kinespherept.model.core.Treatment
import com.kinespherept.model.core.VisitType
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


    static LookupDataService getPopulatedLookupDataService() {
        LookupDataService lds = new LookupDataService(null, null, null, null, null, null, null, null)

        lds.diagnosisTypes = [
                new DiagnosisType(diagnosisTypeId: 1, diagnosisTypeName: 'Spine: Head', displayOrder: 1),
                new DiagnosisType(diagnosisTypeId: 2, diagnosisTypeName: 'Spine: Cervical', displayOrder: 2),
                new DiagnosisType(diagnosisTypeId: 3, diagnosisTypeName: 'Spine: Thoracic', displayOrder: 3)
        ]

        lds.diagnoses = [
                new Diagnosis(diagnosisTypeId: 1, diagnosisType: lds.diagnosisTypes[0], diagnosisCode: 'R51', diagnosisName: 'Headache/facial pain', displayOrder: 1),
                new Diagnosis(diagnosisTypeId: 1, diagnosisType: lds.diagnosisTypes[0], diagnosisCode: 'G44.229', diagnosisName: 'Tension HA: chronic', displayOrder: 2),
                new Diagnosis(diagnosisTypeId: 1, diagnosisType: lds.diagnosisTypes[0], diagnosisCode: 'G44.219', diagnosisName: 'Tension HA: episodic', displayOrder: 3),

                new Diagnosis(diagnosisTypeId: 2, diagnosisType: lds.diagnosisTypes[1], diagnosisCode: 'M54.2', diagnosisName: 'Cervical pain', displayOrder: 1),
                new Diagnosis(diagnosisTypeId: 2, diagnosisType: lds.diagnosisTypes[1], diagnosisCode: 'M54.12', diagnosisName: 'Cervical radicluopathy', displayOrder: 2),
                new Diagnosis(diagnosisTypeId: 2, diagnosisType: lds.diagnosisTypes[1], diagnosisCode: 'M48.02', diagnosisName: 'Cervical stenosis', displayOrder: 3),
                new Diagnosis(diagnosisTypeId: 2, diagnosisType: lds.diagnosisTypes[1], diagnosisCode: 'M50.81', diagnosisName: 'Cervical DDD', displayOrder: 4),
                new Diagnosis(diagnosisTypeId: 2, diagnosisType: lds.diagnosisTypes[1], diagnosisCode: 'M50.820', diagnosisName: 'Other Cervical Disc Disorder C4-C5', displayOrder: 5),
                new Diagnosis(diagnosisTypeId: 2, diagnosisType: lds.diagnosisTypes[1], diagnosisCode: 'M50.822', diagnosisName: 'Other Cervical Disc C5-C6', displayOrder: 6),
                new Diagnosis(diagnosisTypeId: 2, diagnosisType: lds.diagnosisTypes[1], diagnosisCode: 'M50.823', diagnosisName: 'Other Cervical Disc C6-C7', displayOrder: 7),

                new Diagnosis(diagnosisTypeId: 3, diagnosisType: lds.diagnosisTypes[2], diagnosisCode: 'M54.6', diagnosisName: 'Thoracic pain', displayOrder: 1),
                new Diagnosis(diagnosisTypeId: 3, diagnosisType: lds.diagnosisTypes[2], diagnosisCode: 'M51.04', diagnosisName: 'Invtervertebral disc disorder w/ myelopathy thoracic region', displayOrder: 2),
                new Diagnosis(diagnosisTypeId: 3, diagnosisType: lds.diagnosisTypes[2], diagnosisCode: 'M51.05', diagnosisName: 'Invtervertebral disc disorder w/ myelopathy thoracolumbar region', displayOrder: 3),
                new Diagnosis(diagnosisTypeId: 3, diagnosisType: lds.diagnosisTypes[2], diagnosisCode: 'M51.06', diagnosisName: 'Invtervertebral disc disorders with myelopthy, lumbar region', displayOrder: 4),
                new Diagnosis(diagnosisTypeId: 3, diagnosisType: lds.diagnosisTypes[2], diagnosisCode: 'M54.14', diagnosisName: 'Thoracic radiculopathy', displayOrder: 5),
        ]

        lds.insuranceTypes = [
                new InsuranceType(insuranceTypeId: 1, insuranceTypeName: 'BCBS'),
                new InsuranceType(insuranceTypeId: 2, insuranceTypeName: 'Medicare'),
                new InsuranceType(insuranceTypeId: 3, insuranceTypeName: 'Tricare'),
                new InsuranceType(insuranceTypeId: 4, insuranceTypeName: 'UHC'),
                new InsuranceType(insuranceTypeId: 5, insuranceTypeName: 'UHC-UMR'),
                new InsuranceType(insuranceTypeId: 6, insuranceTypeName: 'Work Comp'),
                new InsuranceType(insuranceTypeId: 7, insuranceTypeName: 'Cash'),
                new InsuranceType(insuranceTypeId: 8, insuranceTypeName: 'Cash-Don\'t bill'),
                new InsuranceType(insuranceTypeId: 9, insuranceTypeName: 'Humana'),
        ]

        lds.patientTypes = [
                new PatientType(patientTypeId: 1, patientTypeName: 'Orthopedic'),
                new PatientType(patientTypeId: 2, patientTypeName: 'Scoliosis'),
                new PatientType(patientTypeId: 3, patientTypeName: 'Dancer'),
                new PatientType(patientTypeId: 4, patientTypeName: 'POTS'),
                new PatientType(patientTypeId: 5, patientTypeName: 'Neuro'),
                new PatientType(patientTypeId: 6, patientTypeName: 'Client'),
                new PatientType(patientTypeId: 7, patientTypeName: 'Oral'),
                new PatientType(patientTypeId: 8, patientTypeName: 'Hypermobility')
        ]

        lds.treatments = [
                new Treatment(treatmentId: 1, treatmentCode: '97112', treatmentName: 'Neuromuscular Re-Education', displayOrder: 1, evaluation: false),
                new Treatment(treatmentId: 2, treatmentCode: '97110', treatmentName: 'Therapeutic Exercise', displayOrder: 2, evaluation: false),
                new Treatment(treatmentId: 3, treatmentCode: '97140', treatmentName: 'Manual Therapy', displayOrder: 3, evaluation: false),
                new Treatment(treatmentId: 4, treatmentCode: '97530', treatmentName: 'Therapeutic Activities', displayOrder: 4, evaluation: false),
                new Treatment(treatmentId: 5, treatmentCode: '97116', treatmentName: 'Gait Training', displayOrder: 5, evaluation: false),
                new Treatment(treatmentId: 6, treatmentCode: '97035', treatmentName: 'Ultrasound', displayOrder: 6, evaluation: false),
                new Treatment(treatmentId: 7, treatmentCode: '97799', treatmentName: 'Dry Needling', displayOrder: 7, evaluation: false),
                new Treatment(treatmentId: 8, treatmentCode: '97161', treatmentName: 'Eval low complex', displayOrder: 8, evaluation: true),
                new Treatment(treatmentId: 9, treatmentCode: '97162', treatmentName: 'Eval mod complex', displayOrder: 9, evaluation: true),
                new Treatment(treatmentId: 10, treatmentCode: '97163', treatmentName: 'Eval high complex', displayOrder: 10, evaluation: true),
                new Treatment(treatmentId: 11, treatmentCode: '97164', treatmentName: 'Re-evaluation', displayOrder: 11, evaluation: true)
        ]

        lds.visitTypes = [
                new VisitType(visitTypeId: 1, visitTypeName: 'Initial'),
                new VisitType(visitTypeId: 2, visitTypeName: 'Follow up'),
                new VisitType(visitTypeId: 3, visitTypeName: 'Cancel/No Show')
        ]

        lds
    }

}
