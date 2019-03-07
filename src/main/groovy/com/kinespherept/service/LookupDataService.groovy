package com.kinespherept.service

import com.google.common.collect.Lists
import com.google.common.collect.Sets
import com.kinespherept.dao.LookupDao
import com.kinespherept.dao.VisitDao
import com.kinespherept.dao.repository.DiagnosisRepository
import com.kinespherept.dao.repository.DiagnosisTypeRepository
import com.kinespherept.dao.repository.InsuranceTypeRepository
import com.kinespherept.dao.repository.PatientTypeRepository
import com.kinespherept.dao.repository.TreatmentRepository
import com.kinespherept.dao.repository.VisitTypeRepository
import com.kinespherept.model.core.Diagnosis
import com.kinespherept.model.core.DiagnosisType
import com.kinespherept.model.core.InsuranceType
import com.kinespherept.model.core.PatientType
import com.kinespherept.model.core.Treatment
import com.kinespherept.model.core.VisitDiagnosis
import com.kinespherept.model.core.VisitTreatment
import com.kinespherept.model.core.VisitType
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

import javax.annotation.PostConstruct
import java.util.stream.Collectors

/**
 * A service layer that wraps the various daos for the look up type data
 * within the application.
 */
@Service
@Slf4j
class LookupDataService {

    // dependent components (all repositories!)
    DiagnosisRepository diagnosisRepository
    DiagnosisTypeRepository diagnosisTypeRepository
    InsuranceTypeRepository insuranceTypeRepository
    PatientTypeRepository patientTypeRepository
    TreatmentRepository treatmentRepository
    VisitTypeRepository visitTypeRepository
    LookupDao lookupDao
    VisitDao visitDao

    // local cache
    List<Diagnosis> diagnoses = []
    List<DiagnosisType> diagnosisTypes = []
    List<InsuranceType> insuranceTypes = []
    List<PatientType> patientTypes = []
    List<Treatment> treatments = []
    List<VisitType> visitTypes = []



    LookupDataService(
            DiagnosisRepository diagnosisRepository,
            DiagnosisTypeRepository diagnosisTypeRepository,
            InsuranceTypeRepository insuranceTypeRepository,
            PatientTypeRepository patientTypeRepository,
            TreatmentRepository treatmentRepository,
            VisitTypeRepository visitTypeRepository,
            LookupDao lookupDao, VisitDao visitDao
    ) {
        this.diagnosisRepository = diagnosisRepository
        this.diagnosisTypeRepository = diagnosisTypeRepository
        this.insuranceTypeRepository = insuranceTypeRepository
        this.patientTypeRepository = patientTypeRepository
        this.treatmentRepository = treatmentRepository
        this.visitTypeRepository = visitTypeRepository
        this.lookupDao = lookupDao
        this.visitDao = visitDao
    }


    @PostConstruct
    void init() {
        log.debug "LookupDataService.init() :: "
        diagnoses = Lists.newArrayList(diagnosisRepository.findAll())
        diagnosisTypes = Lists.newArrayList(diagnosisTypeRepository.findAllByOrderByDisplayOrderAsc())
        insuranceTypes = Lists.newArrayList(insuranceTypeRepository.findAll())
        patientTypes = Lists.newArrayList(patientTypeRepository.findAll())
        treatments = Lists.newArrayList(treatmentRepository.findAll())
        visitTypes = Lists.newArrayList(visitTypeRepository.findAll())

        // correlate intertwined data
        hookupDiagnosisData()
    }

    void hookupDiagnosisData() {
        diagnoses.each {
            it.diagnosisType = findDiagnosisTypeById(it.diagnosisTypeId)
        }
    }


    /**
     * Saves the new VisitType to the database, and refreshes the full list of visit types.
     * Returns the new VisitType item that was created
     */
    VisitType addVisitType(VisitType visitType) {
        VisitType vs = visitTypeRepository.save(visitType)
        visitTypes = Lists.newArrayList(visitTypeRepository.findAll())
        vs
    }

    /**
     * Adds the given diagnosis type to the database.  The ID of the type is assigned
     * in this method.  All other diagnosis types with the same or later display order
     * get incremented by 1.
     */
    void addDiagnosisType(DiagnosisType diagnosisType) {
        // grab the next ID from the table
        diagnosisType.diagnosisTypeId = lookupDao.getMaxIdValue(DiagnosisType) + 1

        // loop over the diagnosis types, and update the order as necessary
        diagnosisTypes.each { dt ->
            if(diagnosisType.displayOrder <= dt.displayOrder) {
                dt.displayOrder++
                diagnosisTypeRepository.save(dt)
            }
        }
        diagnosisTypeRepository.save(diagnosisType)

        diagnosisTypes = Lists.newArrayList(diagnosisTypeRepository.findAllByOrderByDisplayOrderAsc())
        hookupDiagnosisData()
    }

