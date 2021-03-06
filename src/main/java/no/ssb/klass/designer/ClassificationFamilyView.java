package no.ssb.klass.designer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.collect.ImmutableMap;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ClassResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.repository.ClassificationFamilySummary;
import no.ssb.klass.designer.MainView.ClassificationFilter;
import no.ssb.klass.designer.components.ClassificationListViewSelection;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.FavoriteUtils;
import no.ssb.klass.designer.util.KlassTheme;
import no.ssb.klass.designer.util.VaadinUtil;

@PrototypeScope
@SpringView(name = ClassificationFamilyView.NAME)
@SuppressWarnings("serial")
public class ClassificationFamilyView extends ClassificationFamilyDesign implements FilteringView {

    public static final String NAME = ""; // empty name will make it default view for navigator

    private final ClassificationFilter classificationFilter;

    @Autowired
    private ClassificationFacade classificationFacade;
    @Autowired
    private UserContext userContext;

    public ClassificationFamilyView() {
        this.classificationFilter = VaadinUtil.getKlassState().getClassificationFilter();
    }

    private void showFamilyButtons() {
        List<ClassificationFamilySummary> classificationFamilySummaries = classificationFacade
                .findAllClassificationFamilySummaries(classificationFilter.getCurrentSection(), classificationFilter
                        .getCurrentClassificationType());
        familyGrid.removeAllComponents();
        for (ClassificationFamilySummary familySummary : classificationFamilySummaries) {
            HorizontalLayout familyButton = createFamilyButton(familySummary);
            familyGrid.addComponent(familyButton);
        }
    }

    private HorizontalLayout createFamilyButton(ClassificationFamilySummary familySummary) {
        Button button = new Button(familySummary.getClassificationFamilyName());
        button.setData(familySummary.getId().toString());
        button.addStyleName("borderless link-button family-icon");
        button.addClickListener(e -> VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(
                ClassificationListView.PARAM_FAMILY_ID, (String) e.getButton().getData())));
        button.setIcon(new ClassResource(familySummary.getIconPath()));
        Label sizeLabel = new Label(" (" + familySummary.getNumberOfClassifications() + ")");
        sizeLabel.setId(familySummary.getClassificationFamilyName() + "-size");
        HorizontalLayout layout = new HorizontalLayout(button, sizeLabel);
        layout.setComponentAlignment(sizeLabel, Alignment.MIDDLE_LEFT);
        return layout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        showFamilyButtons();
        showFavorites();
    }

    private void showFavorites() {
        if (userContext.hasFavorites()) {
            favoriteIntro.setVisible(false);
            favoritePanel.setVisible(true);
            favoritePanel.setScrollLeft(0);
            favoritePanel.setScrollTop(0);
            favoriteGrid.removeAllComponents();
            for (ClassificationSeries favorite : userContext.getFavorites()) {
                favoriteGrid.addComponent(createFavoriteList(favorite));
            }
        } else {
            favoritePanel.setVisible(false);
            favoriteIntro.setVisible(true);
        }
    }

    private HorizontalLayout createFavoriteList(ClassificationSeries favorite) {
        Button button = new Button(FavoriteUtils.getFavoriteIcon(userContext.hasFavorites()));
        button.setData(favorite);
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        button.setDescription("Fjern favoritt");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Button pushedFavorite = event.getButton();
                ClassificationSeries cs = (ClassificationSeries) pushedFavorite.getData();
                boolean isFavorite = userContext.toggleFavorite(cs);
                pushedFavorite.setIcon(FavoriteUtils.getFavoriteIcon(isFavorite));
                showFavorites();
            }
        });
        Button favoriteLink = new Button(favorite.getNameInPrimaryLanguage());
        favoriteLink.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        favoriteLink.addStyleName(KlassTheme.BUTTON_AS_LINK);
        favoriteLink.addClickListener(e -> favoriteClickHandler(favorite));

        HorizontalLayout layout = new HorizontalLayout(button, favoriteLink);
        layout.addStyleName("favorite");
        return layout;
    }

    private void favoriteClickHandler(ClassificationSeries classification) {
        VaadinUtil.getKlassState().setClassificationListViewSelection(ClassificationListViewSelection
                .newClassificationListViewSelection(classification));
        VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(
                ClassificationListView.PARAM_FAMILY_ID, classification.getClassificationFamily().getId().toString()));
    }
}
