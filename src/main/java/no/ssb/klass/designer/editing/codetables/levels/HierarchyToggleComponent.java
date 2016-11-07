package no.ssb.klass.designer.editing.codetables.levels;

import com.google.common.eventbus.EventBus;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.designer.editing.codetables.events.CloseHierarchyEvent;
import no.ssb.klass.designer.editing.codetables.events.OpenHierarchyEvent;

/**
 * @author Mads Lundemo, SSB.
 */
public class HierarchyToggleComponent extends CustomComponent {

    private static final String CLOSE_HIERARCHY_TEXT = "Lukk hierarki";
    private static final String OPEN_HIERARCHY_TEXT = "Åpne hierarki";

    private boolean isHierarchyOpen = false;
    private EventBus eventBus;
    private Button button;

    public HierarchyToggleComponent() {
        button = new Button(OPEN_HIERARCHY_TEXT);
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addClickListener(event -> toggleHierarchy());
        setWidth("-1px");
        setCompositionRoot(button);
    }

    public void init(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    private void toggleHierarchy() {
        if (isHierarchyOpen) {
            closeHierarchy();
            button.setCaption(OPEN_HIERARCHY_TEXT);
        } else {
            openHierarchy();
            button.setCaption(CLOSE_HIERARCHY_TEXT);
        }
        isHierarchyOpen = !isHierarchyOpen;

    }

    private void closeHierarchy() {
        eventBus.post(new CloseHierarchyEvent());
    }

    private void openHierarchy() {
        eventBus.post(new OpenHierarchyEvent());
    }

}
