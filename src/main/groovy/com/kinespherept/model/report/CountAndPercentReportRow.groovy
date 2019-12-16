package com.kinespherept.model.report

import java.time.Month

class CountAndPercentReportRow {

    Month month
    int totalCount = 0

    // key=representation of column type
    // value=CountAndPercentCell that represents the key
    Map<String, CountAndPercentCell> dataMap = [:]

}
