package com.kinespherept.model.core

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name='visit_diagnosis')
@Canonical
class VisitDiagnosis {

    @Id @Column(name='visit_diagnosis_id') @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long visitDiagnosisId

    @Column(name='visit_id') Long visitId
    @Column(name='diagnosis_id') Integer diagnosisId
}
