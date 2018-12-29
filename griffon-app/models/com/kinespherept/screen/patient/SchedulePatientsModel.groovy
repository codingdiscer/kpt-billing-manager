package com.kinespherept.screen.patient

import com.kinespherept.model.core.Patient
import com.kinespherept.model.core.Visit
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
class SchedulePatientsModel {

    @MVCMember @Nonnull SchedulePatientsController controller


    // search related
    @FXObservable @ChangeListener('applyFilter')
    String searchFilter = ''
    Closure applyFilter = { StringProperty ob, ov, nv -> controller.applyFilter(ob.value) }


//    // info about the currently selected patient
//    @FXObservable String patientName = ''
//    @FXObservable String insuranceTypeName = ''
//    @FXObservable String patientTypeName = ''
//    @FXObservable String notes = ''
//
//    Patient patient

    // daily schedule related
    @FXObservable List<String> therapists = []

    @FXObservable @ChangeListener('selectTherapist')
    String therapistsChoice
    Closure selectTherapist = { StringProperty ob, ov, nv -> controller.refreshVisitors() }

    @FXObservable @ChangeListener('selectDate')
    LocalDate visitDate = LocalDate.now()
    Closure selectDate = { ObjectProperty<LocalDate> ob, ov, nv -> controller.refreshVisitors() }

    // next two lists are parallel, to get quicker access to the Visit object
    @FXObservable List<String> visitors = []
    List<Visit> visits = []

    // TODO - figure out how to hook this up to the ListView.selectionModel.selectIndexProperty.changeListener
    //Closure visitorsListSelected = { IntegerProperty ob, ov, nv -> println "visitorsListSelected!!" }

}