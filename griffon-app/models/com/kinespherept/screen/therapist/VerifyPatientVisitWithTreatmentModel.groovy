package com.kinespherept.screen.therapist

import com.kinespherept.model.core.Visit
import griffon.core.artifact.GriffonModel
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor
import javafx.scene.control.Label
import javafx.scene.layout.FlowPane

@ArtifactProviderFor(GriffonModel)
class VerifyPatientVisitWithTreatmentModel {

    // the visit to verify
    Visit visit

    // left side (basic info)
    @FXObservable String visitDate = ''
    @FXObservable String patientName = ''
    @FXObservable String patientType = ''
    @FXObservable String insuranceType = ''
    @FXObservable String visitType = ''
    @FXObservable String notes = ''

    // middle section (diagnoses)
    @FXObservable String diagnosisCount = ''
    List<Label> diagnosisRows = []

    // right section (treatments)
    @FXObservable String treatmentCount = ''
    List<FlowPane> treatmentRows = []

}