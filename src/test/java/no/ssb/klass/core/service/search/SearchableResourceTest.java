package no.ssb.klass.core.service.search;

import static org.junit.Assert.*;

import org.junit.Test;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;

public class SearchableResourceTest {

    public static final String OWNER_SECTION = "000";

    @Test
    public void resourceId() {
        // given
        final Long resourceId = 1L;
        SearchableResource subject = new SearchableResource(resourceId, "name", "", '^', Language.getDefault(),
                "Beskrivelse", OWNER_SECTION, ClassificationType.CLASSIFICATION, "familie");

        // when
        SearchResult result = subject.search("");

        // then
        assertEquals(resourceId, result.getResourceId());
    }

    @Test
    public void resourceName() {
        // given
        final String resourceName = "resourceName";
        SearchableResource subject = new SearchableResource(1L, resourceName, "", '^', Language.getDefault(),
                "Beskrivelse", OWNER_SECTION, ClassificationType.CLASSIFICATION, "familie");

        // when
        SearchResult result = subject.search("");

        // then
        assertEquals(resourceName, result.getResourceName());
    }

    @Test
    public void codelist() {
        // given
        SearchableResource subject = new SearchableResource(1L, "name", "", '^', Language.getDefault(),
                "Beskrivelse", OWNER_SECTION, ClassificationType.CLASSIFICATION, "familie");

        // when
        ClassificationType result = subject.getClassificationType();

        // then
        assertEquals(ClassificationType.CLASSIFICATION, result);
    }

    @Test
    public void oneHit() {
        // given
        SearchableResource subject = new SearchableResource(1L, "name", "Halden", '^', Language.getDefault(),
                "Beskrivelse", OWNER_SECTION, ClassificationType.CLASSIFICATION, "familie");

        // when
        SearchResult result = subject.search("Halden");

        // then
        assertEquals(Long.valueOf(1L), result.getSearchScore());
        assertEquals("Halden", result.getSnippet());
    }

    @Test
    public void multipleHits() {
        // given
        SearchableResource subject = new SearchableResource(1L, "name", "Halden halden", '^', Language
                .getDefault(), "Beskrivelse", OWNER_SECTION, ClassificationType.CLASSIFICATION, "familie");

        // when
        SearchResult result = subject.search("Halden");

        // then
        assertEquals(Long.valueOf(2L), result.getSearchScore());
        assertEquals("Halden halden", result.getSnippet());
    }

    @Test
    public void noHits() {
        // given
        SearchableResource subject = new SearchableResource(1L, "name", "Halden", '^', Language.getDefault(),
                "Beskrivelse", OWNER_SECTION, ClassificationType.CLASSIFICATION, "familie");

        // when
        SearchResult result = subject.search("Sarpsborg");

        // then
        assertEquals(Long.valueOf(0L), result.getSearchScore());
    }

    @Test
    public void caseInsensitive() {
        // given
        SearchableResource subject = new SearchableResource(1L, "name", "Halden", '^', Language.getDefault(),
                "Beskrivelse", OWNER_SECTION, ClassificationType.CLASSIFICATION, "familie");

        // when
        SearchResult result = subject.search("halden");

        // then
        assertEquals(Long.valueOf(1L), result.getSearchScore());
        assertEquals("Halden", result.getSnippet());
    }

    @Test
    public void stemming() {
        // given
        SearchableResource subject = new SearchableResource(1L, "name", "Kommuner kommune saks sakser", '^',
                Language.getDefault(), "Beskrivelse", OWNER_SECTION, ClassificationType.CLASSIFICATION, "familie");

        // then
        assertEquals(Long.valueOf(2L), subject.search("Kommune").getSearchScore());
        assertEquals(Long.valueOf(2L), subject.search("Kommuner").getSearchScore());
        assertEquals(Long.valueOf(2L), subject.search("saks").getSearchScore());
        assertEquals(Long.valueOf(2L), subject.search("sakser").getSearchScore());
    }

    @Test
    public void snippetFirstSentence() {
        // given
        SearchableResource subject = new SearchableResource(1L, "name", "first sentence^second sentence", '^',
                Language.getDefault(), "Beskrivelse", OWNER_SECTION, ClassificationType.CLASSIFICATION, "familie");

        // when
        SearchResult result = subject.search("first");

        // then
        assertEquals("first sentence", result.getSnippet());
    }

    @Test
    public void snippetSecondSentence() {
        // given
        SearchableResource subject = new SearchableResource(1L, "name", "first sentence^second sentence", '^',
                Language.getDefault(), "Beskrivelse", OWNER_SECTION, ClassificationType.CLASSIFICATION, "familie");

        // when
        SearchResult result = subject.search("second");

        // then
        assertEquals("second sentence", result.getSnippet());
    }

    @Test
    public void snippetShowsOriginalCase() {
        // given
        SearchableResource subject = new SearchableResource(1L, "name", "First sentence^Second sentence", '^',
                Language.getDefault(), "Beskrivelse", OWNER_SECTION, ClassificationType.CLASSIFICATION, "familie");

        // when
        SearchResult result = subject.search("second");

        // then
        assertEquals("Second sentence", result.getSnippet());
    }

}
