package no.ssb.klass.core.service;

import static com.google.common.base.Preconditions.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.service.search.SearchResult;
import no.ssb.klass.core.service.search.SearchableResource;
import no.ssb.klass.core.util.TimeUtil;

@Service
public class SearchServiceImpl implements SearchService {
    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);
    private final Map<Long, List<SearchableResource>> searchableClassifications;
    private static final char DELIMITER = '^';
    private final ClassificationSeriesRepository classificationRepository;

    @Autowired
    public SearchServiceImpl(ClassificationSeriesRepository classificationRepository) {
        this.classificationRepository = classificationRepository;
        searchableClassifications = new ConcurrentHashMap<>();
    }

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
    @Override
    public Page<SearchResult> search(String query, Pageable pageable, String filterOnSection,
            ClassificationType classificationType) {
        Date start = TimeUtil.now();
        // @formatter:off
        List<SearchResult> searchResults = searchableClassifications.values().parallelStream()
                .flatMap(searchableResourceList -> searchableResourceList.stream())
                .filter(searchableResource -> filterOnSection == null || searchableResource.getOwnerSection().equals(filterOnSection))
                .filter(searchableResource -> classificationType == null || searchableResource.getClassificationType().equals(classificationType))
                
                .map(searchableResource -> searchableResource.search(query))
                
                .filter(searchResult -> searchResult.getSearchScore() != 0)
        
                .collect(groupingBy(searchResult -> searchResult.getResourceId())).values().stream()
                .map(searchResultsPerClassification -> selectPrioritizedLanguage(searchResultsPerClassification))
                
                .collect(toList());
        // @formatter:on
        Collections.sort(searchResults);

        Page<SearchResult> page = new PageImpl<>(extractPage(pageable, searchResults), pageable, searchResults.size());
        log.debug("Search for: '" + query + "' resulted in " + searchResults.size() + " hits. Took (ms): " + TimeUtil
                .millisecondsSince(start));
        return page;
    }

    private SearchResult selectPrioritizedLanguage(List<SearchResult> searchResults) {
        for (Language language : Language.getDefaultPrioritizedOrder()) {
            for (SearchResult searchResult : searchResults) {
                if (searchResult.getLanguage().equals(language)) {
                    return searchResult;
                }
            }
        }
        throw new IllegalStateException("SearchResult did not match any language: " + searchResults);
    }

    private List<SearchResult> extractPage(Pageable pageable, List<SearchResult> searchResults) {
        int pageSize = Math.min(pageable.getPageSize(), Math.max(0, searchResults.size() - pageable.getOffset()));
        if (pageable.getOffset() + pageSize > searchResults.size()) {
            return new LinkedList<>();
        }
        return searchResults.subList(pageable.getOffset(), pageable.getOffset() + pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    @Async
    public void indexAsynch(Long classificationSeriesId) {
        checkNotNull(classificationSeriesId);
        ClassificationSeries classification = classificationRepository.getOne(classificationSeriesId);
        indexSynch(classification);
    }

    @Override
    @Transactional(readOnly = true)
    public void indexSynch(ClassificationSeries classification) {
        if (classification.isCopyrighted() || classification.isDeleted()) {
            // copyrighted and deleted classifications may not be searched. Remove in case was searchable before.
            searchableClassifications.remove(classification.getId());
            return;
        }
        Date start = TimeUtil.now();

        List<SearchableResource> searchableResources = new ArrayList<>();
        for (Language language : Language.values()) {
            if (classification.isPublished(language)) {
                String classificationAsString = createStringRepresentation(classification, language);
                searchableResources.add(
                        new SearchableResource(classification.getId(),
                                classification.getName(language),
                                classificationAsString, DELIMITER,
                                language, classification.getDescription(language),
                                classification.getContactPerson().getSection(),
                                classification.getClassificationType(),
                                classification.getClassificationFamily().getName()));
            }
        }
        searchableClassifications.put(classification.getId(), searchableResources);
        log.info("Indexing: " + classification.getNameInPrimaryLanguage() + ". Took (ms): " + TimeUtil
                .millisecondsSince(start));
    }

    private String createStringRepresentation(ClassificationSeries classification, Language language) {
        List<ClassificationVersion> versions = extractPublishedVersions(classification, language);
        List<ClassificationVariant> variants = extractPublishedVariants(classification, language);
        List<CorrespondenceTable> correspondenceTables = extractPublishedCorrespondenceTables(classification, language);
        List<ClassificationItem> items = extractItems(classification, language);

        List<String> parts = Lists.newArrayList(
                indexClassifications(classification, language),
                indexVersions(versions, language),
                indexVariants(variants, language),
                indexCorrespondenceTables(correspondenceTables, language),
                indexItems(items, language));
        return Joiner.on(DELIMITER).join(parts);
    }

    private String indexItems(List<ClassificationItem> items, Language language) {
        Set<String> terms = new HashSet<>();
        for (ClassificationItem item : items) {
            terms.add(item.getCode() + " " + item.getOfficialName(language) + " " + item.getShortName(language) + " "
                    + item.getNotes(language));
        }
        return Joiner.on(DELIMITER).join(terms);
    }

    private String indexCorrespondenceTables(List<CorrespondenceTable> correspondenceTables, Language language) {
        Set<String> terms = new HashSet<>();
        for (CorrespondenceTable correspondenceTable : correspondenceTables) {
            terms.add(correspondenceTable.getName(language));
            terms.add(correspondenceTable.getDescription(language));
        }
        return Joiner.on(DELIMITER).join(terms);
    }

    private String indexVariants(List<ClassificationVariant> variants, Language language) {
        Set<String> terms = new HashSet<>();
        for (ClassificationVariant variant : variants) {
            terms.add(variant.getName(language));
            terms.add(variant.getIntroduction(language));
            if (language.equals(variant.getPrimaryLanguage())) {
                terms.add(variant.getContactPerson().getFullname());
            }
        }
        return Joiner.on(DELIMITER).join(terms);
    }

    private String indexVersions(List<ClassificationVersion> versions, Language language) {
        Set<String> terms = new HashSet<>();
        for (ClassificationVersion version : versions) {
            terms.add(version.getLegalBase(language));
            terms.add(version.getPublications(language));
            terms.add(version.getDerivedFrom(language));
            terms.add(version.getIntroduction(language));
        }
        return Joiner.on(DELIMITER).join(terms);
    }

    private String indexClassifications(ClassificationSeries classification, Language language) {
        Set<String> terms = new HashSet<>();
        terms.add(classification.getName(language));
        terms.add(classification.getDescription(language));
        if (language.equals(classification.getPrimaryLanguage())) {
            terms.add(classification.getContactPerson().getFullname());
            for (StatisticalUnit statisticalUnit : classification.getStatisticalUnits()) {
                terms.add(statisticalUnit.getName(language));
            }
        }
        return Joiner.on(DELIMITER).join(terms);
    }

    private List<ClassificationItem> extractItems(ClassificationSeries classification, Language language) {
        List<ClassificationItem> items = new ArrayList<>();
        items.addAll(extractPublishedVersions(classification, language).stream().flatMap(
                version -> version.getAllClassificationItems().stream()).collect(toList()));

        items.addAll(extractPublishedVariants(classification, language).stream().flatMap(variant -> variant
                .getAllClassificationItems().stream()).collect(toList()));
        return items;
    }

    private List<CorrespondenceTable> extractPublishedCorrespondenceTables(
            ClassificationSeries classification, Language language) {
        return classification.getClassificationVersions().stream()
                .flatMap(version -> version.getCorrespondenceTables().stream())
                .filter(correspondanceTable -> correspondanceTable.isPublished(language))
                .collect(toList());
    }

    private List<ClassificationVariant> extractPublishedVariants(ClassificationSeries classification,
            Language language) {
        return classification.getClassificationVersions().stream()
                .flatMap(version -> version.getClassificationVariants().stream())
                .filter(variant -> variant.isPublished(language))
                .collect(toList());
    }

    private List<ClassificationVersion> extractPublishedVersions(ClassificationSeries classification,
            Language language) {
        return classification.getClassificationVersions().stream()
                .filter(version -> version.isPublished(language))
                .collect(toList());
    }

    int size() {
        return searchableClassifications.size();
    }
}
