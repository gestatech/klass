package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import no.ssb.klass.core.util.Translatable;

/**
 * A ClassificationVariant supports 2 levels only. The 2 levels are created when constructed.
 */
@Entity
@DiscriminatorValue("variant")
public class ClassificationVariant extends StatisticalClassification {
    @ManyToOne
    private ClassificationVersion classificationVersion;
    private Translatable name;
    @ManyToOne
    private User contactPerson;

    /**
     * Creates a ClassificationVariant. Note that 2 levels are added.
     */
    public ClassificationVariant(Translatable name, User contactPerson) {
        super(Translatable.empty());
        this.name = checkNotNull(name);
        this.contactPerson = checkNotNull(contactPerson);
        checkArgument(!name.isEmpty(), "Name is empty");
        super.addLevel(new Level(1));
        super.addLevel(new Level(2));
    }

    public void setClassificationVersion(ClassificationVersion classificationVersion) {
        this.classificationVersion = classificationVersion;
    }

    public ClassificationVersion getClassificationVersion() {
        return classificationVersion;
    }

    @Override
    public Language getPrimaryLanguage() {
        return classificationVersion.getClassification().getPrimaryLanguage();
    }

    @Override
    public String getCategoryName() {
        return "Variant";
    }

    @Override
    public ClassificationSeries getOwnerClassification() {
        return classificationVersion.getClassification();
    }

    public String getName(Language language) {
        return name.getString(language);
    }

    public void setName(Language language, String value) {
        name = name.withLanguage(value, language);
    }

    @Override
    public String getNameInPrimaryLanguage() {
        return getName(getPrimaryLanguage());
    }

    @Override
    public User getContactPerson() {
        return contactPerson;
    }

    @Override
    public void addLevel(Level newLevel) {
        throw new UnsupportedOperationException(
                "ClassificationVariant can only have 2 levels, and these are created in constructor");
    }

    public void updateContactPerson(User contactPerson) {
        this.contactPerson = checkNotNull(contactPerson);
    }

    @Override
    public boolean isIncludeShortName() {
        return false;
    }

    @Override
    public boolean isIncludeNotes() {
        return false;
    }

    @Override
    public boolean isDeleted() {
        return super.isDeleted() || classificationVersion.isDeleted();
    }

    @Override
    public String canPublish(Language language) {
        if (!classificationVersion.isPublished(language)) {
            return "Publiser versjon '" + classificationVersion.getName(language) + "'";
        } else {
            return "";
        }
    }
}
