package no.ssb.klass.core.service.search;

import static com.google.common.base.Preconditions.*;
import static java.util.stream.Collectors.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.google.gwt.thirdparty.guava.common.base.Joiner;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;

public class SearchableResource {
    public static final int BOOST_HIT_IN_RESOURCE_NAME = 100;
    private static final String WORDS = "\\W+";
    private final Long resourceId;
    private final String resourceName;
    private final String resourceAsString;
    private final String description;
    private final String ownerSection;
    private final ClassificationType classificationType;
    private final String familyName;
    private final Map<String, Long> wordCount;
    private final char delimiter;
    private final Language language;

    public SearchableResource(Long resourceId, String resourceName, String resourceAsString, char delimiter,
            Language language, String description, String ownerSection, ClassificationType classificationType,
            String familyName) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceAsString = resourceAsString;
        this.description = description;
        this.ownerSection = checkNotNull(ownerSection);
        this.classificationType = classificationType;
        this.familyName = familyName;
        this.delimiter = delimiter;
        this.language = language;
        this.wordCount = createWordCount(resourceAsString);
    }

    public SearchResult search(String query) {
        long totalCount = 0;
        Set<String> snippets = new HashSet<>();
        for (String token : tokenize(query)) {
            long count = wordCount.getOrDefault(token, 0L);
            if (count != 0) {
                snippets.add(getSnippet(token));
                totalCount += count;
            }
            if (StringUtils.containsIgnoreCase(resourceName, token)) {
                totalCount += BOOST_HIT_IN_RESOURCE_NAME;
            }
        }

        return new SearchResult(resourceId, resourceName, language, totalCount, formatSnippets(snippets), description,
                ownerSection, classificationType, familyName);
    }

    private Map<String, Long> createWordCount(String resourceAsString) {
        List<String> words = tokenize(resourceAsString);
        return words.stream().collect(groupingBy(Function.identity(), counting()));
    }

    private List<String> tokenize(String text) {
        String lowercase = lowerCase(text);
        String[] words = splitWords(lowercase);
        return rootForm(words);
    }

    private List<String> rootForm(String[] words) {
        return Stemmer.newInstance(language).stem(words);
    }

    private String[] splitWords(String text) {
        // (?U) adds unicode support for the word splitter, so that words with æøå is split correctly
        return text.split("(?U)" + WORDS);
    }

    private String lowerCase(String text) {
        return text.toLowerCase();
    }

    private String getSnippet(String token) {
        int index = StringUtils.indexOfIgnoreCase(resourceAsString, token);
        int indexOfDelimiterAfter = resourceAsString.indexOf(delimiter, index);
        if (indexOfDelimiterAfter == -1) {
            indexOfDelimiterAfter = resourceAsString.length();
        }
        int indexOfDelimiterBefore = resourceAsString.lastIndexOf(delimiter, index);
        if (indexOfDelimiterBefore == -1) {
            indexOfDelimiterBefore = 0;
        } else {
            indexOfDelimiterBefore++;
        }
        return resourceAsString.substring(indexOfDelimiterBefore, indexOfDelimiterAfter);
    }

    private String formatSnippets(Set<String> snippets) {
        return Joiner.on(". ").join(snippets);
    }

    public String getOwnerSection() {
        return ownerSection;
    }

    public ClassificationType getClassificationType() {
        return classificationType;
    }
}
