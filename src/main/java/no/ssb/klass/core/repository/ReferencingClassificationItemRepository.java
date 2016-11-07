package no.ssb.klass.core.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ReferencingClassificationItem;

@org.springframework.stereotype.Repository
public interface ReferencingClassificationItemRepository extends Repository<ReferencingClassificationItem, Long> {
    /**
     * Finds ReferencingClassificationItems that references a specific classificationItem
     * 
     * @param classificationItem
     *            classificationItem to match
     * @return list of ReferencingClassificationItem referencing input classificationItem
     */
    List<ReferencingClassificationItem> findByReference(ClassificationItem classificationItem);
}
