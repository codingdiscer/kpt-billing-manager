package com.kinespherept.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.rowset.SqlRowSet
import org.springframework.stereotype.Service

import java.time.LocalDate

@Service
class VisitDao {

    JdbcTemplate jdbcTemplate

    VisitDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate
    }

    /**
     * returns a list of diagnosis-ids that represent all of the diagnoses that the
     * patient has been given on visits that have occurred since the given date (inclusive).
     */
    List<Integer> getRecentPatientDiagnoses(long patientId, LocalDate sinceDate) {
        jdbcTemplate.queryForList('select distinct diagnosis_id from visit_diagnosis where visit_id in (select visit_id from visit where visit_date >= ? and patient_id = ?)', [sinceDate, patientId].toArray(), Integer)
    }

    boolean diagnosisIsUsed(int diagnosisId) {
        jdbcTemplate.queryForRowSet('select visit_diagnosis_id from visit_diagnosis where diagnosis_id = ?', [diagnosisId].toArray()).first()
    }

}
