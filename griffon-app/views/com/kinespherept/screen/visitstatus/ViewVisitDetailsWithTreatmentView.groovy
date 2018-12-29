package com.kinespherept.screen.visitstatus

import com.kinespherept.BaseView
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class ViewVisitDetailsWithTreatmentView extends BaseView {

    @MVCMember @Nonnull ViewVisitDetailsWithTreatmentController controller
    @MVCMember @Nonnull ViewVisitDetailsWithTreatmentModel model

    @FXML VBox diagnosisRows
    @FXML VBox treatmentRows


    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    void initUI() {
        log.debug "initUI()"
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.VIEW_VISIT_DETAILS_WITH_TREATMENT, scene)
    }

    /**
     * Builds a FlowPane object with 2 UI elements that represents a row for a single treatment option
     * - Label (treatment name)
     * - Label (treatment qty)
     */
    FlowPane buildTreatmentRow(String treatmentDisplayableName, int treatmentQty) {
        // build the flow pane and the 4 elements
        FlowPane flowPane = new FlowPane(hgap: 3.0, prefHeight: 25.0)

        // add all the children to the flow pane
        flowPane.children.addAll(
                new Label(text: treatmentDisplayableName, prefWidth: 200),
                new Label(text: String.valueOf(treatmentQty))
        )
        flowPane
    }

    Label buildDiagnosisRow(String diagnosisNameForDisplay) {
        new Label(text: diagnosisNameForDisplay, prefWidth: 200)
    }




}
