package no.ssb.klass.rest.dto.hal;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;

@JacksonXmlRootElement(localName = "correspondenceTable")
public class CorrespondenceTableResource extends CorrespondenceTableSummaryResource {
    private final String description;
    private final List<ChangelogResource> changelogs;
    private final LevelResource sourceLevel;
    private final LevelResource targetLevel;

    private final List<CorrespondenceMapResource> correspondenceMaps;

    public CorrespondenceTableResource(CorrespondenceTable correspondenceTable, Language language) {
        super(correspondenceTable, language);
        this.description = correspondenceTable.getDescription(language);
        this.sourceLevel = correspondenceTable.getSourceLevel().isPresent() ? new LevelResource(correspondenceTable
                .getSourceLevel().get(), language) : null;
        this.targetLevel = correspondenceTable.getTargetLevel().isPresent() ? new LevelResource(correspondenceTable
                .getTargetLevel().get(), language) : null;
        this.correspondenceMaps = CorrespondenceMapResource.convert(correspondenceTable.getCorrespondenceMaps(),
                language);
        this.changelogs = ChangelogResource.convert(correspondenceTable.getChangelogs());
    }

    public String getDescription() {
        return description;
    }

    @JacksonXmlElementWrapper(localName = "changelogs")
    @JacksonXmlProperty(localName = "changelog")
    public List<ChangelogResource> getChangelogs() {
        return changelogs;
    }

    public LevelResource getSourceLevel() {
        return sourceLevel;
    }

    public LevelResource getTargetLevel() {
        return targetLevel;
    }

    @JacksonXmlElementWrapper(localName = "correspondenceMaps")
    @JacksonXmlProperty(localName = "correspondenceMap")
    public List<CorrespondenceMapResource> getCorrespondenceMaps() {
        return correspondenceMaps;
    }
}
