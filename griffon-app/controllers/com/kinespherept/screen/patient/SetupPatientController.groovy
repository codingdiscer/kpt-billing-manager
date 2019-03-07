package com.kinespherept.screen.patient

import com.kinespherept.autowire.GriffonAutowire
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.config.SpringConfig
import com.kinespherept.enums.Mutation
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.core.Patient
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.LookupDataService
import com.kinespherept.service.PatientService
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import groovy.util.logging.Slf4j

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonController)
@Slf4j
class SetupPatientController {

    @MVCMember @Nonnull SetupPatientModel model
    @MVCMember @Nonnull SetupPatientView view

    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire PatientService patientService
    @SpringAutowire CommonProperties commonProperties

    @GriffonAutowire AdministerPatientsController administerPatientsController


    @PostConstruct
    void init() {
        log.debug "init()"
        SpringConfig.autowire(this)
    }

    void prepareForm(Mutation mutation, Patient patient) {
        log.info "prepareForm(), mutation=${mutation}"
        model.patient = patient
        model.mutation = mutation
        if(mutation == Mutation.CREATE) {
            model.firstName = ''
            model.lastName = ''
            model.patientTypes.clear()
            model.patientTypes.addAll(lookupDataService.patientTypes.collect { it.patientTypeName })
            model.insuranceTypes.clear()
            model.insuranceTypes.addAll(lookupDataService.insuranceTypes.collect { it.insuranceTypeName })
            model.notes = ''

            // set the label on the button
            view.mutateButton.text = 'Create Patient'

        } else if(mutation == Mutation.UPDATE) {
            model.firstName = patient.firstName
            model.lastName = patient.lastName
            model.patientTypes.clear()
            model.patientTypes.addAll(lookupDataService.patientTypes.collect { it.patientTypeName })
            model.patientTypesChoice = patient.patientType.patientTypeName
            model.insuranceTypes.clear()
            model.insuranceTypes.addAll(lookupDataService.insuranceTypes.collect { it.insuranceTypeName })
            model.insuranceTypesChoice = patient.insuranceType.insuranceTypeName
            model.notes = patient.notes

            // set the label on the button
            view.mutateButton.text = 'Update Patient'
        }
    }

    void clearForm() {
        log.info "clearForm()"
        model.firstName = ''
        model.lastName = ''
        model.patientTypes.clear()
        model.patientTypes.addAll(lookupDataService.patientTypes.collect { it.patientTypeName })
        model.insuranceTypes.clear()
        model.insuranceTypes.addAll(lookupDataService.insuranceTypes.collect { it.insuranceTypeName })
    }


    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void mutatePatient() {
        log.info "mutatePatient() :: mutation=${model.mutation}, firstName=${model.firstName}, lastName=${model.lastName}, patientType=${model.patientTypesChoice}, insuranceType=${model.insuranceTypesChoice}, notes=${model.notes}"

        // clear the error messages
        model.errorMessage = ''

        if(model.firstName.trim().size() == 0) {
            model.errorMessage = 'Please enter a first name'
            return
        }
        if(model.lastName.trim().size() == 0) {
            model.errorMessage = 'Please enter a last name'
            return
        }
        if(model.patientTypesChoice == '') {
            model.errorMessage = 'Please select a patient type'
            return
        }
        if(model.insuranceTypesChoice == '') {
            model.errorMessage = 'Please select an insurance type'
            return
        }


        if(model.mutation == Mutation.CREATE) {
            model.patient = new Patient()
        }

        model.patient.firstName = model.firstName
        model.patient.lastName = model.lastName
        model.patient.patientTypeId = lookupDataService.findPatientTypeByName(
                model.patientTypesChoice)?.patientTypeId
        model.patient.insuranceTypeId = lookupDataService.findInsuranceTypeByName(
                model.insuranceTypesChoice)?.insuranceTypeId
        model.patient.notes = model.notes

        // save the patient that we've collected info for
        patientService.savePatient(model.patient)

        // update the patient display view
        administerPatientsController.prepareForm()
        SceneManager.changeTheScene(SceneDefinition.ADMINISTER_PATIENTS)
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void cancel() {
        SceneManager.changeTheScene(SceneDefinition.ADMINISTER_PATIENTS)
    }

}