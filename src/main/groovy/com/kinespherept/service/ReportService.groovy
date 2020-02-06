package com.kinespherept.service

import com.kinespherept.dao.repository.ReportMetricRepository
import com.kinespherept.dao.repository.ReportRepository
import com.kinespherept.enums.ReportType
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.PatientType
import com.kinespherept.model.core.Report
import com.kinespherept.model.core.ReportMetric
import com.kinespherept.model.core.ReportStatus
import com.kinespherept.model.core.Treatment
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitType
import com.kinespherept.model.report.CountAndPercentCell
import com.kinespherept.model.report.CountAndPercentReport
import com.kinespherept.model.report.CountAndPercentReportRow
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.transaction.Transactional
import java.time.LocalDate
import java.time.temporal.ChronoField

@Service
@Slf4j
@Transactional
class ReportService {

    /**
     * This is a bit of a hack unfortunately.  The 'Cancel / No Show' visit type becomes
     * important because of how metrics are reported.  The customer wants to see the
     * various metrics, excluding visits that are flagged as 'Cancel or No Show' (which makes sense).
     * However, this exclusion means that when counting visits of a certain type -
     * like all visits for insurance_type = 'cash', we need to also NOT count the visits
     * that have a visit_status = 'cancel/no show'.
     */
    static String VISIT_TYPE_CANCEL_NO_SHOW_NOT_FOUND = 'Unable to find VisitType "cancel/no show" within the ReportService - this is a critical error.'
    static String INSURANCE_TYPE_UHC_NOT_FOUND = 'Unable to find InsuranceType "UHC" within the ReportService - this is a critical error.'
    static String TREATMENT_DRY_NEEDLING_NOT_FOUND = 'Unable to find Treatment "Dry Needling 3 or more" within the ReportService - this is a critical error.'


    VisitType initialVisitType
    VisitType followUpVisitType
    VisitType cancelNoShowVisitType
    InsuranceType uhcInsuranceType
    InsuranceType uhcUmrInsuranceType
    Treatment dryNeedling3OrMore


    EmployeeService employeeService
    LookupDataService lookupDataService
    ReportMetricRepository reportMetricRepository
    ReportRepository reportRepository
    VisitService visitService

    ReportService(EmployeeService employeeService, LookupDataService lookupDataService,
                  ReportMetricRepository reportMetricRepository,
                  ReportRepository reportRepository, VisitService visitService)
    {
        this.employeeService = employeeService
        this.lookupDataService = lookupDataService
        this.reportMetricRepository = reportMetricRepository
        this.reportRepository = reportRepository
        this.visitService = visitService
    }

    /**
     * Extracts the 'cancel/no show' visit type once so we have it around when we need
     * to perform checks for "find metrics of this flavor, excluding cancel/no shows"
     */
    @PostConstruct
    void init() {
        initialVisitType = lookupDataService.findVisitTypeByName(VisitType.INITIAL)
        followUpVisitType = lookupDataService.findVisitTypeByName(VisitType.FOLLOW_UP)
        cancelNoShowVisitType = lookupDataService.findVisitTypeByName(VisitType.CANCEL_NO_SHOW)
        if(!cancelNoShowVisitType) {
            log.error(VISIT_TYPE_CANCEL_NO_SHOW_NOT_FOUND)
            throw new IllegalStateException(VISIT_TYPE_CANCEL_NO_SHOW_NOT_FOUND)
        }

        uhcInsuranceType = lookupDataService.findInsuranceTypeByName(InsuranceType.UNITED_HEALTHCARE)
        uhcUmrInsuranceType = lookupDataService.findInsuranceTypeByName(InsuranceType.UNITED_HEALTHCARE_UMR)
        if(!uhcInsuranceType || !uhcUmrInsuranceType) {
            log.error(INSURANCE_TYPE_UHC_NOT_FOUND)
            throw new IllegalStateException(INSURANCE_TYPE_UHC_NOT_FOUND)
        }

        dryNeedling3OrMore = lookupDataService.findTreatmentByTreatmentCode(Treatment.TREATMENT_CODE_DRY_NEEDLING_3_OR_MORE)
        if(!dryNeedling3OrMore) {
            log.error(TREATMENT_DRY_NEEDLING_NOT_FOUND)
            throw new IllegalStateException(TREATMENT_DRY_NEEDLING_NOT_FOUND)
        }
    }


