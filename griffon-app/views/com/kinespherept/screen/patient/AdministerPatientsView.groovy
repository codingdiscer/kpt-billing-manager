package com.kinespherept.screen.patient

import com.kinespherept.BaseView
import com.kinespherept.autowire.PostSpringConstruct
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.model.core.Patient
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class AdministerPatientsView extends BaseView {

    @MVCMember @Nonnull AdministerPatientsController controller
    @MVCMember @Nonnull AdministerPatientsModel model

    @SpringAutowire CommonProperties commonProperties


    // the list of search results
    @FXML ListView<Button> patientSearchResults

    // buttons that need toggling
    @FXML Button editPatientButton
    @FXML Button setupVisitButton

    // panes that hold other components
    @FXML AnchorPane navigationPane

    @FXML AnchorPane rootAnchorPane



    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    void initUI() {
        log.debug "initUI()"
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.ADMINISTER_PATIENTS, scene)
    }

    @PostSpringConstruct
    void initAfterSpring() {
        rootAnchorPane.style = commonProperties.patientsBackground
    }

    Button buildPatientSearchResult(Patient patient) {
        new Button(id: patient.patientId,
                text: patient.getLastNameFirst(),
                onAction: { a -> controller.selectPatient(patient) },
                prefWidth: 150.0, prefHeight: 20,
                font:  new Font(12),
                textAlignment: TextAlignment.LEFT
        )
    }


    void enableButtons() {
        editPatientButton.disable = false
        setupVisitButton.disable = false
    }

    void disableButtons() {
        editPatientButton.disable = true
        setupVisitButton.disable = true
    }

}
