package com.kinespherept.screen.report

import com.kinespherept.BaseView
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.model.report.CountAndPercentCell
import com.kinespherept.model.report.CountAndPercentReport
import com.kinespherept.model.report.CountAndPercentReportRow
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class BrowseReportsView extends BaseView {

    // magic strings!
    static String STYLE_BLACK_BORDER = '-fx-border-color: black;'

//
    static String STYLE_BLACK_BORDER_BG_ONE = '-fx-border-color: black; -fx-background-color: #adebeb;'
    static String STYLE_BLACK_BORDER_BG_TWO = '-fx-border-color: black; -fx-background-color: #d6f5f5;'





    static double COLUMN_WIDTH_MONTH            = 80.0
    static double COLUMN_WIDTH_TOTAL_VISITS     = 80.0

    static double COLUMN_WIDTH_CELL_COUNT       = 40.0
    static double COLUMN_WIDTH_CELL_PERCENT     = 40.0


    @MVCMember @Nonnull BrowseReportsController controller
    @MVCMember @Nonnull BrowseReportsModel model

    @FXML AnchorPane navigationPane
    @FXML GridPane reportDataGridPane

    @FXML FlowPane timeRangeFlowPane

    @FXML Button renderReportButton
    @FXML Label refreshMessageLabel


    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    void initUI() {
        log.debug "initUI()"
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.BROWSE_REPORTS, scene)
        renderReportButton.setText('Go!')
    }



    void enableTimeRangeElements() {
        timeRangeFlowPane.disable = false
    }

    void disableTimeRangeElements() {
        timeRangeFlowPane.disable = true
    }


    void enableRenderReportButton() {
        renderReportButton.disable = false
        refreshMessageLabel.visible = true
    }

    void disableRenderReportButton() {
        renderReportButton.disable = true
        refreshMessageLabel.visible = false
    }


    ColumnConstraints buildColumnConstraints(double width) {
        new ColumnConstraints(prefWidth: width, minWidth: width, maxWidth: width)
    }

    Label buildLabel(String text, double width, boolean addTooltip = false) {
        Label label = new Label(text: text, alignment: Pos.CENTER, prefWidth: width, minWidth: width, maxWidth: width,
                style: STYLE_BLACK_BORDER)
        if(addTooltip) {
            label = applyTooltip(label, text)
        }
        label
    }

    Label buildLabel(String text, double width, String style, boolean addTooltip = false) {
        Label label = new Label(text: text, alignment: Pos.CENTER, prefWidth: width, minWidth: width, maxWidth: width,
                style: style)
        if(addTooltip) {
            label = applyTooltip(label, text)
        }
        label
    }

    Label applyTooltip(Label label, String tooltipText) {
        label.tooltip = new Tooltip(tooltipText)
        label
    }

    /**
     * master method that lays out the provided report data
     */
    void displayCountAndPercentReport(CountAndPercentReport report) {

        // clear the existing data so we can start fresh
        reportDataGridPane.columnConstraints.clear()
        reportDataGridPane.rowConstraints.clear()
        reportDataGridPane.children.clear()

        reportDataGridPane.rowConstraints << new RowConstraints(prefHeight: 25)

        reportDataGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_MONTH)
        reportDataGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_TOTAL_VISITS)

        report.columnTypes.eachWithIndex { String type, int index ->
            // need to alternate background color here
            reportDataGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_CELL_COUNT)
            reportDataGridPane.columnConstraints << buildColumnConstraints(COLUMN_WIDTH_CELL_PERCENT)
        }

        // first row
        reportDataGridPane.add(buildLabel('Month', COLUMN_WIDTH_MONTH), 0, 0)
        reportDataGridPane.add(buildLabel('Total Visits', COLUMN_WIDTH_TOTAL_VISITS), 1, 0)

        int columnIndex = 2

        report.columnTypes.eachWithIndex { String type, int index ->

            reportDataGridPane.add(buildLabel(type, COLUMN_WIDTH_CELL_COUNT, index % 2 == 0 ? STYLE_BLACK_BORDER_BG_ONE : STYLE_BLACK_BORDER_BG_TWO, true), columnIndex++, 0)
            reportDataGridPane.add(buildLabel('Pct', COLUMN_WIDTH_CELL_PERCENT, index % 2 == 0 ? STYLE_BLACK_BORDER_BG_ONE : STYLE_BLACK_BORDER_BG_TWO), columnIndex++, 0)
        }

        int rowIndex = 1


        report.rows.each { CountAndPercentReportRow row ->

            // month & full column count
            reportDataGridPane.add(buildLabel(row.month.name(), COLUMN_WIDTH_MONTH), 0, rowIndex)
            reportDataGridPane.add(buildLabel(String.valueOf(row.totalCount), COLUMN_WIDTH_TOTAL_VISITS), 1, rowIndex)

            columnIndex = 2

            // loop over the columnTypes
            report.columnTypes.eachWithIndex { String columnType, int index ->
                CountAndPercentCell cell = row.dataMap.get(columnType)
                reportDataGridPane.add(buildLabel(String.valueOf(cell.count), COLUMN_WIDTH_CELL_COUNT, index % 2 == 0 ? STYLE_BLACK_BORDER_BG_ONE : STYLE_BLACK_BORDER_BG_TWO ), columnIndex++, rowIndex)
                reportDataGridPane.add(buildLabel("${cell.percent}%", COLUMN_WIDTH_CELL_PERCENT, index % 2 == 0 ? STYLE_BLACK_BORDER_BG_ONE : STYLE_BLACK_BORDER_BG_TWO), columnIndex++, rowIndex)
            }

            rowIndex++
        }
    }

}
