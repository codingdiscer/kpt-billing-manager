package com.kinespherept.dao.repository

import com.kinespherept.model.core.DiagnosisType
import org.springframework.data.repository.CrudRepository

interface DiagnosisTypeRepository extends CrudRepository<DiagnosisType, Integer> {

    List<DiagnosisType> findAllByOrderByDisplayOrderAsc()

}
