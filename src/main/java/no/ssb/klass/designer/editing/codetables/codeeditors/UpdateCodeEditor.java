package no.ssb.klass.designer.editing.codetables.codeeditors;

import com.google.common.eventbus.EventBus;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.editing.codetables.events.CodeUpdatedEvent;

/**
 * UpdateCodeEditor is a code editor that allows to update existing codes.
 * <p>
 * Generates events for following actions:
 * <ul>
 * <li>CodeUpdatedEvent</li>
 * </ul>
 */
public class UpdateCodeEditor extends CodeEditor {
    private final ConcreteClassificationItem editItem;

    public UpdateCodeEditor(EventBus eventBus, StatisticalClassification version, ClassificationItem editItem,
            Language language) {
        super(eventBus, version, language);
        code.setValue(editItem.getCode());
        title.setValue(editItem.getOfficialName(language));

        if (includeShortName) {
            shortname.setValue(editItem.getShortName(language));
            shortname.setVisible(true);
        }
        if (includeNotes) {
            notes.setValue(editItem.getNotes(language));
            notes.setVisible(true);
        }
        this.editItem = (ConcreteClassificationItem) editItem;
        storeInitialStateForDirtyChecking();
    }

    @Override
    protected Object createEvent() {
        editItem.setCode(code.getValue());
        editItem.setOfficialName(title.getValue(), language);
        if (includeShortName) {
            editItem.setShortName(shortname.getValue(), language);
        }
        if (includeNotes) {
            editItem.setNotes(notes.getValue(), language);
        }
        return new CodeUpdatedEvent(editItem);
    }

    @Override
    protected boolean mustCheckIfAlreadyExists() {
        return hasCodeChanged();
    }

    private boolean hasCodeChanged() {
        return !code.getValue().equals(editItem.getCode());
    }

    @Override
    protected ClassificationItem cancelEdit() {
        return editItem;
    }
}
