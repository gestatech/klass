package no.ssb.klass.designer.components.search;

import com.google.common.collect.ImmutableMap;
import com.vaadin.ui.CustomComponent;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.designer.ClassificationListView;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
public class SearchResultComponent extends CustomComponent {

    private SearchResultDesign design = new SearchResultDesign();

    public SearchResultComponent() {
        setCompositionRoot(design);
    }

    public void setLink(String title, ClassificationSeries classification) {
        design.titleButton.setCaption(title);
        design.titleButton.addClickListener(event -> createSearchClickHandler(classification));
    }

    private void createSearchClickHandler(ClassificationSeries classification) {
        VaadinUtil.getKlassState().setClassificationListViewSelection(ClassificationListViewSelection
                .newClassificationListViewSelection(classification));
        VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(ClassificationListView.PARAM_FAMILY_ID,
                classification.getClassificationFamily().getId().toString()));
    }

    @Override
    public void setDescription(String description) {
        design.descriptionLabel.setValue(description);
    }

    public void setHierarchy(String hierarchy) {
        design.hierarchyLabel.setValue(hierarchy);
    }

}
