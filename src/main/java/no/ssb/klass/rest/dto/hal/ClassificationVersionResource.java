package no.ssb.klass.rest.dto.hal;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;

@JacksonXmlRootElement(localName = "classificationVersion")
public class ClassificationVersionResource extends ClassificationVersionSummaryResource {
    private final String introduction;
    private final ContactPersonResource contactPerson;
    private final String owningSection;
    private final String legalBase;
    private final String publications;
    private final String derivedFrom;

    private final List<CorrespondenceTableSummaryResource> correspondenceTables;
    private final List<ClassificationVariantSummaryResource> classificationVariants;
    private final List<ChangelogResource> changelogs;
    private final List<LevelResource> levels;
    private final List<ClassificationItemResource> classificationItems;

    public ClassificationVersionResource(ClassificationVersion version, Language language) {
        super(version, language);
        this.introduction = version.getIntroduction(language);
        this.contactPerson = new ContactPersonResource(version.getContactPerson());
        this.owningSection = version.getContactPerson().getSection();
        this.legalBase = version.getLegalBase(language);
        this.publications = version.getPublications(language);
        this.derivedFrom = version.getDerivedFrom(language);
        this.levels = LevelResource.convert(version.getLevels(), language);
        this.correspondenceTables = CorrespondenceTableSummaryResource
                .convert(version.getPublicCorrespondenceTables(), language);
        this.classificationVariants = ClassificationVariantSummaryResource
                .convert(version.getPublicClassificationVariants(), language);
        this.classificationItems = ClassificationItemResource.convert(version.getLevels(), language);
        this.changelogs = ChangelogResource.convert(version.getChangelogs());
    }

    public String getIntroduction() {
        return introduction;
    }

    public ContactPersonResource getContactPerson() {
        return contactPerson;
    }

    public String getOwningSection() {
        return owningSection;
    }

    public String getLegalBase() {
        return legalBase;
    }

    public String getPublications() {
        return publications;
    }

    public String getDerivedFrom() {
        return derivedFrom;
    }


    @JacksonXmlElementWrapper(localName = "correspondenceTables")
    @JacksonXmlProperty(localName = "correspondenceTable")
    public List<CorrespondenceTableSummaryResource> getCorrespondenceTables() {
        return correspondenceTables;
    }

    @JacksonXmlElementWrapper(localName = "classificationVariants")
    @JacksonXmlProperty(localName = "classificationVariant")
    public List<ClassificationVariantSummaryResource> getClassificationVariants() {
        return classificationVariants;
    }

    @JacksonXmlElementWrapper(localName = "changelogs")
    @JacksonXmlProperty(localName = "changelog")
    public List<ChangelogResource> getChangelogs() {
        return changelogs;
    }

    @JacksonXmlElementWrapper(localName = "levels")
    @JacksonXmlProperty(localName = "level")
    public List<LevelResource> getLevels() {
        return levels;
    }

    @JacksonXmlElementWrapper(localName = "classificationItems")
    @JacksonXmlProperty(localName = "classificationItem")
    public List<ClassificationItemResource> getClassificationItems() {
        return classificationItems;
    }
}
