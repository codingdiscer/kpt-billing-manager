package com.kinespherept.screen.visitstatus

import com.kinespherept.model.core.Patient
import com.kinespherept.model.core.PatientVisit
import com.kinespherept.model.core.VisitStatus
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

    // tied to the label that declares how many results were found
    @FXObservable String resultCountMessage = ''

    // tied to the 'from' date selector field
    @FXObservable @ChangeListener('selectFromDate')
    LocalDate fromDate = null
    Closure selectFromDate = { ObjectProperty<LocalDate> ob, ov, nv -> controller.selectFromDate() }

    // tied to the 'to/on' date selector field
    @FXObservable @ChangeListener('selectToDate')
    LocalDate toDate = LocalDate.now()
    Closure selectToDate = { ObjectProperty<LocalDate> ob, ov, nv -> controller.selectToDate() }

    // tied to the 'status' drop down
    @FXObservable List<String> visitStatuses = []
    @FXObservable @ChangeListener('changeVisitStatus')
    String visitStatusesChoice = VisitStatus.VISIT_CREATED.text
    Closure changeVisitStatus = { StringProperty ob, ov, nv -> controller.changeVisitStatus() }

    // tied to the 'insurance' drop down
    @FXObservable List<String> insuranceTypes = []
    @FXObservable @ChangeListener('changeInsuranceType')
    String insuranceTypesChoice = ALL
    Closure changeInsuranceType = { StringProperty ob, ov, nv -> controller.changeInsuranceType() }

    // tied to the 'therapists' drop down
    @FXObservable List<String> therapists = []
    @FXObservable @ChangeListener('changeTherapist')
    String therapistsChoice = ALL
    Closure changeTherapist = { StringProperty ob, ov, nv -> controller.changeTherapist() }

    // tied to the patient search text field
    String patientSearch = ''
    List<Patient> filteredPatients = []

    // tied to the 'patients' drop down
    @FXObservable List<String> patients = []
    //@FXObservable @ChangeListener('selectPatient')
    String patientsChoice = ''
    //Closure selectPatient = { StringProperty ob, ov, nv -> controller.selectPatient() }



//    @FXObservable List<String> patientTypes = []
//    @FXObservable String patientTypesChoice = ''
//
//    @FXObservable List<String> visitTypes = []
//    @FXObservable String visitTypesChoice = ''


    // fields that hold some state
    List<PatientVisit> visits = []

}