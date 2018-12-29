package com.kinespherept.autowire

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target([ElementType.FIELD])
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface GriffonAutowire {

    String mvcGroupName() default ''
}
