package com.kinespherept.service

import com.kinespherept.dao.repository.EmployeeLoginRepository
import com.kinespherept.dao.repository.EmployeeRepository
import com.kinespherept.dao.repository.EmployeeRolesRepository
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.EmployeeRole
import spock.lang.Specification
import spock.lang.Unroll

class EmployeeServiceSpec extends Specification {

    // the class under test
    EmployeeService employeeService

    // dependent objects
    EmployeeRepository employeeRepository
    EmployeeLoginRepository employeeLoginRepository
    EmployeeRolesRepository employeeRolesRepository



    void setup() {
        employeeRepository = Mock()
        employeeLoginRepository = Mock()
        employeeRolesRepository = Mock()
        employeeService = new EmployeeService(
            employeeRepository, employeeRolesRepository, employeeLoginRepository
        )
    }


    @Unroll
    def 'test findByRoleExplicit(#role)'() {
        expect:
        employeeService.employees.addAll([
                new Employee(employeeId: 1, roles: [ EmployeeRole.ADMINISTRATOR, EmployeeRole.INSURANCE_BILLER]),
                new Employee(employeeId: 2, roles: [ EmployeeRole.SCHEDULER, EmployeeRole.THERAPIST]),
                new Employee(employeeId: 3, roles: [ EmployeeRole.ADMINISTRATOR, EmployeeRole.SUPER_ADMIN]),
                new Employee(employeeId: 4, roles: [ EmployeeRole.SUPER_ADMIN, EmployeeRole.THERAPIST])
        ])
        List<Employee> employees = employeeService.findByRoleExplicit(role)
        employees.collect { it.employeeId } == employeeIds

        where:
        employeeIds     | role
        [3, 4]          | EmployeeRole.SUPER_ADMIN
        [1, 3]          | EmployeeRole.ADMINISTRATOR
        [2, 4]          | EmployeeRole.THERAPIST
        [2]             | EmployeeRole.SCHEDULER
        [1]             | EmployeeRole.INSURANCE_BILLER
    }


    static EmployeeService getPopulatedEmployeeService() {
        EmployeeService es = new EmployeeService(null, null, null)
        es.employees = [
                new Employee(employeeId: 1, username: 'ndowma', fullname: 'Noelle Dowma'),
                new Employee(employeeId: 2, username: 'aherrman', fullname: 'Ashley Herrman'),
                new Employee(employeeId: 3, username: 'sgalloway', fullname: 'Stacy Galloway'),
                new Employee(employeeId: 4, username: 'scurtis', fullname: 'Stacey Curtis-Yeager'),
                new Employee(employeeId: 5, username: 'tneff', fullname: 'Tamara Neff'),
                new Employee(employeeId: 6, username: 'brisenhoover', fullname: 'Bree Risenhoover') ]
        es
    }
}
