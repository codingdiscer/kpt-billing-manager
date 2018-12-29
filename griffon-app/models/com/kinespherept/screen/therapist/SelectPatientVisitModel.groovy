package com.kinespherept.screen.therapist

import com.kinespherept.model.core.Visit
import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.ChangeListener
import griffon.transform.FXObservable
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty

import javax.annotation.Nonnull
import java.time.LocalDate

@ArtifactProviderFor(GriffonModel)
class SelectPatientVisitModel {

    @MVCMember @Nonnull SelectPatientVisitController controller

    // the date selector box
    @FXObservable @ChangeListener('selectDate')
    LocalDate visitDate = LocalDate.now()
    Closure selectDate = { ObjectProperty<LocalDate> ob, ov, nv -> if(!controller.preparingForm) { controller.refreshVisitors() }}

    // next two lists are parallel, to get quicker access to the Visit object.  visitors holds the patient.displayableName
    @FXObservable List<String> visitors = []
    List<Visit> visits = []

    // the currently selected visit to display the details for
    Visit selectedVisit = null

    // this flags whether the form should "show all" or "show only pending" visits
    boolean showAllVisits = true

}
