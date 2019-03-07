package com.kinespherept.screen.visitstatus

import com.kinespherept.BaseView
import com.kinespherept.autowire.PostSpringConstruct
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitStatus
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.LookupDataService
import com.kinespherept.service.VisitService
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.Separator
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints
import org.springframework.util.StringUtils

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class TrackVisitStatusView extends BaseView {

    // magic strings!
    static String STYLE_BLACK_BORDER = '-fx-border-color: black;'

    // magic numbers!
    static double COLUMN_WIDTH_DATE_OF_SERVICE  = 80.0
    static double COLUMN_WIDTH_VISIT_NUMBER     = 30.0
    static double COLUMN_WIDTH_DX_CHANGE        = 50.0
    static double COLUMN_WIDTH_TX_CODES         = 200.0
    static double COLUMN_WIDTH_TX_COUNT         = 30.0
    static double COLUMN_WIDTH_NOTES            = 50.0
    static double COLUMN_WIDTH_DETAILS          = 60.0
    static double COLUMN_WIDTH_ACTIONS          = 380.0
    static double COLUMN_WIDTH_VISIT_TYPE       = 100.0
    static double COLUMN_WIDTH_STATUS           = 120.0

    static double COLUMN_WIDTH_PATIENT_NAME     = 150.0
    static double COLUMN_WIDTH_INSURANCE        = 80.0
    static double COLUMN_WIDTH_THERAPIST        = 50.0




    // magic string!
    static String SELECT_STATUS = 'Select a status..'

    @MVCMember @Nonnull TrackVisitStatusController controller
    @MVCMember @Nonnull TrackVisitStatusModel model

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire VisitService visitService

    // panes that hold other components
    @FXML AnchorPane navigationPane
    @FXML AnchorPane rootAnchorPane

    @FXML ProgressIndicator spinner
    @FXML Button clearPatientSearchButton

    // the grid panes that display the results
    @FXML GridPane visitHeadersGridPane
    @FXML GridPane visitResultsGridPane

    void showSpinner(boolean showSpinner) {
        spinner.visible = showSpinner
    }


    ColumnConstraints buildColumnConstraints(double width) {
        new ColumnConstraints(prefWidth: width, minWidth: width, maxWidth: width)
    }

    Label buildLabel(String text, double width) {
        new Label(text: text, alignment: Pos.CENTER, prefWidth: width, minWidth: width, maxWidth: width,
                style: STYLE_BLACK_BORDER)
    }

    Label buildLabel(String text, double width, String tooltip) {
        new Label(text: text, alignment: Pos.CENTER, prefWidth: width, minWidth: width, maxWidth: width,
                style: STYLE_BLACK_BORDER, tooltip: new Tooltip(tooltip))
    }

    void prepareGridsByStatus() {
        visitHeadersGridPane.hgap = 1
        visitHeadersGridPane.columnConstraints.clear()
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DATE_OF_SERVICE)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_VISIT_NUMBER)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_PATIENT_NAME)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_INSURANCE)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_THERAPIST)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DX_CHANGE)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_TX_CODES)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_TX_COUNT)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_NOTES)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DETAILS)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_ACTIONS)

        visitHeadersGridPane.rowConstraints.clear()
        visitHeadersGridPane.rowConstraints << new RowConstraints(prefHeight: 30)

        int columnIndex = 0

        visitHeadersGridPane.children.clear()
        visitHeadersGridPane.add(buildLabel('DoS', COLUMN_WIDTH_DATE_OF_SERVICE, 'Date Of Service'), columnIndex++, 0)

        visitHeadersGridPane.add(buildLabel('Vst #', COLUMN_WIDTH_VISIT_NUMBER,'Visit number'), columnIndex++, 0)

        visitHeadersGridPane.add(buildLabel('Patient Name', COLUMN_WIDTH_PATIENT_NAME), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Insurance', COLUMN_WIDTH_INSURANCE), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Thrpst', COLUMN_WIDTH_THERAPIST), columnIndex++, 0)

        visitHeadersGridPane.add(buildLabel('Dx Chg', COLUMN_WIDTH_DX_CHANGE,'Indicates if diagnoses changed from the previous visit'),columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Tx Codes', COLUMN_WIDTH_TX_CODES,'List of the performed treatment codes'), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('#', COLUMN_WIDTH_TX_COUNT,'The total number of performed treatments'), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Notes', COLUMN_WIDTH_NOTES,'Indicates if notes were taken during the visit'), columnIndex++, 0)

        visitHeadersGridPane.add(buildLabel('See Visit', COLUMN_WIDTH_DETAILS), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Actions (Set status to...)',COLUMN_WIDTH_ACTIONS), columnIndex++, 0)

        visitResultsGridPane.hgap = 1
        visitResultsGridPane.columnConstraints.clear()
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DATE_OF_SERVICE)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_VISIT_NUMBER)

        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_PATIENT_NAME)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_INSURANCE)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_THERAPIST)

        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DX_CHANGE)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_TX_CODES)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_TX_COUNT)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_NOTES)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DETAILS)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_ACTIONS)

        visitResultsGridPane.rowConstraints.clear()
        visitResultsGridPane.rowConstraints << new RowConstraints(prefHeight: 30)
    }


    void prepareGridsByPatient() {
        visitHeadersGridPane.hgap = 1
        visitHeadersGridPane.columnConstraints.clear()
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DATE_OF_SERVICE)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_VISIT_NUMBER)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_VISIT_TYPE)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_STATUS)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_INSURANCE)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_THERAPIST)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DX_CHANGE)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_TX_CODES)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_TX_COUNT)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_NOTES)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DETAILS)
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_ACTIONS)

        visitHeadersGridPane.rowConstraints.clear()
        visitHeadersGridPane.rowConstraints << new RowConstraints(prefHeight: 30)

        int columnIndex = 0

        visitHeadersGridPane.children.clear()
        visitHeadersGridPane.add(buildLabel('DoS', COLUMN_WIDTH_DATE_OF_SERVICE, 'Date Of Service'), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Vst #', COLUMN_WIDTH_VISIT_NUMBER, 'Visit number'), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Visit Type', COLUMN_WIDTH_VISIT_TYPE), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Status', COLUMN_WIDTH_STATUS), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Insurance', COLUMN_WIDTH_INSURANCE), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Thrpst', COLUMN_WIDTH_THERAPIST), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Dx Chg', COLUMN_WIDTH_DX_CHANGE, 'Indicates if diagnoses changed from the previous visit'),columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Tx Codes', COLUMN_WIDTH_TX_CODES, 'List of the performed treatment codes'), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('#', COLUMN_WIDTH_TX_COUNT,'The total number of performed treatments'), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Notes', COLUMN_WIDTH_NOTES, 'Indicates if notes were taken during the visit'), columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('See Visit', COLUMN_WIDTH_DETAILS),columnIndex++, 0)
        visitHeadersGridPane.add(buildLabel('Actions (Set status to...)', COLUMN_WIDTH_ACTIONS), columnIndex++, 0)

        visitResultsGridPane.hgap = 1
        visitResultsGridPane.columnConstraints.clear()
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DATE_OF_SERVICE)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_VISIT_NUMBER)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_VISIT_TYPE)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_STATUS)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_INSURANCE)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_THERAPIST)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DX_CHANGE)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_TX_CODES)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_TX_COUNT)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_NOTES)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_DETAILS)
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_ACTIONS)

        visitResultsGridPane.rowConstraints.clear()
        visitResultsGridPane.rowConstraints << new RowConstraints(prefHeight: 30)
    }


    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }

    void initUI() {
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.TRACK_VISIT_STATUS, scene)
    }

    @PostSpringConstruct
    void initAfterSpring() {
        rootAnchorPane.style = commonProperties.statusTrackerBackground
        clearPatientSearchButton.text = 'Clear'
    }


    void buildResultRowByStatus(Visit visit, int rowNumber) {
        int columnIndex = 0

        // date, visit number, last name, first name
        visitResultsGridPane.add(buildLabel(visit.visitDate.format(commonProperties.dateFormatter),COLUMN_WIDTH_DATE_OF_SERVICE), columnIndex++, rowNumber)
        visitResultsGridPane.add(buildLabel(visit.visitNumber > 0 ? String.valueOf(visit.visitNumber) : '',
                COLUMN_WIDTH_VISIT_NUMBER), columnIndex++, rowNumber)

        visitResultsGridPane.add(new Label(text: "${visit.patient.lastName}, ${visit.patient.firstName}", prefWidth: COLUMN_WIDTH_PATIENT_NAME, style: STYLE_BLACK_BORDER), columnIndex++, rowNumber)
        visitResultsGridPane.add(buildLabel(visit.insuranceType.insuranceTypeName, COLUMN_WIDTH_INSURANCE), columnIndex++, rowNumber)
        visitResultsGridPane.add(buildLabel(visit.therapist.fullname.substring(0, visit.therapist.fullname.indexOf(' ')), COLUMN_WIDTH_THERAPIST), columnIndex++, rowNumber)

        // dx
        visitResultsGridPane.add(buildLabel(visit.sameDiagnosisAsPrevious ? 'YES' : '', COLUMN_WIDTH_DX_CHANGE), columnIndex++, rowNumber)

        // tx
        String txText = visit.visitTreatments.collect {
            // see if more than 1 treatment (if so, be sure to include it)
            it.treatmentQuantity > 1 ?
                    "${lookupDataService.findTreatmentById(it.treatmentId).treatmentCode} (${it.treatmentQuantity})" : // yep, so show the quantity
                    lookupDataService.findTreatmentById(it.treatmentId).treatmentCode  // nope, just 1
        }.join(', ')
        //Label txLabel = new Label(text: txText, prefWidth: COLUMN_WIDTH_TX_CODES, style: STYLE_BLACK_BORDER, tooltip: new Tooltip(txText))
        visitResultsGridPane.add(buildLabel(txText, COLUMN_WIDTH_TX_CODES, txText), columnIndex++, rowNumber)

        // tx count
        int txCount = visit.visitTreatments.collect { it.treatmentQuantity }.inject(0) { a, b -> a + b}
        visitResultsGridPane.add(buildLabel(String.valueOf(txCount), COLUMN_WIDTH_TX_COUNT), columnIndex++, rowNumber)

        // notes ?
        Label notesLabel = StringUtils.isEmpty(visit.notes) ?
                buildLabel('', COLUMN_WIDTH_NOTES) :
                buildLabel('YES',  COLUMN_WIDTH_NOTES, visit.notes)
        visitResultsGridPane.add(notesLabel, columnIndex++, rowNumber)

        // view details button
        visitResultsGridPane.add(new Button(text: 'Details', onAction: { a -> controller.viewVisitDetails(visit) } ),
                columnIndex++, rowNumber)

        // action button(s)
        visitResultsGridPane.add(buildActionButtonFlowPane(visit), columnIndex++, rowNumber)
    }


    void buildResultRowByPatient(Visit visit, int rowNumber) {
        int columnIndex = 0

        // date, visit number, last name, first name
        visitResultsGridPane.add(buildLabel(visit.visitDate.format(commonProperties.dateFormatter), COLUMN_WIDTH_DATE_OF_SERVICE), columnIndex++, rowNumber)
        visitResultsGridPane.add(buildLabel(visit.visitNumber > 0 ? String.valueOf(visit.visitNumber) : '', COLUMN_WIDTH_VISIT_NUMBER), columnIndex++, rowNumber)

        // visit type
        visitResultsGridPane.add(buildLabel(visit.visitType?.visitTypeName, COLUMN_WIDTH_VISIT_TYPE), columnIndex++, rowNumber)

        // visit status
        visitResultsGridPane.add(buildLabel(visit.visitStatus?.text, COLUMN_WIDTH_STATUS), columnIndex++, rowNumber)
        visitResultsGridPane.add(buildLabel(visit.insuranceType.insuranceTypeName, COLUMN_WIDTH_INSURANCE), columnIndex++, rowNumber)
        visitResultsGridPane.add(buildLabel(visit.therapist.fullname.substring(0, visit.therapist.fullname.indexOf(' ')), COLUMN_WIDTH_THERAPIST), columnIndex++, rowNumber)

        // dx
        visitResultsGridPane.add(buildLabel(visit.sameDiagnosisAsPrevious ? 'YES' : '', COLUMN_WIDTH_DX_CHANGE), columnIndex++, rowNumber)

        // tx
        String txText = visit.visitTreatments.collect {
            // see if more than 1 treatment (if so, be sure to include it)
            it.treatmentQuantity > 1 ?
                    "${lookupDataService.findTreatmentById(it.treatmentId).treatmentCode} (${it.treatmentQuantity})" : // yep, so show the quantity
                    lookupDataService.findTreatmentById(it.treatmentId).treatmentCode  // nope, just 1
        }.join(', ')
        visitResultsGridPane.add(buildLabel(txText, COLUMN_WIDTH_TX_CODES, txText), columnIndex++, rowNumber)

        // tx count
        int txCount = visit.visitTreatments.collect { it.treatmentQuantity }.inject(0) { a, b -> a + b}
        visitResultsGridPane.add(buildLabel(String.valueOf(txCount), COLUMN_WIDTH_TX_COUNT), columnIndex++, rowNumber)

        // notes ?
        Label notesLabel = StringUtils.isEmpty(visit.notes) ?
                buildLabel('', COLUMN_WIDTH_NOTES) :
                buildLabel('YES',  COLUMN_WIDTH_NOTES, visit.notes)
        visitResultsGridPane.add(notesLabel, columnIndex++, rowNumber)

        // view details button
        visitResultsGridPane.add(new Button(text: 'Details', onAction: { a -> controller.viewVisitDetails(visit) } ),
                columnIndex++, rowNumber)

        // action button(s)
        visitResultsGridPane.add(buildActionButtonFlowPane(visit),columnIndex++, rowNumber)
    }



    /**
     * Builds a button with the label of the given VisitStatus, and when clicked, changes the visit to that status
     */
    Button buildButtonForStatus(Visit visit, VisitStatus visitStatus) {
        new Button(text: visitStatus.text, id: visit.visitId,
                onAction: { a -> controller.changeVisitStatus(visit, visitStatus)})
    }

    /**
     * Builds the FlowPane of status change options for the given visit
     */
    FlowPane buildActionButtonFlowPane(Visit visit) {
        FlowPane flowPane = new FlowPane(alignment: Pos.CENTER_LEFT)

        // prepare the list of all status to change to (exclude the current status)
        List<String> options = [SELECT_STATUS]
        options.addAll(VisitStatus.values().findAll { it != visit.visitStatus }.collect { it.text })

        // create the drop-down box and add the options
        ChoiceBox<String> directSetChoiceBox = new ChoiceBox<>()
        directSetChoiceBox.items.addAll(options)
        directSetChoiceBox.selectionModel.select(SELECT_STATUS)

        // add the choice box and button behind it
        flowPane.children.add(directSetChoiceBox)
        flowPane.children.add(new Button(text: 'Change status', id: visit.visitId,
                onAction: { a ->
                     if(directSetChoiceBox.selectionModel.selectedItem != SELECT_STATUS) {
                         controller.changeVisitStatus(visit,
                                 VisitStatus.findFromText(directSetChoiceBox.selectionModel.selectedItem))
                     }
                }))

        flowPane
    }

}