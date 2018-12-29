package com.kinespherept.screen.therapist

import com.kinespherept.BaseView
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.scene.layout.FlowPane

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class VerifyPatientVisitNoTreatmentView extends BaseView {

    @MVCMember @Nonnull VerifyPatientVisitNoTreatmentController controller
    @MVCMember @Nonnull VerifyPatientVisitNoTreatmentModel model


    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    void initUI() {
        log.debug "initUI()"
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.VERIFY_PATIENT_VISIT_NO_TREATMENT, scene)
    }

}
