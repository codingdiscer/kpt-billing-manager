package com.kinespherept.screen.patient

import com.kinespherept.enums.Mutation
import com.kinespherept.model.core.Patient
import com.kinespherept.screen.patient.SetupPatientController
import griffon.core.artifact.GriffonModel
import griffon.inject.MVCMember
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor
import groovy.util.logging.Slf4j

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonModel)
@Slf4j
class SetupPatientModel {

    @MVCMember @Nonnull SetupPatientController controller

    @FXObservable String firstName = ''
    @FXObservable String lastName = ''
    @FXObservable List<String> patientTypes = []
    @FXObservable String patientTypesChoice = ''
    @FXObservable List<String> insuranceTypes = []
    @FXObservable String insuranceTypesChoice = ''
    @FXObservable String notes = ''

    Mutation mutation = Mutation.UNSET
    Patient patient
}