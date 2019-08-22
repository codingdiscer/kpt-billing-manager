package com.kinespherept.model.core

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

@Entity
@Table(name='report')
class Report {

    @Id @Column(name='report_id') @GeneratedValue(strategy=GenerationType.IDENTITY)
    Integer reportId

    @Column(name='report_date') LocalDate reportDate
    @Column(name='report_status') @Enumerated(EnumType.STRING) ReportStatus reportStatus

    @Transient Map<String, ReportMetric> metricsMap = [:]
}
