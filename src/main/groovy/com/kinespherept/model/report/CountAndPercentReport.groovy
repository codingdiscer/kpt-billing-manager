package com.kinespherept.model.report

import com.kinespherept.enums.ReportType
import groovy.transform.Canonical

@Canonical
class CountAndPercentReport {

    ReportType reportType

    List<String> columnTypes = []
    List<CountAndPercentReportRow> rows = []
    CountAndPercentReportRow summaryRow
}
