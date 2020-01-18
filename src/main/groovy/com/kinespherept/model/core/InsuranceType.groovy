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

    // magic strings - unfortunate hard-coding of insurance type names
    static String UNITED_HEALTHCARE = 'UHC'
    static String UNITED_HEALTHCARE_UMR = 'UHC-UMR'


    @Id @Column(name='insurance_type_id') Integer insuranceTypeId
    @Column(name='insurance_type_name') String insuranceTypeName
    @Column(name='insurance_type_shorthand') String insuranceTypeShorthand
}
