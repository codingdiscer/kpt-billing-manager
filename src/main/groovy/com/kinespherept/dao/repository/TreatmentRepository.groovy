package com.kinespherept.dao.repository

import com.kinespherept.model.core.Treatment
import org.springframework.data.repository.CrudRepository


interface TreatmentRepository extends CrudRepository<Treatment, Integer> {

    List<Treatment> findAllByOrderByDisplayOrderAsc()

}