    /**
     * Updates the given diagnosis type to the database.
     */
    void updateDiagnosisType(DiagnosisType diagnosisType) {
        List<DiagnosisType> newOrder = []

        // loop over the diagnosis types, and update the order as necessary
        int order = 1
        diagnosisTypes.each { dt ->
            if(order == diagnosisType.displayOrder) {
                newOrder << diagnosisType
                order++
            }
            if(dt.diagnosisTypeId != diagnosisType.diagnosisTypeId) {
                newOrder << dt
                order++
            }
        }

        // if the new order for the diagnosisType is the last entry, then it got missed in the above loop,
        //so add it now
        if(newOrder.size() < diagnosisTypes.size()) {
            newOrder << diagnosisType
        }

        // update the order now and save them..
        newOrder.eachWithIndex{ DiagnosisType entry, int i ->
            entry.displayOrder = i + 1
            diagnosisTypeRepository.save(entry)
        }

        diagnosisTypes = Lists.newArrayList(diagnosisTypeRepository.findAllByOrderByDisplayOrderAsc())
        hookupDiagnosisData()
    }

    /**
     * Returns true if the given diagnosis type is referenced by any diagnosis.
     */
    boolean diagnosisTypeIsUsed(int diagnosisTypeId) {
        diagnosisRepository.findAllByDiagnosisTypeId(diagnosisTypeId).size() > 0
    }

    /**
     * Deletes the diagnosis type by the given ID.
     */
    void deleteDiagnosisType(DiagnosisType diagnosisType) {
        if(diagnosisTypeIsUsed(diagnosisType.diagnosisTypeId)) {
            throw new IllegalArgumentException("Attempted to delete DiagnosisType[${diagnosisType.diagnosisTypeName}] failed because it has children Diagnosis records")
        }

        // loop over the diagnosis types, and update the order as necessary
        diagnosisTypes.each { dt ->
            if(diagnosisType.displayOrder <= dt.displayOrder) {
                dt.displayOrder--
                diagnosisTypeRepository.save(dt)
            }
        }

        // perform the delete
        diagnosisTypeRepository.delete(diagnosisType)
        // refresh the data and the form
        diagnosisTypes = Lists.newArrayList(diagnosisTypeRepository.findAllByOrderByDisplayOrderAsc())
        hookupDiagnosisData()
    }

    /**
     * Returns a list of {@link Diagnosis} where either the diagnosis name, code, or type contains any part of the
     * given search string.  The results are sorted first by diagnosisTypeOrder, then by displayOrder.
     * @param searchString Any string to search the diagnoses on
     */
    List<Diagnosis> searchDiagnoses(String searchString) {
        // only do the search on a non-empty string
        if(StringUtils.isEmpty(searchString)) {
            return []
        }

        // stream the search!
        diagnoses.stream().filter({ d ->
            d.diagnosisName.toLowerCase().contains(searchString.toLowerCase()) ||
            d.diagnosisType?.diagnosisTypeName.toLowerCase().contains(searchString.toLowerCase()) ||
            d.diagnosisCode.toLowerCase().contains(searchString.toLowerCase())
        })
        .sorted(Comparator.comparingInt({ Diagnosis d -> d.diagnosisType.displayOrder })
            .thenComparingInt({ Diagnosis d -> d.displayOrder })
        )
        .collect(Collectors.toList())
    }


    /**
     * Adds the given diagnosis to the database.  The ID of the type is assigned
     * in this method.  All other diagnoses in the same dx type with the same or later display order
     * get incremented by 1.
     */
    void addDiagnosis(Diagnosis diagnosis) {
        // grab the next ID from the table
        diagnosis.diagnosisId = lookupDao.getMaxIdValue(Diagnosis) + 1

        log.info "just set diagnosis.id to [${diagnosis.diagnosisId}]"

        // loop over the diagnoses, and update the order as necessary
        findDiagnosesByDiagnosisTypeId(diagnosis.diagnosisTypeId).each { dx ->
            if(diagnosis.displayOrder <= dx.displayOrder) {
                dx.displayOrder++
                diagnosisRepository.save(dx)
            }
        }
        diagnosisRepository.save(diagnosis)

        diagnoses = Lists.newArrayList(diagnosisRepository.findAll())
        hookupDiagnosisData()
    }


    void deleteDiagnosis(Diagnosis diagnosis) {
        if(visitDao.diagnosisIsUsed(diagnosis.diagnosisId)) {
            throw new IllegalArgumentException("Attempted to delete Diagnosis[${diagnosis.diagnosisName}] failed because it is referenced in a visit")
        }

        // loop over the diagnoses of the same dx-type, and update the order as necessary
        findDiagnosesByDiagnosisTypeId(diagnosis.diagnosisTypeId).each { dx ->
            if(diagnosis.displayOrder <= dx.displayOrder) {
                dx.displayOrder--
                diagnosisRepository.save(dx)
            }
        }

        // perform the delete
        diagnosisRepository.delete(diagnosis)
        // refresh the data and the form
        diagnoses = Lists.newArrayList(diagnosisRepository.findAll())
        hookupDiagnosisData()
    }

