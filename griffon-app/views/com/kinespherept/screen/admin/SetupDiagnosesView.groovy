package com.kinespherept.screen.admin

import com.kinespherept.BaseView
import com.kinespherept.autowire.PostSpringConstruct
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class SetupDiagnosesView extends BaseView {

    @MVCMember @Nonnull SetupDiagnosesController controller
    @MVCMember @Nonnull SetupDiagnosesModel model

    @SpringAutowire CommonProperties commonProperties

    // panes that hold other components
    @FXML AnchorPane navigationPane
    @FXML AnchorPane diagnosisSelector

    @FXML AnchorPane rootAnchorPane

    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    void initUI() {
        log.debug "initUI()"
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.SETUP_DIAGNOSES, scene)
    }

    @PostSpringConstruct
    void initAfterSpring() {
        rootAnchorPane.style = commonProperties.addDiagnosisBackground
    }
}
