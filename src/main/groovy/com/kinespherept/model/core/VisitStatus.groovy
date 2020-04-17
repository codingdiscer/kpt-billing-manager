package com.kinespherept.model.core

/**
 * The full set of statuses that can apply to a visit.
 *
 */
enum VisitStatus {

    VISIT_CREATED           ('Visit created'),
    SEEN_BY_THERAPIST       ('Seen by therapist'),
    PREPARED_FOR_BILLING    ('Prepared for billing'),
    BILLED_TO_INSURANCE     ('Billed to insurance'),
    REMITTANCE_ENTERED      ('Remittance entered'),
    AWAITING_SECONDARY      ('Awaiting secondary'),
    BILL_SENT_TO_PATIENT    ('Bill sent to patient'),
    PAID_IN_FULL            ('Paid in full'),
    REFUND_NEEDED           ('Refund needed'),
    ENTERED_IN_TURBO_PT     ('Entered in TurboPT')

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
