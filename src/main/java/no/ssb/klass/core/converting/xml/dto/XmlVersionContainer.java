package no.ssb.klass.core.converting.xml.dto;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.Language;

/**
 * @author Mads Lundemo, SSB.
 */

@JacksonXmlRootElement(localName = "versjon")
public class XmlVersionContainer {

    public static final String CODE = "kode";
    public static final String PARENT = "forelder";

    public static final String NAME_NB = "navn_bokmål";
    public static final String NAME_NN = "navn_nynorsk";
    public static final String NAME_EN = "navn_engelsk";

    public static final String SHORT_NAME_NB = "kortnavn_bokmål";
    public static final String SHORT_NAME_NN = "kortnavn_nynorsk";
    public static final String SHORT_NAME_EN = "kortnavn_engelsk";

    public static final String NOTES_NB = "noter_bokmål";
    public static final String NOTES_NN = "noter_nynorsk";
    public static final String NOTES_EN = "noter_engelsk";

    @JacksonXmlProperty(localName = "element")
    @JacksonXmlElementWrapper(useWrapping = false)
    private XmlVersionItem[] items;

    public XmlVersionContainer(List<XmlVersionItem> itemList) {
        items = new XmlVersionItem[itemList.size()];
        itemList.toArray(items);
    }

    protected XmlVersionContainer() {

    }

    public void setItems(XmlVersionItem[] items) {
        this.items = items;
    }

    public List<XmlVersionItem> getItems() {
        return Arrays.asList(items);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class XmlVersionItem implements XmlCodeHierarchy {

        @JacksonXmlProperty(localName = CODE)
        private String code;
        @JacksonXmlProperty(localName = PARENT)
        private String parentCode;

        @JacksonXmlProperty(localName = NAME_NB)
        private String nameNB;
        @JacksonXmlProperty(localName = NAME_NN)
        private String nameNN;
        @JacksonXmlProperty(localName = NAME_EN)
        private String nameEN;

        @JacksonXmlProperty(localName = SHORT_NAME_NB)
        private String shortNameNB;
        @JacksonXmlProperty(localName = SHORT_NAME_NN)
        private String shortNameNN;
        @JacksonXmlProperty(localName = SHORT_NAME_EN)
        private String shortNameEN;

        @JacksonXmlProperty(localName = NOTES_NB)
        private String notesNB;
        @JacksonXmlProperty(localName = NOTES_NN)
        private String notesNN;
        @JacksonXmlProperty(localName = NOTES_EN)
        private String notesEN;

        public XmlVersionItem(ClassificationItem item) {
            code = item.getCode();
            parentCode = item.getParent() == null ? "" : item.getParent().getCode();

            nameNB = item.getOfficialName(Language.NB);
            nameNN = item.getOfficialName(Language.NN);
            nameEN = item.getOfficialName(Language.EN);

            shortNameNB = item.getShortName(Language.NB);
            shortNameNN = item.getShortName(Language.NN);
            shortNameEN = item.getShortName(Language.EN);

            notesNB = item.getNotes(Language.NB);
            notesNN = item.getNotes(Language.NN);
            notesEN = item.getNotes(Language.EN);
        }

        protected XmlVersionItem() {

        }

        public String getCode() {
            return code;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getNameNB() {
            return nameNB;
        }

        public String getNameNN() {
            return nameNN;
        }

        public String getNameEN() {
            return nameEN;
        }

        public String getShortNameNB() {
            return shortNameNB;
        }

        public String getShortNameNN() {
            return shortNameNN;
        }

        public String getShortNameEN() {
            return shortNameEN;
        }

        public String getNotesNB() {
            return notesNB;
        }

        public String getNotesNN() {
            return notesNN;
        }

        public String getNotesEN() {
            return notesEN;
        }

        @JsonIgnore
        public boolean isEmpty() {
            return new EqualsBuilder()
                    .append(code, null)
                    .append(parentCode, null)
                    .append(nameNB, null)
                    .append(nameNN, null)
                    .append(nameEN, null)
                    .append(shortNameNB, null)
                    .append(shortNameNN, null)
                    .append(shortNameEN, null)
                    .append(notesNB, null)
                    .append(notesNN, null)
                    .append(notesEN, null)
                    .isEquals();
        }
    }
}