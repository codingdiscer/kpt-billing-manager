package com.kinespherept.model.core

/**
 * Represents the possible statuses of a {@link Report}
 */
enum ReportStatus {
    NEW,            // signifies the report object is created, but nothing has been done to it yet
    REQUESTED,      // the report has been requested by the user
    IN_PROGRESS,    // the metrics for a report are being generated as we speak!
    COMPLETE        // the metrics for a report have been generated
}