    /**
     * Builds a new report from the given {@link LocalDate} object.  Only the year & month
     * are used (day is ignored).
     */
    Report buildNewReport(LocalDate date) {
        new Report(reportDate: setDateToFirstOfMonth(date), reportStatus: ReportStatus.NEW)
    }


    LocalDate setDateToFirstOfMonth(LocalDate localDate) {
        localDate.minusDays(localDate.get(ChronoField.DAY_OF_MONTH) - 1 as long)
    }

    /**
     * Returns a list of Reports that range in order from the given fromDate to the given toDate.
     * A query is made to the db to inclusively retrieve the reports from within this range.
     * If no report is found for any months within the range, those reports are immediately created, saved,
     * and returned in the proper sorted order (from earliest to latest).
     */
    List<Report> getReportHeadersBetweenDateRange(LocalDate fromDate, LocalDate toDate) {
        // normalize..
        LocalDate normalizedFromDate = setDateToFirstOfMonth(fromDate)
        LocalDate normalizedToDate = setDateToFirstOfMonth(toDate)

        // pull from the db first
        List<Report> dbReports = reportRepository.findByReportDateRange(normalizedFromDate, normalizedToDate)

        log.debug "getReportHeadersBetweenDateRange() : found [${dbReports.size()}] current reports in date range from [${normalizedFromDate}] to [${normalizedToDate}]"

        List<Report> outReports = []

        // loop over the date range
        for(LocalDate date = normalizedFromDate; date.isBefore(normalizedToDate.plusMonths(1)); date = date.plusMonths(1)) {
            // pull an existing report...
            Report report = dbReports.find { it.reportDate.isEqual(date) }

            if(!report) {
                // nope!  so create it and save it
                report = reportRepository.save(buildNewReport(date))
                log.debug "getReportHeadersBetweenDateRange() : just saved report with date [${date}], report.id=${report.reportId}"
            }

            outReports << report
        }

        outReports
    }

    /**
     *
     */
    List<Report> loadMetrics(List<Report> reports) {

        reports.each { Report report ->

            reportMetricRepository.findByReportId(report.reportId).each { ReportMetric rm ->
                report.metricsMap.put(getMetricKey(rm), rm)
            }

            log.info "just loaded ${report.metricsMap.size()} metrics into report with date ${report.reportDate}"
        }

        reports
    }

    /**
     * does the following:
     * - set status=IN_PROGRESS (& save)
     * - delete existing metrics
     * - generate new metrics (& save)
     * - set status=COMPLETE; generatedDate=Now (& save)
     */
    Report generateAndSaveMetrics(Report report) {
        // track that we are generating the report
        report.reportStatus = ReportStatus.IN_PROGRESS
        report = reportRepository.save(report)
        reportMetricRepository.deleteByReportId(report.reportId)


        report = generateMetrics(report)
        reportMetricRepository.saveAll(report.metricsMap.values().toList())

        report.reportStatus = ReportStatus.COMPLETE
        report.generatedDate = LocalDate.now()
        reportRepository.save(report)
    }


    Report generateMetrics(Report report) {
        List<Visit> visits = visitService.getVisitsBetweenDates(
                report.reportDate, report.reportDate.plusMonths(1))

        // key="{patientType}^{insuranceType}^{visitType}^{therapistName}^{txCount}"
        Map<String, ReportMetric> metricsMap = [:]

        visits.each { Visit visit ->
            String key = getMetricKey(visit)
            if(metricsMap.containsKey(key)) {
                metricsMap.get(key).count++
                metricsMap.get(key).treatmentCount += getTreatmentCount(visit)
            } else {
                metricsMap[key] = buildMetric(report, visit, 1)
            }
        }

        report.metricsMap = metricsMap
        report
    }

    String getMetricKey(Visit visit) {
        getMetricKey(visit.patientType, visit.insuranceType, visit.visitType, visit.therapist, visit.visitTreatments.size())
    }

