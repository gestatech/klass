package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.google.common.base.Strings;

import no.ssb.klass.core.util.Translatable;

@Entity
public class ConcreteClassificationItem extends ClassificationItem {
    private String code;
    @Column(length = 2048)
    private Translatable officialName;
    @Column(length = 1024)
    private Translatable shortName;
    @Column(length = 6000)
    private Translatable notes;

    protected ConcreteClassificationItem() {
    }

    public ConcreteClassificationItem(String code, Translatable officialName, Translatable shortName) {
        this(code, officialName, shortName, Translatable.empty());
    }

    public ConcreteClassificationItem(String code, Translatable officialName, Translatable shortName,
            Translatable notes) {
        checkArgument(!Strings.isNullOrEmpty(code));
        checkNotNull(officialName);
        checkArgument(!officialName.isEmpty());
        this.code = code;
        this.officialName = officialName;
        this.shortName = checkNotNull(shortName);
        this.notes = checkNotNull(notes);
    }

    /**
     * @return code, never null
     */
    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = checkNotNull(code);
    }

    /**
     * @return official name for specified language, if none empty string is returned, never null
     */
    @Override
    public String getOfficialName(Language language) {
        return officialName.getString(language);
    }

    public void setOfficialName(String officialName, Language language) {
        checkNotNull(officialName);
        this.officialName = this.officialName.withLanguage(officialName, language);
    }

    /**
     * @return short name for specified language, if none empty string is returned, never null
     */
    @Override
    public String getShortName(Language language) {
        return shortName.getString(language);
    }

    public void setShortName(String shortName, Language language) {
        this.shortName = this.shortName.withLanguage(shortName, language);
    }

    public void setNotes(String notes, Language language) {
        this.notes = this.notes.withLanguage(notes, language);
    }

    @Override
    public String getNotes(Language language) {
        return notes.getString(language);
    }

    @Override
    public ClassificationItem copy() {
        ConcreteClassificationItem copy = new ConcreteClassificationItem(code, officialName, shortName);
        copy.notes = notes;
        return copy;
    }

    @Override
    public boolean isReference() {
        return false;
    }

    @Override
    boolean hasNotes() {
        return !notes.isEmpty();
    }

    @Override
    boolean hasShortName() {
        return !shortName.isEmpty();
    }

    @Override
    public String toString() {
        return "ConcreteClassificationItem [code=" + code + "]";
    }
}
