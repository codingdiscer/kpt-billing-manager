import com.kinespherept.Launcher
import com.kinespherept.config.SpringConfig
import griffon.core.GriffonApplication
import groovy.util.logging.Slf4j
import org.codehaus.griffon.runtime.core.AbstractLifecycleHandler
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.Environment

import javax.annotation.Nonnull
import javax.inject.Inject

@Slf4j
class Initialize extends AbstractLifecycleHandler {

    @Inject
    Initialize(@Nonnull GriffonApplication application) {
        super(application)
    }


    @Override
    void execute() {

//        log.info "Initialize.execute() :: about to start the ApplicationContext"
//        ApplicationContext applicationContext = new AnnotationConfigApplicationContext('com.kinespherept')
//        Environment environment = applicationContext.getEnvironment()
//        //environment.setActiveProfiles('local')
//
//        //-Dspring.datasource.username=postgres
//        //-Dspring.datasource.password=postgres
//
//        //environment.systemProperties['spring.datasource.username'] = Launcher.username
//        //environment.systemProperties['spring.datasource.password'] = Launcher.password
//
//        println "Initialize.execute() : environment.systemProperties['spring.datasource.username']= ${environment.systemProperties['spring.datasource.username']}"
//        println "Initialize.execute() : environment.systemProperties['spring.datasource.password']= ${environment.systemProperties['spring.datasource.password']}"
//
//        applicationContext.setEnvironment(environment)
//        log.info "Initialize.execute() :: environment=${environment}"
//
//        SpringConfig.applicationContext = applicationContext
    }
}