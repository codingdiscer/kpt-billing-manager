package com.kinespherept.model.core

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Canonical
@Entity
@Table(name='treatment')
class Treatment {

    // hard-code a couple of the treatments so they can be compared in a validation step
    static String TREATMENT_CODE_THERPEUTIC_ACTIVITES = '97530'
    static String TREATMENT_CODE_DRY_NEEDLING_3_OR_MORE = '20561'

    @Id @Column(name='treatment_id') Integer treatmentId
    @Column(name='treatment_code') String treatmentCode
    @Column(name='treatment_name') String treatmentName
    @Column(name='display_order') int displayOrder
    @Column(name='is_evaluation') boolean evaluation

    String getDisplayableName() {
        "(${treatmentCode}) ${treatmentName}"
    }

}
