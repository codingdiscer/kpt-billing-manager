package com.kinespherept.model.core

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Transient
import java.time.LocalDate

/**
 * Represents all the facets of a patient's visit to the clinic
 */
@Canonical(excludes='patient')
@Entity
@Table(name='visit')
class Visit {
    @Id @Column(name='visit_id') @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long visitId

    @Column(name='visit_date') LocalDate visitDate
    @Column(name='patient_id') Long patientId
    @Column(name='patient_type_id') Integer patientTypeId
    @Column(name='insurance_type_id') Integer insuranceTypeId
    @Column(name='therapist_id') Integer therapistId
    @Column(name='visit_type_id') Integer visitTypeId
    @Column(name='visit_status') @Enumerated(EnumType.STRING) VisitStatus visitStatus
    @Column(name='notes') String notes
    @Column(name='visit_number') int visitNumber

    @Transient Patient patient
    @Transient PatientType patientType
    @Transient InsuranceType insuranceType
    @Transient VisitType visitType
    @Transient Employee therapist
    @Transient List<VisitDiagnosis> visitDiagnoses = []
    @Transient List<VisitTreatment> visitTreatments = []
    @Transient List<VisitStatusChange> visitStatusChanges = []
    @Transient Visit previousVisit
    @Transient boolean sameDiagnosisAsPrevious = false
}
