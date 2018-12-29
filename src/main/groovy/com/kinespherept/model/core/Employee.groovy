package com.kinespherept.model.core

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Transient

@Canonical
@Entity
@Table(name='employee')
class Employee {

    @Id @Column(name='employee_id') Integer employeeId
    @Column String username
    @Column String fullname

    @Transient List<EmployeeRole> roles = []

}
