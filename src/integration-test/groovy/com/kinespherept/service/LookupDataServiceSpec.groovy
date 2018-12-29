package com.kinespherept.service

import com.kinespherept.BaseKinesphereIntegrationSpec
import com.kinespherept.config.KinesphereDaoConfig
import com.kinespherept.dao.repository.DiagnosisRepository
import com.kinespherept.dao.repository.InsuranceTypeRepository
import com.kinespherept.dao.repository.PatientTypeRepository
import com.kinespherept.dao.repository.TreatmentRepository
import com.kinespherept.dao.repository.VisitTypeRepository
import com.kinespherept.model.core.Diagnosis
import com.kinespherept.model.core.VisitType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer, classes =
        [KinesphereDaoConfig, DiagnosisRepository, InsuranceTypeRepository, PatientTypeRepository,
        TreatmentRepository, VisitTypeRepository, LookupDataService])
class LookupDataServiceSpec extends BaseKinesphereIntegrationSpec {



    // the class under test
    @Autowired LookupDataService service

    // other useful things to have on-hand
    @Autowired DiagnosisRepository diagnosisRepository

    // clears the relevant tables before each run
    def setup() {
        println "LookupDataServiceSpec.setup() :: yo"
        truncateTable('diagnosis')
        truncateTable('insurance_type')
        truncateTable('patient_type')
        truncateTable('treatment')
        truncateTable('visit_type')

//        diagnosisRepository.save(new Diagnosis(diagnosisId: 1, diagnosisCode: 'R51',
//                diagnosisName: 'headache/facial pain', diagnosisType: 'Spine: Head', diagnosisTypeOrder: 1, displayOrder: 1))


    }

    // TODO : remove this when it becomes useful, or if init() gains some more functionality
    def 'test init()'() {
        when:
        List<Diagnosis> d = service.diagnoses
        println "diagnoses=${d}"

        then:
        d != null
    }

    def 'test addVisitType()'() {
        given:
        VisitType newVisitType1 = new VisitType(visitTypeName: 'a')
        VisitType newVisitType2 = new VisitType(visitTypeName: 'b')

        when:
        service.addVisitType(newVisitType1)
        service.addVisitType(newVisitType2)

        then:
        service.visitTypes.size() == 2
        service.visitTypes[0].visitTypeName == 'a'
        service.visitTypes[1].visitTypeName == 'b'
    }


//    @Configuration
//    static class LookupDataServiceSpecConfig {
//
//
//        @Bean LookupDataService lookupDataService() {
//            //log.info "lookupDataService(), env=${env}"
//            //log.info "lookupDataService()"
//            println "lookupDataService()"
//
//            new LookupDataService()
//
//        }
//
//    }

}
