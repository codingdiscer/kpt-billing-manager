package com.kinespherept.dao.repository

import com.kinespherept.model.core.VisitTreatment
import org.springframework.data.repository.CrudRepository

interface VisitTreatmentRepository extends CrudRepository<VisitTreatment, Long> {

    List<VisitTreatment> getByVisitId(Long visitId)

    void deleteByVisitId(Long visitId)

}
