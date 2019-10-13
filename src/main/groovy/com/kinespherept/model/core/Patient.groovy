package com.kinespherept.model.core

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Transient

/**
 * Represents a patient of the business
 */
@Canonical
@Entity
@Table(name='patient')
class Patient {
    @Id @Column(name='patient_id') @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long patientId

    @Column(name='first_name') String firstName
    @Column(name='last_name') String lastName
    @Column(name='patient_type_id') Integer patientTypeId
    @Column(name='insurance_type_id') Integer insuranceTypeId
    @Column(name='notes') String notes

    // these will get populated in the service layer
    @Transient PatientType patientType
    @Transient InsuranceType insuranceType
    @Transient List<Visit> visits = []


    String getDisplayableName() {
        "${firstName} ${lastName}".toString()
    }

    String getLastNameFirst() {
        "${lastName}, ${firstName}".toString()
    }



}
