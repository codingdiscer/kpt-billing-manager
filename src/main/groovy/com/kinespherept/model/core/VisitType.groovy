package com.kinespherept.model.core

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * Represents the type of visit that occurred (initial | follow up | cancel/no show)
 */
@Canonical
@Entity
@Table(name='visit_type')
class VisitType {

    @Id @Column(name='visit_type_id')
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Integer visitTypeId


    @Column(name='visit_type_name') String visitTypeName
}
