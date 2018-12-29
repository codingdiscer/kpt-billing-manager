package com.kinespherept.model.core

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Transient


@Canonical
@Entity
@Table(name='diagnosis')
class Diagnosis {
    @Id @Column(name='diagnosis_id') Integer diagnosisId
    @Column(name='diagnosis_type_id') int diagnosisTypeId
    @Column(name='diagnosis_code') String diagnosisCode
    @Column(name='diagnosis_name') String diagnosisName
    @Column(name='display_order') int displayOrder

    @Transient DiagnosisType diagnosisType

    String getNameForDisplay() {
        "${diagnosisName} (${diagnosisCode})"
    }

    String getTypeForDisplay() {
        "${diagnosisType.diagnosisTypeName}"
    }

}
