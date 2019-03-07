package com.kinespherept.screen.admin

import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.ChangeListener
import griffon.transform.FXObservable
import javafx.beans.property.StringProperty

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonModel)
class SetupDiagnosesModel {

    @MVCMember @Nonnull SetupDiagnosesController controller

//    // how to attach a change listener to a property
//    @FXObservable @ChangeListener('handleEvent')
//    String searchFilter = ''          // property
//    Closure handleEvent = { StringProperty ob, ov, nv -> controller.doSomething(ob.value) }

    //***********************************
    // diagnosis type section
    //***********************************

    //***********************************
    // diagnosis type update section

    // the list for display
    @FXObservable List<String> diagnosisTypesDisplay = ['a', 'b']

    // needs a @ChangeListener...
    @FXObservable List<String> dtUpdateDiagnosisType = ['c', 'd']

    @FXObservable @ChangeListener('selectDtForUpdate')
    String dtUpdateDiagnosisTypeChoice = 'd'
    Closure selectDtForUpdate = { StringProperty ob, ov, nv -> controller.prepareDtUpdateForm(ob.value) }

    @FXObservable String dtUpdateDiagnosisTypeName = 'e'

    @FXObservable List<String> dtUpdateDisplayOrder = ['1','2','3']
    @FXObservable String dtUpdateDisplayOrderChoice = '3'

    @FXObservable String dtUpdateErrorMessage = 'f'


    //***********************************
    // diagnosis type add section

    @FXObservable String dtAddDiagnosisTypeName = 'g'

    @FXObservable List<String> dtAddDisplayOrder = ['4','5','6']
    @FXObservable String dtAddDisplayOrderChoice = '5'

    @FXObservable String dtAddErrorMessage = 'h'


    //***********************************
    // diagnosis section
    //***********************************

    //***********************************
    // diagnosis update section

    // needs a @ChangeListener...
    @FXObservable List<String> dxUpdateDiagnosisTypeSelect = ['m', 'n']

    @ChangeListener('selectDxTypeForUpdate')
    @FXObservable String dxUpdateDiagnosisTypeSelectChoice = 'm'
    Closure selectDxTypeForUpdate = { StringProperty ob, ov, nv -> controller.prepareDxTypeUpdateForm(ob.value) }

    @FXObservable List<String> dxUpdateDiagnosis = ['o', 'p']

    @FXObservable @ChangeListener('selectDxForUpdate')
    String dxUpdateDiagnosisChoice = 'o'
    Closure selectDxForUpdate = { StringProperty ob, ov, nv -> controller.prepareDxUpdateForm(ob.value) }


    @FXObservable String dxUpdateDiagnosisCode = 'q'
    @FXObservable String dxUpdateDiagnosisName = 'r'

    @FXObservable List<String> dxUpdateDiagnosisType = ['s','t']

    @FXObservable @ChangeListener('updateDxDisplayOrderChoices')
    String dxUpdateDiagnosisTypeChoice = 's'
    Closure updateDxDisplayOrderChoices = { StringProperty ob, ov, nv -> controller.updateDxDisplayOrderChoices(ob.value) }


    @FXObservable List<String> dxUpdateDisplayOrder = ['1','2','3']
    @FXObservable String dxUpdateDisplayOrderChoice = '3'

    @FXObservable String dxUpdateErrorMessage = ''

    //***********************************
    // diagnosis add section

    @FXObservable String dxAddDiagnosisCode = 'u'
    @FXObservable String dxAddDiagnosisName = 'v'



    @FXObservable List<String> dxAddDiagnosisType = ['w','x']

    @FXObservable @ChangeListener('selectDtForAdd')
    String dxAddDiagnosisTypeChoice = 'x'
    Closure selectDtForAdd = { StringProperty ob, ov, nv -> controller.prepareDxAddForm(ob.value) }


    @FXObservable List<String> dxAddDisplayOrder = ['1','2','3']
    @FXObservable String dxAddDisplayOrderChoice = '3'

    @FXObservable String dxAddErrorMessage = ''
}
