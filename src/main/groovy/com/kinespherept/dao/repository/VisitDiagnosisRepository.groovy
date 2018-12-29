package com.kinespherept.dao.repository

import com.kinespherept.model.core.VisitDiagnosis
import org.springframework.data.repository.CrudRepository


interface VisitDiagnosisRepository extends CrudRepository<VisitDiagnosis, Long> {

    List<VisitDiagnosis> getByVisitId(Long visitId)

    void deleteByVisitId(Long visitId)
}
