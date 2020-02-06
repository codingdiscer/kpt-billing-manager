package com.kinespherept.screen.report

import com.kinespherept.enums.TimeRangePreset
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.Month

class BrowseReportsControllerSpec extends Specification {

    // the class under test
    BrowseReportsController controller

    // supporting components
    BrowseReportsModel model
    BrowseReportsView view

    static {
        // force initialization JavaFX Toolkit
        new javafx.embed.swing.JFXPanel()
    }


    void setup() {
        model = Spy()
        view = Spy()
        controller = Spy()
        controller.model = model
        controller.view = view
                //new BrowseReportsController(model: model, view: view, preparingForm: true)
        controller.preparingForm = true
        model.controller = controller

    }


    @Unroll
    def 'test setDatesForPreset() for LAST_MONTH - #nowMonth'() {
        when:
        model.currentYearExceptIfJanuary = 2020
        controller.setDatesForPreset(TimeRangePreset.LAST_MONTH)

        then:
        1 * view.disableTimeRangeElements() >> { }
        1 * controller.getNow() >> LocalDate.of(nowYear, nowMonth, 1)
        model.yearsChoice == yearsChoice
        model.startMonthsChoice == startMonthChoice
        model.endMonthsChoice == endMonthsChoice

        where:
        nowYear | nowMonth          | yearsChoice   | startMonthChoice  | endMonthsChoice
        2020    | Month.FEBRUARY    | '2020'        | 'JANUARY'         | '2020 JANUARY'
        2020    | Month.MARCH       | '2020'        | 'FEBRUARY'        | '2020 FEBRUARY'
        2020    | Month.APRIL       | '2020'        | 'MARCH'           | '2020 MARCH'
        2020    | Month.MAY         | '2020'        | 'APRIL'           | '2020 APRIL'
        2020    | Month.JUNE        | '2020'        | 'MAY'             | '2020 MAY'
        2020    | Month.JULY        | '2020'        | 'JUNE'            | '2020 JUNE'
        2020    | Month.AUGUST      | '2020'        | 'JULY'            | '2020 JULY'
        2020    | Month.SEPTEMBER   | '2020'        | 'AUGUST'          | '2020 AUGUST'
        2020    | Month.OCTOBER     | '2020'        | 'SEPTEMBER'       | '2020 SEPTEMBER'
        2020    | Month.NOVEMBER    | '2020'        | 'OCTOBER'         | '2020 OCTOBER'
        2020    | Month.DECEMBER    | '2020'        | 'NOVEMBER'        | '2020 NOVEMBER'
        2021    | Month.JANUARY     | '2020'        | 'DECEMBER'        | '2020 DECEMBER'
    }

    @Unroll
    def 'test setDatesForPreset() for LAST_3_MONTHS - #nowMonth'() {
        when:
        model.currentYearExceptIfJanuary = 2020
        controller.setDatesForPreset(TimeRangePreset.LAST_3_MONTHS)

        then:
        1 * view.disableTimeRangeElements() >> { }
        1 * controller.getNow() >> LocalDate.of(nowYear, nowMonth, 1)
        model.yearsChoice == yearsChoice
        model.startMonthsChoice == startMonthChoice
        model.endMonthsChoice == endMonthsChoice

        where:
        nowYear | nowMonth          | yearsChoice   | startMonthChoice  | endMonthsChoice
        2020    | Month.FEBRUARY    | '2019'        | 'NOVEMBER'        | '2020 JANUARY'
        2020    | Month.MARCH       | '2019'        | 'DECEMBER'        | '2020 FEBRUARY'
        2020    | Month.APRIL       | '2020'        | 'JANUARY'         | '2020 MARCH'
        2020    | Month.MAY         | '2020'        | 'FEBRUARY'        | '2020 APRIL'
        2020    | Month.JUNE        | '2020'        | 'MARCH'           | '2020 MAY'
        2020    | Month.JULY        | '2020'        | 'APRIL'           | '2020 JUNE'
        2020    | Month.AUGUST      | '2020'        | 'MAY'             | '2020 JULY'
        2020    | Month.SEPTEMBER   | '2020'        | 'JUNE'            | '2020 AUGUST'
        2020    | Month.OCTOBER     | '2020'        | 'JULY'            | '2020 SEPTEMBER'
        2020    | Month.NOVEMBER    | '2020'        | 'AUGUST'          | '2020 OCTOBER'
        2020    | Month.DECEMBER    | '2020'        | 'SEPTEMBER'       | '2020 NOVEMBER'
        2021    | Month.JANUARY     | '2020'        | 'OCTOBER'         | '2020 DECEMBER'
    }

}
