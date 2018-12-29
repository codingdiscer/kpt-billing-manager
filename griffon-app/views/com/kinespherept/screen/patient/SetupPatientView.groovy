package com.kinespherept.screen.patient

import com.kinespherept.BaseView
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import groovy.util.logging.Slf4j
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
@Slf4j
class SetupPatientView extends BaseView {

    @MVCMember @Nonnull SetupPatientController controller
    @MVCMember @Nonnull SetupPatientModel model

    @SpringAutowire CommonProperties commonProperties

    @FXML Button mutateButton
    @FXML AnchorPane rootAnchorPane


    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }

    void initUI() {
        log.info "initUI()"
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.SETUP_PATIENT, scene)
    }
}