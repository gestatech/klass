package no.ssb.klass.designer.admin.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class UsageStatisticsRows {
    public interface ReportDescription<E extends Enum<?>> {
        String getDisplayName();

        String getFilename();

        E getChoice();
    }

    public enum UseStatisticsModeChoice implements ReportDescription<UseStatisticsModeChoice> {
        TOTAL_CLASSIFIC("Totalt antall kodeverk hentet ut", "total_hentet_ut.csv"),
        NUMBEROF_SEARCH_RETURNED_NULL("Antall søk som returnerte nulltreff", "søk_med_null_treff.csv"),
        TOTAL_SEARCH_WORDS("Totalt antall søkeord benyttet", "totalt_antall_søkeord.csv");

        private final String displayName;
        private final String filename;

        UseStatisticsModeChoice(String displayName, String filename) {
            this.displayName = displayName;
            this.filename = filename;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public UseStatisticsModeChoice getChoice() {
            return this;
        }
    }

    private final Map<ReportDescription<UseStatisticsModeChoice>, Integer> rows;

    public UsageStatisticsRows(int numberOfClassifications, int numberOfSearchReturnedNull, int totalSearchWords) {
        rows = new LinkedHashMap<>();
        rows.put(UseStatisticsModeChoice.TOTAL_CLASSIFIC, numberOfClassifications);
        rows.put(UseStatisticsModeChoice.NUMBEROF_SEARCH_RETURNED_NULL, numberOfSearchReturnedNull);
        rows.put(UseStatisticsModeChoice.TOTAL_SEARCH_WORDS, totalSearchWords);
    }

    public Map<ReportDescription<UseStatisticsModeChoice>, Integer> getRows() {
        return rows;
    }
}
