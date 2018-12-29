package com.kinespherept.screen.visitstatus

import com.kinespherept.model.core.Visit
import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.FXObservable
import javafx.scene.control.Label
import javafx.scene.layout.FlowPane

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonModel)
class ViewVisitDetailsWithTreatmentModel {

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

    // middle section (diagnoses)
    @FXObservable String diagnosisCount = ''
    //List<Diagnosis> diagnoses = []
    // each Label holds this : <diagnosis.nameForDisplay>
    List<Label> diagnosisRows = []


    // right section (treatments)
    @FXObservable String treatmentCount = ''
    //List<VisitTreatment> visitTreatments = []
    // each FlowPane holds this : <treatment.name>, <countLabel>
    List<FlowPane> treatmentRows = []

}
