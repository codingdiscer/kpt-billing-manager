package com.kinespherept.config

import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.format.DateTimeFormatter

/**
 * This class contains common properties that could be externally loaded, but have
 * useful defaults otherwise.
 */
@Component
class CommonProperties {

    /**
     * the maximum number of diagnoses that can be applied per patient visit (at the visit level)
     */
    int maxDiagnosisForPatientVisit = 4

    /**
     * The format to use when displaying a date.
     */
    String dateFormat = 'MM-dd-yyyy'

    /**
     * The object used to perform the date formatting, where the pattern is set from the dateFormat property.
     */
    DateTimeFormatter dateFormatter

    /**
     * Color defaults
     */
    String schedulerBackground = '-fx-background-image: url("bg-schedule.png");'
    String patientsBackground = '-fx-background-image: url("bg-patients.jpg");'
    String therapistBackground = '-fx-background-image: url("bg-therapist.png");'
    String statusTrackerBackground = '-fx-background-image: url("bg-tracker.png");'
    String addDiagnosisBackground = '-fx-background-image: url("bg-diagnosis.png");'
    String metricsBackgroundColor = "BLUE"



    @PostConstruct
    void init() {
        dateFormatter = DateTimeFormatter.ofPattern(dateFormat)
    }

}
