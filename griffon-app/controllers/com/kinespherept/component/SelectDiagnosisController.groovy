package com.kinespherept.component

import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.SpringConfig
import com.kinespherept.model.core.Diagnosis
import com.kinespherept.service.LookupDataService
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import groovy.util.logging.Slf4j
import javafx.scene.control.Button
import javafx.scene.control.Labeled
import javafx.scene.layout.AnchorPane
import org.springframework.util.StringUtils

import javax.annotation.Nonnull
import javax.annotation.PostConstruct
import java.util.stream.Collectors

@ArtifactProviderFor(GriffonController)
@Slf4j
class SelectDiagnosisController {

    @MVCMember @Nonnull SelectDiagnosisModel model
    @MVCMember @Nonnull SelectDiagnosisView view


    @SpringAutowire LookupDataService lookupDataService

    @PostConstruct
    void init() {
        log.debug "init()"
        SpringConfig.autowire(this)
    }


    //void prepareForm(AnchorPane anchorPane, List<VisitDiagnosis> selectedDiagnoses, int diagnosisMaxCount) {
    void prepareForm(AnchorPane anchorPane, List<Diagnosis> selectedDiagnoses, int diagnosisMaxCount) {
        log.info "prepareForm() :: anchorPane=${anchorPane}, selectedDiagnoses.size()=${selectedDiagnoses.size()}"

        // prepare the search stuff (the filter text and the full list of diagnoses)
        clearFilter()

        // prepare the list of already selected diagnoses
        model.selectedDiagnoses.clear()
        model.diagnosisList.clear()
        selectedDiagnoses?.each { Diagnosis d ->
            model.selectedDiagnoses << d
            model.diagnosisList << view.buildSelectedDiagnosis(d)
        }

        model.diagnosisMaxCount = String.valueOf(diagnosisMaxCount)
        updateDiagnosisCounts()

        view.prepareView(anchorPane)
    }

    void updateDiagnosisCounts() {
        model.diagnosisCount = String.valueOf(model.selectedDiagnoses.size())

        // map the diagnoses into their types, put them in a set (to remove duplicates)...
        Set<String> set = model.selectedDiagnoses.stream()
        .map({ Diagnosis d -> d.diagnosisType })
        .collect(Collectors.toSet())

        // then count the number of entries
        model.diagnosisCategoryCount = String.valueOf(set.size())
    }



    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void clearFilter() {
        model.searchFilter = ''
        model.diagnosisSearchList.clear()

        prepareDisplayableDiagnosisList(lookupDataService.diagnoses).each { d ->
            model.diagnosisSearchList << d
        }

    }


    /**
     * Called when the text field holding the search filter is changed - this method makes calls to
     * update the search result UI element.
     * @param searchFilter The new search value
     */
    void applyFilter(String searchFilter) {
        log.debug "applyFilter(${searchFilter})"
        if(StringUtils.isEmpty(searchFilter)) {
            clearFilter()
        } else {
            model.diagnosisSearchList.clear()
            prepareDisplayableDiagnosisList(lookupDataService.searchDiagnoses(searchFilter)).each { d ->
                model.diagnosisSearchList << d
            }
        }
    }


    /**
     * Returns a list of {@link javafx.scene.control.Labeled} objects (Labels and Buttons) that should make up the search list of
     * diagnoses.  The diagnoses are all grouped by their type, then for each group type, a single label is
     * added (displaying the group type), then each individual element in the group is added as a button.
     * @param diagnoses The list of {@link com.kinespherept.model.core.Diagnosis} objects to prepare in the search result list
     */
    List<Labeled> prepareDisplayableDiagnosisList(List<Diagnosis> diagnoses) {
        // sort just to be sure
        diagnoses.sort(Comparator.comparingInt({ Diagnosis d -> d.diagnosisType.displayOrder })
                .thenComparingInt({ Diagnosis d -> d.displayOrder }))

        Map<Integer, List<Diagnosis>> diagnosisTypes =
                diagnoses.stream().collect(Collectors.groupingBy({
                    Diagnosis d -> d.diagnosisType.displayOrder }))

        // the list we'll return
        List<Labeled> list = []

        diagnosisTypes.each { Integer order, List<Diagnosis> dlist ->
            // add the label for the diagnosis type
            list << view.buildDiagnosisTypeLabel(dlist[0])
            // add the button for each actual entry in the type listing
            dlist.each {
                list << view.buildDiagnosisSearchResult(it)
            }
        }

        list
    }



    void selectDiagnosis(Diagnosis diagnosis) {
        log.debug "selectDiagnosis(${diagnosis})"

        // see if the diagnosis is already in the list - only add if it isn't found in the list
        // ...also, only add if the list contains less than the max quantity of elements for this screen
        if(model.selectedDiagnoses.size() < Integer.parseInt(model.diagnosisMaxCount) &&
                !model.selectedDiagnoses.contains(diagnosis)) {
            model.selectedDiagnoses << diagnosis
            model.diagnosisList << view.buildSelectedDiagnosis(diagnosis)
            updateDiagnosisCounts()
        }

    }

    void removeDiagnosis(Button diagnosis) {
        log.debug "removeDiagnosis(${diagnosis})"
        model.diagnosisList.remove(diagnosis)
        model.selectedDiagnoses.remove(lookupDataService.findDiagnosisById(Integer.parseInt(diagnosis.id)))
        updateDiagnosisCounts()
    }


    List<Diagnosis> getSelectedDiagnoses() {
        model.selectedDiagnoses
    }



}