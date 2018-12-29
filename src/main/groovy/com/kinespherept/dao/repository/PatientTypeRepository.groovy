package com.kinespherept.dao.repository

import com.kinespherept.model.core.PatientType
import org.springframework.data.repository.CrudRepository


interface PatientTypeRepository extends CrudRepository<PatientType, Integer> {

}
