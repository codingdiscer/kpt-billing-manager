package com.kinespherept.session

import com.kinespherept.model.core.Employee
import org.springframework.stereotype.Component

import java.time.LocalDateTime

/**
 * Holds details related to an employee being logged into the application
 */
@Component
class EmployeeSession {

    Employee employee
    LocalDateTime loggedInTime
    LocalDateTime lastAction

}
