package com.kinespherept.screen.visitstatus

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
class ViewVisitDetailsNoTreatmentView extends BaseView {

    @MVCMember @Nonnull ViewVisitDetailsNoTreatmentController controller
    @MVCMember @Nonnull ViewVisitDetailsNoTreatmentModel model


    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    void initUI() {
        log.debug "initUI()"
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.VIEW_VISIT_DETAILS_NO_TREATMENT, scene)
    }

}
