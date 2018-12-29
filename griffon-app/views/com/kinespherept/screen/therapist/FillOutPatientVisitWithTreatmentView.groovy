package com.kinespherept.screen.therapist

import com.kinespherept.BaseView
import com.kinespherept.autowire.PostSpringConstruct
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.layout.AnchorPane
import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class FillOutPatientVisitWithTreatmentView extends BaseView {

    @MVCMember @Nonnull FillOutPatientVisitWithTreatmentController controller
    @MVCMember @Nonnull FillOutPatientVisitWithTreatmentModel model

    @SpringAutowire CommonProperties commonProperties

    // holds the list of visits for the therapist on the chosen day
    @FXML ListView<String> visitors

    // panes that hold other components
    @FXML AnchorPane navigationPane
    @FXML AnchorPane diagnosisSelector
    @FXML AnchorPane treatmentSelector

    @FXML AnchorPane rootAnchorPane

    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    void initUI() {
        log.debug("initUI()")
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_WITH_TREATMENT, scene)
    }

    @PostSpringConstruct
    void initAfterSpring() {
        rootAnchorPane.style = commonProperties.therapistBackground
    }


}