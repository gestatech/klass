package no.ssb.klass.rest.dto.hal;

import static java.util.stream.Collectors.*;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.rest.ClassificationController;

public class CorrespondenceTableSummaryResource extends KlassResource {
    private final String name;
    private final ContactPersonResource contactPerson;
    private final String owningSection;
    private final String source;
    private final String target;
    private final boolean changeTable;
    private final Date lastModified;
    private final List<String> published;

    protected CorrespondenceTableSummaryResource(CorrespondenceTable correspondenceTable, Language language) {
        this.name = correspondenceTable.getName(language);
        this.contactPerson = new ContactPersonResource(correspondenceTable.getContactPerson());
        this.owningSection = correspondenceTable.getContactPerson().getSection();
        this.source = correspondenceTable.getSource().getName(language);
        this.target = correspondenceTable.getTarget().getName(language);
        this.changeTable = Objects.equals(correspondenceTable.getSource().getClassification(), correspondenceTable
                .getTarget().getClassification());
        this.lastModified = correspondenceTable.getLastModified();
        this.published = Arrays.stream(Language.getDefaultPrioritizedOrder())
                .filter(correspondenceTable::isPublished)
                .map(Language::getLanguageCode)
                .collect(toList());
        addLink(createSelfLink(correspondenceTable.getId()));
    }

    private Link createSelfLink(long id) {
        return linkTo(methodOn(ClassificationController.class).correspondenceTables(id, null)).withSelfRel();
    }

    public String getName() {
        return name;
    }

    public ContactPersonResource getContactPerson() {
        return contactPerson;
    }

    public String getOwningSection() {
        return owningSection;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public boolean isChangeTable() {
        return changeTable;
    }

    @JacksonXmlElementWrapper(localName = "publishedLanguages")
    @JacksonXmlProperty(localName = "published")
    public List<String> getPublished() {
        return published;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public Date getLastModified() {
        return lastModified;
    }

    public static List<CorrespondenceTableSummaryResource> convert(List<CorrespondenceTable> correspondenceTables,
            Language language) {
        return correspondenceTables.stream().map(c -> new CorrespondenceTableSummaryResource(c, language)).collect(
                Collectors.toList());
    }
}
