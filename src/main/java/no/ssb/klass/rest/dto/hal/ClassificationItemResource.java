package no.ssb.klass.rest.dto.hal;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;

@JsonPropertyOrder(value = { "code", "level", "name", "shortName", "parentCode", "notes" })
public class ClassificationItemResource {
    private final String code;
    private final String level;
    private final String name;
    private final String shortName;
    private final String notes;
    private final String parentCode;

    public ClassificationItemResource(ClassificationItem classificationItem, Level level, Language language) {
        this.code = classificationItem.getCode();
        this.level = Integer.toString(level.getLevelNumber());
        this.name = classificationItem.getOfficialName(language);
        this.shortName = classificationItem.getShortName(language);
        this.notes = classificationItem.getNotes(language);
        this.parentCode = classificationItem.getParent() == null ? "" : classificationItem.getParent().getCode();
    }

    public String getCode() {
        return code;
    }

    public String getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getNotes() {
        return notes;
    }

    public String getParentCode() {
        return parentCode;
    }

    public static List<ClassificationItemResource> convert(List<Level> levels, Language language) {
        return levels.stream().flatMap(level -> convert(level, language).stream()).collect(toList());
    }

    private static List<ClassificationItemResource> convert(Level level, Language language) {
        return level.getClassificationItems().stream().map(item -> new ClassificationItemResource(item, level,
                language)).collect(toList());
    }
}
