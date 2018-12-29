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
import javafx.scene.control.ListView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class SelectPatientVisitView extends BaseView {

    @MVCMember @Nonnull SelectPatientVisitController controller
    @MVCMember @Nonnull SelectPatientVisitModel model

    @SpringAutowire CommonProperties commonProperties

    // holds the list of visits for the therapist on the chosen day
    @FXML ListView<String> visitors

    // the root pane to set the background on
    @FXML AnchorPane rootAnchorPane

    // panes that hold other components
    @FXML AnchorPane navigationPane


    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    void initUI() {
        log.debug "initUI()"
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.SELECT_PATIENT_VISIT, scene)
    }

    @PostSpringConstruct
    void initAfterSpring() {
        rootAnchorPane.style = commonProperties.therapistBackground
    }

}
