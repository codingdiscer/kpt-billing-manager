package com.kinespherept.screen.visitstatus

import com.kinespherept.model.core.Visit
import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import griffon.transform.FXObservable


@ArtifactProviderFor(GriffonModel)
class ViewVisitDetailsNoTreatmentModel {

    // the visit to verify
    Visit visit

    // left side (basic info)
    @FXObservable String visitDate = ''
    @FXObservable String visitNumber = ''
    @FXObservable String patientName = ''
    @FXObservable String patientType = ''
    @FXObservable String insuranceType = ''
    @FXObservable String visitType = ''
    @FXObservable String therapist = ''
    @FXObservable String notes = ''

}
