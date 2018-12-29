package com.kinespherept.screen.visitstatus

import com.kinespherept.model.core.PatientVisit
import com.kinespherept.screen.visitstatus.TrackVisitStatusController
import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.transform.ChangeListener
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty

import javax.annotation.Nonnull
import java.time.LocalDate

@ArtifactProviderFor(GriffonModel)
class TrackVisitStatusModel {

    // magic string!
    static final ALL = 'All'


    @MVCMember @Nonnull TrackVisitStatusController controller

    // fields that match up to form elements
    @FXObservable @ChangeListener('selectFromDate')
    LocalDate fromDate = LocalDate.now()
    Closure selectFromDate = { ObjectProperty<LocalDate> ob, ov, nv -> controller.selectFromDate() }


    @FXObservable @ChangeListener('selectToDate')
    LocalDate toDate = null
    Closure selectToDate = { ObjectProperty<LocalDate> ob, ov, nv -> controller.selectToDate() }

    @FXObservable List<String> visitStatuses = []
    @FXObservable @ChangeListener('changeVisitStatus')
    String visitStatusesChoice = ''
    Closure changeVisitStatus = { StringProperty ob, ov, nv -> controller.changeVisitStatus() }

    @FXObservable List<String> insuranceTypes = []
    @FXObservable @ChangeListener('changeInsuranceType')
    String insuranceTypesChoice = ''
    Closure changeInsuranceType = { StringProperty ob, ov, nv -> controller.changeInsuranceType() }

    @FXObservable List<String> therapists = []
    @FXObservable @ChangeListener('changeTherapist')
    String therapistsChoice = ''
    Closure changeTherapist = { StringProperty ob, ov, nv -> controller.changeTherapist() }



//    @FXObservable List<String> patientTypes = []
//    @FXObservable String patientTypesChoice = ''
//
//    @FXObservable List<String> visitTypes = []
//    @FXObservable String visitTypesChoice = ''


    // fields that hold some state
    List<PatientVisit> visits = []

}