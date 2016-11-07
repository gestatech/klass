package no.ssb.klass.designer.editing.codetables;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.Or;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.ReferencingClassificationItem;
import no.ssb.klass.designer.editing.codetables.events.CodeCreatedEvent;
import no.ssb.klass.designer.editing.codetables.events.ReferenceChangedEvent.ReferenceCreatedEvent;
import no.ssb.klass.designer.editing.codetables.events.ReferenceChangedEvent.ReferenceRemovedEvent;
import no.ssb.klass.designer.editing.codetables.utils.DragDropUtils;

/**
 * VariantCodeTable lists classificationItems for a ClassificationVariant. It supports dragging and dropping
 * classificationItems from its owning ClassificationVersion (a separate codeTable).
 * <p>
 * Only supports creating new classificationItems for the first level. These classificationItems are then considered
 * grouping elements, which classificationItems from owning ClassificationVersion can be dropped upon.
 * <p>
 * Generates events for following actions:
 * <ul>
 * <li>CodeDeletedEvent</li>
 * <li>ReferenceCreatedEvent - so that classificationItem in owning ClassificationVersion can be marked as used in
 * variant</li>
 * <li>ReferenceRemovedEvent - so that classificationItem in owning ClassificationVersion can be marked as not used in
 * variant</li>
 * </ul>
 */
public class VariantCodeTable extends PrimaryCodeTable {

    public VariantCodeTable() {
        super();
        setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return new Or(VerticalLocationIs.TOP, VerticalLocationIs.MIDDLE);
            }

            @Override
            public void drop(DragAndDropEvent event) {
                DataBoundTransferable transferable = (DataBoundTransferable) event.getTransferable();
                AbstractSelectTargetDetails target = (AbstractSelectTargetDetails) event.getTargetDetails();
                Object targetItemId = target.getItemIdOver();
                ClassificationItem targetItem = getClassificationItemColumn(getItem(targetItemId));
                boolean isTargetNewCodeEditor = targetItem == null;
                if (target.getDropLocation() == VerticalDropLocation.MIDDLE && !areChildrenAllowed(targetItemId)
                        && !isTargetNewCodeEditor) {
                    Notification.show("Ikke mulig å legge til under elementer her", Type.WARNING_MESSAGE);
                    return;
                }
                if (isAnyDraggedCodeAlreadyPresent(transferable)) {
                    return;
                }

                Table sourceTable = (Table) transferable.getSourceComponent();
                Level level;
                for (Object sourceItemId : DragDropUtils.getSourceItemIds(transferable)) {
                    ClassificationItem classificationItem = createReferencingClassificationItem(sourceTable,
                            sourceItemId);
                    Object insertBefore;
                    ClassificationItem parent;
                    if (isTargetNewCodeEditor) {
                        // Dropped over NewCodeEditor
                        level = version.getFirstLevel().get();
                        insertBefore = targetItemId;
                        parent = null;
                    } else {
                        if (target.getDropLocation() == VerticalDropLocation.TOP) {
                            // Dropped above an existing element, the dropped element shall be a sibling
                            level = targetItem.getLevel();
                            insertBefore = targetItemId;
                            parent = targetItem.getParent();
                        } else {
                            // Dropped onto an existing element, the dropped element shall be a child
                            level = version.getNextLevel(targetItem.getLevel()).get();
                            insertBefore = null;
                            parent = targetItem;
                            setCollapsed(targetItemId, false);
                        }
                    }

                    version.addClassificationItem(classificationItem, level.getLevelNumber(), parent);
                    eventbus.post(new CodeCreatedEvent(classificationItem, insertBefore));
                    updateSourceItem(sourceItemId);
                }
            }

            private boolean isAnyDraggedCodeAlreadyPresent(DataBoundTransferable transferable) {
                Table sourceTable = (Table) transferable.getSourceComponent();
                for (Object sourceItemId : DragDropUtils.getSourceItemIds(transferable)) {
                    ClassificationItem sourceClassificationItem = getSourceClassificationItem(sourceTable,
                            sourceItemId);
                    if (version.hasClassificationItem(sourceClassificationItem.getCode())) {
                        Notification.show("Kode: " + sourceClassificationItem.getCode() + ", finnes allerede",
                                Type.WARNING_MESSAGE);
                        return true;
                    }
                }
                return false;
            }

            private ClassificationItem createReferencingClassificationItem(Table sourceTable, Object sourceItemId) {
                ClassificationItem classificationItem = new ReferencingClassificationItem(getSourceClassificationItem(
                        sourceTable, sourceItemId));
                return classificationItem;
            }

            private ClassificationItem getSourceClassificationItem(Table sourceTable, Object sourceItemId) {
                return getClassificationItemColumn(sourceTable.getItem(sourceItemId));
            }

            private void updateSourceItem(Object sourceItemId) {
                eventbus.post(new ReferenceCreatedEvent(sourceItemId));
            }
        });
    }

    @Override
    protected void updateChildrenAllowed(ClassificationItem classificationItem) {
        boolean isFirstLevel = version.isFirstLevel(classificationItem.getLevel());
        getContainer().setChildrenAllowed(classificationItem.getUuid(), isFirstLevel);
    }

    @Override
    protected void moveNewCodeEditorLast(ClassificationItem parent) {
    }

    @Override
    protected void deleteItem(ClassificationItem classificationItem) {
        postReferenceRemoveEvent(classificationItem);
        super.deleteItem(classificationItem);
    }

    private void postReferenceRemoveEvent(ClassificationItem classificationItem) {
        if (classificationItem.isReference()) {
            eventbus.post(new ReferenceRemovedEvent(classificationItem.getUuid()));
        }
        for (ClassificationItem child : version.getChildrenOfClassificationItem(classificationItem)) {
            postReferenceRemoveEvent(child);
        }
    }

    @Override
    protected void addNewCodeEditor(ClassificationItem parent, Level level) {
        if (isReadOnly()) {
            return;
        }
        if (!version.isFirstLevel(level)) {
            return;
        }
        super.addNewCodeEditor(parent, level);
    }
}
