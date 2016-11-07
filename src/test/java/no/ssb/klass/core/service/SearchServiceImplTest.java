package no.ssb.klass.core.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.service.search.SearchResult;
import no.ssb.klass.core.service.search.SearchableResource;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.testutil.TestUtil;

public class SearchServiceImplTest {
    private SearchServiceImpl subject;
    private String allSections = null;
    private ClassificationType allClassificationTypes = null;

    @Before
    public void setup() {
        subject = new SearchServiceImpl(mock(ClassificationSeriesRepository.class));
    }

    @Test
    public void search() {
        // when
        Page<SearchResult> result = subject.search("any", createPageable(), null, null);

        // then
        assertEquals(0, result.getTotalElements());
    }

    @Test
    public void indexAndSearch() {
        // given
        ClassificationSeries classification = createClassification(1, "Kommuner");

        // when
        subject.indexSynch(classification);
        Page<SearchResult> result = subject.search("kommune", createPageable(), null, null);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals(Long.valueOf(1), result.getContent().get(0).getResourceId());
    }

    @Test
    public void indexAndSearchWithNorwegianCharacter() {
        // given
        ClassificationSeries classification = createClassification(1, "Kommuner");

        // when
        subject.indexSynch(classification);
        Page<SearchResult> result = subject.search("jordbær", createPageable(), null, null);

        // then
        assertEquals(0, result.getTotalElements());
    }

    @Test
    public void multipleIndexAndSearch() {
        // given
        ClassificationSeries classificationOne = createClassification(1, "Kommuner");
        ClassificationSeries classificationTwo = createClassification(2, "Kommune");
        ClassificationSeries classificationThree = createClassification(3, "Bydel");

        // when
        subject.indexSynch(classificationOne);
        subject.indexSynch(classificationTwo);
        subject.indexSynch(classificationThree);
        Page<SearchResult> result = subject.search("kommune", createPageable(), null, null);

        // then
        assertEquals(2, result.getTotalElements());
    }

    @Test
    public void includeCodelistsInSearch() {
        // given
        ClassificationSeries classificationOne = createClassification(1, "Kommuner");
        ClassificationSeries classificationTwo = createCodelist(2, "Kommune");

        // when
        subject.indexSynch(classificationOne);
        subject.indexSynch(classificationTwo);
        Page<SearchResult> result = subject.search("kommune", createPageable(), null, null);

        // then
        assertEquals(2, result.getTotalElements());
    }

