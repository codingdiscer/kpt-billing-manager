package com.kinespherept.screen.patient

import com.kinespherept.model.core.Patient
import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.ChangeListener
import griffon.transform.FXObservable
import javafx.beans.property.StringProperty

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonModel)
class AdministerPatientsModel {

    @MVCMember @Nonnull AdministerPatientsController controller

    // search related
    @FXObservable @ChangeListener('applyFilter')
    String searchFilter = ''
    Closure applyFilter = { StringProperty ob, ov, nv -> controller.applyFilter(ob.value) }


    // info about the currently selected patient
    @FXObservable String patientName = ''
    @FXObservable String insuranceTypeName = ''
    @FXObservable String patientTypeName = ''
    @FXObservable String notes = ''

    Patient patient


//    // how to attach a change listener to a property
//    @FXObservable @ChangeListener('handleEvent')
//    String searchFilter = ''          // property
//    Closure handleEvent = { StringProperty ob, ov, nv -> controller.doSomething(ob.value) }


//    // how to declare list property, and the 'choice' for that property
//    @FXObservable List<String> patientTypes = []
//    @FXObservable String patientTypesChoice = ''


}
