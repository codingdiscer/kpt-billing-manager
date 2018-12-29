package com.kinespherept.model.core

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Represents the intersection of roles and employees
 */
@Entity
@Table(name='employee_roles')
class EmployeeRoles {

    @Id @Column(name='employee_roles_id') Integer employeeRolesId
    //@Column(name='employee_role_id') Integer employeRoleId
    @Column(name='employee_role_id') EmployeeRole employeeRole
    @Column(name='employee_id') Integer employeeId

//    // because this class is just a bridge, it doesn't need to really care about the objects it links
//    @Transient EmployeeRole employeeRole
//    @Transient Employee employee

}
