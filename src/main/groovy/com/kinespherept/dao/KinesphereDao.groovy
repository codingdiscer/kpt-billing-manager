package com.kinespherept.dao

import com.kinespherept.dao.repository.EmployeeRepository
import com.kinespherept.model.core.Employee
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

@Service
@Slf4j
class KinesphereDao {

    EmployeeRepository employeeRepository

    KinesphereDao(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository
    }


    @PostConstruct
    void init() {
        log.debug "KinesphereDao.init() :: employeeRepository=${employeeRepository}"
    }

    @Deprecated
    Employee login(String username, String password) {
        employeeRepository.findByUsernameAndPassword(username, password)
    }


}