    @Test
    public void excludeCodelistsInSearch() {
        // given
        ClassificationSeries classificationOne = createClassification(1, "Kommuner");
        ClassificationSeries classificationTwo = createCodelist(2, "Kommune");

        // when
        subject.indexSynch(classificationOne);
        subject.indexSynch(classificationTwo);
        Page<SearchResult> result = subject.search("kommune", createPageable(), null,
                ClassificationType.CLASSIFICATION);

        // then
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void copyrightedNotIndexed() {
        // given
        ClassificationSeries copyrightedClassification = TestUtil.createCopyrightedClassificationWithId(1,
                "copyrighted");

        // when
        subject.indexSynch(copyrightedClassification);

        // then
        assertEquals(0, subject.size());
    }

    @Test
    public void deletedNotIndexed() {
        // given
        ClassificationSeries classification = createClassification(1, "Kommuner");
        classification.setDeleted();

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(0, subject.size());
    }

    @Test
    public void removeCopyrightedFromIndex() {
        // given
        ClassificationSeries classification = TestUtil.createClassificationWithId(1, "name");
        subject.indexSynch(classification);
        assertEquals(1, subject.size());

        // when
        classification.setCopyrighted(true);
        subject.indexSynch(classification);

        // then
        assertEquals(0, subject.size());
    }

    @Test
    public void removeDeletedFromIndex() {
        // given
        ClassificationSeries classification = createClassification(1, "name");
        subject.indexSynch(classification);
        assertEquals(1, subject.size());

        // when
        classification.setDeleted();
        subject.indexSynch(classification);

        // then
        assertEquals(0, subject.size());
    }

    @Test
    public void onlyPublishedVersionsAreIndexed() {
        // given
        ClassificationSeries classification = TestUtil.createClassificationWithId(1, "name");
        ClassificationVersion published = TestUtil.createClassificationVersion(DateRange.create("2000-01-01",
                "2010-01-01"));
        published.setIntroduction("PUBLISHED", Language.getDefault());
        ClassificationVersion notPublished = TestUtil.createClassificationVersion(DateRange.create("2010-01-01",
                "2020-01-01"));
        notPublished.unpublish(Language.getDefault());
        notPublished.setIntroduction("NOT_PUBLISHED", Language.getDefault());
        classification.addClassificationVersion(published);
        classification.addClassificationVersion(notPublished);
        subject.indexSynch(classification);
        assertEquals(1, subject.size());

        // then
        assertEquals(1, performSearch("PUBLISHED"));
        assertEquals(0, performSearch("NOT_PUBLISHED"));
    }

    @Test
    public void onlyPublishedVariantsAreIndexed() {
        // given
        ClassificationSeries classification = TestUtil.createClassificationWithId(1, "name");
        ClassificationVersion version = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        ClassificationVariant published = TestUtil.createClassificationVariant("PUBLISHED", TestUtil.createUser());
        ClassificationVariant notPublished = TestUtil.createClassificationVariant("NOT_PUBLISHED", TestUtil
                .createUser());
        notPublished.unpublish(Language.getDefault());
        classification.addClassificationVersion(version);
        version.addClassificationVariant(published);
        version.addClassificationVariant(notPublished);
        subject.indexSynch(classification);
        assertEquals(1, subject.size());

        // then
        assertEquals(1, performSearch("PUBLISHED"));
        assertEquals(0, performSearch("NOT_PUBLISHED"));
    }

    @Test
    public void onlyPublishedCorrespondenceTablesAreIndexed() {
        // given
        ClassificationSeries sourceClassification = TestUtil.createClassificationWithId(1, "source");
        ClassificationVersion sourceVersion = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        sourceClassification.addClassificationVersion(sourceVersion);
        ClassificationSeries targetClassification = TestUtil.createClassificationWithId(1, "target");
        ClassificationVersion targetVersion = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        targetClassification.addClassificationVersion(targetVersion);
        CorrespondenceTable published = TestUtil.createCorrespondenceTable("PUBLISHED", sourceVersion, targetVersion);
        CorrespondenceTable notPublished = TestUtil.createCorrespondenceTable("NOT_PUBLISHED", sourceVersion,
                targetVersion);
        notPublished.unpublish(Language.getDefault());
        sourceVersion.addCorrespondenceTable(published);
        sourceVersion.addCorrespondenceTable(notPublished);
        subject.indexSynch(sourceClassification);
        assertEquals(1, subject.size());

        // then
        assertEquals(1, performSearch("PUBLISHED"));
        assertEquals(0, performSearch("NOT_PUBLISHED"));
    }

    @Test
    public void searchSelectsPrioritizedLanguage() {
        // given
        Translatable name = new Translatable("name", "name", "name");
        ClassificationSeries classification = new ClassificationSeries(name, Translatable.create("description",
                Language.NB), false, Language.NB, ClassificationType.CLASSIFICATION, TestUtil.createUser());
        classification.setId(1L);
        ClassificationVersion version = new ClassificationVersion(TestUtil.anyDateRange());
        version.publish(Language.NN);
        version.publish(Language.NB);
        version.publish(Language.EN);
        classification.addClassificationVersion(version);
        classification.setClassificationFamily(TestUtil.createClassificationFamily("family"));

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch("name"));
        assertEquals(Language.getDefaultPrioritizedOrder()[0], getFirstSearchResult("name").getLanguage());
    }

    @Test
    public void matchingSection() {
        // given
        ClassificationSeries classification = createClassification(1, "Kommuner");

        // when
        subject.indexSynch(classification);
        long result = performSearch("Kommuner", classification.getContactPerson().getSection(), allClassificationTypes);

        // then
        assertEquals(1, result);
    }

    @Test
    public void notMatchingSection() {
        // given
        ClassificationSeries classification = createClassification(1, "Kommuner");

        // when
        subject.indexSynch(classification);
        long result = performSearch("Kommuner", "unknown section", allClassificationTypes);

        // then
        assertEquals(0, result);
    }

    @Test
    public void matchingClassificationType() {
        // given
        ClassificationSeries classification = createClassification(1, "Kommuner");

        // when
        subject.indexSynch(classification);
        long result = performSearch("Kommuner", allSections, classification.getClassificationType());

        // then
        assertEquals(1, result);
    }

