package com.kinespherept.screen

import com.kinespherept.BaseView
import com.kinespherept.config.GriffonConfig
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.screen.LoginController
import com.kinespherept.screen.LoginModel
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import groovy.util.logging.Slf4j
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ProgressIndicator

import javax.annotation.Nonnull
import javax.annotation.PostConstruct


@ArtifactProviderFor(GriffonView)
@Slf4j
class LoginView extends BaseView {

    @MVCMember @Nonnull LoginController controller
    @MVCMember @Nonnull LoginModel model

    @FXML ProgressIndicator progressIndicator
    @FXML Button loginButton

    void viewProgressIndicator() {
        progressIndicator.visible = true
    }

    void disableLogin() {
        loginButton.disable = true
    }


    @PostConstruct
    void init() {
        baseInit(this)
    }

    void initUI() {
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.LOGIN, scene)

        // this is the last component, so it will trigger the GriffonAutowire to commence
        //this is needed because of circular references within the components, which means that all components
        // (ie - mvc-groups) are fully loaded because they can be injected into each other
        GriffonConfig.performAutowire()
    }
}