package com.kinespherept.service

import com.kinespherept.dao.repository.ReportMetricRepository
import com.kinespherept.dao.repository.ReportRepository
import com.kinespherept.dao.repository.VisitRepository
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.PatientType
import com.kinespherept.model.core.Report
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitType
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
    }


    def 'test that init sets the cancelNoShow object'() {
        when:   'before init is called'
        reportService.cancelNoShowVisitType = null

        then:   'the stashed visit type is empty'
        reportService.cancelNoShowVisitType == null


        when:
        reportService.init()

        then:
        reportService.cancelNoShowVisitType.visitTypeName == ReportService.VISIT_TYPE_CANCEL_NO_SHOW
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
 */

        [
                // noelle

                //  ortho
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0], es.employees[0]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0], es.employees[0]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[1], es.employees[0]),
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[2], es.employees[0]),

                // scoli
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[1], lds.visitTypes[0], es.employees[0]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[1], lds.visitTypes[1], es.employees[0]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[1], lds.visitTypes[1], es.employees[0]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[3], lds.visitTypes[0], es.employees[0]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[3], lds.visitTypes[1], es.employees[0]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[3], lds.visitTypes[1], es.employees[0]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[3], lds.visitTypes[1], es.employees[0]),
                buildVisit(lds.patientTypes[1], lds.insuranceTypes[3], lds.visitTypes[2], es.employees[0]),




                // ashley
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0], es.employees[1]),



                // tamara
                buildVisit(lds.patientTypes[0], lds.insuranceTypes[0], lds.visitTypes[0], es.employees[4])



        ]
    }




    Visit buildVisit(PatientType pt, InsuranceType it, VisitType vt, Employee emp) {
        new Visit(patientType: pt, patientTypeId: pt.patientTypeId,
                insuranceType: it, insuranceTypeId: it.insuranceTypeId,
                visitType: vt, visitTypeId: vt.visitTypeId,
                therapist: emp, therapistId: emp.employeeId,
        )
    }


}
