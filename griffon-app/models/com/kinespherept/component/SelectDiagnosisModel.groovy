package com.kinespherept.component

import com.kinespherept.model.core.Diagnosis
import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.transform.ChangeListener
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.StringProperty
import javafx.scene.control.Button
import javafx.scene.control.Labeled

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonModel)
class SelectDiagnosisModel {

    @MVCMember @Nonnull SelectDiagnosisController controller

    // the list of diagnosis objects that have been selected
    List<Diagnosis> selectedDiagnoses = []

    // the UI list of selected diagnoses
    @FXObservable List<Button> diagnosisList = []

    // search related
    @FXObservable @ChangeListener('applyFilter')
    String searchFilter = ''
    Closure applyFilter = { StringProperty ob, ov, nv -> controller.applyFilter(ob.value) }

    // the list of results from the search
    @FXObservable List<Labeled> diagnosisSearchList = []

    // labels for the counts - these unfortunately must be String values (to map to the UI Label)
    @FXObservable String diagnosisCount = "0"
    @FXObservable String diagnosisMaxCount = "0"
    @FXObservable String diagnosisCategoryCount = "0"



}