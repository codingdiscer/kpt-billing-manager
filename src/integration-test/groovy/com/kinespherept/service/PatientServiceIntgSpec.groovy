package com.kinespherept.service

import com.kinespherept.BaseKinesphereIntegrationSpec
import com.kinespherept.config.KinesphereDaoConfig
import com.kinespherept.dao.repository.DiagnosisRepository
import com.kinespherept.dao.repository.InsuranceTypeRepository
import com.kinespherept.dao.repository.PatientTypeRepository
import com.kinespherept.dao.repository.TreatmentRepository
import com.kinespherept.dao.repository.VisitTypeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer, classes =
        [KinesphereDaoConfig, DiagnosisRepository, InsuranceTypeRepository, PatientTypeRepository,
                TreatmentRepository, VisitTypeRepository, LookupDataService])
class PatientServiceIntgSpec extends BaseKinesphereIntegrationSpec {

    // the class under test
    @Autowired PatientService patientService



}
