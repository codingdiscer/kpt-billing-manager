package com.kinespherept.model.core

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table

/**
 * Represents the intersection of roles and employees
 */
@Entity
@Table(name='employee_roles')
class EmployeeRoles {

    @Id @Column(name='employee_roles_id') Integer employeeRolesId
    @Column(name='employee_role') @Enumerated(EnumType.STRING) EmployeeRole employeeRole
    @Column(name='employee_id') Integer employeeId

}
