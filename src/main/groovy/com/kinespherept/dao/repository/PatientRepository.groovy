package com.kinespherept.dao.repository

import com.kinespherept.model.core.Patient
import org.springframework.data.repository.CrudRepository

interface PatientRepository extends CrudRepository<Patient, Long> {

}