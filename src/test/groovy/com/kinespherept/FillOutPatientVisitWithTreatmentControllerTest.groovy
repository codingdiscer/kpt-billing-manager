package com.kinespherept

import com.kinespherept.screen.therapist.FillOutPatientVisitWithTreatmentController
import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import org.junit.Rule
import org.junit.Test

import static org.junit.Assert.fail

@TestFor(FillOutPatientVisitWithTreatmentController)
class FillOutPatientVisitWithTreatmentControllerTest {
    static {
        // force initialization JavaFX Toolkit
        new javafx.embed.swing.JFXPanel()
    }

    private FillOutPatientVisitWithTreatmentController controller

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Test
    void smokeTest() {
        fail('Not yet implemented!')
    }
}