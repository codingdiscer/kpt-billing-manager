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
@Table(name='diagnosis_type')
class DiagnosisType {
    @Id @Column(name='diagnosis_type_id') Integer diagnosisTypeId
    @Column(name='diagnosis_type_name') String diagnosisTypeName
    @Column(name='display_order') int displayOrder
}
