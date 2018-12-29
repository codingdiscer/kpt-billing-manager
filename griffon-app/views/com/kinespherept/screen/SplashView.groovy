package com.kinespherept.screen

import com.kinespherept.BaseView
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.SpringConfig
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import groovy.util.logging.Slf4j
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.stage.Stage

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
@Slf4j
class SplashView extends BaseView {

    @MVCMember @Nonnull SplashController controller
    @MVCMember @Nonnull SplashModel model

    @FXML Button getStartedButton

    @PostConstruct
    void init() {
        baseInit(this)
    }

    void initUI() {
        baseInitUI(controller, model)

        // prepare the application stage and make all the necessary connections
        Stage stage = (Stage) getApplication().createApplicationContainer(Collections.<String, Object> emptyMap())
        getApplication().getWindowManager().attach("mainWindow", stage)

        SceneManager manager = SceneManager.getManager()

        manager.setMainStage(stage)

        // and then check out what we've built!
        manager.addScene(SceneDefinition.SPLASH, scene)
        manager.changeScene(SceneDefinition.SPLASH)

        // update the text label on the button
        getStartedButton.text = 'Get Started'
    }

}