    String getMetricKey(PatientType pt, InsuranceType it, VisitType vt, Employee th, int txCount) {
        "${pt.patientTypeName}^${it.insuranceTypeName}^${vt.visitTypeName}^${th.username}^{${txCount}".toString()
    }

    String getMetricKey(ReportMetric metric) {
        getMetricKey(lookupDataService.findPatientTypeById(metric.patientTypeId),
                lookupDataService.findInsuranceTypeById(metric.insuranceTypeId),
                lookupDataService.findVisitTypeById(metric.visitTypeId),
                employeeService.findById(metric.therapistId), metric.treatmentCount)
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
                treatmentCount: getTreatmentCount(visit),
                count: count
        )
    }

    /**
     * Calculates a "treatment count" for the given visit.  The following rules are in place, evaluated in order:
     * - if the visit has no treatments, then the count is 0
     * - if the visit has a "united healthcare" insurance type, then the count is 2
     * - beyond those 2 rules, the following applies as a sum
     *   -> +2 if the treatment is an evaluation
     *   -> +(treatment.quantity) for each treatment that is not an evaluation
     */
    int getTreatmentCount(Visit visit) {
        if(visit.visitTreatments.size() == 0) {
            return 0    // a visit without treatments counts as 0
        }

        if(visit.insuranceTypeId == uhcInsuranceType.insuranceTypeId ||
                visit.insuranceTypeId == uhcUmrInsuranceType.insuranceTypeId)
        {
            return 2    // uhc/uhc-umr is always 2
        }

        // from here, just count the number of treatment-qty's (evals count as 2)
        int count = 0
        visit.visitTreatments.each { vt ->
            Treatment treatment = lookupDataService.findTreatmentById(vt.treatmentId)
            if(treatment.isEvaluation()) {
                count += 2
            } else if(treatment.treatmentId == dryNeedling3OrMore.treatmentId) {
                // "dry needling 3 or more" counts as 2 treatments
                count += vt.treatmentQuantity * 2
            } else {
                count += vt.treatmentQuantity
            }
        }

        count
    }

    int getMetricCount(Report report, PatientType pt, InsuranceType it, VisitType vt, Employee th, int txCount) {
        ReportMetric metric = report.metricsMap[getMetricKey(pt, it, vt, th, txCount)]
        if(metric) {
            return metric.count
        }
        0
    }

    int getMetricCount(Report report, PatientType pt, InsuranceType it, VisitType vt, Employee th) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, pt, it, vt, th)) {
                count += metric.count
            }
        }
        count

    }

    boolean metricMatches(ReportMetric metric, PatientType pt, InsuranceType it, VisitType vt, Employee th) {
        if(metric.patientTypeId == pt.patientTypeId && metric.insuranceTypeId == it.insuranceTypeId &&
                metric.visitTypeId == vt.visitTypeId && metric.therapistId == th.employeeId)
        {
            return true
        }
        false
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

    int getMetricCountExcludeCancelNoShow(Report report, PatientType pt) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metric.visitTypeId != cancelNoShowVisitType.visitTypeId && metricMatches(metric, pt)) {
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

    int getMetricCount(Report report, VisitType vt) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, vt)) {
                count += metric.count
            }
        }
        count
    }

    int getMetricCountExcludeCancelNoShow(Report report, VisitType vt) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metric.visitTypeId != cancelNoShowVisitType.visitTypeId && metricMatches(metric, vt)) {
                count += metric.count
            }
        }
        count
    }

    int getMetricCount(Report report, VisitType vt, Employee th) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metricMatches(metric, vt, th)) {
                count += metric.count
            }
        }
        count
    }

    int getMetricCountExcludeCancelNoShow(Report report, VisitType vt, Employee th) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metric.visitTypeId != cancelNoShowVisitType.visitTypeId && metricMatches(metric, vt, th)) {
                count += metric.count
            }
        }
        count
    }

    boolean metricMatches(ReportMetric metric, VisitType vt) {
        if(metric.visitTypeId == vt.visitTypeId) {
            return true
        }
        false
    }

    boolean metricMatches(ReportMetric metric, VisitType vt, Employee th) {
        if(metric.visitTypeId == vt.visitTypeId && metric.therapistId == th.employeeId) {
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

    int getMetricCountExcludeCancelNoShow(Report report, InsuranceType it) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metric.visitTypeId != cancelNoShowVisitType.visitTypeId && metricMatches(metric, it)) {
                count += metric.count
            }
        }
        count
    }

    int getMetricTreatmentCountExcludeCancelNoShow(Report report, InsuranceType it) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metric.visitTypeId != cancelNoShowVisitType.visitTypeId && metricMatches(metric, it)) {
                count += metric.treatmentCount
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

    int getMetricCountExcludeCancelNoShow(Report report, PatientType pt, Employee th) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metric.visitTypeId != cancelNoShowVisitType.visitTypeId && metricMatches(metric, pt, th)) {
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

    int getMetricCountExcludeCancelNoShow(Report report, InsuranceType it, Employee th) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metric.visitTypeId != cancelNoShowVisitType.visitTypeId && metricMatches(metric, it, th)) {
                count += metric.count
            }
        }
        count
    }

    int getMetricTreatmentCountExcludeCancelNoShow(Report report, InsuranceType it, Employee th) {
        int count = 0
        report.metricsMap.values().each { ReportMetric metric ->
            if(metric.visitTypeId != cancelNoShowVisitType.visitTypeId && metricMatches(metric, it, th)) {
                count += metric.treatmentCount
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


    CountAndPercentReport produceReportType(List<Report> reports, Employee therapist, ReportType reportType) {
        CountAndPercentReport cpReport

        switch(reportType) {
            case ReportType.INSURANCE_TYPES_SIMPLE:
                return buildSummaryRow(produceInsuranceTypesSimpleReport(reports, therapist))
            case ReportType.INSURANCE_TYPES_BY_TREATMENT:
                return buildSummaryRow(produceInsuranceTypesByTreatmentReport(reports, therapist))
            case ReportType.PATIENT_TYPES:
                return buildSummaryRow(producePatientTypesReport(reports, therapist))
            case ReportType.VISIT_TYPES:
                return buildSummaryRow(produceVisitTypesReport(reports, therapist))
        }

        cpReport
    }

    CountAndPercentReport produceInsuranceTypesSimpleReport(List<Report> reports, Employee therapist) {
        CountAndPercentReport cpReport = new CountAndPercentReport(reportType: ReportType.INSURANCE_TYPES_SIMPLE,
                // get just a list of the insurance type shorthand names
                columnTypes: lookupDataService.insuranceTypes.collect { it.insuranceTypeShorthand }
        )

        // loop over the reports to build each CountAndPercentReportRow
        reports.each { Report report ->

            CountAndPercentReportRow row = new CountAndPercentReportRow(month: report.reportDate.month)

            // determine the total count first
            double totalCount = 0.0

            // loop over the insurance types to build each CountAndPercentCell
            lookupDataService.insuranceTypes.each { InsuranceType type ->
                totalCount += (therapist ? getMetricCountExcludeCancelNoShow(report, type, therapist) : getMetricCountExcludeCancelNoShow(report, type))
            }

            row.totalCount = totalCount

            // loop over the insurance types to build each CountAndPercentCell
            lookupDataService.insuranceTypes.each { InsuranceType type ->
                double itCount = (therapist ? getMetricCountExcludeCancelNoShow(report, type, therapist) : getMetricCountExcludeCancelNoShow(report, type))

                if(totalCount > 0.0) {
                    row.dataMap.put(type.insuranceTypeShorthand,
                            new CountAndPercentCell(count: itCount, percent: Math.round( (itCount * 100.0) / totalCount )))
                } else {
                    row.dataMap.put(type.insuranceTypeShorthand, new CountAndPercentCell(count: 0.0, percent: 0.0))
                }

            }

            cpReport.rows << row
        }

        cpReport
    }


    CountAndPercentReport produceInsuranceTypesByTreatmentReport(List<Report> reports, Employee therapist) {
        CountAndPercentReport cpReport = new CountAndPercentReport(reportType: ReportType.INSURANCE_TYPES_SIMPLE,
                // get just a list of the insurance type shorthand names
                columnTypes: lookupDataService.insuranceTypes.collect { it.insuranceTypeShorthand }
        )

        // loop over the reports to build each CountAndPercentReportRow
        reports.each { Report report ->

            CountAndPercentReportRow row = new CountAndPercentReportRow(month: report.reportDate.month)

            // determine the total count first
            //double totalCount = 0

            // loop over the insurance types to build each CountAndPercentCell
            lookupDataService.insuranceTypes.each { InsuranceType type ->
                row.totalCount += (therapist ? getMetricTreatmentCountExcludeCancelNoShow(report, type, therapist) : getMetricTreatmentCountExcludeCancelNoShow(report, type))
            }

            // divide by 4 to accommodate 4 treatments in an hour
            row.totalCount = row.totalCount / 4.0

            // loop over the insurance types to build each CountAndPercentCell
            lookupDataService.insuranceTypes.each { InsuranceType type ->
                double itCount = (therapist ? getMetricTreatmentCountExcludeCancelNoShow(report, type, therapist) : getMetricTreatmentCountExcludeCancelNoShow(report, type))

                // divide by 4 to accommodate 4 treatments in an hour
                itCount = itCount / 4.0

                if(row.totalCount > 0.0) {
                    row.dataMap.put(type.insuranceTypeShorthand,
                            new CountAndPercentCell(count: itCount, percent: Math.round( (itCount * 100.0) / row.totalCount )))
                } else {
                    row.dataMap.put(type.insuranceTypeShorthand, new CountAndPercentCell(count: 0.0, percent: 0.0))
                }

            }

            cpReport.rows << row
        }

        cpReport
    }


    CountAndPercentReport producePatientTypesReport(List<Report> reports, Employee therapist) {
        CountAndPercentReport cpReport = new CountAndPercentReport(reportType: ReportType.PATIENT_TYPES,
                // get just a list of the patient type names
                columnTypes: lookupDataService.patientTypes.collect { it.patientTypeName }
        )

        // loop over the reports to build each CountAndPercentReportRow
        reports.each { Report report ->

            CountAndPercentReportRow row = new CountAndPercentReportRow(month: report.reportDate.month)

            // determine the total count first
            double totalCount = 0

            // loop over the patient types to build each CountAndPercentCell
            lookupDataService.patientTypes.each { PatientType type ->
                totalCount += (therapist ? getMetricCountExcludeCancelNoShow(report, type, therapist) : getMetricCountExcludeCancelNoShow(report, type))
            }

            row.totalCount = totalCount

            // loop over the patient types to build each CountAndPercentCell
            lookupDataService.patientTypes.each { PatientType type ->
                double ptCount = (therapist ? getMetricCountExcludeCancelNoShow(report, type, therapist) : getMetricCountExcludeCancelNoShow(report, type))

                if(totalCount > 0) {
                    row.dataMap.put(type.patientTypeName,
                            new CountAndPercentCell(count: ptCount, percent: Math.round( (ptCount * 100.0) / totalCount )))
                } else {
                    row.dataMap.put(type.patientTypeName, new CountAndPercentCell(count: 0.0, percent: 0.0))
                }

            }

            cpReport.rows << row
        }

        cpReport
    }

    CountAndPercentReport produceVisitTypesReport(List<Report> reports, Employee therapist) {
        CountAndPercentReport cpReport = new CountAndPercentReport(reportType: ReportType.VISIT_TYPES,
                // get just a list of the patient type names
                columnTypes: lookupDataService.visitTypes.collect { it.visitTypeName }
        )

        // loop over the reports to build each CountAndPercentReportRow
        reports.each { Report report ->

            CountAndPercentReportRow row = new CountAndPercentReportRow(month: report.reportDate.month)

            // determine the total count first
            double totalCount = 0

            if(therapist) {
                totalCount += getMetricCountExcludeCancelNoShow(report, initialVisitType, therapist)
                totalCount += getMetricCountExcludeCancelNoShow(report, followUpVisitType, therapist)
                totalCount += getMetricCount(report, cancelNoShowVisitType, therapist)
            } else {
                totalCount += getMetricCountExcludeCancelNoShow(report, initialVisitType)
                totalCount += getMetricCountExcludeCancelNoShow(report, followUpVisitType)
                totalCount += getMetricCount(report, cancelNoShowVisitType)
            }

            row.totalCount = totalCount


            if(therapist) {
                double vtCount = getMetricCountExcludeCancelNoShow(report, initialVisitType, therapist)
                if(vtCount > 0) {
                    row.dataMap.put(initialVisitType.visitTypeName,
                            new CountAndPercentCell(count: vtCount, percent: Math.round( (vtCount * 100.0) / totalCount )))
                } else {
                    row.dataMap.put(initialVisitType.visitTypeName, new CountAndPercentCell(count: 0, percent: 0))
                }

                vtCount = getMetricCountExcludeCancelNoShow(report, followUpVisitType, therapist)
                if(vtCount > 0) {
                    row.dataMap.put(followUpVisitType.visitTypeName,
                            new CountAndPercentCell(count: vtCount, percent: Math.round( (vtCount * 100.0) / totalCount )))
                } else {
                    row.dataMap.put(followUpVisitType.visitTypeName, new CountAndPercentCell(count: 0, percent: 0))
                }

                vtCount = getMetricCount(report, cancelNoShowVisitType, therapist)
                if(vtCount > 0) {
                    row.dataMap.put(cancelNoShowVisitType.visitTypeName,
                            new CountAndPercentCell(count: vtCount, percent: Math.round( (vtCount * 100.0) / totalCount )))
                } else {
                    row.dataMap.put(cancelNoShowVisitType.visitTypeName, new CountAndPercentCell(count: 0, percent: 0))
                }
            } else {
                double vtCount = getMetricCountExcludeCancelNoShow(report, initialVisitType)
                if(vtCount > 0) {
                    row.dataMap.put(initialVisitType.visitTypeName,
                            new CountAndPercentCell(count: vtCount, percent: Math.round( (vtCount * 100.0) / totalCount )))
                } else {
                    row.dataMap.put(initialVisitType.visitTypeName, new CountAndPercentCell(count: 0, percent: 0))
                }

                vtCount = getMetricCountExcludeCancelNoShow(report, followUpVisitType)
                if(vtCount > 0) {
                    row.dataMap.put(followUpVisitType.visitTypeName,
                            new CountAndPercentCell(count: vtCount, percent: Math.round( (vtCount * 100.0) / totalCount )))
                } else {
                    row.dataMap.put(followUpVisitType.visitTypeName, new CountAndPercentCell(count: 0, percent: 0))
                }

                vtCount = getMetricCount(report, cancelNoShowVisitType)
                if(vtCount > 0) {
                    row.dataMap.put(cancelNoShowVisitType.visitTypeName,
                            new CountAndPercentCell(count: vtCount, percent: Math.round( (vtCount * 100.0) / totalCount )))
                } else {
                    row.dataMap.put(cancelNoShowVisitType.visitTypeName, new CountAndPercentCell(count: 0, percent: 0))
                }
            }

            cpReport.rows << row
        }

        cpReport
    }


    CountAndPercentReport buildSummaryRow(CountAndPercentReport report) {
        CountAndPercentReportRow sumRow = new CountAndPercentReportRow()

        // loop over each row/month in the report
        report.rows.each { row ->
            // add each row.count as a running tally for the sum row
            sumRow.totalCount += row.totalCount

            // loop over each column, and basically do the same thing (sum per column)
            report.columnTypes.each { columnType ->
                CountAndPercentCell cell = new CountAndPercentCell()

                if(sumRow.dataMap.containsKey(columnType)) {
                    cell = sumRow.dataMap.get(columnType)
                } else {
                    sumRow.dataMap.put(columnType, cell)
                }

                cell.count += row.dataMap.get(columnType).count
            }
        }

        // loop over columns another time for the sum row, and calculate the percents
        report.columnTypes.each { columnType ->
            double colCount = sumRow.dataMap.get(columnType).count
            if(colCount > 0) {
                sumRow.dataMap.get(columnType).percent = Math.round( (colCount * 100.0) / sumRow.totalCount )
            }
        }

        report.summaryRow = sumRow

        // return the updated report
        report
    }
}
