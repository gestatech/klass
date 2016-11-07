package no.ssb.klass.designer.editing;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.eventbus.EventBus;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.UI;

import no.ssb.klass.core.converting.xml.ClassificationVariantXmlService;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.ComponentUtil;
import no.ssb.klass.designer.windows.AutomaticTranslationWindow;

@SpringComponent
@PrototypeScope
public class VariantCodeEditorView extends VariantCodeEditorDesign implements HasEditingState {

    private EventBus eventbus;
    private ClassificationVariant variant;
    private ImportExportComponent<ClassificationVariant> importExportComponent;

    @Autowired
    private ClassificationVariantXmlService xmlService;

    @Autowired
    private ClassificationFacade classificationFacade;

    @Autowired
    private ApplicationContext applicationContext;

    public VariantCodeEditorView() {
        this.eventbus = new EventBus("variant");
        eventbus.register(variantCodeTable);
        eventbus.register(variantLevels);
        eventbus.register(originalVersion);
        eventbus.register(translationCodeTable);
        eventbus.register(hierarchyToggleComponent);
        selectEditLanguageOrVariant.addEditLanguagesListener(this::switchCodeTables);
        selectEditLanguageOrVariant.addLanguageChangeListener(language -> {
            translationCodeTable.commitDirtyCodeEditors();
            translationCodeTable.init(eventbus, variant, language);
        });
    }

    private void switchCodeTables(boolean isLanguagesVisible) {
        originalVersion.setVisible(!isLanguagesVisible);
        originalVersionName.setVisible(!isLanguagesVisible);
        translationCodeTable.setVisible(isLanguagesVisible);
    }

    public void init(ClassificationVariant variant) {
        this.variant = variant;
        primaryLanguage.setValue(variant.getPrimaryLanguage().getDisplayName());
        variantCodeTable.init(eventbus, variant, variant.getPrimaryLanguage(), classificationFacade);
        variantLevels.init(eventbus, variant, variant.getPrimaryLanguage(), classificationFacade);
        variantLevels.hideLevels();
        variantName.setValue(variant.getNameInPrimaryLanguage());

        originalVersion.init(eventbus, reloadToAvoidLazyInitializationException(variant.getClassificationVersion()),
                variant.getPrimaryLanguage());
        hierarchyToggleComponent.init(eventbus);
        originalVersion.markAsReferenced(variant.getAllClassificationItems());
        originalVersionName.setValue(variant.getClassificationVersion().getNameInPrimaryLanguage());

        selectEditLanguageOrVariant.initWithAutomaticTranslation(variant.getPrimaryLanguage(), () -> UI.getCurrent()
                .addWindow(createAutomaticTranslationWindow(variant)));
        translationCodeTable.init(eventbus, variant, Language.getSecondLanguage(variant.getPrimaryLanguage()));
        switchCodeTables(false);

        importExportComponent = new ImportExportComponent<>(
                applicationContext, xmlService, importButton, exportButton);
        importExportComponent.init(variant, "Variant");
        importExportComponent.setOnCompleteCallback(this::updateView);
        importExportComponent.setClearEntityCallback(this::clearBeforeImport);

    }

    private void clearBeforeImport(ClassificationVariant variant) {
        new ArrayList<>(variant.getAllConcreteClassificationItems())
                .forEach(variant::deleteClassificationItem);

    }

    @Override
    public void restorePreviousEditingState(EditingState editingState) {
        if (editingState.isLanguageTabVisible()) {
            selectEditLanguageOrVariant.showLanguagesAndNotify(true, editingState.isSecondNotThirdLanguageVisible());
        }
    }

    @Override
    public EditingState currentEditingState() {
        return EditingState.newCodeEditorEditingState(selectEditLanguageOrVariant.isLanguagesVisible(),
                selectEditLanguageOrVariant.isSecondNotThirdLanguageSelected());
    }

    @Override
    public boolean hasChanges() {
        return variantCodeTable.hasChanges() || originalVersion.hasChanges() || translationCodeTable.hasChanges();
    }

    private AutomaticTranslationWindow createAutomaticTranslationWindow(ClassificationVariant variant) {
        return new AutomaticTranslationWindow(variant, new TranslationListener(variant) {
            @Override
            protected void translationPerformed(Language language) {
                translationCodeTable.init(eventbus, variant, language);
            }
        });
    }

    private ClassificationVersion reloadToAvoidLazyInitializationException(ClassificationVersion version) {
        return classificationFacade.getRequiredClassificationVersion(version.getId());
    }

    private void updateView(boolean success) {
        variantCodeTable.refresh();
        translationCodeTable.refresh();
        originalVersion.refresh();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        ComponentUtil.setReadOnlyRecursively(this, readOnly);
        selectEditLanguageOrVariant.setReadOnly(readOnly);
        importButton.setEnabled(!readOnly);
    }


    public void prepareSave() {
        variantCodeTable.commitDirtyCodeEditors();
        translationCodeTable.commitDirtyCodeEditors();
    }
}
