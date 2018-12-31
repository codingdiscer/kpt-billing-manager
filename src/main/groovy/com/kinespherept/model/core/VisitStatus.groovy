package com.kinespherept.model.core

/**
 * The full set of statuses that can apply to a visit.
 *
 *
 * ** CAUTION **
 * The order of the roles declared below determines their index as stored in the db.
 * Therefore - CHANGING THE ORDER OF THE ENTRIES CHANGES THE ROLES IN THE DATABASE!!
 *
 */
enum VisitStatus {

    /**
     * CAUTION -- CHANGING THE ORDER OF THE ENTRIES CHANGES THE STATUS IN THE DATABASE!!
     * TODO - redesign/refactor to fix the hard-code dependency that creates this situation (??)
     */

    VISIT_CREATED           ('Visit created'),              // id = 0
    SEEN_BY_THERAPIST       ('Seen by therapist'),          // id = 1
    PREPARED_FOR_BILLING    ('Prepared for billing'),       // id = 2
    BILLED_TO_INSURANCE     ('Billed to insurance'),        // id = 3
    REMITTANCE_ENTERED      ('Remittance entered'),         // id = 4
    AWAITING_SECONDARY      ('Awaiting secondary'),         // id = 5
    BILL_SENT_TO_PATIENT    ('Bill sent to patient'),       // id = 6
    PAID_IN_FULL            ('Paid in full')                // id = 7

    // ARCHIVED - ??

    // the friendly label for the enum
    String text

    private VisitStatus(String text) {
        this.text = text
    }


    static VisitStatus findFromText(String text) {
        VisitStatus.values().find { it.text == text }
    }

}
