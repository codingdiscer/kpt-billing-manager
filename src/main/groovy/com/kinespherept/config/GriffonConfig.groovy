package com.kinespherept.config

import com.kinespherept.autowire.GriffonAutowire
import griffon.core.GriffonApplication
import groovy.util.logging.Slf4j
import org.springframework.util.StringUtils

import java.lang.reflect.Field

@Slf4j
class GriffonConfig {

    // a reference to the application that runs everything
    static GriffonApplication griffonApplication

    // a list of MVC artifacts (GriffonMvcArtifact) that require auto-wiring...just not yet...
    //static List<Object> autowireQueue = []
    static Set<Object> autowireQueue = []

    /**
     * adds the given MVC artifact to the list of objects that require autowiring
     */
    static void queueForAutowire(Object thisObject) {
        autowireQueue << thisObject
    }

    /**
     * performs the work of auto-wiring the components together
     */
    static void performAutowire() {
        autowireQueue.each({ autowire(it) })
    }


    private static void autowire(Object thisObject) {
        List<Field> fields = SpringConfig.getAllDeclaredFields(thisObject.class)
        fields.each { Field field ->
            if(field.getAnnotation(GriffonAutowire)) {

                String shortName = null


                GriffonAutowire ga = field.getAnnotation(GriffonAutowire)
                if(!StringUtils.isEmpty(ga.mvcGroupName())) {
                    log.info "will autowire a Griffon component for mvcGroupName =[${ga.mvcGroupName()}]"
                    shortName = ga.mvcGroupName()
                }

                if(field.name.endsWith('Controller')) {
                    shortName = shortName ? shortName : field.name.replace('Controller', '')
                    Object component = griffonApplication.mvcGroupManager.getController(shortName, field.type)
                    if(component) {
                        thisObject.class.getMethod(SpringConfig.fieldSetterName(field), field.type).invoke(thisObject, component)
                    } else {
                        log.error("Unable to find controller with class [${field.type}] and name [${shortName}] in the GriffonApplication.mvcGroupManager")
                    }
                } else if(field.name.endsWith('View')) {
                    shortName = shortName ? shortName : field.name.replace('View', '')
                    Object component = griffonApplication.mvcGroupManager.getView(shortName, field.type)
                    if(component) {
                        thisObject.class.getMethod(SpringConfig.fieldSetterName(field), field.type).invoke(thisObject, component)
                    } else {
                        log.error("Unable to find view with class [${field.type}] and name [${shortName}] in the GriffonApplication.mvcGroupManager")
                    }
                } else if(field.name.endsWith('Model')) {
                    shortName = shortName ? shortName : field.name.replace('Model', '')
                    Object component = griffonApplication.mvcGroupManager.getModel(shortName, field.type)
                    if(component) {
                        thisObject.class.getMethod(SpringConfig.fieldSetterName(field), field.type).invoke(thisObject, component)
                    } else {
                        log.error("Unable to find model with class [${field.type}] and name [${shortName}] in the GriffonApplication.mvcGroupManager")
                    }
                }
            }
        }
    }


}
