package no.ssb.klass.rest.dto.hal;

import static java.util.stream.Collectors.*;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.rest.ClassificationController;

public class ClassificationVariantSummaryResource extends KlassResource {
    private final String name;
    private final Date lastModified;
    private final List<String> published;

    protected ClassificationVariantSummaryResource(ClassificationVariant variant, Language language) {
        this.name = variant.getName(language);
        this.lastModified = variant.getLastModified();
        this.published = Arrays.stream(Language.getDefaultPrioritizedOrder())
                .filter(variant::isPublished)
                .map(Language::getLanguageCode)
                .collect(toList());
        addLink(createSelfLink(variant.getId()));
    }

    private Link createSelfLink(long id) {
        return linkTo(methodOn(ClassificationController.class).variants(id, null)).withSelfRel();
    }

    public String getName() {
        return name;
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

    public static List<ClassificationVariantSummaryResource> convert(List<ClassificationVariant> variants,
            Language language) {
        return variants.stream().map(variant -> new ClassificationVariantSummaryResource(variant, language)).collect(
                Collectors.toList());
    }
}
