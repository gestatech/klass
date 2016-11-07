package no.ssb.klass.designer.editing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;

import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.designer.EditingView;
import no.ssb.klass.designer.components.BreadcumbPanel.Breadcrumb;
import no.ssb.klass.designer.components.common.TypeAndNameHeaderComponent;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.service.InformSubscribers;
import no.ssb.klass.designer.user.UserContext;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@PrototypeScope
@SpringView(name = CreateVariantView.NAME)
@SuppressWarnings("serial")
public class CreateVariantView extends CreateVariantDesign implements EditingView {

    public static final String NAME = "createVariant";
    public static final String PARAM_NAME = "variantName";
    public static final String PARAM_VERSION_ID = "versionId";

    private boolean ignoreChanges = false;

    @Autowired
    private ClassificationFacade classificationFacade;
    @Autowired
    private UserContext userContext;

    private ClassificationVariant variant;

    public CreateVariantView() {
        actionButtons.setConfirmText("Lagre");
        actionButtons.addConfirmClickListener(this::saveClick);
        actionButtons.addCancelClickListener(this::cancelClick);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String name = ParameterUtil.getRequiredStringParameter(PARAM_NAME, event.getParameters());
        ClassificationVersion version = getVersionFromParameters(event);
        header.setTypeText(TypeAndNameHeaderComponent.TEXT_VARIANT_OF + version.getNameInPrimaryLanguage());
        metadataEditor.init(version.getPrimaryLanguage());
        metadataEditor.setName(version.getPrimaryLanguage(), name);
        metadataEditor.setContactPerson(userContext.getDetachedUserObject());

        variant = new ClassificationVariant(Translatable.create(name, version.getPrimaryLanguage()), userContext
                .getDetachedUserObject());
        variant.setClassificationVersion(version);
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        return Breadcrumb.newBreadcrumbs(variant);
    }

    @Override
    public boolean hasChanges() {
        return !ignoreChanges && metadataEditor.hasChanges();
    }

    @Override
    public void ignoreChanges() {
        ignoreChanges = true;
    }

    public ClassificationVersion getVersionFromParameters(ViewChangeListener.ViewChangeEvent event) {
        Long versionId = ParameterUtil.getRequiredLongParameter(PARAM_VERSION_ID, event.getParameters());
        return classificationFacade.getRequiredClassificationVersion(versionId);
    }

    private void saveClick(Button.ClickEvent clickEvent) {
        if (metadataEditor.validate()) {
            ignoreChanges = true;
            for (Language language : Language.getAllSupportedLanguages()) {
                variant.setName(language, metadataEditor.getName(language));
                variant.setIntroduction(metadataEditor.getDescription(language), language);
            }
            variant.updateContactPerson(metadataEditor.getContactPerson());
            classificationFacade.saveAndIndexVariant(variant, InformSubscribers.createNotInformSubscribers());
            VaadinUtil.showSavedMessage();
            VaadinUtil.getKlassState().setEditingState(metadataEditor.currentEditingState());
            VaadinUtil.navigateTo(EditVariantEditorView.NAME,
                    ImmutableMap.of(EditVariantEditorView.PARAM_ID, variant.getId().toString()));
        }
    }

    private void cancelClick(Button.ClickEvent clickEvent) {
        VaadinUtil.navigateTo(Iterables.getLast(getBreadcrumbs()));
    }
}
