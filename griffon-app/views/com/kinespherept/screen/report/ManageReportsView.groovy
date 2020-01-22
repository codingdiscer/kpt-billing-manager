package com.kinespherept.screen.report

import com.kinespherept.BaseView
import com.kinespherept.model.core.ReportStatus
import com.kinespherept.model.navigation.SceneDefinition
import com.kinespherept.model.report.MonthReport
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonView)
class ManageReportsView extends BaseView {


    // shared column types
    static double COLUMN_WIDTH_MONTH      = 70.0
    static double COLUMN_WIDTH_STATUS     = 200.0
    static double COLUMN_WIDTH_ACTIONS    = 120.0


    @MVCMember @Nonnull ManageReportsController controller
    @MVCMember @Nonnull ManageReportsModel model



    @FXML AnchorPane navigationPane

    @FXML GridPane monthReportGridPane
    @FXML Button generateAllReportsButton


    void toggleGenerateAllReportsButton(boolean disable) {
        generateAllReportsButton.disable = disable
    }



    Label buildLabel(String text, double width) {
        new Label(text: text, alignment: Pos.CENTER, prefWidth: width, minWidth: width, maxWidth: width,
                /*style: STYLE_BLACK_BORDER*/)
    }

    /**
     * Refreshes and populates the "report grid".
     * @param buildButtons If true, buttons to "generate a report" are displayed.  When false, a progress spinner is displayed instead of a button on each row.
     *
     *
     * This method has one side-effect - The "generate all reports" button is also enabled or disabled based on the
     * value of the buildButtons parameter (to match the behavior of building buttons or showing spinners)
     */
    void refreshMonthReports(boolean buildButtons) {

        // side-effect!  if(buildButtons), then enable the "generate all reports" button; otherwise vice-versa
        generateAllReportsButton.disable = !buildButtons

        monthReportGridPane.children.clear()

        // the header row
        monthReportGridPane.add(buildLabel('Month', COLUMN_WIDTH_MONTH), 0, 0)
        monthReportGridPane.add(buildLabel('Status', COLUMN_WIDTH_STATUS), 1, 0)
        monthReportGridPane.add(buildLabel('Actions', COLUMN_WIDTH_ACTIONS), 2, 0)

        int rowIndex = 1

        model.reports.each { MonthReport report ->

            monthReportGridPane.add(buildLabel(report.month, COLUMN_WIDTH_MONTH), 0, rowIndex)
            monthReportGridPane.add(buildLabel(report.status, COLUMN_WIDTH_STATUS), 1, rowIndex)

            // button to gen report, or label saying not ready yet
            if(report.futureMonth) {
                monthReportGridPane.add(buildLabel('No actions available', COLUMN_WIDTH_ACTIONS), 2, rowIndex)
            } else {
                String buttonLabel = 'Regenerate'
                if(report.report.reportStatus == ReportStatus.NEW) {
                    buttonLabel = 'Generate'
                }
                if(buildButtons) {
                    monthReportGridPane.add(
                            new Button(text: buttonLabel, onAction: { a -> controller.generateReport(report) })
                            , 2, rowIndex)
                } else {
                    monthReportGridPane.add(
                            new ProgressIndicator(), 2, rowIndex)
                }
            }
            rowIndex++
        }

    }



    @PostConstruct
    void init() {
        log.debug "init()"
        baseInit(this)
    }


    void initUI() {
        log.debug "initUI()"
        baseInitUI(controller, model)
        sceneManager.addScene(SceneDefinition.MANAGE_REPORTS, scene)
    }

}
