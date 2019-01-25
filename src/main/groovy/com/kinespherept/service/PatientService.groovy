package com.kinespherept.service

import com.google.common.collect.Lists
import com.kinespherept.dao.repository.PatientRepository
import com.kinespherept.model.core.Diagnosis
import com.kinespherept.model.core.Patient
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

import javax.annotation.PostConstruct
import javax.transaction.Transactional
import java.util.stream.Collectors

/**
 *
 */
@Service
@Slf4j
class PatientService {

    // created and stored here
    List<Patient> patients = []

    // dependent components
    PatientRepository patientRepository
    LookupDataService lookupDataService


    PatientService(PatientRepository patientRepository, LookupDataService lookupDataService)
    {
        this.patientRepository = patientRepository
        this.lookupDataService = lookupDataService
    }


    @PostConstruct
    void init() {
        log.debug "LookupDataService.init() :: "
        patients = Lists.newArrayList(patientRepository.findAll().toSorted { p1, p2 ->
            p1.displayableName <=> p2.displayableName
        })
        hookupLookupData()
    }

    void hookupLookupData() {
        patients.parallelStream().forEach({ p ->
            if(p.insuranceTypeId) {
                p.insuranceType = lookupDataService.insuranceTypes.find { it.insuranceTypeId == p.insuranceTypeId }
            }
            if(p.patientTypeId) {
                p.patientType = lookupDataService.patientTypes.find { it.patientTypeId == p.patientTypeId }
            }
        })
    }

    Patient hookupPatient(Patient patient) {
        patient.insuranceType = lookupDataService.insuranceTypes.find { it.insuranceTypeId == patient.insuranceTypeId }
        patient.patientType = lookupDataService.patientTypes.find { it.patientTypeId == patient.patientTypeId }
        patient
    }


    /**
     * Returns a list of Patients where either the first or last name match any part of the given search string.
     * The results are sorted by last name.
     * @param searchString Any string to search the patients on
     */
    List<Patient> searchPatients(String searchString) {
        // only do the search on a non-empty string
        if(StringUtils.isEmpty(searchString)) {
            return []
        }

        // stream the search!
        patients.stream().filter({ p ->
            p.firstName.toLowerCase().contains(searchString.toLowerCase()) || p.lastName.toLowerCase().contains(searchString.toLowerCase())
        }).collect(Collectors.toList()).sort({ Patient p1, Patient p2 ->
            // if the patients have the same last name, sort by first name
            if(p1.lastName.toLowerCase() == p2.lastName.toLowerCase()) {
                return p1.firstName.toLowerCase().compareTo(p2.firstName.toLowerCase())
            }
            // ..otherwise sort by last name
            p1.lastName.toLowerCase().compareTo(p2.lastName.toLowerCase())
        })
    }


    Patient findById(Long patientId) {
        patients.find { it.patientId == patientId }
    }

    Patient findByDisplayableName(String displayableName) {
        patients.find { it.displayableName == displayableName }
    }

    Patient findByLastNameFirst(String lastNameFirst) {
        patients.find { it.lastNameFirst == lastNameFirst }
    }

    /**
     * This method is used to save a new patient record, or update an existing patient record
     */
    @Transactional
    Patient savePatient(Patient patient) {
        log.debug "savePatient() :: just before being saved :: ${patient}"

        // save the patient (upsert!)
        Patient savedPatient = patientRepository.save(patient)

        log.debug "savePatient() :: just after being saved (patient) :: ${patient}"
        log.debug "savePatient() :: just after being saved (savedPatient) :: ${savedPatient}"

        // see if the patient is found in the cache...and add if not
        if(!patients.find { it.patientId == patient.patientId }) {
            patients.add(patient)
        }

        hookupPatient(patient)
    }

}


