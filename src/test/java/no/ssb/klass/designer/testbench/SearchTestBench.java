package no.ssb.klass.designer.testbench;

import static org.junit.Assert.*;

import org.junit.Test;

import no.ssb.klass.designer.testbench.pages.ClassificationFamilyPage;
import no.ssb.klass.designer.testbench.pages.ClassificationListPage;
import no.ssb.klass.designer.testbench.pages.SearchResultPage;

public class SearchTestBench extends AbstractTestBench {
    @Test
    public void searchTest() {
        ClassificationFamilyPage homePage = getHomePage();

        // Search
        SearchResultPage searchResultPage = homePage.search("politi");
        assertEquals(1, searchResultPage.countSearchResults());

        // Follow search result link
        ClassificationListPage classificationListPage = searchResultPage.selectSearchResult(STANDARD_FOR_POLITIDISTRIKT);
        classificationListPage.tilbakeTilHovedsiden();
    }
}
