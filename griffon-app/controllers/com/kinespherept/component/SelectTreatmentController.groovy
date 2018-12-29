package com.kinespherept.component

import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.SpringConfig
import com.kinespherept.model.core.Treatment
import com.kinespherept.model.core.VisitTreatment
import com.kinespherept.service.LookupDataService
import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import groovy.util.logging.Slf4j
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane

import javax.annotation.Nonnull
import javax.annotation.PostConstruct

@ArtifactProviderFor(GriffonController)
@Slf4j
class SelectTreatmentController {

    @MVCMember @Nonnull SelectTreatmentModel model
    @MVCMember @Nonnull SelectTreatmentView view

    @SpringAutowire LookupDataService lookupDataService

    @PostConstruct
    void init() {
        SpringConfig.autowire(this)
    }

    void prepareForm(AnchorPane anchorPane, List<VisitTreatment> visitTreatments) {
        log.debug("prepareForm() :: anchorPane=${anchorPane}, visitTreatments.size()=${visitTreatments.size()}")

        view.prepareView(anchorPane)

        // clear the existing stuff
        view.treatmentRows.children.clear()
        model.treatmentRows.clear()

        // build the new treatments and keep everything in sync
        lookupDataService.treatments.each { treatment ->
            VisitTreatment match = visitTreatments.find { it.treatmentId == treatment.treatmentId }

            // create the view elements and save to the model
            if(match) {
                model.treatmentRows.add(view.buildTreatmentRow(treatment, match.treatmentQuantity))
            } else {
                model.treatmentRows.add(view.buildTreatmentRow(treatment, 0))
            }
        }

        view.treatmentRows.children.addAll(model.treatmentRows)
        refreshTreatmentCount()
    }

    /**
     * Called by one of the add buttons to increment the quantity of the
     * associated treatment
     */
    void addTreatment(Treatment treatment, Label qtyLabel) {
        log.debug "addTreatment() :: treatment=${treatment}"
        qtyLabel.setText(String.valueOf(Integer.valueOf(qtyLabel.getText()) + 1))
        refreshTreatmentCount()
    }

    /**
     * Called by one of the subtract buttons to decrement the quantity of the
     * associated treatment.  The quantity cannot be decreased below zero.
     */
    void subtractTreatment(Treatment treatment, Label qtyLabel) {
        log.debug "subtractTreatment() :: treatment=${treatment}"
        if(Integer.valueOf(qtyLabel.getText()) > 0) {
            qtyLabel.setText(String.valueOf(Integer.valueOf(qtyLabel.getText()) - 1))
        }
        refreshTreatmentCount()
    }

    /**
     * Counts the total number of treatments that have been selected within the form,
     * and updates the label that displays the full count
     */
    void refreshTreatmentCount() {
        model.treatmentCount = String.valueOf(getTreatmentCount())
    }

    /**
     * Returns the total number of treatments that have been selected within the form.
     */
    int getTreatmentCount() {
        int count = 0
        model.treatmentRows.each { FlowPane flowPane ->
            count += Integer.valueOf((flowPane.children[3] as Label).text)
        }
        count
    }

    List<VisitTreatment> getSelectedTreatments(long visitId) {
        List<VisitTreatment> treatments = []

        model.treatmentRows.each { FlowPane flowPane ->
            int vtCount = Integer.valueOf((flowPane.children[3] as Label).text)
            if(vtCount > 0) {
                treatments <<
                        new VisitTreatment(
                                visitId: visitId,
                                treatmentId: lookupDataService.findTreatmentByDisplayableName((flowPane.children[0] as Label).text).treatmentId,
                                treatmentQuantity: vtCount
                        )
            }
        }

        treatments
    }

}