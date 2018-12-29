package com.kinespherept.config

import com.google.common.base.CaseFormat
import com.kinespherept.autowire.PostSpringConstruct
import com.kinespherept.autowire.SpringAutowire
import griffon.javafx.JavaFXGriffonApplication
import groovy.util.logging.Slf4j
import org.springframework.context.ApplicationContext

import java.lang.reflect.Field
import java.lang.reflect.Method

@Slf4j
class SpringConfig {

    static ApplicationContext applicationContext


    static void autowire(Object thisObject) {
        //applicationContext.getAutowireCapableBeanFactory().autowireBean()
        if(applicationContext) {
            List<Field> fields = getAllDeclaredFields(thisObject.class)
            fields.each { Field field ->
                if (field.getAnnotation(SpringAutowire)) {
                    // see if there is a component in the appContext with this class
                    Object component = applicationContext.getBean(field.type)
                    if (component) {
                        thisObject.class.getMethod(fieldSetterName(field), field.type).invoke(thisObject, component)
                    } else {
                        log.error("Unable to find component with class [${field.type}] in the Spring ApplicationContext")
                    }
                }
            }
        }
    }

    /**
     * Convenience method that builds a setter method name starting from a field definition.
     * Basically, just put "get" in front of the field name (though deal with capitalization as well)
     */
    static String fieldSetterName(Field field) {
        CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, 'set-' +
                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field.name)
        )
    }

    /**
     * Returns a list of {@link Field} objects that represent all the explicitly declared fields in the
     * given class, including super-classes.
     * @param clazz The class to reflectively interrogate
     */
    static List<Field> getAllDeclaredFields(Class clazz) {
        // the list of fields to return
        List<Field> fields = []
        // add all the fields directly declared in this class
        fields.addAll(clazz.declaredFields)

        // see if the superclass of the given class is in the same package
        if(clazz.package.name.startsWith(clazz.superclass.package.name)) {
            fields.addAll(getAllDeclaredFields(clazz.superclass))
        }
        fields
    }


    static void callPostSpringConstruct(Object thisObject) {
        Method method = getPostSpringConstructMethod(thisObject.class)
        if(method) {
            method.invoke(thisObject, null)
        }
    }


    static Method getPostSpringConstructMethod(Class clazz) {
        Method method = null
        clazz.getDeclaredMethods().each {
            if(it.getAnnotation(PostSpringConstruct)) {
                method = it
                return  // breaks out of the .each{}
            }
        }
        method
    }


}
