package no.ssb.klass.designer.windows;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.vaadin.data.Validator;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Window;

import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.designer.editing.CreateVariantView;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@SpringComponent
@PrototypeScope
class NewVariantWindowLogic extends NewVariantWindowDesign {

    private Window parent;
    private ClassificationVersion version;

    @Autowired
    private ClassificationFacade classificationFacade;

    NewVariantWindowLogic(Window parent) {
        this.parent = parent;
        actionButtons.setConfirmText("Opprett");
        actionButtons.addCancelClickListener(event -> parent.close());
        actionButtons.addConfirmClickListener(event -> createVariant());
        actionButtons.setConfirmAsPrimaryButton();
    }

    public void init(Long versionId) {
        version = classificationFacade.getRequiredClassificationVersion(versionId);
        headerLabel.setValue("Opprett ny variant av " + version.getNameInPrimaryLanguage());

    }

    private void createVariant() {
        if (validName()) {
            VaadinUtil.navigateTo(CreateVariantView.NAME, ImmutableMap.of(CreateVariantView.PARAM_VERSION_ID, String
                    .valueOf(version.getId()), CreateVariantView.PARAM_NAME, nameTextField.getValue()));
            parent.close();
        }
    }

    private boolean validName() {
        try {
            nameTextField.setComponentError(null);
            nameTextField.validate();
            return true;
        } catch (Validator.InvalidValueException ex) {
            String message = Strings.isNullOrEmpty(ex.getMessage()) ? "Påkrevd felt" : ex.getMessage();
            nameTextField.setComponentError(new UserError(message));
            return false;
        }
    }

}
