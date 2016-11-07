package no.ssb.klass.designer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.search.SearchResult;
import no.ssb.klass.designer.MainView.ClassificationFilter;
import no.ssb.klass.designer.components.search.SearchResultComponent;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.KlassTheme;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@SuppressWarnings("serial")
@UIScope
@SpringView(name = ClassificationSearchView.NAME)
public class ClassificationSearchView extends ClassificationSearchDesign implements FilteringView {

    public static final String NAME = "search";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_PAGE_NO = "page";

    @Value("${klass.search.resultsPerPage}")
    private Integer resultsPerPage;
    @Value("${klass.search.maxDescriptionLength}")
    private Integer maxDescriptionLength;

    @Autowired
    private ClassificationFacade classificationFacade;
    @Autowired
    private SearchService searchService;

    private Page<SearchResult> searchResult;
    private String searchQuery;
    private ClassificationFilter classificationFilter;

    public ClassificationSearchView() {
        this.classificationFilter = VaadinUtil.getKlassState().getClassificationFilter();
    }

    private Page<SearchResult> getPage(String searchQuery, int page) {
        return searchService.search(searchQuery, new PageRequest(page, resultsPerPage),
                classificationFilter.getCurrentSection(), classificationFilter.getCurrentClassificationType());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        String parameters = viewChangeEvent.getParameters();
        int page = ParameterUtil.getRequiredIntParameter(PARAM_PAGE_NO, parameters);
        searchQuery = ParameterUtil.getRequiredStringParameter(PARAM_QUERY, parameters);
        searchBox.setSearchText(searchQuery);
        searchResult = getPage(searchQuery, page);
        setResultsText();
        populatePage();
    }

    public void setResultsText() {
        long resultCount = searchResult.getTotalElements();
        if (resultCount == 0) {
            resultsText.setValue("Fant ingen treff på \"" + searchQuery + "\" ");
        } else {
            resultsText.setValue("Resultat på \"" + searchQuery + "\" " + resultCount + " stk.");
        }
    }

    private void gotoPage(int page) {
        VaadinUtil.navigateTo(NAME, ImmutableMap.of(PARAM_QUERY, searchQuery, PARAM_PAGE_NO, String.valueOf(page)));
    }

    private void populatePage() {
        clearSearchResults();

        List<SearchResult> pageContent = Lists.reverse(searchResult.getContent());
        for (SearchResult searchResult : pageContent) {

            SearchResultComponent searchResultUI = new SearchResultComponent();

            ClassificationSeries series = classificationFacade.getRequiredClassificationSeries(searchResult
                    .getResourceId());
            searchResultUI.setLink(searchResult.getResourceName(), series);
            searchResultUI.setDescription(limitedDescription(searchResult.getDescription()));
            String hierarchy = searchResult.getClassificationType().getDisplayName(Language.getDefault())
                    + " - " + searchResult
                            .getClassificationFamilyName()
                    + " - " + searchResult.getOwnerSection();
            searchResultUI.setHierarchy(hierarchy);
            searchResultComponent.addComponentAsFirst(searchResultUI);
        }
        populatePageSelector();
    }

    private String limitedDescription(String description) {
        if (description.length() > maxDescriptionLength) {
            return description.substring(0, maxDescriptionLength) + "[...]";
        }
        return description;
    }

    private void populatePageSelector() {
        int totalPages = searchResult.getTotalPages();
        if (totalPages > 1) {
            for (int i = 0; i < totalPages; i++) {
                Button button = new Button(String.valueOf(i + 1));
                button.setData(i);

                boolean selected = i == searchResult.getNumber();
                button.setPrimaryStyleName(selected ? KlassTheme.PAGE_NUMBER_HIGHLIGHTED : KlassTheme.BUTTON_AS_LINK);
                button.setStyleName(KlassTheme.PAGE_NUMBER);
                button.addClickListener(event -> gotoPage((Integer) event.getButton().getData()));
                pagingComponent.addComponent(button);
            }
        }
    }

    private void clearSearchResults() {
        searchResultComponent.removeAllComponents();
        pagingComponent.removeAllComponents();
        searchResultComponent.addComponent(pagingComponent);
    }
}
