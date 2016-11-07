package no.ssb.klass.designer.admin.util;

import java.util.LinkedHashMap;
import java.util.Map;

import no.ssb.klass.designer.admin.util.UsageStatisticsRows.ReportDescription;

public class ContentUseStatisticRows {

    public enum ReportModeChoice implements ReportDescription<ReportModeChoice> {
        TOTAL("Totalt antall kodeverk", "total.csv"),
        PUBLISHED("Publiserte kodeverk", "publisert.csv"),
        UNPUBLISHED("Upubliserte kodeverk", "upublisert.csv"),
        MISSING_LANG("Publiserte versjoner som mangler språk", "mangler_språk.csv");

        private final String displayName;
        private final String filename;

        ReportModeChoice(String displayName, String filename) {
            this.displayName = displayName;
            this.filename = filename;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public ReportModeChoice getChoice() {
            return this;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }

    private final Map<ReportDescription<ReportModeChoice>, Integer> rows;

    public ContentUseStatisticRows(int numberOfClassifications, int publishedClassifications,
            int unpublishedClassifications,
            int publishedVersionsWithMissingLanguages) {
        rows = new LinkedHashMap<>();
        rows.put(ReportModeChoice.TOTAL, numberOfClassifications);
        rows.put(ReportModeChoice.PUBLISHED, publishedClassifications);
        rows.put(ReportModeChoice.UNPUBLISHED, unpublishedClassifications);
        rows.put(ReportModeChoice.MISSING_LANG, publishedVersionsWithMissingLanguages);
    }

    public Map<ReportDescription<ReportModeChoice>, Integer> getRows() {
        return rows;
    }
}
