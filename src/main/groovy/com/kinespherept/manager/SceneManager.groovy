package com.kinespherept.manager

import com.kinespherept.model.navigation.SceneDefinition
import griffon.transform.ThreadingAware
import groovy.util.logging.Slf4j
import javafx.scene.Scene
import javafx.stage.Stage
import org.springframework.stereotype.Component

/**
 * This class holds on to all the scenes known to the system.
 */
//@Component
@Slf4j
//@ThreadingAware
class SceneManager {

    static SceneManager manager

    static SceneManager getManager() {
        if(!manager) {
            manager = new SceneManager()
        }
        manager
    }

    static void changeTheScene(SceneDefinition sceneLabel) {
        getManager().changeScene(sceneLabel)
    }


    Map<SceneDefinition, Scene> sceneMap = [:]
    Stage mainStage

    /**
     * Sets the main stage that scene changes will occur against
     */
    void setMainStage(Stage stage) {
        mainStage = stage
    }

    /**
     * Called by a view to add a scene to the manager
     */
    void addScene(SceneDefinition sceneDefinition, Scene scene) {
        sceneMap.put(sceneDefinition, scene)
    }

    /**
     * Changes the scene on the main stage to the given scene
     * @param sceneLabel The label that declares what scene to change to
     */
    void changeScene(SceneDefinition sceneLabel) {

        Scene scene = sceneMap.get(sceneLabel)
        if (scene) {
            mainStage.title = sceneLabel.title
            mainStage.width = sceneLabel.width
            mainStage.height = sceneLabel.height
            mainStage.scene = scene
        } else {
            log.error("Unable to change scene to [${sceneLabel}] :: this should not happen!")
        }
    }

//    void changeSceneInUIThread(SceneDefinition sceneLabel) {
//        runInsideUISync {
//            changeScene(sceneLabel)
//        }
//    }

    Scene getScene(SceneDefinition sceneLabel) {
        if(!sceneMap.get(sceneLabel)) {
            log.error("Unable to return scene for [${sceneLabel}] :: this should not happen!")
            return null
        }
        sceneMap.get(sceneLabel)
    }

}
