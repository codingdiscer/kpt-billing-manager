package com.kinespherept.model.report

import com.kinespherept.model.core.Report

/**
 * Represents the data within a row of the "manage reports" scene.
 */
class MonthReport {


    String month
    String status

    boolean futureMonth = false

    Report report
}
