package com.kinespherept.model.core

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import java.time.LocalDateTime

/**
 * Represents a timestamp that an employee logged in at
 */
@Canonical
@Entity
@Table(name='employee_login')
class EmployeeLogin {
    @Id @Column(name='employee_login_id') @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long employeeLoginId

    @Column(name='employee_id') Integer employeeId
    @Column(name='login_time') LocalDateTime loginTime
}

