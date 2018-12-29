package com.kinespherept.dao.repository

import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitStatus
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

import java.time.LocalDate

interface VisitRepository extends CrudRepository<Visit, Long> {

    List<Visit> findByVisitDateAndTherapistId(LocalDate visitDate, Integer therapistId)

    //
    // queries for visitstatus tracker screen - search by visitStatus, fromDate & ...  (not toDate)
    //
    List<Visit> findByVisitStatusAndVisitDate(VisitStatus visitStatus, LocalDate visitDate)
    List<Visit> findByVisitStatusAndVisitDateAndInsuranceTypeId(VisitStatus visitStatus, LocalDate visitDate, Integer insuranceTypeId)
    List<Visit> findByVisitStatusAndVisitDateAndTherapistId(VisitStatus visitStatus, LocalDate visitDate, Integer therapistId)
    List<Visit> findByVisitStatusAndVisitDateAndInsuranceTypeIdAndTherapistId(VisitStatus visitStatus, LocalDate visitDate, Integer insuranceTypeId, Integer therapistId)

    //
    // queries for visitstatus tracker screen - search by visitStatus, fromDate, toDate & ...
    //
    @Query('SELECT v FROM Visit v WHERE v.visitStatus = :visitStatus AND v.visitDate >= :fromDate AND v.visitDate <= :toDate')
    List<Visit> findByVisitStatusAndFromDateAndToDate(@Param('visitStatus') VisitStatus visitStatus,
                                                      @Param('fromDate') LocalDate fromDate,
                                                      @Param('toDate') LocalDate toDate)

    @Query('SELECT v FROM Visit v WHERE v.visitStatus = :visitStatus AND v.visitDate >= :fromDate AND v.visitDate <= :toDate AND v.insuranceTypeId = :insuranceTypeId')
    List<Visit> findByVisitStatusAndFromDateAndToDateAndInsuranceTypeId(@Param('visitStatus') VisitStatus visitStatus,
                                                      @Param('fromDate') LocalDate fromDate,
                                                      @Param('toDate') LocalDate toDate,
                                                      @Param('insuranceTypeId') Integer insuranceTypeId)

    @Query('SELECT v FROM Visit v WHERE v.visitStatus = :visitStatus AND v.visitDate >= :fromDate AND v.visitDate <= :toDate AND v.therapistId = :therapistId')
    List<Visit> findByVisitStatusAndFromDateAndToDateAndTherapistId(@Param('visitStatus') VisitStatus visitStatus,
                                                                    @Param('fromDate') LocalDate fromDate,
                                                                    @Param('toDate') LocalDate toDate,
                                                                    @Param('therapistId') Integer therapistId)

    @Query('SELECT v FROM Visit v WHERE v.visitStatus = :visitStatus AND v.visitDate >= :fromDate AND v.visitDate <= :toDate AND v.insuranceTypeId = :insuranceTypeId AND v.therapistId = :therapistId')
    List<Visit> findByVisitStatusAndFromDateAndToDateAndInsuranceTypeIdAndTherapistId(
            @Param('visitStatus') VisitStatus visitStatus, @Param('fromDate') LocalDate fromDate,
            @Param('toDate') LocalDate toDate, @Param('insuranceTypeId') Integer insuranceTypeId,
            @Param('therapistId') Integer therapistId)



    List<Visit> findByPatientIdOrderByVisitDateAsc(Long patientId)

    List<Visit> findByPatientIdAndVisitDateOrderByVisitIdAsc(Long patientId, LocalDate localDate)

    long countByPatientId(long patientId)

}



