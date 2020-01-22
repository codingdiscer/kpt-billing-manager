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
@Table(name='report_metric')
class ReportMetric {

    @Id @Column(name='report_metric_id') @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long reportMetricId

    @Column(name='report_id') Integer reportId

    @Column(name='patient_type_id') Integer patientTypeId
    @Column(name='insurance_type_id') Integer insuranceTypeId
    @Column(name='therapist_id') Integer therapistId
    @Column(name='visit_type_id') Integer visitTypeId
    @Column(name='count') int count = 0
    @Column(name='treatment_count') int treatmentCount = 0

    @Transient PatientType patientType
    @Transient InsuranceType insuranceType
    @Transient VisitType visitType
    @Transient Employee therapist
}
