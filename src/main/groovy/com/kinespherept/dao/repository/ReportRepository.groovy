package com.kinespherept.dao.repository

import com.kinespherept.model.core.Report
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

import java.time.LocalDate

interface ReportRepository extends CrudRepository<Report, Integer> {




    @Query('SELECT r FROM Report r WHERE r.reportDate >= :fromDate AND r.reportDate <= :toDate ORDER BY r.reportDate')
    List<Report> findByReportDateRange(@Param('fromDate') LocalDate fromDate,
                                       @Param('toDate') LocalDate toDate)


}
