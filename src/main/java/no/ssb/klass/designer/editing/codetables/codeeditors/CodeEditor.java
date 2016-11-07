package no.ssb.klass.designer.editing.codetables.codeeditors;

import java.util.Objects;

import com.google.common.eventbus.EventBus;
import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.designer.components.common.layouts.HorizontalSpacedLayout;
import no.ssb.klass.designer.components.common.layouts.VerticalSpacedLayout;
import no.ssb.klass.designer.editing.codetables.events.CancelEditEvent;
import no.ssb.klass.designer.listeners.EnterListener;
import no.ssb.klass.designer.util.KlassTheme;

/**
 * A CodeEditor edits a row in a codeTable
 * <p>
 * Implementation note: subclasses must call {@link #storeInitialStateForDirtyChecking()} after textfields are
 * initialized or reset.
 */
public abstract class CodeEditor extends CustomComponent {
    protected final TextField code;
    protected final TextField title;
    protected final TextField shortname;
    protected final TextArea notes;
    protected final Language language;
    protected final boolean includeShortName;
    protected final boolean includeNotes;
    private final StatisticalClassification version;
    private final EventBus eventBus;

    CodeEditor(EventBus eventBus, StatisticalClassification version, Language language) {
        this.includeShortName = version.isIncludeShortName();
        this.includeNotes = version.isIncludeNotes();
        this.language = language;
        this.code = new TextField();
        this.title = new TextField();
        this.version = version;
        this.eventBus = eventBus;

        VerticalLayout layout = new VerticalSpacedLayout(wrapCodeAndTitle(code, title));

        if (includeShortName) {
            shortname = createShortname();
            shortname.setVisible(false);
            layout.addComponent(shortname);
        } else {
            shortname = null;
        }
        if (includeNotes) {
            notes = createNotes();
            notes.setVisible(false);
            layout.addComponent(notes);
        } else {
            notes = null;
        }

        layout.addLayoutClickListener(event -> {
            if (includeShortName) {
                shortname.setVisible(true);
            }
            if (includeNotes) {
                notes.setVisible(true);
            }
        });

        setCompositionRoot(wrapInPanel(layout));
    }

    /**
     * Closes codeEditor, any uncommited changes will be commited
     */
    public void closeEditor() {
        if (isDirty()) {
            commitChanges();
        } else {
            cancelChanges();
        }
    }

    /**
     * Must be called by any subclasses after textfields are initialized or reset.
     */
    protected final void storeInitialStateForDirtyChecking() {
        storeInitialState(code);
        storeInitialState(title);
        storeInitialState(shortname);
        storeInitialState(notes);
    }

    protected abstract Object createEvent();

    protected abstract boolean mustCheckIfAlreadyExists();

    protected abstract ClassificationItem cancelEdit();

    private Panel wrapInPanel(Layout layout) {
        // wrap in panel to respond to Enter
        Panel panel = new Panel(layout);
        panel.setSizeFull();
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        panel.addShortcutListener(new EnterListener() {
            @Override
            public void enterPressed() {
                commitChanges();
            }
        });

        panel.addShortcutListener(new ShortcutListener("Escape", KeyCode.ESCAPE, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                cancelChanges();
            }
        });
        return panel;
    }

    private void commitChanges() {
        if (!validInput(version)) {
            return;
        }
        eventBus.post(createEvent());
    }

    private void cancelChanges() {
        ClassificationItem classificationItem = cancelEdit();
        if (classificationItem != null) {
            eventBus.post(new CancelEditEvent(classificationItem));
        }
    }

    private boolean validInput(StatisticalClassification version) {
        code.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
        title.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
        if (Strings.isNullOrEmpty(code.getValue())) {
            code.addStyleName(KlassTheme.TEXTFIELD_ERROR);
            Notification.show("Kode må fylles ut", Type.WARNING_MESSAGE);
            return false;
        }
        if (mustCheckIfAlreadyExists()) {
            if (version.hasClassificationItem(code.getValue())) {
                code.addStyleName(KlassTheme.TEXTFIELD_ERROR);
                Notification.show("Element finnes allerede med kode: " + code.getValue(), Type.WARNING_MESSAGE);
                return false;
            }
        }
        if (Strings.isNullOrEmpty(title.getValue())) {
            title.addStyleName(KlassTheme.TEXTFIELD_ERROR);
            Notification.show("Navn må fylles ut", Type.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private TextArea createNotes() {
        TextArea notes = new TextArea();
        notes.setInputPrompt("Noter");
        notes.setWidth("100%");
        notes.setRows(3);
        return notes;
    }

    private TextField createShortname() {
        TextField shortname = new TextField();
        shortname.setInputPrompt("Kortnavn");
        shortname.setWidth("10em");
        return shortname;
    }

    private HorizontalLayout wrapCodeAndTitle(TextField code, TextField title) {
        code.setInputPrompt("Kode");
        code.setWidth("4em");
        title.setInputPrompt("Navn");
        title.setWidth("100%");
        HorizontalLayout layout = new HorizontalSpacedLayout(code, title);
        layout.setSizeFull();
        layout.setExpandRatio(title, 1);
        return layout;
    }

    private void storeInitialState(AbstractTextField textField) {
        if (textField == null) {
            return;
        }
        textField.setData(textField.getValue());
    }

    public boolean isDirty() {
        return isDirty(code) || isDirty(title) || isDirty(shortname) || isDirty(notes);
    }

    private boolean isDirty(AbstractTextField textField) {
        if (textField == null) {
            return false;
        }
        return !Objects.equals(textField.getValue(), textField.getData());
    }
}
