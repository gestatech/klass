package no.ssb.klass.rest.applicationtest.providers;

import java.util.Arrays;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.testutil.TestUtil;

/**
 * @author Mads Lundemo, SSB.
 */
public final class TestDataProvider {

    public static final String KOMMUNEINNDELING_NAVN_NO = "Standard for kommuneinndeling";
    public static final String KOMMUNEINNDELING_NAVN_NN = "Standard for kommuneinndeling (Nynorsk)";
    public static final String KOMMUNEINNDELING_NAVN_EN = "Standard for kommuneinndeling(English)";

    public static final String KOMMUNEINNDELING_BESKRIVELSE_NO =
            "kommune inndelingen er en administrativ inndeling av kommuner i Norge";
    public static final String KOMMUNEINNDELING_BESKRIVELSE_NN = "kommune beskrivelse";
    public static final String KOMMUNEINNDELING_BESKRIVELSE_EN = "English description";

    public static final String BYDELSINNDELING_NAVN_NO = "Standard for bydelsinndeling";
    public static final String BYDELSINNDELING_BESKRIVELSE_NO = "Bydel utgjør geografiske områder i en kommune";

    public static final String FAMILIEGRUPPERING_NAVN_NO = "Standard for gruppering av familier";
    public static final String FAMILIEGRUPPERING_BESKRIVELSE_NO =
            "Standarden beskriver de ulike familitypene som i dag brukes i SSBs familistatistikk";

    private TestDataProvider() {
    }

    public static ClassificationSeries createClassificationKommuneinndeling() {
        ClassificationSeries classification = TestUtil.createClassification(KOMMUNEINNDELING_NAVN_NO,
                KOMMUNEINNDELING_NAVN_NN, KOMMUNEINNDELING_NAVN_EN, KOMMUNEINNDELING_BESKRIVELSE_NO,
                KOMMUNEINNDELING_BESKRIVELSE_NN, KOMMUNEINNDELING_BESKRIVELSE_EN);

        ClassificationVersion version2014 = TestUtil.createClassificationVersion(DateRange.create("2014-01-01", null));

        Level level = TestUtil.createLevel(1);
        version2014.addLevel(level);
        version2014.addClassificationItem(TestUtil.createClassificationItem("0101", "Halden"), level.getLevelNumber(),
                null);
        version2014.addClassificationItem(TestUtil.createClassificationItem("0104", "Moss"), level.getLevelNumber(),
                null);
        version2014.addClassificationItem(TestUtil.createClassificationItem("0301", "Oslo"), level.getLevelNumber(),
                null);
        version2014.addClassificationItem(TestUtil.createClassificationItem("1739", "Raarvihke Røyrvik"), level
                .getLevelNumber(), null);
        version2014.addClassificationItem(TestUtil.createClassificationItem("1939", "Omasvuotna Storfjord Omasvuonon"),
                level.getLevelNumber(), null);
        classification.addClassificationVersion(version2014);

        ClassificationVersion version2012 = TestUtil.createClassificationVersion(DateRange.create("2012-01-01",
                "2014-01-01"));

        Level level2012 = TestUtil.createLevel(1);
        version2012.addLevel(level2012);
        version2012.addClassificationItem(TestUtil.createClassificationItem("0101", "Halden"), level2012
                .getLevelNumber(), null);
        version2012.addClassificationItem(TestUtil.createClassificationItem("0104", "Moss"), level2012.getLevelNumber(),
                null);
        version2012.addClassificationItem(TestUtil.createClassificationItem("0301", "Oslo"), level2012.getLevelNumber(),
                null);
        version2012.addClassificationItem(TestUtil.createClassificationItem("1739", "Røyrvik"), level2012
                .getLevelNumber(), null);
        version2012.addClassificationItem(TestUtil.createClassificationItem("1939", "Storfjord"), level2012
                .getLevelNumber(), null);
        version2012.publish(Language.NB);
        classification.addClassificationVersion(version2012);

        return classification;
    }

    public static CorrespondenceTable createAndAddChangeCorrespondenceTable(
            ClassificationSeries kommuneClassification) {
        ClassificationVersion kommune2014 = kommuneClassification.getClassificationVersions().get(0);
        ClassificationVersion kommune2012 = kommuneClassification.getClassificationVersions().get(1);
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(kommune2014, kommune2012);
        kommune2014.addCorrespondenceTable(correspondenceTable);
        correspondenceTable.addCorrespondenceMap(new CorrespondenceMap(kommune2014.findItem("1739"), kommune2012
                .findItem("1739")));
        correspondenceTable.addCorrespondenceMap(new CorrespondenceMap(kommune2014.findItem("1939"), kommune2012
                .findItem("1939")));
        return correspondenceTable;
    }

