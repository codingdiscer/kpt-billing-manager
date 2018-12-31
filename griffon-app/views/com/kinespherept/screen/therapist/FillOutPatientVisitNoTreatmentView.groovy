package com.kinespherept.screen.therapist

import com.kinespherept.BaseView
import com.kinespherept.autowire.PostSpringConstruct
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.scene.control.ListView
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.AnchorPane

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class FillOutPatientVisitNoTreatmentView extends BaseView {

    @MVCMember @Nonnull FillOutPatientVisitNoTreatmentController controller
    @MVCMember @Nonnull FillOutPatientVisitNoTreatmentModel model

    @SpringAutowire CommonProperties commonProperties

    // holds the list of visits for the therapist on the chosen day
    @FXML ListView<String> visitors

    // panes that hold other components
    @FXML AnchorPane navigationPane

    @FXML AnchorPane rootAnchorPane

    // connections to the "show all" / "show pending" radio button options
    @FXML ToggleGroup visitFilter
    @FXML RadioButton showPending
    @FXML RadioButton showAll

    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    void initUI() {
        log.debug "initUI()"
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.FILL_OUT_PATIENT_VISIT_NO_TREATMENT, scene)
    }

    @PostSpringConstruct
    void initAfterSpring() {
        rootAnchorPane.style = commonProperties.therapistBackground
    }

    /**
     * Sets the radio button toggle for the "show pending" / "show all" options
     */
    void setToggle(boolean showAllVisits) {
        if(showAllVisits) {
            visitFilter.selectToggle(showAll)
        } else {
            visitFilter.selectToggle(showPending)
        }
    }


}
