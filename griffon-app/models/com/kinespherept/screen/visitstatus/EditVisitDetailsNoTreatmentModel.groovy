package com.kinespherept.screen.visitstatus

import com.kinespherept.model.core.Visit
import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.ChangeListener
import griffon.transform.FXObservable
import javafx.beans.property.StringProperty

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonModel)
class EditVisitDetailsNoTreatmentModel {

    @MVCMember @Nonnull EditVisitDetailsNoTreatmentController controller

    // the visit to verify
    Visit visit

    // non-editable info
    @FXObservable String visitDate = ''
    @FXObservable String visitNumber = ''
    @FXObservable String patientName = ''

    // editable info
    @FXObservable List<String> patientTypes = []
    @FXObservable @ChangeListener('checkTreatmentTypeChange') String patientTypesChoice = ''
    Closure checkTreatmentTypeChange = { StringProperty sp, ov, nv -> controller.checkTreatmentTypeChange() }

    @FXObservable List<String> insuranceTypes = []
    @FXObservable @ChangeListener('checkTreatmentTypeChange') String insuranceTypesChoice = ''

    @FXObservable List<String> visitTypes = []
    @FXObservable @ChangeListener('checkTreatmentTypeChange') String visitTypesChoice = ''

    @FXObservable List<String> therapists = []
    @FXObservable String therapistsChoice = ''

    @FXObservable String notes = ''

}
