package com.kinespherept.enums

/**
 * Represents the different options on the report detail screen for the different preset time ranges.
 */
enum TimeRangePreset {

    LAST_MONTH('Last month'),
    LAST_3_MONTHS('Last 3 months'),
    THIS_YEAR('This year'),
    LAST_YEAR('Last year'),
    CUSTOM('Custom')


    String label

    private TimeRangePreset(String label) {
        this.label = label
    }

    static TimeRangePreset byLabel(String label) {
        TimeRangePreset trp = null
        values().each {
           if(it.label == label) {
               trp = it
           }
        }
        trp
    }

}
