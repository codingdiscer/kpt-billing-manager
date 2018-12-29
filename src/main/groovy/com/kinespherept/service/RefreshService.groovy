package com.kinespherept.service

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class RefreshService {

    EmployeeService employeeService
    LookupDataService lookupDataService
    PatientService patientService


    RefreshService(EmployeeService employeeService, LookupDataService lookupDataService, PatientService patientService) {
        this.employeeService = employeeService
        this.lookupDataService = lookupDataService
        this.patientService = patientService
    }


    void refreshAllData() {
        log.info "refreshAllData() !"
        employeeService.init()
        lookupDataService.init()
        patientService.init()
    }

}
