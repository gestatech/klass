package no.ssb.klass.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.service.search.SearchResult;

public interface SearchService {

    /**
     * Search for classifications
     * 
     * @param query
     *            query to match, may be many words. Each word will then be searched for
     * @param pageable
     * @param filterOnSection
     *            null means all sections
     * @param classificationType
     *            null means all classificationTypes
     * @return searchResults
     */
    Page<SearchResult> search(String query, Pageable pageable, String filterOnSection,
            ClassificationType classificationType);

    /**
     * Indexes a classification and makes it searchable.
     * 
     * <p>
     * Note:
     * <ul>
     * <li>If classification is copyrighted the classification is not made searchable</li>
     * <li>Classification is indexed in each language</li>
     * </ul>
     * 
     * <p>
     * Implementation note: Indexing is done asynchronously in order to be more responsive for front end application.
     * 
     * @param classification
     */
    void indexAsynch(Long classificationSeriesId);

    /**
     * Same as indexAsynch, but performs indexing within same thread. This means user must wait while indexing, so in
     * normal cases prefer indexAsynch.
     * <p>
     * Mostly to be used by unit tests
     * 
     * @param classificationSeries
     */
    void indexSynch(ClassificationSeries classificationSeries);
}