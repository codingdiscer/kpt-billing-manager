package com.kinespherept.model.report

import groovy.transform.Canonical

import java.time.Month

@Canonical
class CountAndPercentReportRow {

    Month month
    int totalCount = 0

    // key=representation of column type
    // value=CountAndPercentCell that represents the key
    Map<String, CountAndPercentCell> dataMap = [:]

}
