package com.kinespherept.screen.patient

import com.kinespherept.enums.Mutation
import com.kinespherept.model.core.Patient
import com.kinespherept.model.core.Visit
import griffon.core.artifact.GriffonModel
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor

import java.time.LocalDate

@ArtifactProviderFor(GriffonModel)
class SetupVisitModel {

    // fields that match up to form elements
    @FXObservable String patientName = ''
    @FXObservable LocalDate visitDate = LocalDate.now()
    @FXObservable List<String> patientTypes = []
    @FXObservable String patientTypesChoice = ''
    @FXObservable List<String> visitTypes = []
    @FXObservable String visitTypesChoice = ''
    @FXObservable List<String> insuranceTypes = []
    @FXObservable String insuranceTypesChoice = ''
    @FXObservable List<String> therapists = []
    @FXObservable String therapistsChoice = ''
    @FXObservable String notes = ''

    // fields that hold some state
    Patient patient         // the patient associated to the visit
    Mutation mutation       // create or edit
    Visit visit             // the visit to edit

}