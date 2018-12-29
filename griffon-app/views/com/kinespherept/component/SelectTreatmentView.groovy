package com.kinespherept.component

import com.kinespherept.BaseView
import com.kinespherept.model.core.Treatment
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class SelectTreatmentView extends BaseView {

    @MVCMember @Nonnull SelectTreatmentController controller
    @MVCMember @Nonnull SelectTreatmentModel model

    // the container for the FlowPane objects created with the buildTreatmentRow() method
    @FXML VBox treatmentRows

    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }

    /**
     * Builds a FlowPane object with 4 UI elements that represents a row for a single treatment option
     * - Label (treatment name)
     * - Button (add a treatment qty)
     * - Button (subtract a treatment qty)
     * - Label (treatment qty)
     */
    FlowPane buildTreatmentRow(Treatment treatment, int treatmentQty) {
        // build the flow pane and the 4 elements
        FlowPane flowPane = new FlowPane(hgap: 3.0, prefHeight: 25.0)
        Label nameLabel = new Label(text: treatment.displayableName, prefWidth: 225)
        Label quantityLabel = new Label(text: String.valueOf(treatmentQty), id: String.valueOf(treatment.treatmentId))
        Button addButton = new Button(text: '+', onAction: { a -> controller.addTreatment(treatment, quantityLabel) })
        Button subtractButton = new Button(text: '-', onAction: { a -> controller.subtractTreatment(treatment, quantityLabel) })

        // add all the children to the flow pane
        flowPane.children.addAll(nameLabel, addButton, subtractButton, quantityLabel)
        flowPane
    }

    void prepareView(AnchorPane anchorPane) {
        log.debug "prepareView() :: anchorPane=${anchorPane} for scene=${SceneDefinition.TREATMENT_SELECTOR}"

        // here's a hack (?) that seems necessary until further notice.  the problem is that one screen
        // wasn't loading with this scene after a first scene was loaded.  so, this hack reloads this view
        // component, and clears out the children of the given AnchorPane.
        if(sceneManager.getScene(SceneDefinition.TREATMENT_SELECTOR)?.root?.childrenUnmodifiable.size() == 0) {
            log.warn "TREATMENT_SELECTOR scene has no children, calling initUI() again..."
            initUI()
            anchorPane.children.clear()
        }

        // embed the diagnosis selector component into the anchor pane
        setSceneInAnchorPane(anchorPane, SceneDefinition.TREATMENT_SELECTOR)
    }


    void initUI() {
        log.debug("initUI()")
        baseInitUI(controller, model)

        sceneManager.addScene(SceneDefinition.TREATMENT_SELECTOR, scene)
    }


}