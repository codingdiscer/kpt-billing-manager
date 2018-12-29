package com.kinespherept.screen.patient

import com.kinespherept.BaseView
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class SetupVisitView extends BaseView {

    @MVCMember @Nonnull SetupVisitController controller
    @MVCMember @Nonnull SetupVisitModel model

    @SpringAutowire CommonProperties commonProperties

    @FXML AnchorPane rootAnchorPane


    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }

    void initUI() {
        baseInitUI(controller, model)

        sceneManager.addScene(SceneDefinition.SETUP_VISIT, scene)
    }
}