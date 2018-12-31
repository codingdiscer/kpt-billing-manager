package com.kinespherept.screen

import com.kinespherept.config.GriffonConfig
import com.kinespherept.screen.patient.SchedulePatientsController
import com.kinespherept.screen.patient.SetupPatientController
import com.kinespherept.screen.therapist.FillOutPatientVisitNoTreatmentController
import com.kinespherept.screen.therapist.SelectPatientVisitController
import com.kinespherept.screen.visitstatus.TrackVisitStatusController
import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.SpringConfig
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Employee
import com.kinespherept.model.core.EmployeeLogin
import com.kinespherept.model.core.EmployeeRole
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.EmployeeService
import com.kinespherept.session.EmployeeSession
import griffon.core.artifact.GriffonController
import griffon.core.mvc.MVCGroup
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.Environment
import org.springframework.util.StringUtils

import javax.annotation.Nonnull
import javax.annotation.PostConstruct


/**
 * Handles actions from the login page
 */
@ArtifactProviderFor(GriffonController)
@Slf4j
class LoginController {

    static String DATABASE_URL_PROPERTY = 'spring.datasource.url'

    @MVCMember @Nonnull LoginModel model
    @MVCMember @Nonnull LoginView view

    @SpringAutowire EmployeeService employeeService
    @SpringAutowire EmployeeSession employeeSession

    @GriffonAutowire SchedulePatientsController schedulePatientsController
    @GriffonAutowire SelectPatientVisitController selectPatientVisitController

    // temp for testing
    @GriffonAutowire TrackVisitStatusController trackVisitStatusController


    String databaseUrl

    @PostConstruct
    void init() {
        SpringConfig.autowire(this)
    }


    void prepareForm() {
        if(!System.properties[(DATABASE_URL_PROPERTY)]) {
            model.message = 'WARNING - database url not supplied.'
            view.disableLogin()
        } else {
            databaseUrl = System.properties[(DATABASE_URL_PROPERTY)]
        }
    }

    /**
     * Attempts to perform the login.  Goes through these steps:
     * - tries to connect to the db with the given user/pass
     *   - if not successful, log an error and return immediately
     * - assuming user/pass were successful, it starts up the Spring context
     * - perform the login steps (set the session and log it)
     * - change scene to the most appropriate scene for the user
     */
    void go() {

        // clear the message first, then continue
        runInsideUISync {
            model.setMessage('')
        }

        // make sure a user & pass are entered
        if(StringUtils.isEmpty(model.getUsername()) || StringUtils.isEmpty(model.getPassword())) {
            runInsideUISync {
                model.setMessage('Enter a username and password to proceed.')
            }
            return
        }

        runInsideUISync {
            model.setMessage("Ok, trying as [${model.getUsername()}]")
        }

        if(successfulLogin()) {
            runInsideUISync {
                model.setMessage("Login successful!  We'll get started in a moment (~20 seconds)")
                view.viewProgressIndicator()
            }


            // start up spring
            startSpring()

            // hook up griffon mvc to the spring components
            performSpringAutowire()

            Employee employee = employeeService.findByUsername(model.getUsername())

            if(employee) {
                performLogin(employee)
            } else {
                // this block should NOT run - why would someone be able to login, but doesn't have an employee profile?
                runInsideUISync {
                    model.setMessage("Something doesn't seem right.  Try again.")
                }
            }

        } else {
            runInsideUISync {
                model.setMessage('Nope.')
            }
        }
    }


    void performSpringAutowire() {
        // first autowire all the spring components into the griffon MVC artifacts..
        GriffonConfig.griffonApplication.mvcGroupManager.groups.each{ String groupName, MVCGroup group ->
                SpringConfig.autowire(group.controller)
                SpringConfig.autowire(group.model)
                SpringConfig.autowire(group.view)
        }

        // then call any methods annotated with @PostSpringConstruct
        GriffonConfig.griffonApplication.mvcGroupManager.groups.each{ String groupName, MVCGroup group ->
                SpringConfig.callPostSpringConstruct(group.controller)
                SpringConfig.callPostSpringConstruct(group.model)
                SpringConfig.callPostSpringConstruct(group.view)
        }
    }


    boolean successfulLogin() {
        // assume it worked until we see it didn't...
        boolean success = true
        try {
            Sql sql = Sql.newInstance(databaseUrl,
                    model.getUsername(), model.getPassword(),
                    'org.postgresql.Driver')
            sql.close()
        } catch(Exception e) {
            // any exception means total failure...
            success = false
        }
        success
    }

    void startSpring() {
        log.info "startSpring() :: about to start the ApplicationContext"

        // hard-code postgres as the db
        System.properties['spring.jpa.database-platform'] = 'org.hibernate.dialect.PostgreSQLDialect'
        System.properties['spring.datasource.driver-class-name'] = 'org.postgresql.Driver'
        System.properties[DATABASE_URL_PROPERTY] = databaseUrl
        // pass in the creds for the db
        System.properties['spring.datasource.username'] = model.username
        System.properties['spring.datasource.password'] = model.password

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext('com.kinespherept')
        Environment environment = applicationContext.getEnvironment()


        applicationContext.setEnvironment(environment)
        //log.debug "startSpring() :: environment=${environment}"

        SpringConfig.applicationContext = applicationContext
    }




    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void quickLogin() {

        Employee employee = employeeService.findByFullname(model.employeesChoice)

        log.info "quickLogin() :: selected employee=${employee}"

        performLogin(employee)
    }


    //@Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void performLogin(Employee employee) {
        // save a login entry
        EmployeeLogin login = employeeService.trackLogin(employee)

        // prepare the session
        employeeSession.employee = employee
        employeeSession.loggedInTime = login.loginTime
        log.info "performLogin() :: employee=${employee}"
        log.info "performLogin() :: employeeSession=${employeeSession}"


        runInsideUISync {
            if (employee?.roles.contains(EmployeeRole.SCHEDULER)) {
                schedulePatientsController.prepareForm()
                    SceneManager.changeTheScene(SceneDefinition.SCHEDULE_PATIENTS)
                //SceneManager.getManager().changeScene(SceneDefinition.SCHEDULE_PATIENTS)
            } else if (employee?.roles.contains(EmployeeRole.INSURANCE_BILLER)) {
                trackVisitStatusController.prepareForm()
                SceneManager.changeTheScene(SceneDefinition.TRACK_VISIT_STATUS)

            } else if (employee?.roles.contains(EmployeeRole.THERAPIST)) {
                selectPatientVisitController.prepareForm()
                // change to the therapist's daily schedule
                SceneManager.changeTheScene(SceneDefinition.SELECT_PATIENT_VISIT)
            } else {
                log.info "performLogin() :: no login logic implemented for employee ${employee}"
            }
        }

    }


}