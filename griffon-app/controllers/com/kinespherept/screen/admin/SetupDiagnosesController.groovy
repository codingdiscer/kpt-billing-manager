package com.kinespherept.screen.admin

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.component.NavigationController
import com.kinespherept.component.SelectDiagnosisController
import com.kinespherept.config.SpringConfig
import com.kinespherept.dao.VisitDao
import com.kinespherept.enums.NavigationSection
import com.kinespherept.model.core.Diagnosis
import com.kinespherept.model.core.DiagnosisType
import com.kinespherept.service.LookupDataService
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import groovy.util.logging.Slf4j
import org.springframework.util.StringUtils

import javax.annotation.Nonnull
import javax.annotation.PostConstruct


@ArtifactProviderFor(GriffonController)
@Slf4j
class SetupDiagnosesController {

    @MVCMember @Nonnull SetupDiagnosesModel model
    @MVCMember @Nonnull SetupDiagnosesView view

    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire VisitDao visitDao

    @GriffonAutowire NavigationController navigationController
    //@GriffonAutowire(mvcGroupName='selectDiagnosis2') SelectDiagnosisController selectDiagnosisController
    @GriffonAutowire SelectDiagnosisController selectDiagnosisController


    // this flag signifies we are in the prepareForm() or setVisitAndDisplay() method.  this is needed/useful because that method
    // sets a couple of model fields that have onChange() events attached to them, and this flag will tell
    // those methods not to take any action that would otherwise be taken if a user triggered the onChange() event.
    boolean preparingForm = false

    @PostConstruct
    void init() {
        log.debug "init()"
        SpringConfig.autowire(this)
    }


    void prepareForm() {
        log.info "prepareForm() :: selectDiagnosisController.hashcode=${selectDiagnosisController.hashCode()}, view.anchorPane.hashcode=${view.diagnosisSelector.hashCode()}"
        preparingForm = true

        navigationController.prepareForm(view.navigationPane, NavigationSection.MANAGE_DIAGNOSES)
        selectDiagnosisController.prepareForm(view.diagnosisSelector, [], 4)

        //
        // prepare the diagnosis-types section
        //

        // dx-type update section
        model.diagnosisTypesDisplay.clear()
        model.diagnosisTypesDisplay.addAll(lookupDataService.getDiagnosisTypes().collect{ "[${it.displayOrder}] ${it.diagnosisTypeName}" })
        model.dtUpdateDiagnosisType.clear()
        model.dtUpdateDiagnosisType << '<select entry to edit>'
        model.dtUpdateDiagnosisType.addAll(lookupDataService.getDiagnosisTypes().collect{ it.diagnosisTypeName })
        model.dtUpdateDiagnosisTypeChoice = '<select entry to edit>'
        model.dtUpdateDiagnosisTypeName = ''
        model.dtUpdateDisplayOrder.clear()
        model.dtUpdateDisplayOrderChoice = ''
        model.dtUpdateErrorMessage = ''


        // dx-type add section
        model.dtAddDiagnosisTypeName = ''
        model.dtAddDisplayOrder.clear()
        model.dtAddDisplayOrder.addAll( getListOfNumbersUpTo(lookupDataService.getDiagnosisTypes().size() + 1) )
        model.dtAddErrorMessage = ''

        //
        // prepare the diagnosis section
        //
        model.dxUpdateDiagnosisTypeSelect.clear()
        model.dxUpdateDiagnosisTypeSelect << '<select entry to edit>'
        model.dxUpdateDiagnosisTypeSelect.addAll(lookupDataService.getDiagnosisTypes().collect{ it.diagnosisTypeName })
        model.dxUpdateDiagnosisTypeSelectChoice = '<select entry to edit>'
        // clear the rest of the update section
        model.dxUpdateDiagnosis.clear()
        model.dxUpdateDiagnosisChoice = ''
        model.dxUpdateDiagnosisCode = ''
        model.dxUpdateDiagnosisName = ''
        model.dxUpdateDiagnosisType.clear()
        model.dxUpdateDiagnosisTypeChoice = ''
        model.dxUpdateDisplayOrder.clear()
        model.dxUpdateDisplayOrderChoice = ''

        // reset the add section
        model.dxAddDiagnosisCode = ''
        model.dxAddDiagnosisName = ''
        model.dxAddDiagnosisType.clear()
        model.dxAddDiagnosisType << ''
        model.dxAddDiagnosisType.addAll(lookupDataService.getDiagnosisTypes().collect{ it.diagnosisTypeName })
        model.dxAddDiagnosisTypeChoice = ''
        model.dxAddDisplayOrder.clear()
        model.dxAddDisplayOrder << '<select type>'
        model.dxAddDisplayOrderChoice = '<select type>'


        // signify we are done preparing the form
        preparingForm = false
    }

