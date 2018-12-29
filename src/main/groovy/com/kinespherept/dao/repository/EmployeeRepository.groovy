package com.kinespherept.dao.repository

import com.kinespherept.model.core.Employee
import org.springframework.data.repository.CrudRepository

interface EmployeeRepository extends CrudRepository<Employee, Integer> {

    //Employee findByUsername(String username)

}
