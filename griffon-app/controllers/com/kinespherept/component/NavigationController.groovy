package com.kinespherept.component

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.NavigationSection
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.EmployeeRole
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.screen.admin.SetupDiagnosesController
import com.kinespherept.screen.patient.AdministerPatientsController
import com.kinespherept.screen.patient.SchedulePatientsController
import com.kinespherept.screen.therapist.SelectPatientVisitController
import com.kinespherept.screen.visitstatus.TrackVisitStatusController
import com.kinespherept.service.RefreshService
import com.kinespherept.session.EmployeeSession
import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import groovy.util.logging.Slf4j
import javafx.scene.layout.AnchorPane

import javax.annotation.Nonnull
import javax.annotation.PostConstruct


@ArtifactProviderFor(GriffonController)
@Slf4j
class NavigationController {

    @MVCMember @Nonnull NavigationModel model
    @MVCMember @Nonnull NavigationView view

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire EmployeeSession employeeSession
    @SpringAutowire RefreshService refreshService

    @GriffonAutowire AdministerPatientsController administerPatientsController
    @GriffonAutowire SchedulePatientsController schedulePatientsController
    @GriffonAutowire SelectPatientVisitController selectPatientVisitController
    @GriffonAutowire SetupDiagnosesController setupDiagnosesController
    @GriffonAutowire TrackVisitStatusController trackVisitStatusController

    @PostConstruct
    void init() {
        log.debug "init()"
        SpringConfig.autowire(this)
    }


    void prepareForm(AnchorPane navigationPane, NavigationSection navSection) {
        log.debug "prepareForm() with navigationPane=${navigationPane} ; ${navigationPane.hashCode()}"

        // do a full reset of the view
        view.initUI()

        // reset the buttons in the nav pane
        view.navigationLinks.children.clear()

        // always start with `refresh` button
        view.navigationLinks.children.add(view.prepareNavigationButton('Refresh', '-fx-background-color: LIGHTGRAY', { a -> refreshService.refreshAllData() }))

        if(employeeSession.employee.roles.contains(EmployeeRole.SCHEDULER)) {
            view.navigationLinks.children.add(view.prepareNavigationButton(
                    'Schedule',
                    modifyStyle(commonProperties.schedulerBackground, NavigationSection.SCHEDULER, navSection),
                    { a ->
                        schedulePatientsController.prepareForm()
                        SceneManager.changeTheScene(SceneDefinition.SCHEDULE_PATIENTS)
                    }
            ))
        }

        if(employeeSession.employee.roles.contains(EmployeeRole.PATIENTS)) {
            view.navigationLinks.children.add(view.prepareNavigationButton(
                    'Patients',
                    modifyStyle(commonProperties.patientsBackground, NavigationSection.PATIENTS, navSection),
                    { a ->
                        administerPatientsController.prepareForm()
                        SceneManager.changeTheScene(SceneDefinition.ADMINISTER_PATIENTS)
                    }
            ))
        }

        if(employeeSession.employee.roles.contains(EmployeeRole.THERAPIST)) {
            view.navigationLinks.children.add(view.prepareNavigationButton(
                    'Therapist',
                    modifyStyle(commonProperties.therapistBackground, NavigationSection.THERAPIST, navSection),
                    { a ->
                        selectPatientVisitController.prepareForm(false)
                        SceneManager.changeTheScene(SceneDefinition.SELECT_PATIENT_VISIT)
                    }
            ))
        }

        if(employeeSession.employee.roles.contains(EmployeeRole.INSURANCE_BILLER)) {
            view.navigationLinks.children.add(view.prepareNavigationButton(
                    'Visit Status Tracker',
                    modifyStyle(commonProperties.statusTrackerBackground, NavigationSection.STATUS_TRACKER, navSection),
                    { a ->
                        trackVisitStatusController.prepareForm()
                        SceneManager.changeTheScene(SceneDefinition.TRACK_VISIT_STATUS)
                    }
            ))
        }

        if(employeeSession.employee.roles.contains(EmployeeRole.DIAGNOSIS_CODER)) {
            view.navigationLinks.children.add(view.prepareNavigationButton(
                    'Manage Diagnoses',
                    modifyStyle(commonProperties.addDiagnosisBackground, NavigationSection.MANAGE_DIAGNOSES, navSection),
                    { a ->
                        setupDiagnosesController.prepareForm()
                        SceneManager.changeTheScene(SceneDefinition.SETUP_DIAGNOSES)
                    }
            ))
        }

        // prepare the navigation pane
        view.prepareForm(navigationPane)
    }

    /**
     * Adds a border around this style element, with the goal of having a way of signifying which
     * navigation element is currently selected.
     */
    String modifyStyle(String baseStyle, NavigationSection modifyIfSection, NavigationSection thisSection) {
        baseStyle + (modifyIfSection == thisSection ? '-fx-border-width: 3; -fx-border-color: DARKGRAY' : '')
    }

}
