package com.kinespherept.dao.repository

import com.kinespherept.model.core.EmployeeLogin
import org.springframework.data.repository.CrudRepository

interface EmployeeLoginRepository extends CrudRepository<EmployeeLogin, Long> {
}
