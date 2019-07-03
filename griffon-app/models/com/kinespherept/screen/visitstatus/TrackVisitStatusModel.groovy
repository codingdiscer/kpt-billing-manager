package com.kinespherept.screen.visitstatus

import com.kinespherept.autowire.PostSpringConstruct
import com.kinespherept.enums.SearchType
import com.kinespherept.model.core.Patient

import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitStatus
import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.transform.ChangeListener
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ChoiceBox

import javax.annotation.Nonnull
import java.time.LocalDate

@ArtifactProviderFor(GriffonModel)
class TrackVisitStatusModel {

    // magic strings!
    static final ALL = 'All'
    static final PATIENT_SEARCH = '<-- Search'


    @MVCMember @Nonnull TrackVisitStatusController controller

    // tied to the label that declares how many results were found
    @FXObservable String resultsMessage = ''

    // tied to the label that declares how many results were found
    @FXObservable String errorMessage = ''

    // tied to the 'from' date selector field
    @FXObservable @ChangeListener('selectFromDate')
    LocalDate fromDate = null
    Closure selectFromDate = { ObjectProperty<LocalDate> ob, ov, nv -> controller.selectFromDate() }

    // tied to the 'to/on' date selector field
    @FXObservable @ChangeListener('selectToDate')
    LocalDate toDate = LocalDate.now()
    Closure selectToDate = { ObjectProperty<LocalDate> ob, ov, nv -> controller.selectToDate() }

    // tied to the 'status' drop down [status search type]
    javafx.collections.ObservableList<String> statusTypeVisitStatuses = FXCollections.observableList([])
    String statusTypeVisitStatusesChoice = VisitStatus.VISIT_CREATED.text

    // tied to the 'insurance' drop down
    javafx.collections.ObservableList<String> insuranceTypes = FXCollections.observableList([])
    String insuranceTypesChoice = ALL

    // tied to the 'therapists' drop down
    javafx.collections.ObservableList<String> therapists = FXCollections.observableList([])
    String therapistsChoice = ALL

    StringProperty patientSearchProperty = new SimpleStringProperty('')
    List<Patient> filteredPatients = []


    // tied to the 'patients' drop down
    javafx.collections.ObservableList<String> patients = FXCollections.observableList([PATIENT_SEARCH])
    String patientsChoice = PATIENT_SEARCH

    // tied to the 'status' drop down [status search type]
    javafx.collections.ObservableList<String> patientTypeVisitStatuses = FXCollections.observableList([])
    String patientTypeVisitStatusesChoice = ALL

    // tied to the 'change to status' drop down
    @FXObservable List<String> changeToStatuses = []
    @FXObservable String changeToStatusesChoice = VisitStatus.VISIT_CREATED.text

    //
    // fields that hold some state
    //

    // keep track of the last search type for when returning to this screen
    SearchType searchType = SearchType.STATUS

    // this string represents the first entry in the "filtered patients" drop-down
    String filteredPatientCount = ''

    // useful reference to the select patient
    Patient selectedPatient

    // the list of visits based on the filter criteria
    List<Visit> visits = []


    // the list of Checkbox entries, each represents a row in the search output (for multi-select actions..)
    List<CheckBox> visitCheckBoxes = []
    List<ChoiceBox<String>> statusChoiceBoxes = []
    List<Button> updateStatusButtons = []


    @PostSpringConstruct
    void initAfterSpring() {
        patientSearchProperty.addListener({ StringProperty ob, ov, nv -> controller.changePatientFilter() } as javafx.beans.value.ChangeListener)
    }


}