package com.kinespherept.screen.report

import com.kinespherept.enums.ReportType
import com.kinespherept.enums.TimeRangePreset
import com.kinespherept.model.report.CountAndPercentReport
import com.kinespherept.model.report.MonthReport
import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.ChangeListener
import griffon.transform.FXObservable
import javafx.beans.property.StringProperty

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonModel)
class BrowseReportsModel {

//    //
//    static String TIME_RANGE_PRESET_LAST_3_MONTHS = 'Insurance breakdown'
//    static String TIME_RANGE_PRESET_THIS_YEAR = 'Patient types'
//    static String TIME_RANGE_PRESET_LAST_YEAR = 'Patient types'
//
//    // report types!
//    static String REPORT_TYPE_INSURANCE_BREAKDOWN = 'Insurance breakdown'
//    static String REPORT_TYPE_PATIENT_TYPES = 'Patient types'


    //static String REPORT_TYPE_EVAL_FOLLOW_UPS = 'Eval / Follow-ups'
    //static String REPORT_TYPE_ = ''

    static String ALL = 'All'


    @MVCMember @Nonnull BrowseReportsController controller


    @FXObservable List<String> years = []
    @FXObservable @ChangeListener('changeYear')
    String yearsChoice = ''
    Closure changeYear = { StringProperty ob, ov, nv -> controller.changeYear() }


    @FXObservable List<String> startMonths = []
    @FXObservable @ChangeListener('changeStartMonth')
    String startMonthsChoice = ''
    Closure changeStartMonth = { StringProperty ob, ov, nv -> controller.changeStartMonth() }


    @FXObservable List<String> endMonths = []
    @FXObservable @ChangeListener('changeEndMonth')
    String endMonthsChoice = ''
    Closure changeEndMonth = { StringProperty ob, ov, nv -> controller.changeEndMonth() }


    @FXObservable List<String> presets = [ TimeRangePreset.LAST_MONTH.label, TimeRangePreset.LAST_3_MONTHS.label,
                                           TimeRangePreset.THIS_YEAR.label, TimeRangePreset.LAST_YEAR.label,
                                           TimeRangePreset.CUSTOM.label ]
    @FXObservable @ChangeListener('changePreset')
    String presetsChoice = TimeRangePreset.LAST_MONTH.label
    Closure changePreset = { StringProperty ob, ov, nv -> controller.changePreset() }


    @FXObservable List<String> therapists = []
    @FXObservable @ChangeListener('changeTherapist')
    String therapistsChoice = ''
    Closure changeTherapist = { StringProperty ob, ov, nv -> controller.changeTherapist() }


    @FXObservable List<String> reportTypes = [ReportType.INSURANCE_TYPES_SIMPLE.label,
                                              ReportType.PATIENT_TYPES.label,
                                              ReportType.VISIT_TYPES.label ]
    @FXObservable @ChangeListener('changeReportType')
    String reportTypesChoice = ReportType.INSURANCE_TYPES_SIMPLE.label
    Closure changeReportType = { StringProperty ob, ov, nv -> controller.changeReportType() }


    @FXObservable
    String reportMessage = ''


    // holds the current start & end month values for comparison
    String currentPreset = ''
    String currentYear = ''
    String currentStartMonth = ''
    String currentEndMonth = ''
    String currentTherapist = ''
    String currentReportType = ''





    // contains the list of reports for the currently selected time range
    List<MonthReport> reports = []

    List<Integer> allYears = []
    int currentYearExceptIfJanuary = 0

    // the report that is currently being displayed
    CountAndPercentReport countAndPercentReport
}
