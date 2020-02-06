package com.kinespherept.service

import com.kinespherept.dao.repository.ReportMetricRepository
import com.kinespherept.dao.repository.ReportRepository
import com.kinespherept.dao.repository.VisitRepository
import com.kinespherept.enums.ReportType
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.PatientType
import com.kinespherept.model.core.Report
import com.kinespherept.model.core.Treatment
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitTreatment
import com.kinespherept.model.core.VisitType
import com.kinespherept.model.report.CountAndPercentReport
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.temporal.ChronoField

class ReportServiceSpec extends Specification {


    // the class under test
    ReportService reportService

    // supporting classes
    EmployeeService employeeService
    LookupDataService lookupDataService
    ReportMetricRepository reportMetricRepository
    ReportRepository reportRepository
    VisitService visitService


    void setup() {
        employeeService = EmployeeServiceSpec.populatedEmployeeService
        lookupDataService = LookupDataServiceSpec.populatedLookupDataService
        reportMetricRepository = Mock()
        reportRepository = Mock()
        visitService = Mock()


        reportService = new ReportService(employeeService, lookupDataService, reportMetricRepository,
                reportRepository, visitService)

        reportService.init()
    }


    def 'test that init sets the visitType & insuranceType & treatment objects'() {
        when:   'before init is called, all should be null'
        reportService.initialVisitType = null
        reportService.followUpVisitType = null
        reportService.cancelNoShowVisitType = null
        reportService.uhcInsuranceType = null
        reportService.uhcUmrInsuranceType = null
        reportService.dryNeedling3OrMore = null

        then:   'the stashed visit type is empty'
        reportService.initialVisitType == null
        reportService.followUpVisitType == null
        reportService.cancelNoShowVisitType == null
        reportService.uhcInsuranceType == null
        reportService.uhcUmrInsuranceType == null
        reportService.dryNeedling3OrMore == null


        when:
        reportService.init()

        then:
        reportService.initialVisitType.visitTypeName == VisitType.INITIAL
        reportService.followUpVisitType.visitTypeName == VisitType.FOLLOW_UP
        reportService.cancelNoShowVisitType.visitTypeName == VisitType.CANCEL_NO_SHOW
        reportService.uhcInsuranceType.insuranceTypeName == InsuranceType.UNITED_HEALTHCARE
        reportService.uhcUmrInsuranceType.insuranceTypeName == InsuranceType.UNITED_HEALTHCARE_UMR
        reportService.dryNeedling3OrMore.treatmentCode == Treatment.TREATMENT_CODE_DRY_NEEDLING_3_OR_MORE
    }


    @Unroll
    def 'test setDateToFirstOfMonth() works as expected'() {
        expect:
        reportService.setDateToFirstOfMonth(date).get(ChronoField.DAY_OF_MONTH) == 1

        where:
        date << [LocalDate.of(2019, 5, 4),
                 LocalDate.of(2019, 5, 31),
                 LocalDate.of(2019, 5, 1)]
    }


    def 'test getReportHeadersBetweenDateRange()'() {
        given:
        LocalDate fromDate = LocalDate.of(2018, 1, 1)
        LocalDate toDate = LocalDate.of(2018, 6, 1)

        List<Report> repoReports = [
                new Report(reportDate: LocalDate.of(2018, 1, 1)),
                new Report(reportDate: LocalDate.of(2018, 2, 1)),
                new Report(reportDate: LocalDate.of(2018, 3, 1)),
                new Report(reportDate: LocalDate.of(2018, 4, 1))
        ]


        when:
        List<Report> outReports = reportService.getReportHeadersBetweenDateRange(fromDate, toDate)

        then:
        1 * reportRepository.findByReportDateRange(fromDate, toDate) >> repoReports
        1 * reportRepository.save( { it.reportDate == LocalDate.of(2018, 5, 1) } )
        1 * reportRepository.save( { it.reportDate == LocalDate.of(2018, 6, 1) } )
        outReports.size() == 6
    }