    public static ClassificationSeries createClassificationBydelsinndeling() {
        ClassificationSeries classification = TestUtil.createClassification(BYDELSINNDELING_NAVN_NO,
                BYDELSINNDELING_BESKRIVELSE_NO);

        ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("2014-01-01", null));
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        version.addClassificationItem(TestUtil.createClassificationItem("030101", "Gamle Oslo"), level.getLevelNumber(),
                null);
        version.addClassificationItem(TestUtil.createClassificationItem("030102", "Grünerløkka"), level
                .getLevelNumber(), null);
        version.addClassificationItem(TestUtil.createClassificationItem("030103", "Sagene"), level.getLevelNumber(),
                null);
        version.addClassificationItem(TestUtil.createClassificationItem("030104", "St. Hanshaugen"), level
                .getLevelNumber(), null);
        version.addClassificationItem(TestUtil.createClassificationItem("030105", "Frogner"), level.getLevelNumber(),
                null);
        version.publish(Language.EN);
        version.publish(Language.NN);
        version.publish(Language.NB);
        classification.addClassificationVersion(version);
        return classification;
    }

    public static ClassificationSeries createFamiliegrupperingCodelist(User user) {
        ClassificationSeries classification = TestUtil.createCodelist(FAMILIEGRUPPERING_NAVN_NO,
                FAMILIEGRUPPERING_BESKRIVELSE_NO);

        classification.setContactPerson(user);
        ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("2006-01-01", null));
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        version.addClassificationItem(TestUtil.createClassificationItem("1.1.1", "Enpersonfamilie, person under 30 år"),
                level.getLevelNumber(), null);
        ClassificationVariant variant = TestUtil.createClassificationVariant(
                "Variant - Tilleggsinndeling for familier", user);
        variant.addClassificationItem(TestUtil.createClassificationItem("A", "Enpersonfamilie"), 1, null);
        variant.addClassificationItem(TestUtil.createClassificationItem("B", "Ektepar"), 1, null);
        variant.addClassificationItem(TestUtil.createClassificationItem("A_", "Enpersonfamilie"), 2, variant.findItem(
                "A"));
        variant.addClassificationItem(TestUtil.createClassificationItem("BA", "Ektepar med barn (yngste barn 0-17 år)"),
                2, variant.findItem("B"));
        variant.addClassificationItem(TestUtil.createClassificationItem("BB", "Ektepar uten barn 0-17 år"), 2, variant
                .findItem("B"));
        version.addClassificationVariant(variant);
        classification.addClassificationVersion(version);
        return classification;
    }

    public static CorrespondenceTable createAndAddCorrespondenceTable(ClassificationSeries kommuneClassification,
            ClassificationSeries bydelClassification) {
        ClassificationVersion kommune = kommuneClassification.getClassificationVersions().get(0);
        ClassificationVersion bydel = bydelClassification.getClassificationVersions().get(0);
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(kommune, bydel);
        for (String bydelCode : Arrays.asList("030101", "030102", "030103")) {
            correspondenceTable.addCorrespondenceMap(new CorrespondenceMap(kommune.findItem("0301"), bydel.findItem(
                    bydelCode)));
        }
        kommune.addCorrespondenceTable(correspondenceTable);
        return correspondenceTable;
    }

    public static ClassificationVersion createClassificationVersionWithTranslations() {
        ClassificationSeries classification = TestUtil.createCodelist(FAMILIEGRUPPERING_NAVN_NO,
                FAMILIEGRUPPERING_BESKRIVELSE_NO);
        ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("2014-01-01", null));
        classification.addClassificationVersion(version);
        Level level1 = TestUtil.createLevel(1);
        Level level2 = TestUtil.createLevel(2);
        Level level3 = TestUtil.createLevel(3);
        version.addLevel(level1);
        version.addLevel(level2);
        version.addLevel(level3);

        ConcreteClassificationItem item1 = new ConcreteClassificationItem("030101",
                new Translatable("Norge", "Noreg", "Norway"),
                new Translatable("no", "nn", "en"),
                new Translatable("ikke", "ikkje", "not"));

        ConcreteClassificationItem item2 = new ConcreteClassificationItem("030102",
                new Translatable("Pinnsvin", "Bustyvel", "hedgehog"), new Translatable("pi", "bu", "he"));

        ConcreteClassificationItem item3 = new ConcreteClassificationItem("030103",
                new Translatable("bulldoser", "stålstut", "bulldozer"), new Translatable("bs", "st", "bz"));

        version.addClassificationItem(item1, 1, null);
        version.addClassificationItem(item2, 2, item1);
        version.addClassificationItem(item3, 3, item2);
        return version;
    }

}