package com.kinespherept.model.core

import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Canonical
@Entity
@Table(name='insurance_type')
class InsuranceType {
    @Id @Column(name='insurance_type_id') Integer insuranceTypeId
    @Column(name='insurance_type_name') String insuranceTypeName
    @Column(name='insurance_type_shorthand') String insuranceTypeShorthand
}