    def 'test generateMetrics() properly counts the metrics'() {
        given:
        Report report = new Report(reportDate: LocalDate.of(2019, 5, 1))

        LookupDataService lds = LookupDataServiceSpec.getPopulatedLookupDataService()
        EmployeeService es = EmployeeServiceSpec.getPopulatedEmployeeService()

        List<PatientType> pts = lds.patientTypes
        List<InsuranceType> its = lds.insuranceTypes
        List<VisitType> vts = lds.visitTypes
        List<Employee> emps = es.employees

        when:
        report = reportService.generateMetrics(report)

        then:
        1 * visitService.getVisitsBetweenDates(
                { LocalDate d -> d.getYear() == 2019 && d.getMonthValue() == 5 && d.getDayOfMonth() == 1 },
                { LocalDate d -> d.getYear() == 2019 && d.getMonthValue() == 6 && d.getDayOfMonth() == 1}) >>
                buildVisitsForMetrics()

        reportService.getMetricCount(report, pts[0], its[0], vts[0], emps[0]) == 2
        reportService.getMetricCount(report, pts[0], its[0], vts[1], emps[0]) == 6
        reportService.getMetricCount(report, pts[0], its[0], vts[2], emps[0]) == 1
        reportService.getMetricCount(report, pts[0], its[0], vts[0]) == 4
        reportService.getMetricCount(report, pts[0], vts[0]) == 4

        reportService.getMetricCount(report, pts[1], its[0], vts[2], emps[0]) == 0
        reportService.getMetricCount(report, pts[1], its[1], vts[0], emps[0]) == 1
        reportService.getMetricCount(report, pts[1], its[1], vts[1], emps[0]) == 2
        reportService.getMetricCount(report, pts[1], its[3], vts[0], emps[0]) == 1
        reportService.getMetricCount(report, pts[1], its[3], vts[1], emps[0]) == 3
        reportService.getMetricCount(report, pts[1], its[3], vts[2], emps[0]) == 1
        reportService.getMetricCount(report, pts[1], vts[0]) == 2
        reportService.getMetricCount(report, pts[1], vts[1]) == 5
        reportService.getMetricCount(report, pts[1], vts[2]) == 1

        // insurance & visit type counts...
        reportService.getMetricCount(report, its[0], vts[0]) == 4
        reportService.getMetricCount(report, its[0], vts[1]) == 6
        reportService.getMetricCount(report, its[0], vts[2]) == 1
        reportService.getMetricCount(report, its[1], vts[0]) == 1
        reportService.getMetricCount(report, its[1], vts[1]) == 2
        reportService.getMetricCount(report, its[1], vts[2]) == 0

        // insurance only counts...
        reportService.getMetricCount(report, its[0]) == 11
        reportService.getMetricCount(report, its[1]) == 3
        reportService.getMetricCount(report, its[3]) == 5

        // patient & visit type & therapist counts...
        reportService.getMetricCount(report, pts[0], vts[0], emps[0]) == 2
        reportService.getMetricCount(report, pts[0], vts[1], emps[0]) == 6
        reportService.getMetricCount(report, pts[0], vts[2], emps[0]) == 1
        reportService.getMetricCount(report, pts[1], vts[0], emps[0]) == 2
        reportService.getMetricCount(report, pts[1], vts[1], emps[0]) == 5
        reportService.getMetricCount(report, pts[1], vts[2], emps[0]) == 1

        // patient & visit type counts...
        reportService.getMetricCount(report, pts[0], vts[0]) == 4
        reportService.getMetricCount(report, pts[0], vts[1]) == 6
        reportService.getMetricCount(report, pts[0], vts[2]) == 1
        reportService.getMetricCount(report, pts[1], vts[0]) == 2
        reportService.getMetricCount(report, pts[1], vts[1]) == 5
        reportService.getMetricCount(report, pts[1], vts[2]) == 1

        // insurance & visit type & therapist counts...
        reportService.getMetricCount(report, its[0], vts[0], emps[0]) == 2
        reportService.getMetricCount(report, its[0], vts[1], emps[0]) == 6
        reportService.getMetricCount(report, its[0], vts[2], emps[0]) == 1
        reportService.getMetricCount(report, its[1], vts[0], emps[0]) == 1
        reportService.getMetricCount(report, its[1], vts[1], emps[0]) == 2
        reportService.getMetricCount(report, its[1], vts[2], emps[0]) == 0
        reportService.getMetricCount(report, its[3], vts[0], emps[0]) == 1
        reportService.getMetricCount(report, its[3], vts[1], emps[0]) == 3
        reportService.getMetricCount(report, its[3], vts[2], emps[0]) == 1

        // insurance & visit type counts...
        reportService.getMetricCount(report, its[0], vts[0]) == 4
        reportService.getMetricCount(report, its[0], vts[1]) == 6
        reportService.getMetricCount(report, its[0], vts[2]) == 1
        reportService.getMetricCount(report, its[1], vts[0]) == 1
        reportService.getMetricCount(report, its[1], vts[1]) == 2
        reportService.getMetricCount(report, its[1], vts[2]) == 0
        reportService.getMetricCount(report, its[3], vts[0]) == 1
        reportService.getMetricCount(report, its[3], vts[1]) == 3
        reportService.getMetricCount(report, its[3], vts[2]) == 1
    }


