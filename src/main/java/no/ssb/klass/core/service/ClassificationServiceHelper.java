package no.ssb.klass.core.service;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.core.service.dto.Code;
import no.ssb.klass.core.service.dto.Correspondence;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.KlassResourceNotFoundException;

final class ClassificationServiceHelper {

    private ClassificationServiceHelper() {
        // Utility class
    }

    static List<Code> findVariantClassificationCodes(ClassificationSeries classification, String variantName,
            Language language, DateRange dateRange) {
        List<Code> codes = new ArrayList<>();
        for (ClassificationVersion version : classification.getClassificationVersions()) {
            if (version.isPublished(language) && version.getDateRange().overlaps(dateRange)) {
                ClassificationVariant variant = version.findVariant(variantName, language);
                if (!variant.isPublished(language)) {
                    throw new KlassResourceNotFoundException("ClassificationVariant: " + variant.getName(language) + " "
                            + variant.getClassificationVersion().getDateRange() + ". Is not published in language: "
                            + language);
                }
                codes.addAll(mapClassificationItemsToCodes(variant, version.getDateRange(), language));
            }
        }
        return codes;
    }

    static List<Code> findClassificationCodes(ClassificationSeries classification, DateRange dateRange,
            Language language) {
        List<Code> codes = new ArrayList<>();
        for (ClassificationVersion version : classification.getClassificationVersions()) {
            if (version.getDateRange().overlaps(dateRange)) {
                if (!version.isPublished(language)) {
                    throw new KlassResourceNotFoundException("ClassificationVersion: " + version
                            .getDateRange() + ". Is not published in language: " + language);
                }
                codes.addAll(mapClassificationItemsToCodes(version, version.getDateRange(), language));
            }
        }

        return codes;
    }

    static List<Correspondence> findCorrespondences(ClassificationSeries sourceClassification,
            ClassificationSeries targetClassification, DateRange dateRange, Language language) {
        List<Correspondence> correspondences = new ArrayList<>();
        for (ClassificationVersion version : sourceClassification.getClassificationVersions()) {
            if (version.getDateRange().overlaps(dateRange)) {
                List<CorrespondenceTable> tables = getCorrespondenceTablesWithTarget(version, targetClassification,
                        version.getDateRange(), language);
                for (CorrespondenceTable correspondenceTable : tables) {
                    correspondences.addAll(mapCorrespondenceMapsToCorrespondences(correspondenceTable,
                            version.getDateRange().subRange(correspondenceTable.getTarget().getDateRange()),
                            language));
                }
            }
        }

        return correspondences;
    }

    private static List<CorrespondenceTable> getCorrespondenceTablesWithTarget(ClassificationVersion version,
            ClassificationSeries targetClassification, DateRange sourceDateRange, Language language) {
        List<CorrespondenceTable> correspondenceTables = new ArrayList<>();
        for (CorrespondenceTable correspondenceTable : version.getCorrespondenceTablesWithTarget(
                targetClassification)) {
            if (!correspondenceTable.isPublished(language)) {
                throw new KlassResourceNotFoundException("CorrespondenceTable: " + correspondenceTable.getName(
                        language) + " " + correspondenceTable.getDateRange() + ". Is not published in language: "
                        + language);
            }
            if (correspondenceTable.getTarget().getDateRange().overlaps(sourceDateRange)) {
                correspondenceTables.add(correspondenceTable);
            }
        }
        if (correspondenceTables.isEmpty()) {
            throw new KlassResourceNotFoundException(createCorrespondenceNotFoundErrorMessage(
                    version, targetClassification));
        }
        return correspondenceTables;
    }

    private static String createCorrespondenceNotFoundErrorMessage(ClassificationVersion version,
            ClassificationSeries targetClassification) {
        return "Classification Version: '" + version.getName(Language.getDefault())
                + "' has no correspondence table with Classification: '" + targetClassification.getName(Language
                        .getDefault()) + "'";
    }

    private static List<Correspondence> mapCorrespondenceMapsToCorrespondences(
            CorrespondenceTable correspondenceTable, DateRange subRange, Language language) {
        List<Correspondence> correspondences = new ArrayList<>();
        for (CorrespondenceMap correspondenceMap : correspondenceTable.getCorrespondenceMaps()) {
            ClassificationItem source = correspondenceMap.getSource().orElse(null);
            ClassificationItem target = correspondenceMap.getTarget().orElse(null);
            correspondences.add(new Correspondence(source, target, subRange, language));
        }
        return correspondences;
    }

    private static List<Code> mapClassificationItemsToCodes(StatisticalClassification statisticalClassification,
            DateRange dateRange, Language language) {
        List<Code> codes = new ArrayList<>();
        for (Level level : statisticalClassification.getLevels()) {
            codes.addAll(toCodes(level.getClassificationItems(), level, dateRange, language));
        }
        return codes;
    }

    private static List<Code> toCodes(List<ClassificationItem> classificationItems, Level level, DateRange dateRange,
            Language language) {
        return classificationItems.stream().map(item -> new Code(level, item, dateRange, language)).collect(toList());
    }
}
