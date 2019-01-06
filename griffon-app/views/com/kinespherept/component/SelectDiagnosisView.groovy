package com.kinespherept.component

import com.kinespherept.BaseView
import com.kinespherept.model.core.Diagnosis
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import groovy.util.logging.Slf4j
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
@Slf4j
class SelectDiagnosisView extends BaseView {


    @MVCMember @Nonnull SelectDiagnosisController controller
    @MVCMember @Nonnull SelectDiagnosisModel model

    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    Label buildDiagnosisTypeLabel(Diagnosis diagnosis) {
        new Label(text: diagnosis.getTypeForDisplay(),
                prefWidth: 250.0, prefHeight: 20,
                font:  new Font(12),
                textAlignment: TextAlignment.LEFT,
                style: '-fx-background-color: #6ac4f1'
        )
    }


    Button buildDiagnosisSearchResult(Diagnosis diagnosis) {
        new Button(id: diagnosis.diagnosisId,
                text: diagnosis.getNameForDisplay(),
                onAction: { a -> controller.selectDiagnosis(diagnosis) },
                prefWidth: 250.0, prefHeight: 20,
                font:  new Font(12),
                textAlignment: TextAlignment.LEFT,
                tooltip: new Tooltip(diagnosis.getNameForDisplay())
        )
    }

    Button buildSelectedDiagnosis(Diagnosis diagnosis) {
        Button b = new Button(id: diagnosis.diagnosisId,
                text: diagnosis.getNameForDisplay(),
                prefWidth: 250.0, prefHeight: 20,
                font:  new Font(12),
                textAlignment: TextAlignment.LEFT,
                tooltip: new Tooltip(diagnosis.getNameForDisplay())
        )
        b.onAction = { a -> controller.removeDiagnosis(b) }
        return b
    }



    void prepareView(AnchorPane anchorPane) {
        log.debug "prepareView() :: anchorPane=${anchorPane} for scene=${SceneDefinition.DIAGNOSIS_SELECTOR}"

        // here's a hack (?) that seems necessary until further notice.  the problem is that one screen
        // wasn't loading with this scene after a first scene was loaded.  so, this hack reloads this view
        // component, and clears out the children of the given AnchorPane.
        if(sceneManager.getScene(SceneDefinition.DIAGNOSIS_SELECTOR)?.root?.childrenUnmodifiable.size() == 0) {
            log.warn "DIAGNOSIS_SELECTOR scene has no children, calling initUI() again..."
            initUI()
            anchorPane.children.clear()
        }

        // embed the diagnosis selector component into the anchor pane
        setSceneInAnchorPane(anchorPane, SceneDefinition.DIAGNOSIS_SELECTOR)
        log.debug "prepareView() :: anchorPane.children.size=${anchorPane.children.size()}, children=${anchorPane.children}"
    }


    void initUI() {
        log.debug "initUI()"
        baseInitUI(controller, model)

        sceneManager.addScene(SceneDefinition.DIAGNOSIS_SELECTOR, scene)
    }

}