package com.kinespherept.model.core

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Canonical
@Entity
@Table(name='visit_treatment')
class VisitTreatment {

    @Id @Column(name='visit_treatment_id') @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long visitTreatmentId

    @Column(name='visit_id') Long visitId
    @Column(name='treatment_id') Integer treatmentId
    @Column(name='treatment_quantity') Integer treatmentQuantity

}
