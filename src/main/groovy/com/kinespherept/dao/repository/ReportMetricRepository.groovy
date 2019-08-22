package com.kinespherept.dao.repository

import com.kinespherept.model.core.ReportMetric
import org.springframework.data.repository.CrudRepository

interface ReportMetricRepository extends CrudRepository<ReportMetric, Long> {

    List<ReportMetric> findByReportId(Integer reportId)

}
