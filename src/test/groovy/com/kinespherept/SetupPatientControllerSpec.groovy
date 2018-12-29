package com.kinespherept

import com.kinespherept.model.core.Diagnosis
import com.kinespherept.screen.patient.SetupPatientController
import com.kinespherept.screen.patient.SetupPatientView
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import spock.lang.Specification

class SetupPatientControllerSpec extends Specification {

    // the class under test
    SetupPatientController controller

    // supporting components
    SetupPatientView patientAddView

    static {
        // force initialization JavaFX Toolkit
        new javafx.embed.swing.JFXPanel()
    }


    void setup() {
        patientAddView = Spy()
        controller = new SetupPatientController(view: patientAddView)
    }



    def 'test prepareDisplayableDiagnosisList()'() {
        given:
        List<Diagnosis> list1 = [
                new Diagnosis(diagnosisTypeOrder: 1, diagnosisType: 'typeA', diagnosisName: 'nameA', displayOrder: 1, diagnosisCode: 'codeA'),
                new Diagnosis(diagnosisTypeOrder: 1, diagnosisType: 'typeA', diagnosisName: 'nameB', displayOrder: 2, diagnosisCode: 'codeB'),
                new Diagnosis(diagnosisTypeOrder: 2, diagnosisType: 'typeB', diagnosisName: 'nameC', displayOrder: 1, diagnosisCode: 'codeC'),
                new Diagnosis(diagnosisTypeOrder: 2, diagnosisType: 'typeB', diagnosisName: 'nameD', displayOrder: 2, diagnosisCode: 'codeD'),
        ]


        when:
        List<Labeled> outList = controller.prepareDisplayableDiagnosisList(list1)

        then:
        outList.size() == 6
        outList[0].class == Label
        outList[0].text == 'typeA'
        outList[1].class == Button
        outList[1].text == 'nameA (codeA)'
        outList[2].class == Button
        outList[2].text == 'nameB (codeB)'
        outList[3].class == Label
        outList[3].text == 'typeB'
        outList[4].class == Button
        outList[4].text == 'nameC (codeC)'
        outList[5].class == Button
        outList[5].text == 'nameD (codeD)'

    }


}
