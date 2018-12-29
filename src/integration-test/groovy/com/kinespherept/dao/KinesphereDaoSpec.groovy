package com.kinespherept.dao

import com.kinespherept.BaseKinesphereIntegrationSpec
import com.kinespherept.config.KinesphereDaoConfig
import com.kinespherept.dao.repository.EmployeeRepository
import com.kinespherept.model.core.Employee
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer, classes =
        [KinesphereDaoConfig, EmployeeRepository, KinesphereDao])
class KinesphereDaoSpec extends BaseKinesphereIntegrationSpec {

    // the class under test
    @Autowired KinesphereDao kinesphereDao

    def 'test login() with invalid creds'() {
        when:
        Employee e = kinesphereDao.login('', '')
        println "employee=${e}"

        then:
        e == null
    }


}
