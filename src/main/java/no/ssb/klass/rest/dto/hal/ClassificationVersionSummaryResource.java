package no.ssb.klass.rest.dto.hal;

import static java.util.stream.Collectors.*;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.rest.ClassificationController;
import no.ssb.klass.rest.util.CustomLocalDateSerializer;

public class ClassificationVersionSummaryResource extends KlassResource {
    private final String name;
    private final LocalDate validFrom;
    private final LocalDate validTo;
    private final Date lastModified;
    private final List<String> published;

    protected ClassificationVersionSummaryResource(ClassificationVersion version, Language language) {
        this.name = version.getName(language);
        this.validFrom = version.getDateRange().getFrom();
        this.validTo = version.getDateRange().getTo();
        this.lastModified = version.getLastModified();
        this.published = Arrays.stream(Language.getDefaultPrioritizedOrder())
                .filter(version::isPublished)
                .map(Language::getLanguageCode)
                .collect(toList());
        addLink(createSelfLink(version.getId()));
    }

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getValidFrom() {
        return validFrom;
    }

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getValidTo() {
        return validTo;
    }

    private Link createSelfLink(long id) {
        return linkTo(methodOn(ClassificationController.class).versions(id, null)).withSelfRel();
    }

    @JacksonXmlElementWrapper(localName = "publishedLanguages")
    @JacksonXmlProperty(localName = "published")
    public List<String> getPublished() {
        return published;
    }

    public String getName() {
        return name;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public Date getLastModified() {
        return lastModified;
    }

    public static List<ClassificationVersionSummaryResource> convert(List<ClassificationVersion> versions,
            Language language) {
        return versions.stream().map(version -> new ClassificationVersionSummaryResource(version, language)).collect(
                Collectors.toList());
    }
}
