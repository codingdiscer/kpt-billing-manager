package com.kinespherept.model.core

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Represents the type of patient for a visit (ortho | scoliosis | dancer | client | pots)
 */
@Canonical
@Entity
@Table(name='patient_type')
class PatientType {

    @Id @Column(name='patient_type_id') int patientTypeId
    @Column(name='patient_type_name') String patientTypeName
}
