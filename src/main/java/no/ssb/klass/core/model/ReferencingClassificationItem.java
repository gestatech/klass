package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class ReferencingClassificationItem extends ClassificationItem {
    @OneToOne
    private final ClassificationItem reference;

    // For Hibernate
    protected ReferencingClassificationItem() {
        this.reference = null;
    }

    public ReferencingClassificationItem(ClassificationItem classificationItem) {
        this.reference = checkNotNull(classificationItem);
    }

    @Override
    public String getCode() {
        return reference.getCode();
    }

    @Override
    public String getOfficialName(Language language) {
        return reference.getOfficialName(language);
    }

    @Override
    public String getShortName(Language language) {
        return reference.getShortName(language);
    }

    @Override
    public String getNotes(Language language) {
        return reference.getNotes(language);
    }

    @Override
    public ClassificationItem copy() {
        return new ReferencingClassificationItem(reference);
    }

    @Override
    public boolean isReference() {
        return true;
    }

    @Override
    public String getUuid() {
        return reference.getUuid();
    }

    @Override
    boolean hasNotes() {
        return false;
    }

    @Override
    boolean hasShortName() {
        return false;
    }

    @Override
    public String toString() {
        return "ReferencingClassificationItem [reference=" + reference + "]";
    }
}