    List<String> getListOfNumbersUpTo(int number) {
        List<String> numbers = []
        for(int i = 1; i <= number; i++) {
            numbers << String.valueOf(i)
        }
        numbers
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void updateType() {
        log.info "updateType() : name=${model.dtUpdateDiagnosisTypeName}; order=${model.dtUpdateDisplayOrderChoice}"
        clearErrors()

        // error checking - verify the new name isn't used already
        if(model.dtUpdateDiagnosisTypeName != model.dtUpdateDiagnosisTypeChoice &&
            lookupDataService.findDiagnosisTypeByName(model.dtUpdateDiagnosisTypeName))
        {
            model.dtUpdateErrorMessage = 'That Dx Type name is already used.'
            return
        }

        // update the details of the chosen diagnosis
        DiagnosisType dt = lookupDataService.findDiagnosisTypeByName(model.dtUpdateDiagnosisTypeChoice)
        dt.diagnosisTypeName = model.dtUpdateDiagnosisTypeName
        dt.displayOrder = Integer.valueOf(model.dtUpdateDisplayOrderChoice)
        // save the updates
        lookupDataService.updateDiagnosisType(dt)

        // refresh the form
        prepareForm()
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void deleteType() {
        log.info "deleteType() : name=${model.dtUpdateDiagnosisTypeChoice}"
        clearErrors()

        DiagnosisType dt = lookupDataService.findDiagnosisTypeByName(model.dtUpdateDiagnosisTypeChoice)

        if(dt) {
            if(lookupDataService.diagnosisTypeIsUsed(dt.diagnosisTypeId)) {
                log.info "canNOT delete DT [${model.dtUpdateDiagnosisTypeChoice}] because it is referenced by a diagnosis"
                model.dtUpdateErrorMessage = 'Cannot delete. Dx Type is referenced.'
                return
            } else {
                lookupDataService.deleteDiagnosisType(dt)
                // refresh the form
                prepareForm()
            }
        }


    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void addType() {
        log.info "addType() : name=${model.dtAddDiagnosisTypeName}; order=${model.dtAddDisplayOrderChoice}"
        clearErrors()

        // error checking...

        // make sure the fields were populated
        if(StringUtils.isEmpty(model.dtAddDiagnosisTypeName)) {
            model.dtAddErrorMessage = 'The Dx Type name is required.'
            return
        }
        if(StringUtils.isEmpty(model.dtAddDisplayOrderChoice)) {
            model.dtAddErrorMessage = 'An order must be selected.'
            return
        }

        // - make sure the type name isn't already in use
        if(lookupDataService.findDiagnosisTypeByName(model.dtAddDiagnosisTypeName)) {
            model.dtAddErrorMessage = 'That Dx Type name is already used.'
            return
        }

        // if we got this far, then all good for the validations.  let's add it..
        lookupDataService.addDiagnosisType(new DiagnosisType(diagnosisTypeName: model.dtAddDiagnosisTypeName,
                displayOrder: Integer.parseInt(model.dtAddDisplayOrderChoice)))

        // refresh the form with the updates
        prepareForm()
    }

    void clearErrors() {
        model.dtUpdateErrorMessage = ''
        model.dtAddErrorMessage = ''
        model.dxAddErrorMessage = ''
    }

    void prepareDtUpdateForm(String dtName) {
        if(!preparingForm) {
            DiagnosisType dt = lookupDataService.findDiagnosisTypeByName(dtName)
            if(dt) {
                model.dtUpdateDiagnosisTypeName = dtName
                model.dtUpdateDisplayOrder.clear()
                model.dtUpdateDisplayOrder.addAll(getListOfNumbersUpTo(lookupDataService.getDiagnosisTypes().size()))
                model.dtUpdateDisplayOrderChoice = String.valueOf(dt.displayOrder)
            }
        }
    }


    void prepareDxTypeUpdateForm(String dxTypeName) {
        if(!preparingForm) {
            log.info "prepareDxTypeUpdateForm(${dxTypeName})"

            preparingForm = true

            model.dxUpdateDiagnosis.clear()
            model.dxUpdateDiagnosis.addAll(
                    lookupDataService.findDiagnosesByDiagnosisTypeName(dxTypeName).collect { it.nameForDisplay }
            )

            model.dxUpdateDiagnosisCode = ''
            model.dxUpdateDiagnosisName = ''
            model.dxUpdateDiagnosisType.clear()
            model.dxUpdateDiagnosisTypeChoice = ''
            model.dxUpdateDisplayOrder.clear()
            model.dxUpdateDisplayOrderChoice = ''

            preparingForm = false
        }
    }

    void prepareDxUpdateForm(String dxName) {
        if(!preparingForm) {
            log.info "prepareDxUpdateForm(${dxName})"

            Diagnosis dx = lookupDataService.findDiagnosisByNameForDisplay(dxName)

            model.dxUpdateDiagnosisCode = dx.diagnosisCode
            model.dxUpdateDiagnosisName = dx.diagnosisName
            model.dxUpdateDiagnosisType.clear()
            model.dxUpdateDiagnosisType.addAll(lookupDataService.getDiagnosisTypes().collect{ it.diagnosisTypeName })
            model.dxUpdateDiagnosisTypeChoice = dx.diagnosisType.diagnosisTypeName

            model.dxUpdateDisplayOrder.addAll(getListOfNumbersUpTo(lookupDataService.findDiagnosesByDiagnosisTypeName(dx.diagnosisType.diagnosisTypeName).size()))
            model.dxUpdateDisplayOrderChoice = String.valueOf(dx.displayOrder)
        }
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void updateDiagnosis() {
        log.info "updateDiagnosis() : name=${model.dxUpdateDiagnosisName}; code=${model.dxUpdateDiagnosisCode}; order=${model.dxUpdateDisplayOrderChoice}; type=${model.dxUpdateDiagnosisTypeChoice}"
        clearErrors()

        // do some error checking...

        // error checking - verify the new name isn't used already

        // error checking - verify the new code isn't used already

        

    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void deleteDiagnosis() {
        log.info "deleteDiagnosis() : name=${model.dxUpdateDiagnosisName}; code=${model.dxUpdateDiagnosisCode}; order=${model.dxUpdateDisplayOrderChoice}; type=${model.dxUpdateDiagnosisTypeChoice}"
        clearErrors()

        Diagnosis dx = lookupDataService.findDiagnosisByName(model.dxUpdateDiagnosisName)

        if(visitDao.diagnosisIsUsed(dx.diagnosisId)) {
            log.info "canNOT delete diagnosis [${model.dxUpdateDiagnosisName}] because it is referenced."
            model.dxUpdateErrorMessage = 'Cannot delete. Dx is referenced.'
            return
        } else {
            lookupDataService.deleteDiagnosis(dx)
            // refresh the form
            prepareForm()
        }


    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void addDiagnosis() {
        log.info "addDiagnosis() : name=${model.dxAddDiagnosisName}; code=${model.dxAddDiagnosisCode}; order=${model.dxAddDisplayOrderChoice}; type=${model.dxAddDiagnosisTypeChoice}"
        clearErrors()

        // make sure the fields were populated
        if(StringUtils.isEmpty(model.dxAddDiagnosisName)) {
            model.dxAddErrorMessage = 'The Dx name is required.'
            return
        }
        if(StringUtils.isEmpty(model.dxAddDiagnosisCode)) {
            model.dxAddErrorMessage = 'The Dx code is required.'
            return
        }
        if(StringUtils.isEmpty(model.dxAddDiagnosisTypeChoice)) {
            model.dxAddErrorMessage = 'The Dx type must be selected.'
            return
        }
        if(StringUtils.isEmpty(model.dxAddDisplayOrderChoice)) {
            model.dxAddErrorMessage = 'An order must be selected.'
            return
        }

        // make sure the name isn't already in use
        if(lookupDataService.findDiagnosisByName(model.dxAddDiagnosisName)) {
            model.dtAddErrorMessage = 'That Dx name is already used.'
            return
        }
        // make sure the code isn't already in use
        if(lookupDataService.findDiagnosisByCode(model.dxAddDiagnosisCode)) {
            model.dtAddErrorMessage = 'That Dx code is already used.'
            return
        }

        log.info "this Dx is ready to be added!!"
        lookupDataService.addDiagnosis(new Diagnosis(diagnosisName: model.dxAddDiagnosisName,
            diagnosisCode: model.dxAddDiagnosisCode, displayOrder: Integer.parseInt(model.dxAddDisplayOrderChoice),
            diagnosisTypeId: lookupDataService.findDiagnosisTypeByName(model.dxAddDiagnosisTypeChoice).diagnosisTypeId
        ))

        // refresh the form with the updates
        prepareForm()
    }

    void prepareDxAddForm(String dtName) {
        if(!preparingForm) {
            log.info "prepareDxAddForm(${dtName})"

            model.dxAddDisplayOrder.clear()
            model.dxAddDisplayOrder.addAll(getListOfNumbersUpTo(lookupDataService.findDiagnosesByDiagnosisTypeName(dtName).size() + 1))
            model.dxUpdateDisplayOrderChoice = '1'
        }
    }


}
