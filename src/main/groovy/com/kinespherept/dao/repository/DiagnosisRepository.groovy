package com.kinespherept.dao.repository

import com.kinespherept.model.core.Diagnosis
import org.springframework.data.repository.CrudRepository


interface DiagnosisRepository extends CrudRepository<Diagnosis, Integer> {

    List<Diagnosis> findAllByDiagnosisTypeId(int diagnosisTypeId)
}
