application {
    title = 'Kinesphere PT Visit Status Manager'
    startupGroups = [
            // startup related screen
            'splash', 'navigation',

            // administration screens
            'setupDiagnoses',

            // patient related screens
            'administerPatients', 'schedulePatients', 'setupPatient', 'setupVisit',

            // therapist (select a patient for a visit)
            'selectPatientVisit',

            // therapist related screens (no treatments)
            'fillOutPatientVisitNoTreatment', 'verifyPatientVisitNoTreatment',

            // therapist related screens (with treatments)
            'fillOutPatientVisitWithTreatment', 'verifyPatientVisitWithTreatment',

            // visit-status related
            'trackVisitStatus',
            'viewVisitDetailsNoTreatment', 'editVisitDetailsNoTreatment',
            'viewVisitDetailsWithTreatment', 'editVisitDetailsWithTreatment',

            // sub-components
            'selectDiagnosis', 'selectTreatment',

            // startup related, but must go last because after this one, all the griffon components get wired together
            'login'
    ]

    autoShutdown = true
}
mvcGroups {

    /**
     * start-up (splash) and login screen
     */
    'splash' {
        model      = 'com.kinespherept.screen.SplashModel'
        view       = 'com.kinespherept.screen.SplashView'
        controller = 'com.kinespherept.screen.SplashController'
    }
    'login' {
        model      = 'com.kinespherept.screen.LoginModel'
        view       = 'com.kinespherept.screen.LoginView'
        controller = 'com.kinespherept.screen.LoginController'
    }


    /**
     * administration screens
     */
    'setupDiagnoses' {
        model      = 'com.kinespherept.screen.admin.SetupDiagnosesModel'
        view       = 'com.kinespherept.screen.admin.SetupDiagnosesView'
        controller = 'com.kinespherept.screen.admin.SetupDiagnosesController'
    }


    /**
     * patient related screens
     */
    'administerPatients' {
        model      = 'com.kinespherept.screen.patient.AdministerPatientsModel'
        view       = 'com.kinespherept.screen.patient.AdministerPatientsView'
        controller = 'com.kinespherept.screen.patient.AdministerPatientsController'
    }
    'schedulePatients' {
        model      = 'com.kinespherept.screen.patient.SchedulePatientsModel'
        view       = 'com.kinespherept.screen.patient.SchedulePatientsView'
        controller = 'com.kinespherept.screen.patient.SchedulePatientsController'
    }
    'setupPatient' {
        model      = 'com.kinespherept.screen.patient.SetupPatientModel'
        view       = 'com.kinespherept.screen.patient.SetupPatientView'
        controller = 'com.kinespherept.screen.patient.SetupPatientController'
    }
    'setupVisit' {
        model      = 'com.kinespherept.screen.patient.SetupVisitModel'
        view       = 'com.kinespherept.screen.patient.SetupVisitView'
        controller = 'com.kinespherept.screen.patient.SetupVisitController'
    }



    /**
     * therapist screens (select a patient to visit)
     */
    'selectPatientVisit' {
        model      = 'com.kinespherept.screen.therapist.SelectPatientVisitModel'
        view       = 'com.kinespherept.screen.therapist.SelectPatientVisitView'
        controller = 'com.kinespherept.screen.therapist.SelectPatientVisitController'
    }

    /**
     * therapist screens (no treatments)
     */
    'fillOutPatientVisitNoTreatment' {
        model      = 'com.kinespherept.screen.therapist.FillOutPatientVisitNoTreatmentModel'
        view       = 'com.kinespherept.screen.therapist.FillOutPatientVisitNoTreatmentView'
        controller = 'com.kinespherept.screen.therapist.FillOutPatientVisitNoTreatmentController'
    }
    'verifyPatientVisitNoTreatment' {
        model      = 'com.kinespherept.screen.therapist.VerifyPatientVisitNoTreatmentModel'
        view       = 'com.kinespherept.screen.therapist.VerifyPatientVisitNoTreatmentView'
        controller = 'com.kinespherept.screen.therapist.VerifyPatientVisitNoTreatmentController'
    }

    /**
     * therapist screens (with treatments)
     */
    'fillOutPatientVisitWithTreatment' {
        model      = 'com.kinespherept.screen.therapist.FillOutPatientVisitWithTreatmentModel'
        view       = 'com.kinespherept.screen.therapist.FillOutPatientVisitWithTreatmentView'
        controller = 'com.kinespherept.screen.therapist.FillOutPatientVisitWithTreatmentController'
    }
    'verifyPatientVisitWithTreatment' {
        model      = 'com.kinespherept.screen.therapist.VerifyPatientVisitWithTreatmentModel'
        view       = 'com.kinespherept.screen.therapist.VerifyPatientVisitWithTreatmentView'
        controller = 'com.kinespherept.screen.therapist.VerifyPatientVisitWithTreatmentController'
    }


    /**
     * visit-status related screens
     */
    'trackVisitStatus' {
        model      = 'com.kinespherept.screen.visitstatus.TrackVisitStatusModel'
        view       = 'com.kinespherept.screen.visitstatus.TrackVisitStatusView'
        controller = 'com.kinespherept.screen.visitstatus.TrackVisitStatusController'
    }
    'viewVisitDetailsNoTreatment' {
        model      = 'com.kinespherept.screen.visitstatus.ViewVisitDetailsNoTreatmentModel'
        view       = 'com.kinespherept.screen.visitstatus.ViewVisitDetailsNoTreatmentView'
        controller = 'com.kinespherept.screen.visitstatus.ViewVisitDetailsNoTreatmentController'
    }
    'editVisitDetailsNoTreatment' {
        model      = 'com.kinespherept.screen.visitstatus.EditVisitDetailsNoTreatmentModel'
        view       = 'com.kinespherept.screen.visitstatus.EditVisitDetailsNoTreatmentView'
        controller = 'com.kinespherept.screen.visitstatus.EditVisitDetailsNoTreatmentController'
    }
    'viewVisitDetailsWithTreatment' {
        model      = 'com.kinespherept.screen.visitstatus.ViewVisitDetailsWithTreatmentModel'
        view       = 'com.kinespherept.screen.visitstatus.ViewVisitDetailsWithTreatmentView'
        controller = 'com.kinespherept.screen.visitstatus.ViewVisitDetailsWithTreatmentController'
    }
    'editVisitDetailsWithTreatment' {
        model      = 'com.kinespherept.screen.visitstatus.EditVisitDetailsWithTreatmentModel'
        view       = 'com.kinespherept.screen.visitstatus.EditVisitDetailsWithTreatmentView'
        controller = 'com.kinespherept.screen.visitstatus.EditVisitDetailsWithTreatmentController'
    }



    /**
     * sub-components that get embedded into other screens
     */
    'navigation' {
        model      = 'com.kinespherept.component.NavigationModel'
        view       = 'com.kinespherept.component.NavigationView'
        controller = 'com.kinespherept.component.NavigationController'
    }
    'selectDiagnosis' {
        model      = 'com.kinespherept.component.SelectDiagnosisModel'
        view       = 'com.kinespherept.component.SelectDiagnosisView'
        controller = 'com.kinespherept.component.SelectDiagnosisController'
    }
    'selectTreatment' {
        model      = 'com.kinespherept.component.SelectTreatmentModel'
        view       = 'com.kinespherept.component.SelectTreatmentView'
        controller = 'com.kinespherept.component.SelectTreatmentController'
    }

}