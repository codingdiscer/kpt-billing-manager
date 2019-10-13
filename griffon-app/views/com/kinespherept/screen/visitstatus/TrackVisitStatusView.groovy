package com.kinespherept.screen.visitstatus

import com.kinespherept.BaseView
import com.kinespherept.autowire.PostSpringConstruct
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.CommonProperties
import com.kinespherept.enums.SearchType
import com.kinespherept.model.core.Visit
import com.kinespherept.model.core.VisitStatus
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.service.EmployeeService
import com.kinespherept.service.LookupDataService
import com.kinespherept.service.VisitService
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.RadioButton
import javafx.scene.control.Separator
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
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
    static String SELECT_STATUS = 'Select a status..'

    // magic numbers!

    // shared column types
    static double COLUMN_WIDTH_DATE_OF_SERVICE      = 80.0
    static double COLUMN_WIDTH_VISIT_NUMBER         = 30.0
    static double COLUMN_WIDTH_DX_CHANGE            = 50.0
    static double COLUMN_WIDTH_TX_CODES             = 200.0
    static double COLUMN_WIDTH_TX_COUNT             = 30.0
    static double COLUMN_WIDTH_NOTES                = 50.0
    static double COLUMN_WIDTH_DETAILS              = 60.0

    // patient related
    static double COLUMN_WIDTH_VISIT_TYPE           = 100.0
    static double COLUMN_WIDTH_STATUS               = 120.0


    // status related
    static double COLUMN_WIDTH_PATIENT_NAME         = 150.0
    static double COLUMN_WIDTH_INSURANCE            = 80.0
    static double COLUMN_WIDTH_THERAPIST            = 50.0

    // action columns
    static double COLUMN_WIDTH_SELECT_FOR_UPDATE    = 270.0

    // various field widths
    static double CHOICE_BOX_WIDTH                  = 150.0
    static double TEXTFIELD_PATIENT_SEARCH_WIDTH    = 100.0
    static double BUTTON_UPDATE_STATUSES            = 105.0


    @MVCMember @Nonnull TrackVisitStatusController controller
    @MVCMember @Nonnull TrackVisitStatusModel model

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire EmployeeService employeeService
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

    @FXML FlowPane searchFilterFlowPane
    @FXML ToggleGroup searchTypeFilter
    @FXML RadioButton patientSearchRadioButton
    @FXML RadioButton statusSearchRadioButton
    @FXML Button selectAllButton
    @FXML Button unselectAllButton
    @FXML ChoiceBox<String> changeToStatusesChoiceBox
    @FXML Button updateStatusButton

    /**
     * This property is sorta hacky, but it sets aside the ChoiceBox that will hold the
     * "filtered patients" search results.  This is necessary because this field is built on-the-fly,
     * and occasionally we need to reference it to select a particular entry
     */
    ChoiceBox<String> filteredPatients = null

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
        patientSearchRadioButton.text = 'Search by Patient'
        statusSearchRadioButton.text = 'Search by Status/Ins/Thrpst'
        updateStatusButton.text = 'Update Statuses'
        updateStatusButton.prefWidth = BUTTON_UPDATE_STATUSES
        allowAffectAllRowsButtons(false)
    }


    void showSpinner(boolean showSpinner) {
        spinner.visible = showSpinner
    }

    /**
     * This method is sorta hacky, but the goal is to allow a way to select a specific
     * entry from the generated-on-the-fly "filteredPatients" choicebox field
     */
    void selectFilteredPatient(String patient) {
        filteredPatients?.selectionModel.select(patient)
    }

    /**
     * If true, enables the 'select new status' drop-down & 'update statuses' button; if false, disables them
     */
    void allowMultiRowUpdate(boolean allow) {
        updateStatusButton.disable = !allow
        changeToStatusesChoiceBox.disable = !allow
    }

    /**
     * If true, enables the 'select all' and 'unselect all' buttons; if false, disables them
     * ..this would be used to enable/disable the buttons depending if results are present
     */
    void allowAffectAllRowsButtons(boolean allow) {
        selectAllButton.disable = !allow
        unselectAllButton.disable = !allow
    }


    /**
     * This method is a bit of a hack.  It is called when the user changes the value
     * in the "patient search" filter, and that generates a new set of patients
     * that match the search.  So this method rebuilds the drop-down box that holds
     * the results, and replaces the previous drop-down element within the
     * appropriate FlowPane.
     */
    void resetFilteredPatients() {
        filteredPatients = buildChoiceBox(FXCollections.observableList(model.patients), model.patientsChoice)
        filteredPatients.onAction = { a -> controller.selectPatient(filteredPatients.selectionModel.getSelectedItem()) }
        // the "filtered patients drop-down" is the 5th element in the FlowPane..
        searchFilterFlowPane.children.set(4, filteredPatients)
    }


    /**
     * Dynamically builds the 2nd row search fields.
     * When [SearchType.Patient] -> TextField[search filter]; ChoiceBox[filtered patients]; ChoiceBox[status filter]
     * When [SearchType.Status] -> ChoiceBox[status filter]; ChoiceBox[insurance patients]; ChoiceBox[therapist filter]
     */
    void prepareSearchFilterRow(SearchType searchType) {
        searchFilterFlowPane.children.clear()

        if(searchType == SearchType.PATIENT) {

            // label and textfield to search for names
            searchFilterFlowPane.children.add(new Label(text: 'Patient search '))

            TextField tf = new TextField(prefWidth: TEXTFIELD_PATIENT_SEARCH_WIDTH)
            tf.textProperty().bindBidirectional(model.patientSearchProperty)
            searchFilterFlowPane.children.add(tf)

            // button to clear the patient search
            Button button = new Button(text: 'Clear')
            button.onAction = { a -> controller.clearPatientSearch() }
            searchFilterFlowPane.children.add(button)

            // separator
            searchFilterFlowPane.children.add(buildSmallVerticalSeparator())

            // choicebox filled with filtered patient names
            filteredPatients = buildChoiceBox(FXCollections.observableList(model.patients), model.patientsChoice)
            filteredPatients.onAction = { a -> controller.selectPatient(filteredPatients.selectionModel.getSelectedItem()) }
            searchFilterFlowPane.children.add(filteredPatients)

            // separator
            searchFilterFlowPane.children.add(buildSmallVerticalSeparator())

            // label and choicebox of status filters
            searchFilterFlowPane.children.add(new Label(text: 'Status '))
            ChoiceBox<String> patientStatusChoiceBox = buildChoiceBox(model.patientTypeVisitStatuses, model.patientTypeVisitStatusesChoice)
            patientStatusChoiceBox.onAction = { a -> controller.changePatientTypeVisitStatus(patientStatusChoiceBox.selectionModel.selectedItem) }
            searchFilterFlowPane.children.add(patientStatusChoiceBox)
        } else if(searchType == SearchType.STATUS) {
            // label and ChoiceBox for status options
            searchFilterFlowPane.children.add(new Label(text: 'Status '))
            ChoiceBox<String> statusChoiceBox = buildChoiceBox(model.statusTypeVisitStatuses, model.statusTypeVisitStatusesChoice)
            statusChoiceBox.onAction = { a -> controller.changeStatusTypeVisitStatus(statusChoiceBox.selectionModel.selectedItem) }
            searchFilterFlowPane.children.add(statusChoiceBox)
            // separator
            searchFilterFlowPane.children.add(buildSmallVerticalSeparator())

            // label and ChoiceBox for insurance options
            searchFilterFlowPane.children.add(new Label(text: 'Insurance '))
            ChoiceBox<String> insuranceChoiceBox = buildChoiceBox(model.insuranceTypes, model.insuranceTypesChoice)
            insuranceChoiceBox.onAction = { a -> controller.changeInsuranceType(insuranceChoiceBox.selectionModel.selectedItem) }
            searchFilterFlowPane.children.add(insuranceChoiceBox)

            // separator
            searchFilterFlowPane.children.add(buildSmallVerticalSeparator())

            // label and ChoiceBox for therapist options
            searchFilterFlowPane.children.add(new Label(text: 'Therapist '))
            ChoiceBox<String> therapistChoiceBox = buildChoiceBox(model.therapists, model.therapistsChoice)
            therapistChoiceBox.onAction = { a -> controller.changeTherapist(therapistChoiceBox.selectionModel.selectedItem) }
            searchFilterFlowPane.children.add(therapistChoiceBox)
        }
    }


    /**
     * Builds a ChoiceBox of the given width, tied to the given list of items, with the given selected option.
     */
    ChoiceBox<String> buildChoiceBox(javafx.collections.ObservableList items, String selected) {
        ChoiceBox<String> choiceBox = new ChoiceBox<String>(prefWidth: CHOICE_BOX_WIDTH, items: items)
        choiceBox.selectionModel.select(selected)
        choiceBox
    }

    // builds a small separator UI element (we need a bunch, and you can't re-use a single static one)
    Separator buildSmallVerticalSeparator() {
        new Separator(orientation: Orientation.VERTICAL, prefHeight: 20, prefWidth: 20)
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

    Label buildLabel(boolean disabled, String text, double width) {
        Label label = buildLabel(text, width)
        label.disable = disabled
        label
    }

    Label buildLabel(boolean disabled, String text, double width, String tooltip) {
        Label label = buildLabel(text, width, tooltip)
        label.disable = disabled
        label
    }


    /**
     * Prepares the header & results grid for a "status search"
     */
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
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_SELECT_FOR_UPDATE)

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

        visitHeadersGridPane.add(buildLabel('Select to Change',COLUMN_WIDTH_SELECT_FOR_UPDATE), columnIndex++, 0)

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
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_SELECT_FOR_UPDATE)

        visitResultsGridPane.rowConstraints.clear()
        visitResultsGridPane.rowConstraints << new RowConstraints(prefHeight: 30)
    }

    /**
     * Prepares the header & results grid for a "patient search"
     */
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
        visitHeadersGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_SELECT_FOR_UPDATE)

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
        visitHeadersGridPane.add(buildLabel('Select for Update',COLUMN_WIDTH_SELECT_FOR_UPDATE), columnIndex++, 0)


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
        visitResultsGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_SELECT_FOR_UPDATE)

        visitResultsGridPane.rowConstraints.clear()
        visitResultsGridPane.rowConstraints << new RowConstraints(prefHeight: 30)
    }


    /**
     * Builds a result row for a "status search" for a single visit
     */
    void buildResultRowByStatus(Visit visit, int rowNumber) {
        int columnIndex = 0

        // date, visit number, last name, first name
        visitResultsGridPane.add(buildLabel(visit.visitDate.format(commonProperties.dateFormatter),COLUMN_WIDTH_DATE_OF_SERVICE), columnIndex++, rowNumber)
        visitResultsGridPane.add(buildLabel(visit.visitNumber > 0 ? String.valueOf(visit.visitNumber) : '',
                COLUMN_WIDTH_VISIT_NUMBER), columnIndex++, rowNumber)

        //visitResultsGridPane.add(new Label(text: "${visit.patient.lastName}, ${visit.patient.firstName}", prefWidth: COLUMN_WIDTH_PATIENT_NAME, style: STYLE_BLACK_BORDER), columnIndex++, rowNumber)
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


        // add the 'change status' flow pane
        visitResultsGridPane.add(buildChangeStatusFlowPane(visit), columnIndex++, rowNumber)
    }


    /**
     * Builds a result row for a "patient search" for a single visit
     */
    void buildResultRowByPatient(Visit visit, int rowNumber) {
        int columnIndex = 0

        boolean disabledLabel = false

        if(visit.visitStatus == VisitStatus.PAID_IN_FULL) {
            disabledLabel = true
        }

        // date, visit number
        visitResultsGridPane.add(buildLabel(disabledLabel, visit.visitDate.format(commonProperties.dateFormatter), COLUMN_WIDTH_DATE_OF_SERVICE), columnIndex++, rowNumber)
        visitResultsGridPane.add(buildLabel(disabledLabel, visit.visitNumber > 0 ? String.valueOf(visit.visitNumber) : '', COLUMN_WIDTH_VISIT_NUMBER), columnIndex++, rowNumber)

        // visit type
        visitResultsGridPane.add(buildLabel(disabledLabel, visit.visitType?.visitTypeName, COLUMN_WIDTH_VISIT_TYPE), columnIndex++, rowNumber)

        // visit status
        visitResultsGridPane.add(buildLabel(disabledLabel, visit.visitStatus?.text, COLUMN_WIDTH_STATUS), columnIndex++, rowNumber)
        visitResultsGridPane.add(buildLabel(disabledLabel, visit.insuranceType.insuranceTypeName, COLUMN_WIDTH_INSURANCE), columnIndex++, rowNumber)
        visitResultsGridPane.add(buildLabel(disabledLabel, visit.therapist.fullname.substring(0, visit.therapist.fullname.indexOf(' ')), COLUMN_WIDTH_THERAPIST), columnIndex++, rowNumber)

        // dx
        visitResultsGridPane.add(buildLabel(disabledLabel, visit.sameDiagnosisAsPrevious ? 'YES' : '', COLUMN_WIDTH_DX_CHANGE), columnIndex++, rowNumber)

        // tx
        String txText = visit.visitTreatments.collect {
            // see if more than 1 treatment (if so, be sure to include it)
            it.treatmentQuantity > 1 ?
                    "${lookupDataService.findTreatmentById(it.treatmentId).treatmentCode} (${it.treatmentQuantity})" : // yep, so show the quantity
                    lookupDataService.findTreatmentById(it.treatmentId).treatmentCode  // nope, just 1
        }.join(', ')
        visitResultsGridPane.add(buildLabel(disabledLabel, txText, COLUMN_WIDTH_TX_CODES, txText), columnIndex++, rowNumber)

        // tx count
        int txCount = visit.visitTreatments.collect { it.treatmentQuantity }.inject(0) { a, b -> a + b}
        visitResultsGridPane.add(buildLabel(disabledLabel, String.valueOf(txCount), COLUMN_WIDTH_TX_COUNT), columnIndex++, rowNumber)

        // notes ?
        Label notesLabel = StringUtils.isEmpty(visit.notes) ?
                buildLabel(disabledLabel, '', COLUMN_WIDTH_NOTES) :
                buildLabel(disabledLabel, 'YES',  COLUMN_WIDTH_NOTES, visit.notes)
        visitResultsGridPane.add(notesLabel, columnIndex++, rowNumber)

        // view details button
        visitResultsGridPane.add(new Button(text: 'Details', onAction: { a -> controller.viewVisitDetails(visit) } ),
                columnIndex++, rowNumber)

        // add the 'change status' flow pane
        visitResultsGridPane.add(buildChangeStatusFlowPane(visit), columnIndex++, rowNumber)
    }

    /**
     * Builds the FlowPane of status change options for the given visit, along the lines of:
     * - CheckBox<visitId>  ChoiceBox<VisitStatus>  Button<"Change This Entry">
     */
    FlowPane buildChangeStatusFlowPane(Visit visit) {
        FlowPane flowPane = new FlowPane(alignment: Pos.CENTER_LEFT)

        // build the check box associated with this row
        CheckBox checkBox = new CheckBox(text: '', selected: false, id: String.valueOf(visit.visitId))
        checkBox.onAction = { a -> controller.rowSelectStatusChanged() }

        // prepare the list of all status to change to (exclude the current status)
        List<String> options = [SELECT_STATUS]
        options.addAll(VisitStatus.values().findAll { it != visit.visitStatus }.collect { it.text })

        // create the drop-down box and add the options
        ChoiceBox<String> directSetChoiceBox = new ChoiceBox<>(id: visit.visitId)
        directSetChoiceBox.items.addAll(options)
        directSetChoiceBox.selectionModel.select(SELECT_STATUS)


        // create the 'change this row' button (only good for this row)
        Button button = new Button(text: 'Change this row', id: visit.visitId,
                onAction: { a ->
                    if(directSetChoiceBox.selectionModel.selectedItem != SELECT_STATUS) {
                        controller.changeVisitStatus(visit,
                                VisitStatus.findFromText(directSetChoiceBox.selectionModel.selectedItem))
                    } else {
                        model.errorMessage = 'Must select a status on the same row as the button you pressed.'
                    }
                })

        // add the components to the flow pane
        flowPane.children.add(checkBox)
        flowPane.children.add(directSetChoiceBox)
        flowPane.children.add(button)

        // add the relevant components to the model (so we can manipulate them!)
        model.visitCheckBoxes << checkBox
        model.statusChoiceBoxes << directSetChoiceBox
        model.updateStatusButtons << button

        flowPane
    }

}