    void updateDiagnosisOfSameDxType(Diagnosis diagnosis) {
        // loop over the diagnoses, and update the order as necessary
        List<Diagnosis> dxList = findDiagnosesByDiagnosisTypeId(diagnosis.diagnosisTypeId)

        dxList.remove(diagnosis)

        // re-number all the rest of the dx's, accounting for the order of the updated one
        for(int i = 0; i < dxList.size(); i++ ) {
            if(i < diagnosis.displayOrder - 1) {
                dxList[i].displayOrder = i + 1  // to correct for zero-based counter
                diagnosisRepository.save(dxList[i])
            } else {
                dxList[i].displayOrder = i + 2
                diagnosisRepository.save(dxList[i])
            }
        }

        // save the incoming change
        diagnosisRepository.save(diagnosis)

        // now refresh everything
        diagnoses = Lists.newArrayList(diagnosisRepository.findAll())
        hookupDiagnosisData()
    }

    void moveDiagnosisToNewDxType(Diagnosis diagnosis, String oldDxTypeName) {

        // loop over the diagnoses, and update the order as necessary
        List<Diagnosis> oldDxList = findDiagnosesByDiagnosisTypeName(oldDxTypeName)

        // remove the Dx from the old list
        oldDxList.remove(diagnosis)

        // loop over the old diagnoses, and update the order as necessary
        for(int i = 0; i < oldDxList.size(); i++ ) {
            oldDxList[i].displayOrder = i + 1
            diagnosisRepository.save(oldDxList[i])
        }

        updateDiagnosisOfSameDxType(diagnosis)

        diagnoses = Lists.newArrayList(diagnosisRepository.findAll())
        hookupDiagnosisData()
    }

    List<Diagnosis> findDiagnosesByDiagnosisTypeId(int diagnosisTypeId) {
        DiagnosisType dt = findDiagnosisTypeById(diagnosisTypeId)
        List<Diagnosis> dxList = []

        if(dt) {
            dxList = findDiagnosesByDiagnosisTypeName(dt.diagnosisTypeName)
        }

        dxList.toSorted { a, b -> a.displayOrder <=> b.displayOrder }
    }

    List<Diagnosis> findDiagnosesByDiagnosisTypeName(String diagnosisTypeName) {
        DiagnosisType dt = findDiagnosisTypeByName(diagnosisTypeName)
        List<Diagnosis> dxList = []

        if(dt) {
           dxList = diagnoses.findAll { it.diagnosisTypeId == dt.diagnosisTypeId }
        }

        dxList.toSorted { a, b -> a.displayOrder <=> b.displayOrder }
    }


    PatientType findPatientTypeByName(String patientTypeName) {
        patientTypes.find { it.patientTypeName == patientTypeName }
    }

    PatientType findPatientTypeById(Integer patientId) {
        patientTypes.find { it.patientTypeId == patientId }
    }

    InsuranceType findInsuranceTypeByName(String insuranceTypeName) {
        insuranceTypes.find { it.insuranceTypeName == insuranceTypeName }
    }

    InsuranceType findInsuranceTypeById(Integer insuranceTypeId) {
        insuranceTypes.find { it.insuranceTypeId == insuranceTypeId }
    }

    VisitType findVisitTypeById(Integer visitTypeId) {
        visitTypes.find { it.visitTypeId == visitTypeId }
    }

    VisitType findVisitTypeByName(String visitTypeName) {
        visitTypes.find { it.visitTypeName == visitTypeName }
    }

    Diagnosis findDiagnosisById(Integer diagnosisId) {
        diagnoses.find { it.diagnosisId == diagnosisId }
    }

    Diagnosis findDiagnosisByName(String name) {
        diagnoses.find { it.diagnosisName == name }
    }

    Diagnosis findDiagnosisByCode(String code) {
        diagnoses.find { it.diagnosisCode == code }
    }

    Diagnosis findDiagnosisByNameForDisplay(String displayName) {
        diagnoses.find { it.getNameForDisplay() == displayName }
    }

    DiagnosisType findDiagnosisTypeById(Integer diagnosisTypeId) {
        diagnosisTypes.find { it.diagnosisTypeId == diagnosisTypeId }
    }

    DiagnosisType findDiagnosisTypeByName(String diagnosisTypeName) {
        diagnosisTypes.find { it.diagnosisTypeName == diagnosisTypeName }
    }

    Treatment findTreatmentById(Integer treatmentId) {
        treatments.find { it.treatmentId == treatmentId }
    }

    Treatment findTreatmentByDisplayableName(String displayableName) {
        treatments.find { it.displayableName == displayableName }
    }

    List<DiagnosisType> findDiagnosisTypes(List<VisitDiagnosis> visitDiagnoses) {
        Set<DiagnosisType> types = Sets.newHashSet()
        visitDiagnoses.each { vd -> types.add(
                findDiagnosisTypeById(findDiagnosisById(vd.diagnosisId).diagnosisTypeId))
        }
        types.stream().collect(Collectors.toList())
    }

}
