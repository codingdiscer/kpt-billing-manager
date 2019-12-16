package com.kinespherept.model.navigation

/**
 * Representations of all the scenes built for this application.
 *
 * The intent is to put the width/height, names and labels together in one place.
 *
 */
enum SceneDefinition {

    SPLASH('', 550, 520),
    LOGIN('Login', 600, 450),

    // admin screens
    SETUP_DIAGNOSES('Setup Diagnoses', 960, 580),

    // patient screens
    ADMINISTER_PATIENTS('Administer Patients', 630, 450),
    SCHEDULE_PATIENTS('Setup and Schedule Patients', 600, 500),
    SETUP_VISIT('Setup a Patient Visit', 400, 400),
    SETUP_PATIENT('Add or update a Patient', 420, 500),

    SELECT_PATIENT_VISIT('Select a patient', 340, 450),

    // therapist screens (no treatment)
    FILL_OUT_PATIENT_VISIT_NO_TREATMENT('A day in the life of a therapist', 680, 480),
    VERIFY_PATIENT_VISIT_NO_TREATMENT('Verify the detail of the visit', 260, 450),

    // therapist screens (with treatment)
    FILL_OUT_PATIENT_VISIT_WITH_TREATMENT('A day in the life of a therapist', 1240, 480),
    VERIFY_PATIENT_VISIT_WITH_TREATMENT('Verify the detail of the visit', 800, 400),

    // visit-status screens
    TRACK_VISIT_STATUS('Track Billing for Patient Visits', 1200, 500),
    // ... no treatment
    EDIT_VISIT_DETAILS_NO_TREATMENT('Edit Details', 320, 470),
    VIEW_VISIT_DETAILS_NO_TREATMENT('Visit Details', 260, 520),
    // ... with treatment
    VIEW_VISIT_DETAILS_WITH_TREATMENT('Visit Details', 800, 400),
    EDIT_VISIT_DETAILS_WITH_TREATMENT('Edit Details', 950, 480),

    // reports
    MANAGE_REPORTS('Manage Reports', 715, 480),
    BROWSE_REPORTS('Browse Reports', 1200, 500),

    // sub-components...
    DIAGNOSIS_SELECTOR('Diagnosis Selector', 280, 350),
    TREATMENT_SELECTOR('Treatment Selector', 310, 350),
    NAVIGATION('Navigation', 100, 400),

    VISIT_TYPE_MAIN('Visit Type Main', 600, 400),
    DATE_PICKER('Date Picker', 600, 400)


    private SceneDefinition(String title, int width, int height) {
        this.title = title
        this.width = width
        this.height = height
    }

    String title
    int width
    int height

}
