package no.ssb.klass.designer.editing.codetables;

import com.google.common.eventbus.EventBus;

import no.ssb.klass.core.model.CorrespondenceTable;

/**
 * @author Mads Lundemo, SSB.
 */
public class TargetCorrespondenceTableFilterWrapper extends
        CodeTableFilterWrapper<TargetCorrespondenceTableCodeTable, CorrespondenceTable> {

    @Override
    protected TargetCorrespondenceTableCodeTable createTable() {
        return new TargetCorrespondenceTableCodeTable();
    }

    @Override
    protected void initTable(EventBus eventbus, TargetCorrespondenceTableCodeTable table, CorrespondenceTable entity) {
        table.init(eventbus, entity);
    }

}
