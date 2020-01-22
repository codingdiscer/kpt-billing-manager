package com.kinespherept.enums

/**
 * Represents the different types of reports available for browsing.
 */
enum ReportType {

    INSURANCE_TYPES_SIMPLE('Insurance breakdown - simple'),
    INSURANCE_TYPES_BY_TREATMENT('Insurance breakdown - by treatments'),
    PATIENT_TYPES('Patient types'),
    VISIT_TYPES('Visit types')

    String label

    private ReportType(String label) {
        this.label = label
    }

    static ReportType byLabel(String label) {
        ReportType rt = null
        values().each {
            if(it.label == label) {
                rt = it
            }
        }
        rt
    }

}
