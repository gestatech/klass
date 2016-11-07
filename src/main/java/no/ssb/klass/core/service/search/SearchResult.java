package no.ssb.klass.core.service.search;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;

public class SearchResult implements Comparable<SearchResult> {
    private final Long resourceId;
    private final String resourceName;
    private final String description;
    private final String ownerSection;
    private final ClassificationType classificationType;
    private final Language language;
    private final Long searchScore;
    private final String snippet;
    private final String classificationFamilyName;

    public SearchResult(Long resourceId, String resourceName, Language language, Long searchScore, String snippet,
            String description, String ownerSection, ClassificationType classificationType,
            String classificationFamilyName) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.language = language;
        this.searchScore = searchScore;
        this.snippet = snippet;
        this.description = description;
        this.ownerSection = ownerSection;
        this.classificationType = classificationType;
        this.classificationFamilyName = classificationFamilyName;
    }

    public Language getLanguage() {
        return language;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public Long getSearchScore() {
        return searchScore;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerSection() {
        return ownerSection;
    }

    public ClassificationType getClassificationType() {
        return classificationType;
    }

    public String getClassificationFamilyName() {
        return classificationFamilyName;
    }

    @Override
    public String toString() {
        return "SearchResult [resourceId=" + resourceId + ", language=" + language + ", searchScore=" + searchScore
                + "]";
    }

    /**
     * Reversed so that highest searchScore is first
     */
    @Override
    public int compareTo(SearchResult other) {
        return other.searchScore.compareTo(searchScore);
    }
}
