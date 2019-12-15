package com.kinespherept.component

import com.kinespherept.BaseView
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import groovy.util.logging.Slf4j
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
@Slf4j
class NavigationView extends BaseView {

    static double NAVIGATION_ELEMENT_WIDTH = 100.0
    static double NAVIGATION_ELEMENT_HEIGHT = 55.0


    @MVCMember @Nonnull NavigationController controller
    @MVCMember @Nonnull NavigationModel model

    @FXML FlowPane navigationLinks


    Button prepareNavigationButton(String buttonLabel, String background, Object action) {

        new Button(text: buttonLabel,
                onAction: action,
                prefWidth: NAVIGATION_ELEMENT_WIDTH, prefHeight: NAVIGATION_ELEMENT_HEIGHT,
                font:  new Font(12),
                textAlignment: TextAlignment.CENTER,
                wrapText: true,
                style : background

        )
    }

    void prepareForm(AnchorPane anchorPane) {
        log.debug "prepareForm() :: anchorPane=${anchorPane} ; ${anchorPane.hashCode()} -  for scene=${SceneDefinition.NAVIGATION}"


        // embed the navigation into the anchor pane
        setSceneInAnchorPane(anchorPane, SceneDefinition.NAVIGATION)
    }

    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    void initUI() {
        log.debug "initUI()"
        baseInitUI(controller, model)

        sceneManager.addScene(SceneDefinition.NAVIGATION, scene)
    }

}
