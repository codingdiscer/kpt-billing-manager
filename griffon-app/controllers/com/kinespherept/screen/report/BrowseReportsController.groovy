package com.kinespherept.screen.report

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.component.NavigationController
import com.kinespherept.config.CommonProperties
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.NavigationSection
import com.kinespherept.enums.ReportType
import com.kinespherept.enums.TimeRangePreset
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.EmployeeRole
import com.kinespherept.model.core.Report
import com.kinespherept.model.report.CountAndPercentCell
import com.kinespherept.model.report.CountAndPercentReport
import com.kinespherept.model.report.CountAndPercentReportRow
import com.kinespherept.service.EmployeeService
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
import java.time.Month
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit


@ArtifactProviderFor(GriffonController)
@Slf4j
class BrowseReportsController {

    @MVCMember @Nonnull BrowseReportsModel model
    @MVCMember @Nonnull BrowseReportsView view

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire EmployeeService employeeService
    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire ReportService reportService
    @SpringAutowire VisitService visitService


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

        // check if this is the first time through here
        if(model.currentYearExceptIfJanuary == 0) {
            // set some useful defaults
            model.allYears = visitService.getAllYears()
            model.currentYearExceptIfJanuary = visitService.getCurrentYearExceptIfJanuary()

            populateMonthOptions()
            populateTherapists()
        }

        // display the report with the current dates & filter criteria
        getAndRenderReport()

