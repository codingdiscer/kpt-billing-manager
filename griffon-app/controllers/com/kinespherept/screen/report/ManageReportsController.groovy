package com.kinespherept.screen.report

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.component.NavigationController
import com.kinespherept.config.CommonProperties
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.NavigationSection
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Report
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.model.report.MonthReport
import com.kinespherept.service.LookupDataService
import com.kinespherept.service.ReportService
import com.kinespherept.service.VisitService
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import groovy.util.logging.Slf4j

import javax.annotation.Nonnull
import javax.annotation.PostConstruct
import java.time.LocalDate


@ArtifactProviderFor(GriffonController)
@Slf4j
class ManageReportsController {

    @MVCMember @Nonnull ManageReportsModel model
    @MVCMember @Nonnull ManageReportsView view

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire ReportService reportService
    @SpringAutowire VisitService visitService

    @GriffonAutowire BrowseReportsController browseReportsController
    @GriffonAutowire NavigationController navigationController

    // this flag signifies we are in the prepareForm() method.  this is needed/useful because that method
    // sets a couple of model fields that have onChange() events attached to them, and this flag will tell
    // those methods not to take any action that would otherwise be taken if a user triggered the onChange() event.
    boolean preparingForm = false


    @PostConstruct
    void init() {
        log.debug "init()"
        SpringConfig.autowire(this)
    }

    void prepareForm() {
        log.info "prepareForm()"

        preparingForm = true

        navigationController.prepareForm(view.navigationPane, NavigationSection.REPORTS)


        if(model.yearsChoice.isEmpty()) {
            // populate the year list, and select the current year
            model.years.clear()
            model.years.addAll(visitService.getAllYears().collect { String.valueOf(it) })

            // get oldest visit, get current year - fill in the blanks
            model.yearsChoice = String.valueOf(visitService.getCurrentYearExceptIfJanuary())
            // ..the act of setting the "model.yearsChoice" will trigger to call to the refreshDisplay() method
        }


        preparingForm = false
    }




    MonthReport buildMonthReport(Report report) {
        MonthReport mr = new MonthReport(report: report)

        mr.month = report.reportDate.month.toString()
        mr.futureMonth = LocalDate.now().isBefore(report.reportDate.plusMonths(1))

        if(mr.futureMonth) {
            mr.status = "Cannot generate until ${commonProperties.formatDate(report.reportDate.plusMonths(1))}"
        } else {
            mr.status = report.generatedDate ? "Generated on ${commonProperties.formatDate(report.generatedDate)}" : "Available to be generated"
        }

        mr
    }




    /**
     * Loads the reports for the currently selected year, then refreshes the UI
     * elements for those reports.
     */
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void refreshDisplay() {
        log.info "refreshDisplay() : preparingForm=${preparingForm}"

        List<Report> reports = []

        runOutsideUI {
            // refresh the reports
            reports = reportService.getReportHeadersBetweenDateRange(
                    LocalDate.of(Integer.valueOf(model.yearsChoice), 1, 1),
                    LocalDate.of(Integer.valueOf(model.yearsChoice), 12, 1)
            )

            runInsideUISync {
                model.reports.clear()

                reports.each { Report report ->
                    model.reports << buildMonthReport(report)
                }

                view.refreshMonthReports(true)
            }
        }
    }


    /**
     * Called when any of the "generate report" buttons are pressed
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void generateAllReports() {
        log.info "generateAllReports() for ${model.yearsChoice}"

        // disable the "generate report" buttons
        view.refreshMonthReports(false)

        runOutsideUI {
            model.reports.each { MonthReport mr ->
                reportService.generateAndSaveMetrics(mr.report)
                log.info "..finished generating report for ${mr.month}"
            }

            runInsideUISync {
                refreshDisplay()
            }
        }
    }

    /**
     * Called when any of the "generate report" buttons are pressed
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void generateReport(MonthReport report) {
        log.info "generateReport() for report with date [${report.report.reportDate}]"

        // disable the "generate report" buttons
        view.refreshMonthReports(false)

        runOutsideUI {
            reportService.generateAndSaveMetrics(report.report)

            runInsideUISync {
                refreshDisplay()
            }
        }
    }

    /**
     * Called when the "Browse Reports" button is pressed
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void browseReports() {
        log.info "browseReports()"
        browseReportsController.prepareForm()
        SceneManager.changeTheScene(SceneDefinition.BROWSE_REPORTS)
    }


}
