package no.ssb.klass.designer.editing;

import com.vaadin.ui.Component;

import no.ssb.klass.core.model.CorrespondenceTable;

public interface HasCorrespondenceTableComponent extends HasEditingState, Component {
    void init(CorrespondenceTable correspondenceTable);

    /**
     * Required to be called before saving correspondenceTable
     */
    void prepareSave();
}
