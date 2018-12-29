package com.kinespherept.component

import griffon.core.artifact.GriffonModel
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor
import javafx.scene.layout.FlowPane

@ArtifactProviderFor(GriffonModel)
class SelectTreatmentModel {

    @FXObservable String treatmentCount = '0'

    // each FlowPane holds this : <treatment.name>, <incrementButton>, <decrementButton>, <countLabel>
    List<FlowPane> treatmentRows = []


}