        preparingForm = false
    }

    /**
     * Makes calls to get the report, then passes it to the view to render it
     */
    void getAndRenderReport() {
        // get a report
        model.countAndPercentReport = getReport()

        // pass to the view
        view.displayCountAndPercentReport(model.countAndPercentReport)

        // update the report message
        model.reportMessage = generateReportMessage()

        // update the 'current###' properties in the model
        model.currentPreset = model.presetsChoice
        model.currentYear = model.yearsChoice
        model.currentStartMonth = model.startMonthsChoice
        model.currentEndMonth = model.endMonthsChoice
        model.currentTherapist = model.therapistsChoice
        model.currentReportType = model.reportTypesChoice

        // disable the render button
        view.disableRenderReportButton()
    }

    /**
     * Generates a human-friendly message that indicates the time range & filter criteria
     * that the current report represents
     */
    String generateReportMessage() {
        String message = "Displaying ${model.reportTypesChoice} for "

        if(model.therapistsChoice == BrowseReportsModel.ALL) {
            message += 'all therapists'
        } else {
            message += model.therapistsChoice
        }

        message += " from ${model.startMonthsChoice} ${model.yearsChoice} - ${getEndMonthChoiceMonth()} ${getEndMonthChoiceYear()}"
        //'Displaying Insurance Breakdown for All therapists from Jan 2019 - Dec 2019'

        message
    }

    /**
     * Makes the necessary calls into the ReportService to build the CountAndPercentReport
     * for the currently selected date range & filter criteria
     */
    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    CountAndPercentReport getReport() {
        // extract the current start & end months as
        LocalDate startMonth = LocalDate.of(Integer.valueOf(model.yearsChoice), Month.valueOf(model.startMonthsChoice).value, 1)
        LocalDate endMonth = LocalDate.of(getEndMonthChoiceYear(), getEndMonthChoiceMonth().value, 1)

        log.debug "getReport() - startMonth=${startMonth}"
        log.debug "getReport() - endMonth=${endMonth}"


        List<Report> reports = reportService.getReportHeadersBetweenDateRange(startMonth, endMonth)
        reportService.loadMetrics(reports)

        log.debug "getReport() - therapistsChoice=${model.therapistsChoice}"
        log.debug "getReport() - reportTypesChoice=${model.reportTypesChoice}"

        Employee therapist = null
        if(model.therapistsChoice != BrowseReportsModel.ALL) {
            therapist = employeeService.findByFullname(model.therapistsChoice)
        }

        log.debug "getReport() - therapist=${therapist}"


        ReportType reportType = ReportType.byLabel(model.reportTypesChoice)
        log.debug "getReport() - reportType=${reportType}"

        reportService.produceReportType(reports, therapist, reportType)
    }

    /**
     * Extracts & returns the year portion from the "end months choice" drop-down value
     */
    int getEndMonthChoiceYear() {
        Integer.valueOf(model.endMonthsChoice.substring(0, model.endMonthsChoice.indexOf(' ')))
    }

    /**
     * Extracts & returns the month portion from the "end months choice" drop-down value
     */
    Month getEndMonthChoiceMonth() {
        Month.valueOf(model.endMonthsChoice.substring(model.endMonthsChoice.indexOf(' ') + 1))
    }

    /**
     * Prepares the "start month" & "end month" drop-down lists,
     * and pre-selects the chosen values for each list
     */
    void populateMonthOptions() {
        prepareYears()
        prepareStartMonths()
        setDatesForPreset(TimeRangePreset.byLabel(model.presetsChoice))

    }

    void prepareYears() {
        model.years.clear()
        model.years.addAll(model.allYears.collect { String.valueOf(it) })
    }

    void prepareStartMonths() {
        model.startMonths.clear()
        Month.values().each {
            model.startMonths << it.name()
        }
    }

    void prepareEndMonths(Month firstMonth, int year, boolean rollOverYear) {
        model.endMonths.clear()
        int endIndex = rollOverYear ? firstMonth.value + 11 : 12

        for(int i = firstMonth.value; i <= endIndex; i++) {
            if(i < 13) {
                model.endMonths << "${year} ${Month.of(i).name()}".toString()
            } else {
                model.endMonths << "${(year+1)} ${Month.of(i - 12).name()}".toString()
            }
        }
    }


    void setDatesForPreset(TimeRangePreset preset) {
        switch(preset) {
            case TimeRangePreset.LAST_MONTH:
                view.disableTimeRangeElements()
                LocalDate now = LocalDate.now()
                int startYear = model.currentYearExceptIfJanuary
                if(now.month.value < 1) {
                    println "//TODO - build this!!"
                } else {
                    model.yearsChoice = String.valueOf(model.currentYearExceptIfJanuary)
                    model.startMonthsChoice = now.minus(1, ChronoUnit.MONTHS).month.name()

                    prepareEndMonths(now.minus(1, ChronoUnit.MONTHS).month, startYear, false)
                    model.endMonthsChoice = model.endMonths.get(0).toString()
                }
                break

            case TimeRangePreset.LAST_3_MONTHS:
                view.disableTimeRangeElements()
                LocalDate now = LocalDate.now()
                int startYear = model.currentYearExceptIfJanuary
                if(now.month.value < 3) {
                    println "//TODO - build this!!"
                } else {
                    model.yearsChoice = String.valueOf(model.currentYearExceptIfJanuary)
                    model.startMonthsChoice = now.minus(3, ChronoUnit.MONTHS).month.name()

                    prepareEndMonths(now.minus(3, ChronoUnit.MONTHS).month, startYear, false)
                    model.endMonthsChoice = model.endMonths.get(2).toString()
                }
                break

            case TimeRangePreset.THIS_YEAR:
                view.disableTimeRangeElements()
                model.yearsChoice = String.valueOf(model.currentYearExceptIfJanuary)
                model.startMonthsChoice = Month.JANUARY.name()
                prepareEndMonths(Month.JANUARY, model.currentYearExceptIfJanuary, false)
                model.endMonthsChoice = model.endMonths.get(model.endMonths.size() - 1).toString()
                break

            case TimeRangePreset.LAST_YEAR:
                view.disableTimeRangeElements()
                int yearIndex = model.allYears.indexOf(model.currentYearExceptIfJanuary)
                boolean rollOverYear = false
                if(yearIndex > 0) {
                    yearIndex--
                    rollOverYear = true
                }
                model.yearsChoice = String.valueOf(model.allYears[yearIndex])
                model.startMonthsChoice = Month.JANUARY.name()
                prepareEndMonths(Month.JANUARY, model.allYears[yearIndex], rollOverYear)
                model.endMonthsChoice = model.endMonths.get(model.endMonths.size() - (rollOverYear ? 1 : 1)).toString()
                break

            case TimeRangePreset.CUSTOM:
                view.enableTimeRangeElements()
                break
        }
    }




    void populateTherapists() {
        model.therapists.clear()
        model.therapists << BrowseReportsModel.ALL
        employeeService.findByRoleExplicit(EmployeeRole.THERAPIST).each {
            model.therapists << it.fullname
        }
        model.therapistsChoice = BrowseReportsModel.ALL
    }


    /**
     * called when the drop-down for "year" is changed
     */
    void changeYear() {
        log.info "changeYear() - preparingForm=${preparingForm}"
        if(!preparingForm) {
            checkForCriteriaChange()
        }
    }

    /**
     * called when the drop-down for "start month" is changed
     */
    void changeStartMonth() {
        log.info "changeStartMonth() - preparingForm=${preparingForm}"
        if(!preparingForm) {

            int yearIndex = model.allYears.indexOf(Integer.valueOf(model.yearsChoice))
            boolean rollOverYear = false
            if(yearIndex < model.allYears.size() - 1) {
                rollOverYear = true
            }

            prepareEndMonths( Month.values()[Month.valueOf(model.startMonthsChoice).value],
                    Integer.valueOf(model.yearsChoice), rollOverYear
            )

            checkForCriteriaChange()
        }
    }

    /**
     * called when the drop-down for "end month" is changed
     */
    void changeEndMonth() {
        log.info "changeEndMonth() - preparingForm=${preparingForm}"

        if(!preparingForm) {
            checkForCriteriaChange()
        }
    }

    /**
     * called when the drop-down for "presets" is changed
     */
    void changePreset() {
        log.info "changePreset() - preparingForm=${preparingForm}"
        if(!preparingForm) {
            setDatesForPreset(TimeRangePreset.byLabel(model.presetsChoice))
            checkForCriteriaChange()
        }
    }

    /**
     * called when the drop-down for "therapist" is changed
     */
    void changeTherapist() {
        log.info "changeTherapist() - preparingForm=${preparingForm}"

        if(!preparingForm) {
            checkForCriteriaChange()
        }
    }

    /**
     * called when the drop-down for "report type" is changed
     */
    void changeReportType() {
        log.info "changeReportType() - preparingForm=${preparingForm}"

        if(!preparingForm) {
            checkForCriteriaChange()
        }
    }

    /**
     * Checks the current report criteria (date range preset / custom values, therapist, report type)
     * against the values selected in the drop downs; if any values are found, then the 'Go!' button
     * is enabled; otherwise it is disabled.
     */
    void checkForCriteriaChange() {
        boolean changesFound = false

        // start easy - check for differences between therapist, report type & preset
        if(model.therapistsChoice != model.currentTherapist ||
                model.reportTypesChoice != model.currentReportType ||
                model.presetsChoice != model.currentPreset)
        {
            changesFound = true
        }

        // if no changes found yet, but the preset choice is "custom", then compare dates
        if(!changesFound && model.presetsChoice == TimeRangePreset.CUSTOM.label) {
            if(model.yearsChoice != model.currentYear ||
                    model.startMonthsChoice != model.currentStartMonth ||
                    model.endMonthsChoice != model.currentEndMonth)
            {
                changesFound = true
            }
        }

        // set the state of the render button as appropriate
        if(changesFound) {
            view.enableRenderReportButton()
        } else {
            view.disableRenderReportButton()
        }
    }


    /**
     * Called when the user clicks the "Go!" button (to render the report)
     */
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void renderReport() {
        getAndRenderReport()
    }

}
