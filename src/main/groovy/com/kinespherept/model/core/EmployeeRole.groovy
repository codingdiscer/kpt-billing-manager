package com.kinespherept.model.core

/**
 * The different roles that have specialized permissions within the system.
 *
 * Rules (as of 2018-02-25):
 * - Roles do not inherit permissions from other roles
 *
 * ** CAUTION **
 * The order of the roles declared below determines their index as stored in the db.
 * Therefore - CHANGING THE ORDER OF THE ENTRIES CHANGES THE ROLES IN THE DATABASE!!
 *
 */
enum EmployeeRole {

    /**
     * CAUTION -- CHANGING THE ORDER OF THE ENTRIES CHANGES THE ROLES IN THE DATABASE!!
     */

    // admins
    SUPER_ADMIN,            // id=0
    ADMINISTRATOR,          // id=1

    THERAPIST,              // id=2

    SCHEDULER,              // id=3
    INSURANCE_BILLER,       // id=4

    DIAGNOSIS_CODER,        // id=5

    PATIENTS                // id=6

}
