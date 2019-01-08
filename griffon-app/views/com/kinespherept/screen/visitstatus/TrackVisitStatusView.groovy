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
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import org.springframework.util.StringUtils

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class TrackVisitStatusView extends BaseView {

    // magic string!
    static String SELECT_STATUS = 'Select a status..'

    @MVCMember @Nonnull TrackVisitStatusController controller
    @MVCMember @Nonnull TrackVisitStatusModel model

    @SpringAutowire CommonProperties commonProperties
    @SpringAutowire LookupDataService lookupDataService
    @SpringAutowire VisitService visitService

    @FXML GridPane visitResultsGridPane

    // panes that hold other components
    @FXML AnchorPane navigationPane


    @FXML AnchorPane rootAnchorPane

    @FXML ProgressIndicator spinner

    void showSpinner(boolean showSpinner) {
        spinner.visible = showSpinner
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
    }


    void buildResultRowByStatus(Visit visit, int rowNumber) {
        // date, visit number, last name, first name
        visitResultsGridPane.add(new Label(text: visit.visitDate.format(commonProperties.dateFormatter),
                prefWidth: 80, style: "-fx-border-color: black;", alignment: Pos.CENTER), 0, rowNumber)
        visitResultsGridPane.add(new Label(text: String.valueOf(visit.visitNumber),
                prefWidth: 30, style: "-fx-border-color: black;", alignment: Pos.CENTER), 1, rowNumber)
        visitResultsGridPane.add(new Label(text: visit.patient.lastName, prefWidth: 160, style: "-fx-border-color: black;"), 2, rowNumber)
        visitResultsGridPane.add(new Label(text: visit.patient.firstName, prefWidth: 120, style: "-fx-border-color: black;"), 3, rowNumber)

        // dx
        visitResultsGridPane.add(new Label(text: visit.sameDiagnosisAsPrevious ? 'YES' : '', prefWidth: 50, style: "-fx-border-color: black;", alignment: Pos.CENTER), 4, rowNumber)

        // tx
        String txText = visit.visitTreatments.collect {
            // see if more than 1 treatment (if so, be sure to include it)
            it.treatmentQuantity > 1 ?
                    "${lookupDataService.findTreatmentById(it.treatmentId).treatmentCode} (${it.treatmentQuantity})" : // yep, so show the quantity
                    lookupDataService.findTreatmentById(it.treatmentId).treatmentCode  // nope, just 1
        }.join(', ')
        Label txLabel = new Label(text: txText, prefWidth: 200, style: "-fx-border-color: black;")
        txLabel.setTooltip(new Tooltip(txText))
        visitResultsGridPane.add(txLabel, 5, rowNumber)

        // tx count
        int txCount = visit.visitTreatments.collect { it.treatmentQuantity }.inject(0) { a, b -> a + b}
        visitResultsGridPane.add(new Label(text: String.valueOf(txCount), prefWidth: 30, style: "-fx-border-color: black;", alignment: Pos.CENTER), 6, rowNumber)

        // notes ?
        Label notesLabel = new Label(text: StringUtils.isEmpty(visit.notes) ? "" : "YES",
                prefWidth: 50, style: "-fx-border-color: black;", alignment: Pos.CENTER)
        if(!StringUtils.isEmpty(visit.notes)) {
            notesLabel.setTooltip(new Tooltip(visit.notes))
        }
        visitResultsGridPane.add(notesLabel, 7, rowNumber)

        // view details button
        visitResultsGridPane.add(new Button(text: 'View Details', onAction: { a -> controller.viewVisitDetails(visit) } ),
                8, rowNumber)

        // action button(s)
        switch (visit.visitStatus) {
            case VisitStatus.VISIT_CREATED:
                visitResultsGridPane.add(buildActionButtonFlowPane(visit),9, rowNumber)
                break
            case VisitStatus.SEEN_BY_THERAPIST:
                visitResultsGridPane.add(buildActionButtonFlowPane(visit, VisitStatus.PREPARED_FOR_BILLING),
                        9, rowNumber)
                break
            case VisitStatus.PREPARED_FOR_BILLING:
                visitResultsGridPane.add(buildActionButtonFlowPane(visit, VisitStatus.BILLED_TO_INSURANCE),
                        9, rowNumber)
                break
            case VisitStatus.BILLED_TO_INSURANCE:
                visitResultsGridPane.add(buildActionButtonFlowPane(visit, VisitStatus.REMITTANCE_ENTERED),
                        9, rowNumber)
                break
            case VisitStatus.REMITTANCE_ENTERED:
                visitResultsGridPane.add(buildActionButtonFlowPane(visit, VisitStatus.BILL_SENT_TO_PATIENT),
                        9, rowNumber)
                break
            case VisitStatus.AWAITING_SECONDARY:
                visitResultsGridPane.add(buildActionButtonFlowPane(visit, VisitStatus.PAID_IN_FULL),
                        9, rowNumber)
                break
            case VisitStatus.BILL_SENT_TO_PATIENT:
                visitResultsGridPane.add(buildActionButtonFlowPane(visit, VisitStatus.PAID_IN_FULL),
                        9, rowNumber)
                break
            case VisitStatus.PAID_IN_FULL:
                visitResultsGridPane.add(buildActionButtonFlowPane(visit),9, rowNumber)
                break
        }
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
    FlowPane buildActionButtonFlowPane(Visit visit, VisitStatus ... visitStatuses) {
        FlowPane flowPane = new FlowPane(alignment: Pos.CENTER_LEFT)
        flowPane.children.addAll(visitStatuses.collect { buildButtonForStatus(visit, it) })

        // prepare the list of all status to change to (exclude the current status)
        List<String> options = [SELECT_STATUS]
        options.addAll(VisitStatus.values().findAll { it != visit.visitStatus }.collect { it.text })

        // create the drop-down box and add the options
        ChoiceBox<String> directSetChoiceBox = new ChoiceBox<>()
        directSetChoiceBox.items.addAll(options)
        directSetChoiceBox.selectionModel.select(SELECT_STATUS)

        // add a divider only if a quick status button is supplied
        if(visitStatuses.size() > 0) {
            flowPane.children.add(new Separator(orientation: Orientation.VERTICAL, prefWidth: 10))
        }
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