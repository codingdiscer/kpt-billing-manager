package com.kinespherept.component

import com.kinespherept.model.core.Diagnosis
import com.kinespherept.model.core.DiagnosisType
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import spock.lang.Specification

class SelectDiagnosisControllerSpec extends Specification {

    // the class under test
    SelectDiagnosisController controller

    // supporting components
    SelectDiagnosisView selectDiagnosisView

    static {
        // force initialization JavaFX Toolkit
        new javafx.embed.swing.JFXPanel()
    }


    void setup() {
        selectDiagnosisView = Spy()
        controller = new SelectDiagnosisController(view: selectDiagnosisView)
    }



    def 'test prepareDisplayableDiagnosisList()'() {
        given:
        DiagnosisType dt1 = new DiagnosisType(diagnosisTypeName: 'typeA', displayOrder: 1)
        DiagnosisType dt2 = new DiagnosisType(diagnosisTypeName: 'typeB', displayOrder: 2)

        List<Diagnosis> list1 = [
                new Diagnosis(diagnosisTypeId: 1, diagnosisType: dt1, diagnosisName: 'nameA', displayOrder: 1, diagnosisCode: 'codeA'),
                new Diagnosis(diagnosisTypeId: 1, diagnosisType: dt1, diagnosisName: 'nameB', displayOrder: 2, diagnosisCode: 'codeB'),
                new Diagnosis(diagnosisTypeId: 2, diagnosisType: dt2, diagnosisName: 'nameC', displayOrder: 1, diagnosisCode: 'codeC'),
                new Diagnosis(diagnosisTypeId: 2, diagnosisType: dt2, diagnosisName: 'nameD', displayOrder: 2, diagnosisCode: 'codeD'),
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
