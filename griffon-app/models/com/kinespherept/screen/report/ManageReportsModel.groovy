package com.kinespherept.screen.report

import com.kinespherept.model.report.MonthReport
import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.ChangeListener
import griffon.transform.FXObservable
import javafx.beans.property.StringProperty

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonModel)
class ManageReportsModel {

    @MVCMember @Nonnull ManageReportsController controller


    @FXObservable List<String> years = []
    @FXObservable @ChangeListener('changeYear')
    String yearsChoice = ''
    Closure changeYear = { StringProperty ob, ov, nv -> controller.refreshDisplay() }


    // contains the list of reports for the currently selected year
    List<MonthReport> reports = []



}
