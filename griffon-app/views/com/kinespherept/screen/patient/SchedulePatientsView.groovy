package com.kinespherept.screen.patient

import com.kinespherept.BaseView
import com.kinespherept.autowire.PostSpringConstruct
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.model.core.Patient
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.EmployeeService
import com.kinespherept.service.PatientService
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import groovy.util.logging.Slf4j
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
@Slf4j
class SchedulePatientsView extends BaseView {

    @MVCMember @Nonnull SchedulePatientsController controller
    @MVCMember @Nonnull SchedulePatientsModel model

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire PatientService patientService
    @SpringAutowire EmployeeService employeeService

    @FXML ListView<Button> patientResults
    @FXML ListView<String> visitors

    // panes that hold other components
    @FXML AnchorPane navigationPane



    @FXML AnchorPane rootAnchorPane

    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }





//    void populatePatientResults(List<Patient> patients) {
//        patientResults.items.clear()
//        patients.forEach({ p ->
//            patientResults.items << buildPatientSearchResult(p)})
//    }



    Button buildPatientSearchResult(Patient patient) {
        new Button(id: patient.patientId,
                text: patient.getLastNameFirst(),
                onAction: { a -> controller.selectPatient(patient) },
                prefWidth: 150.0, prefHeight: 20,
                font:  new Font(12),
                textAlignment: TextAlignment.LEFT
        )
    }


    void initUI() {
        log.debug "initUI()"

        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.SCHEDULE_PATIENTS, scene)
    }

    @PostSpringConstruct
    void initAfterSpring() {
        rootAnchorPane.style = commonProperties.schedulerBackground
    }


}