    def 'test getTreatmentCount() for all scenarios'() {
        given:
        int count = 0
        LookupDataService lds = LookupDataServiceSpec.getPopulatedLookupDataService()
        EmployeeService es = EmployeeServiceSpec.getPopulatedEmployeeService()

        when:   'no treatments'
        count = reportService.getTreatmentCount(buildVisit(lds.patientTypes[0], lds.insuranceTypes[3], lds.visitTypes[0],
                es.employees[0], []))
        then:   'count is 0'
        count == 0

        when:   'ins type is uhc'
        count = reportService.getTreatmentCount(buildVisit(lds.patientTypes[0], lds.insuranceTypes[3], lds.visitTypes[0],
                es.employees[0], [buildVisitTreatment(lds.treatments[0], 1)]))

        then:   'count is 2'
        count == 2

        when:   'ins type is uhc-umr'
        count = reportService.getTreatmentCount(buildVisit(lds.patientTypes[0], lds.insuranceTypes[4], lds.visitTypes[0],
                es.employees[0], [buildVisitTreatment(lds.treatments[0], 1)]))

        then:   'count is 2'
        count == 2



        when:   'eval, 0 other treatments'
        count = reportService.getTreatmentCount(buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0],
                es.employees[0], [buildVisitTreatment(lds.treatments[7], 1)]))

        then:   'count is 2'
        count == 2

        when:   'eval, 1 non-eval treatment'
        count = reportService.getTreatmentCount(buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0],
                es.employees[0], [buildVisitTreatment(lds.treatments[7], 1), buildVisitTreatment(lds.treatments[0], 1)]))


        then:   'count is 3'
        count == 3

        when:   'eval, 2 non-eval treatments'
        count = reportService.getTreatmentCount(buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0],
                es.employees[0], [buildVisitTreatment(lds.treatments[7], 1), buildVisitTreatment(lds.treatments[0], 2)]))
        then:   'count is 4'
        count == 4

        when:   '1 non-eval treatment'
        count = reportService.getTreatmentCount(buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0],
                es.employees[0], [buildVisitTreatment(lds.treatments[0], 1)]))
        then:   'count is 1'
        count == 1

        when:   '3 non-eval treatments'
        count = reportService.getTreatmentCount(buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0],
                es.employees[0], [buildVisitTreatment(lds.treatments[2], 2), buildVisitTreatment(lds.treatments[0], 1)]))
        then:   'count is 3'
        count == 3

        when:   '1 dry needling treatments'
        count = reportService.getTreatmentCount(buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0],
                es.employees[0], [buildVisitTreatment(lds.treatments[12], 1)]))
        then:   'count is 2'
        count == 2

        when:   '2 dry needling + another treatment'
        count = reportService.getTreatmentCount(buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0],
                es.employees[0], [buildVisitTreatment(lds.treatments[12], 2), buildVisitTreatment(lds.treatments[0], 1)]))
        then:   'count is 5'
        count == 5

    }


    def 'test produceInsuranceTypesSimpleReport() for all employees'() {
        when:
        List<Report> reports = [ reportService.generateMetrics(reportService.buildNewReport(LocalDate.now())) ]
        CountAndPercentReport cpReport = reportService.produceInsuranceTypesSimpleReport(reports, null)

        then:
        1 * visitService.getVisitsBetweenDates(_, _) >> buildVisitsForMetrics()
        cpReport.reportType == ReportType.INSURANCE_TYPES_SIMPLE
        cpReport.columnTypes.size() == 9
        cpReport.rows.size() == 1
        cpReport.rows[0].totalCount == 17.0
        cpReport.rows[0].dataMap.size() == 9
        cpReport.rows[0].dataMap['BCBS'].count == 10.0
        cpReport.rows[0].dataMap['BCBS'].percent == 59.0
        cpReport.rows[0].dataMap['MedCr'].count == 3.0
        cpReport.rows[0].dataMap['MedCr'].percent == 18.0
        cpReport.rows[0].dataMap['TriCr'].count == 0.0
        cpReport.rows[0].dataMap['TriCr'].percent == 0.0
        cpReport.rows[0].dataMap['UHC'].count == 4.0
        cpReport.rows[0].dataMap['UHC'].percent == 24.0
        cpReport.rows[0].dataMap['UHCUMR'].count == 0.0
        cpReport.rows[0].dataMap['UHCUMR'].percent == 0.0
        cpReport.rows[0].dataMap['WrkCmp'].count == 0.0
        cpReport.rows[0].dataMap['WrkCmp'].percent == 0.0
        cpReport.rows[0].dataMap['Cash'].count == 0.0
        cpReport.rows[0].dataMap['Cash'].percent == 0.0
        cpReport.rows[0].dataMap['Csh-DB'].count == 0.0
        cpReport.rows[0].dataMap['Csh-DB'].percent == 0.0
        cpReport.rows[0].dataMap['Hmna'].count == 0.0
        cpReport.rows[0].dataMap['Hmna'].percent == 0.0

        when:
        reportService.buildSummaryRow(cpReport)

        then:
        cpReport.summaryRow != null
        cpReport.summaryRow.totalCount == 17.0
        cpReport.summaryRow.dataMap.size() == 9
        cpReport.summaryRow.dataMap['BCBS'].count == 10.0
        cpReport.summaryRow.dataMap['BCBS'].percent == 59.0
        cpReport.summaryRow.dataMap['MedCr'].count == 3.0
        cpReport.summaryRow.dataMap['MedCr'].percent == 18.0
        cpReport.summaryRow.dataMap['TriCr'].count == 0.0
        cpReport.summaryRow.dataMap['TriCr'].percent == 0.0
        cpReport.summaryRow.dataMap['UHC'].count == 4.0
        cpReport.summaryRow.dataMap['UHC'].percent == 24.0
        cpReport.summaryRow.dataMap['UHCUMR'].count == 0.0
        cpReport.summaryRow.dataMap['UHCUMR'].percent == 0.0
        cpReport.summaryRow.dataMap['WrkCmp'].count == 0.0
        cpReport.summaryRow.dataMap['WrkCmp'].percent == 0.0
        cpReport.summaryRow.dataMap['Cash'].count == 0.0
        cpReport.summaryRow.dataMap['Cash'].percent == 0.0
        cpReport.summaryRow.dataMap['Csh-DB'].count == 0.0
        cpReport.summaryRow.dataMap['Csh-DB'].percent == 0.0
        cpReport.summaryRow.dataMap['Hmna'].count == 0.0
        cpReport.summaryRow.dataMap['Hmna'].percent == 0.0
    }



    // convenience method that builds a prepared list of Visits to test against
    List<Visit> buildVisitsForMetrics() {
        LookupDataService lds = LookupDataServiceSpec.getPopulatedLookupDataService()
        EmployeeService es = EmployeeServiceSpec.getPopulatedEmployeeService()

/*
           [0]= new PatientType(patientTypeId: 1, patientTypeName: 'Orthopedic'),
           [1]= new PatientType(patientTypeId: 2, patientTypeName: 'Scoliosis'),
           [2]= new PatientType(patientTypeId: 3, patientTypeName: 'Dancer'),
           [3]= new PatientType(patientTypeId: 4, patientTypeName: 'POTS'),
           [4]= new PatientType(patientTypeId: 5, patientTypeName: 'Neuro'),
           [5]= new PatientType(patientTypeId: 6, patientTypeName: 'Client')

           [0]= new InsuranceType(insuranceTypeId: 1, insuranceTypeName: 'BCBS'),
           [1]= new InsuranceType(insuranceTypeId: 2, insuranceTypeName: 'Medicare'),
           [2]= new InsuranceType(insuranceTypeId: 3, insuranceTypeName: 'Tricare'),
           [3]= new InsuranceType(insuranceTypeId: 4, insuranceTypeName: 'UHC'),
           [4]= new InsuranceType(insuranceTypeId: 5, insuranceTypeName: 'UHC-UMR'),
           [5]= new InsuranceType(insuranceTypeId: 6, insuranceTypeName: 'Work Comp'),
           [6]= new InsuranceType(insuranceTypeId: 7, insuranceTypeName: 'Cash'),
           [7]= new InsuranceType(insuranceTypeId: 8, insuranceTypeName: 'Cash-Don\'t bill'),
           [8]= new InsuranceType(insuranceTypeId: 9, insuranceTypeName: 'Humana'),

           [0]= new VisitType(visitTypeId: 1, visitTypeName: 'Initial'),
           [1]= new VisitType(visitTypeId: 2, visitTypeName: 'Follow up'),
           [2]= new VisitType(visitTypeId: 3, visitTypeName: 'Cancel/No Show')

           [0]= new Treatment(treatmentId: 1, treatmentCode: '97112', treatmentName: 'Neuromuscular Re-Education', displayOrder: 1, evaluation: false),
           [1]= new Treatment(treatmentId: 2, treatmentCode: '97110', treatmentName: 'Therapeutic Exercise', displayOrder: 2, evaluation: false),
           [2]= new Treatment(treatmentId: 3, treatmentCode: '97140', treatmentName: 'Manual Therapy', displayOrder: 3, evaluation: false),
           [3]= new Treatment(treatmentId: 4, treatmentCode: '97530', treatmentName: 'Therapeutic Activities', displayOrder: 4, evaluation: false),
           [4]= new Treatment(treatmentId: 5, treatmentCode: '97116', treatmentName: 'Gait Training', displayOrder: 5, evaluation: false),
           [5]= new Treatment(treatmentId: 6, treatmentCode: '97035', treatmentName: 'Ultrasound', displayOrder: 6, evaluation: false),
           [6]= new Treatment(treatmentId: 7, treatmentCode: '20560', treatmentName: 'Dry Needling 1 or 2 muscles', displayOrder: 7, evaluation: false),
           [7]= new Treatment(treatmentId: 8, treatmentCode: '97161', treatmentName: 'Eval low complex', displayOrder: 9, evaluation: true),
           [8]= new Treatment(treatmentId: 9, treatmentCode: '97162', treatmentName: 'Eval mod complex', displayOrder: 10, evaluation: true),
           [9]= new Treatment(treatmentId: 10, treatmentCode: '97163', treatmentName: 'Eval high complex', displayOrder: 11, evaluation: true),
           [10]= new Treatment(treatmentId: 11, treatmentCode: '97164', treatmentName: 'Re-evaluation', displayOrder: 13, evaluation: true),
           [11]= new Treatment(treatmentId: 12, treatmentCode: '97542', treatmentName: 'Wheelchair Assessment', displayOrder: 12, evaluation: true),
           [12]= new Treatment(treatmentId: 13, treatmentCode: '20561', treatmentName: 'Dry Needling 3 or more muscles', displayOrder: 8, evaluation: false),
 */

        [
                // noelle

                //  ortho
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0], es.employees[0], [buildVisitTreatment(lds.treatments[7], 1), buildVisitTreatment(lds.treatments[0], 2)]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0], [buildVisitTreatment(lds.treatments[2], 2), buildVisitTreatment(lds.treatments[3], 2)]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0], [buildVisitTreatment(lds.treatments[12], 4)]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0], [buildVisitTreatment(lds.treatments[12], 1), buildVisitTreatment(lds.treatments[3], 2), buildVisitTreatment(lds.treatments[0], 2)]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0], es.employees[0], [buildVisitTreatment(lds.treatments[7], 1), buildVisitTreatment(lds.treatments[0], 2)]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0], [buildVisitTreatment(lds.treatments[2], 2), buildVisitTreatment(lds.treatments[3], 1)]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0], [buildVisitTreatment(lds.treatments[2], 1), buildVisitTreatment(lds.treatments[3], 1)]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0], [buildVisitTreatment(lds.treatments[3], 2), buildVisitTreatment(lds.treatments[4], 2)]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[2], es.employees[0]),

                // scoli
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[1], lds.visitTypes[0], es.employees[0], [buildVisitTreatment(lds.treatments[7], 1), buildVisitTreatment(lds.treatments[0], 2)]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[1], lds.visitTypes[1], es.employees[0], [buildVisitTreatment(lds.treatments[2], 2)]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[1], lds.visitTypes[1], es.employees[0], [buildVisitTreatment(lds.treatments[2], 4)]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[3], lds.visitTypes[0], es.employees[0], [buildVisitTreatment(lds.treatments[7], 1), buildVisitTreatment(lds.treatments[0], 2)]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[3], lds.visitTypes[1], es.employees[0], [buildVisitTreatment(lds.treatments[2], 1)]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[3], lds.visitTypes[1], es.employees[0], [buildVisitTreatment(lds.treatments[0], 2), buildVisitTreatment(lds.treatments[2], 2)]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[3], lds.visitTypes[1], es.employees[0], [buildVisitTreatment(lds.treatments[1], 2), buildVisitTreatment(lds.treatments[4], 2)]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[3], lds.visitTypes[2], es.employees[0]),


                // ashley
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0], es.employees[1], [buildVisitTreatment(lds.treatments[7], 1), buildVisitTreatment(lds.treatments[0], 2)]),


                // tamara
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0], es.employees[4], [buildVisitTreatment(lds.treatments[7], 1), buildVisitTreatment(lds.treatments[0], 2)])



        ]
    }



    // convenience method to quick build a Visit (no treatments)
    Visit buildVisit(PatientType pt, InsuranceType it, VisitType vt, Employee emp) {
        new Visit(patientType: pt, patientTypeId: pt.patientTypeId,
                insuranceType: it, insuranceTypeId: it.insuranceTypeId,
                visitType: vt, visitTypeId: vt.visitTypeId,
                therapist: emp, therapistId: emp.employeeId,
        )
    }

    // convenience method to quick build a Visit (with treatments)
    Visit buildVisit(PatientType pt, InsuranceType it, VisitType vt, Employee emp, List<VisitTreatment> treatments) {
        new Visit(patientType: pt, patientTypeId: pt.patientTypeId,
                insuranceType: it, insuranceTypeId: it.insuranceTypeId,
                visitType: vt, visitTypeId: vt.visitTypeId,
                therapist: emp, therapistId: emp.employeeId,
                visitTreatments: treatments
        )
    }

    VisitTreatment buildVisitTreatment(Treatment treatment, int qty) {
        new VisitTreatment(treatmentId: treatment.treatmentId, treatmentQuantity: qty)
    }

}
