package com.kinespherept.service

import com.kinespherept.dao.repository.ReportMetricRepository
import com.kinespherept.dao.repository.ReportRepository
import com.kinespherept.dao.repository.VisitRepository
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.PatientType
import com.kinespherept.model.core.Report
import com.kinespherept.model.core.ReportMetric
import com.kinespherept.model.core.ReportStatus
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitType
import org.springframework.stereotype.Service

import java.time.LocalDate
import java.time.temporal.ChronoField

@Service
class ReportService {

    LookupDataService lookupDataService
    ReportMetricRepository reportMetricRepository
    ReportRepository reportRepository
    VisitService visitService

    ReportService(LookupDataService lookupDataService, ReportMetricRepository reportMetricRepository,
                  ReportRepository reportRepository, VisitService visitService)
    {
        this.lookupDataService = lookupDataService
        this.reportMetricRepository = reportMetricRepository
        this.reportRepository = reportRepository
        this.visitService = visitService
    }

    /**
     * Builds a new report from the given {@link LocalDate} object.  Only the year & month
     * are used (day is ignored).
     */
    Report generateReport(LocalDate date, boolean generateMetrics = false) {
        new Report(reportDate: setDateToFirstOfMonth(date), reportStatus: ReportStatus.REQUESTED)
    }


    protected LocalDate setDateToFirstOfMonth(LocalDate localDate) {
        localDate.minusDays(localDate.get(ChronoField.DAY_OF_MONTH) - 1 as long)
    }


    List<Report> getReportHeadersBetweenDateRange(LocalDate fromDate, LocalDate toDate) {
        []
    }

    /**
     *
     * @param reports
     * @return
     */
    List<Report> loadMetrics(List<Report> reports) {
        []
    }


    Report generateMetrics(Report report) {
        List<Visit> visits = visitService.getVisitsBetweenDates(
                report.reportDate, report.reportDate.plusMonths(1))

        // key="{patientType}^{insuranceType}^{visitType}^{therapistName}"
        Map<String, ReportMetric> metricsMap = [:]

        visits.each { Visit visit ->
            String key = getMetricKey(visit)
            if(metricsMap.containsKey(key)) {
                metricsMap.get(key).count++
            } else {
                metricsMap[key] = buildMetric(report, visit, 1)
            }
        }

        report.metricsMap = metricsMap
        report
    }

    String getMetricKey(Visit visit) {
        getMetricKey(visit.patientType, visit.insuranceType, visit.visitType, visit.therapist)
    }

    String getMetricKey(PatientType pt, InsuranceType it, VisitType vt, Employee th) {
        "${pt.patientTypeName}^${it.insuranceTypeName}^${vt.visitTypeName}^${th.username}".toString()
    }

    ReportMetric buildMetric(Report report, Visit visit, int count = 0) {
        new ReportMetric(
                reportId: report.reportId,
                patientType: visit.patientType,
                patientTypeId: visit.patientTypeId,
                insuranceType: visit.insuranceType,
                insuranceTypeId: visit.insuranceTypeId,
                visitType: visit.visitType,
                visitTypeId: visit.visitTypeId,
                therapist: visit.therapist,
                therapistId: visit.therapistId,
                count: count
        )
    }

    int getMetricCount(Report report, PatientType pt, InsuranceType it, VisitType vt, Employee th) {
        ReportMetric metric = report.metricsMap[getMetricKey(pt, it, vt, th)]
        if(metric) {
            return metric.count
        }
        0
    }

    int getMetricCount(Report report, PatientType pt, InsuranceType it, VisitType vt) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, pt, it, vt)) {
                count += metric.count
            }
        }
        count
    }

    boolean metricMatches(ReportMetric metric, PatientType pt, InsuranceType it, VisitType vt) {
        if(metric.patientTypeId == pt.patientTypeId && metric.insuranceTypeId == it.insuranceTypeId &&
                metric.visitTypeId == vt.visitTypeId)
        {
            return true
        }
        false
    }


    int getMetricCount(Report report, PatientType pt, VisitType vt) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, pt, vt)) {
                count += metric.count
            }
        }
        count
    }

    boolean metricMatches(ReportMetric metric, PatientType pt, VisitType vt) {
        if(metric.patientTypeId == pt.patientTypeId && metric.visitTypeId == vt.visitTypeId) {
            return true
        }
        false
    }


    int getMetricCount(Report report, PatientType pt) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, pt)) {
                count += metric.count
            }
        }
        count
    }

    boolean metricMatches(ReportMetric metric, PatientType pt) {
        if(metric.patientTypeId == pt.patientTypeId) {
            return true
        }
        false
    }


    int getMetricCount(Report report, InsuranceType it, VisitType vt) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, it, vt)) {
                count += metric.count
            }
        }
        count
    }

    boolean metricMatches(ReportMetric metric, InsuranceType it, VisitType vt) {
        if(metric.insuranceTypeId == it.insuranceTypeId && metric.visitTypeId == vt.visitTypeId) {
            return true
        }
        false
    }


    int getMetricCount(Report report, InsuranceType it) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, it)) {
                count += metric.count
            }
        }
        count
    }

    boolean metricMatches(ReportMetric metric, InsuranceType it) {
        if(metric.insuranceTypeId == it.insuranceTypeId) {
            return true
        }
        false
    }


    int getMetricCount(Report report, PatientType pt, VisitType vt, Employee th) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, pt, vt, th)) {
                count += metric.count
            }
        }
        count
    }

    boolean metricMatches(ReportMetric metric, PatientType pt, VisitType vt, Employee th) {
        if(metric.patientTypeId == pt.patientTypeId && metric.visitTypeId == vt.visitTypeId &&
                metric.therapistId == th.employeeId)
        {
            return true
        }
        false
    }


    int getMetricCount(Report report, PatientType pt, Employee th) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, pt, th)) {
                count += metric.count
            }
        }
        count
    }

    boolean metricMatches(ReportMetric metric, PatientType pt, Employee th) {
        if(metric.patientTypeId == pt.patientTypeId && metric.therapistId == th.employeeId) {
            return true
        }
        false
    }





    int getMetricCount(Report report, InsuranceType it, VisitType vt, Employee th) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, it, vt, th)) {
                count += metric.count
            }
        }
        count
    }

    boolean metricMatches(ReportMetric metric, InsuranceType it, VisitType vt, Employee th) {
        if(metric.insuranceTypeId == it.insuranceTypeId && metric.visitTypeId == vt.visitTypeId &&
                metric.therapistId == th.employeeId)
        {
            return true
        }
        false
    }


    int getMetricCount(Report report, InsuranceType it, Employee th) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, it, th)) {
                count += metric.count
            }
        }
        count
    }

    boolean metricMatches(ReportMetric metric, InsuranceType it, Employee th) {
        if(metric.insuranceTypeId == it.insuranceTypeId && metric.therapistId == th.employeeId) {
            return true
        }
        false
    }



}
