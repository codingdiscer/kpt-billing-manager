package com.kinespherept.dao.repository

import com.kinespherept.model.core.VisitStatusChange
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional

interface VisitStatusChangeRepository extends CrudRepository<VisitStatusChange, Long> {

    @Transactional
    void deleteByVisitId(Long visitId)

}
