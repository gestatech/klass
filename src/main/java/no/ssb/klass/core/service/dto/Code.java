package no.ssb.klass.core.service.dto;

import static com.google.common.base.Preconditions.*;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.util.DateRange;

public class Code {
    private final String code;
    private final String officialName;
    private final String shortName;
    private final String level;
    private final DateRange dateRange;

    public Code(Level level, ClassificationItem classificationItem, DateRange dateRange, Language language) {
        checkNotNull(level);
        checkNotNull(classificationItem);
        checkNotNull(dateRange);
        checkNotNull(language);
        this.code = classificationItem.getCode();
        this.officialName = classificationItem.getOfficialName(language);
        this.shortName = classificationItem.getShortName(language);
        this.level = Integer.toString(level.getLevelNumber());
        this.dateRange = dateRange;
    }

    public String getCode() {
        return code;
    }

    public String getOfficialName() {
        return officialName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLevel() {
        return level;
    }

    public DateRange getDateRange() {
        return dateRange;
    }
}
