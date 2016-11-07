package no.ssb.klass.designer.editing.codetables;

import com.google.common.eventbus.EventBus;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.BaseEntity;
import no.ssb.klass.designer.classificationlist.AbstractTable;
import no.ssb.klass.designer.listeners.HierarchyTextChangeListener;

/**
 * @author Mads Lundemo, SSB.
 */
public abstract class CodeTableFilterWrapper<T extends BaseCodeTable, S extends BaseEntity> extends AbstractTable {

    private TextField filterBox;
    private T table;

    protected abstract T createTable();

    protected abstract void initTable(EventBus eventbus, T table, S entity);

    public void init(EventBus eventbus, S entity) {
        configureTable();
        filterBox = createFilter(table);
        rootLayout.addComponents(wrapFilter(filterBox), table);
        rootLayout.setExpandRatio(table, 1);
        initTable(eventbus, table, entity);

    }

    private void configureTable() {
        table = createTable();
        table.setSizeFull();
        table.setSelectable(true);
        table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        table.addStyleName(ValoTheme.TABLE_NO_HEADER);
        if (isReadOnly()) {
            table.setReadOnly(true);
        }
    }

    private TextField createFilter(Table classificationTable) {
        TextField filter = new TextField();
        filter.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.TIMEOUT);
        filter.setTextChangeTimeout(500);
        filter.setInputPrompt("Filtrer elementer");
        filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filter.setWidth("100%");
        filter.addTextChangeListener(new HierarchyTextChangeListener(classificationTable, BaseCodeTable.CODE_COLUMN));
        return filter;
    }

    public void refresh() {
        table.refresh();
    }

    public boolean hasChanges() {
        return table.hasChanges();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        if (table != null) {
            table.setReadOnly(readOnly);
        }
    }
}