    @Test
    public void notMatchingClassificationType() {
        // given
        ClassificationSeries classification = createClassification(1, "Kommuner");

        // when
        subject.indexSynch(classification);
        long result = performSearch("Kommuner", allSections, TestUtil.oppositeClassificationType(classification
                .getClassificationType()));

        // then
        assertEquals(0, result);
    }

    @Test
    public void allSectionsAndAllClassificationTypes() {
        // given
        ClassificationSeries classification = createClassification(1, "Kommuner");

        // when
        subject.indexSynch(classification);
        long result = performSearch("Kommuner", allSections, allClassificationTypes);

        // then
        assertEquals(1, result);
    }

    @Test
    public void classificationNameIsIndexed() {
        // given
        final String name = "yyyName";
        ClassificationSeries classification = createClassification(1, name);

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(name));
    }

    @Test
    public void hitInClassificationNameBoostsSearchScore() {
        // given
        final String name = "yyyName";
        ClassificationSeries classification = createClassification(1, name);

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(name));
        assertTrue(subject.search(name, createPageable(), allSections, allClassificationTypes).getContent().get(0)
                .getSearchScore() >= SearchableResource.BOOST_HIT_IN_RESOURCE_NAME);
    }

    @Test
    public void classificationDescriptionIsIndexed() {
        // given
        final String description = "yyyDescription";
        ClassificationSeries classification = createClassificationWithVersion();
        classification.setDescription(Language.getDefault(), description);

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(description));
    }

    @Test
    public void classificationContactPersonIsIndexed() {
        // given
        final User contactPerson = new User("username", "fullname", "section");
        ClassificationSeries classification = createClassificationWithVersion();
        classification.setContactPerson(contactPerson);
        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(contactPerson.getFullname()));
    }

    @Test
    public void classificationStatisticalUnitIsIndexed() {
        // given
        final String statiscalUnitName = "yyyStatiscalUnitName";
        StatisticalUnit statisticalUnit = new StatisticalUnit(Translatable.create(statiscalUnitName, Language
                .getDefault()));
        ClassificationSeries classification = createClassificationWithVersion();
        classification.getStatisticalUnits().add(statisticalUnit);

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(statiscalUnitName));
    }

    @Test
    public void versionLegalBaseIsIndexed() {
        // given
        final String legalBase = "yyyLegalBase";
        ClassificationSeries classification = createClassificationWithVersion();
        classification.getClassificationVersions().get(0).setLegalBase(legalBase, Language.getDefault());

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(legalBase));
    }

    @Test
    public void versionPublicationsIsIndexed() {
        // given
        final String publications = "yyyPublications";
        ClassificationSeries classification = createClassificationWithVersion();
        classification.getClassificationVersions().get(0).setPublications(publications, Language.getDefault());

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(publications));
    }

    @Test
    public void versionDerivedFromIsIndexed() {
        // given
        final String derivedFrom = "yyyDerivedFrom";
        ClassificationSeries classification = createClassificationWithVersion();
        classification.getClassificationVersions().get(0).setDerivedFrom(derivedFrom, Language.getDefault());

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(derivedFrom));
    }

    @Test
    public void versionIntroductionIsIndexed() {
        // given
        final String introduction = "yyyIntroduction";
        ClassificationSeries classification = createClassificationWithVersion();
        classification.getClassificationVersions().get(0).setIntroduction(introduction, Language.getDefault());

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(introduction));
    }

    @Test
    public void variantNameIsIndexed() {
        // given
        final String variantName = "yyyName";
        ClassificationSeries classification = createClassificationWithVersionAndVariant(variantName, TestUtil
                .createUser());

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(variantName));
    }

    @Test
    public void variantContactPersonIsIndexed() {
        // given
        final User variantContactPerson = new User("username", "yyyFullname", "section");
        ClassificationSeries classification = createClassificationWithVersionAndVariant("name", variantContactPerson);

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(variantContactPerson.getFullname()));
    }

    @Test
    public void variantIntroductionIsIndexed() {
        // given
        final String variantIntroduction = "yyyVariantIntroduction";
        ClassificationSeries classification = createClassificationWithVersionAndVariant("anyname", TestUtil
                .createUser());
        classification.getClassificationVersions().get(0).getClassificationVariants().get(0).setIntroduction(
                variantIntroduction, Language.getDefault());

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(variantIntroduction));
    }

    @Test
    public void correspondenceTableDescriptionIsIndexed() {
        // given
        final String correspondenceTableDescription = "yyyDescription";
        ClassificationSeries classification = createClassificationWithVersionAndCorrespondenceTable(
                correspondenceTableDescription);

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(correspondenceTableDescription));
    }

    @Test
    public void versionClassificationItemIsIndexed() {
        // given
        final String code = "yyyCode";
        final String officialName = "yyyOfficialName";
        final String shortName = "yyyShortName";
        final String notes = "yyyNotes";
        ConcreteClassificationItem item = TestUtil.createClassificationItem(code,
                officialName, shortName);
        item.setNotes(notes, Language.getDefault());

        ClassificationSeries classification = createClassificationWithVersion();
        classification.getClassificationVersions().get(0).addNextLevel();
        classification.getClassificationVersions().get(0).addClassificationItem(item, 1, null);

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(code));
        assertEquals(1, performSearch(officialName));
        assertEquals(1, performSearch(shortName));
        assertEquals(1, performSearch(notes));
    }

    @Test
    public void variantClassificationItemIsIndexed() {
        // given
        final String code = "yyyCode";
        final String officialName = "yyyOfficialName";
        final String shortName = "yyyShortName";
        final String notes = "yyyNotes";
        ConcreteClassificationItem item = TestUtil.createClassificationItem(code,
                officialName, shortName);
        item.setNotes(notes, Language.getDefault());

        ClassificationSeries classification = createClassificationWithVersionAndVariant("anyName", TestUtil
                .createUser());
        classification.getClassificationVersions().get(0).getClassificationVariants().get(0).addClassificationItem(item,
                1, null);

        // when
        subject.indexSynch(classification);

        // then
        assertEquals(1, performSearch(code));
        assertEquals(1, performSearch(officialName));
        assertEquals(1, performSearch(shortName));
        assertEquals(1, performSearch(notes));
    }

    private long performSearch(String query, String section, ClassificationType classificationType) {
        return subject.search(query, createPageable(), section, classificationType).getTotalElements();
    }

    private long performSearch(String query) {
        return performSearch(query, allSections, allClassificationTypes);
    }

    private SearchResult getFirstSearchResult(String query) {
        return subject.search(query, createPageable(), null, null).getContent().get(0);
    }

    private ClassificationSeries createClassificationWithVersionAndVariant(String variantName,
            User variantContactPerson) {
        ClassificationSeries classification = createClassificationWithVersion();
        classification.getClassificationVersions().get(0).addClassificationVariant(TestUtil.createClassificationVariant(
                variantName, variantContactPerson));
        return classification;
    }

    private ClassificationSeries createClassificationWithVersionAndCorrespondenceTable(String description) {
        ClassificationVersion sourceVersion = createClassificationWithVersion().getClassificationVersions().get(0);
        ClassificationVersion targetVersion = createClassificationWithVersion().getClassificationVersions().get(0);

        ClassificationSeries classification = createClassificationWithVersion();
        classification.getClassificationVersions().get(0).addCorrespondenceTable(TestUtil.createCorrespondenceTable(
                description, sourceVersion, targetVersion));
        return classification;
    }

    private ClassificationSeries createClassificationWithVersion() {
        return createClassification(1, "anyname");
    }

    private ClassificationSeries createClassification(long id, String name) {
        ClassificationSeries classification = TestUtil.createClassificationWithId(id, name);
        classification.addClassificationVersion(TestUtil.createClassificationVersion(TestUtil.anyDateRange()));
        classification.setClassificationFamily(TestUtil.createClassificationFamily("familie"));
        return classification;
    }

    private ClassificationSeries createCodelist(long id, String name) {
        ClassificationSeries classification = TestUtil.createCodelistWithId(id, name);
        classification.addClassificationVersion(TestUtil.createClassificationVersion(TestUtil.anyDateRange()));
        classification.setClassificationFamily(TestUtil.createClassificationFamily("familie"));
        return classification;
    }

    private Pageable createPageable() {
        return new PageRequest(0, 3);
    }
}
