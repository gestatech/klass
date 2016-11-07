package no.ssb.klass.designer.editing.codetables;

import com.google.common.eventbus.EventBus;

import no.ssb.klass.core.model.CorrespondenceTable;

/**
 * @author Mads Lundemo, SSB.
 */
public class SourceCorrespondenceTableFilterWrapper extends
        CodeTableFilterWrapper<SourceCorrespondenceTableCodeTable, CorrespondenceTable> {

    @Override
    protected SourceCorrespondenceTableCodeTable createTable() {
        return new SourceCorrespondenceTableCodeTable();
    }

    @Override
    protected void initTable(EventBus eventbus, SourceCorrespondenceTableCodeTable table, CorrespondenceTable entity) {
        table.init(eventbus, entity);
    }
}
