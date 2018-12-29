package com.kinespherept.service

import com.google.common.collect.Lists
import com.kinespherept.dao.repository.EmployeeLoginRepository
import com.kinespherept.dao.repository.EmployeeRepository
import com.kinespherept.dao.repository.EmployeeRolesRepository
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.EmployeeLogin
import com.kinespherept.model.core.EmployeeRole
import com.kinespherept.model.core.EmployeeRoles
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import java.time.LocalDateTime
import java.util.stream.Collectors


/**
 * A service layer that wraps the various daos for the employee data
 * within the application.
 */
@Service
@Slf4j
class EmployeeService {

    // created and stored here
    List<Employee> employees = []
    List<EmployeeRoles> employeeRoles = []

    // dependent components
    EmployeeRepository employeeRepository
    EmployeeRolesRepository employeeRolesRepository
    EmployeeLoginRepository employeeLoginRepository


    EmployeeService(EmployeeRepository employeeRepository,
                    EmployeeRolesRepository employeeRolesRepository,
                    EmployeeLoginRepository employeeLoginRepository
    )
    {
        this.employeeRepository = employeeRepository
        this.employeeRolesRepository = employeeRolesRepository
        this.employeeLoginRepository = employeeLoginRepository
    }


    @PostConstruct
    void init() {
        log.debug "init() :: "
        employees = Lists.newArrayList(employeeRepository.findAll())
        employeeRoles = Lists.newArrayList(employeeRolesRepository.findAll())
        log.debug "init() :: employees=${employees}"
        log.debug "init() :: employeeRoles=${employeeRoles}"
        hookupRoles()
    }

    void hookupRoles() {
        employees.parallelStream().forEach({ e ->
            employeeRoles.stream()
                    .filter({ er -> er.employeeId == e.employeeId})
                    .forEach({
                er -> e.roles << er.employeeRole
            })
        })
    }

    List<Employee> findByRoleExplicit(EmployeeRole employeeRole) {
        employees.stream().filter({ emp -> emp.roles.contains(employeeRole) })
                .collect(Collectors.toList())
    }


    Employee findByFullname(String fullname) {
        employees.find { it.fullname == fullname }
    }

    Employee findById(Integer employeeId) {
        employees.find { it.employeeId == employeeId }
    }

    Employee findByUsername(String username) {
        employees.find { it.username == username }
    }


//      TODO - prove this is needed, or delete it
//    Employee login(String username) {
//        employeeRepository.findByUsername(username)
//    }



    EmployeeLogin trackLogin(Employee employee) {
        employeeLoginRepository.save(new EmployeeLogin(employeeId: employee.employeeId,
                loginTime: LocalDateTime.now()))
    }

}
