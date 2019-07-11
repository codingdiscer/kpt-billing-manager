package com.kinespherept.model.core

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import java.time.LocalDateTime


@Entity
@Table(name='visit_status_change')
class VisitStatusChange {

    @Id @Column(name='visit_status_change_id') @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long visitStatusChangeId

    @Column(name='visit_status') @Enumerated(EnumType.STRING) VisitStatus visitStatus
    @Column(name='visit_id') Long visitId
    @Column(name='changed') LocalDateTime changed
    @Column(name='employee_id') Integer employeeId

}
