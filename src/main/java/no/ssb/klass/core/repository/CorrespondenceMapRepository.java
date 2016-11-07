package no.ssb.klass.core.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;

@org.springframework.stereotype.Repository
public interface CorrespondenceMapRepository extends Repository<CorrespondenceMap, Long> {
    /**
     * Finds CorrespondenceMaps whose source references a specific classificationItem
     * 
     * @param classificationItem
     *            classificationItem to match
     * @return list of CorrespondenceMaps whose source references input classificationItem
     */
    List<CorrespondenceMap> findBySource(ClassificationItem item);

    /**
     * Finds CorrespondenceMaps whose target references a specific classificationItem
     * 
     * @param classificationItem
     *            classificationItem to match
     * @return list of CorrespondenceMaps whose target references input classificationItem
     */
    List<CorrespondenceMap> findByTarget(ClassificationItem item);
}
