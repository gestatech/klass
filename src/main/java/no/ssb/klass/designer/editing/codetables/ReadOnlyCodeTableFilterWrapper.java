package no.ssb.klass.designer.editing.codetables;

import com.google.common.eventbus.EventBus;
import com.vaadin.ui.Table;

import no.ssb.klass.core.model.ClassificationVersion;

/**
 * @author Mads Lundemo, SSB.
 */
public class ReadOnlyCodeTableFilterWrapper extends
        CodeTableFilterWrapper<ReadOnlyCodeTable, ClassificationVersion> {

    @Override
    protected ReadOnlyCodeTable createTable() {
        ReadOnlyCodeTable table = new ReadOnlyCodeTable();
        setNotSelectable(table);
        return table;
    }

    @Override
    protected void initTable(EventBus eventbus, ReadOnlyCodeTable table, ClassificationVersion entity) {
        table.init(eventbus, entity, entity.getPrimaryLanguage());
    }

    private void setNotSelectable(Table table) {
        if (isReadOnly()) {
            return;
        }
        table.setMultiSelect(false);
        table.setSelectable(false);
        table.setDragMode(Table.TableDragMode.ROW);
    }
}
