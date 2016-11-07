package no.ssb.klass.core.converting.xml;

import static no.ssb.klass.core.converting.xml.dto.XmlVersionContainer.*;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Strings;

import no.ssb.klass.core.converting.exception.ImportException;
import no.ssb.klass.core.converting.exception.VersionImportException;
import no.ssb.klass.core.converting.xml.abstracts.XmlCodeHierarchyService;
import no.ssb.klass.core.converting.xml.dto.XmlCodeHierarchy;
import no.ssb.klass.core.converting.xml.dto.XmlVersionExportContainer;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.util.Translatable;

/**
 *
 * Service that takes care of xml import and export for ClassificationVersion
 *
 * @author Mads Lundemo, SSB.
 */
@Service
public class ClassificationVersionXmlService extends XmlCodeHierarchyService<ClassificationVersion, XmlVersionItem> {

    public InputStream toXmlStream(ClassificationVersion version) {
        XmlVersionExportContainer container = versionToDto(version);
        ObjectWriter writer = getObjectWriter(XmlVersionExportContainer.class);
        return createInputStream(container, writer);
    }

    public void fromXmlStreamAndMerge(InputStream stream, ClassificationVersion version) throws ImportException {
        List<XmlVersionItem> values = readInputStream(stream, XmlVersionItem.class);
        checkForExistingCodes(version, values);
        checkForMissingTitles(values);
        checkForMissingCodes(values);
        Map<ClassificationItem, ClassificationItem> itemMap =
                createClassificationItems(version, values.stream()
                        .map(versionItem -> (XmlCodeHierarchy) versionItem)
                        .collect(Collectors.toList()));
        mergeItemsWithClassification(version, itemMap);
    }

    private void checkForMissingCodes(List<XmlVersionItem> values) throws ImportException {
        for (XmlVersionItem versionItem : values) {
            if (!versionItem.isEmpty() && Strings.isNullOrEmpty(versionItem.getCode())) {
                throw new ImportException("Ett eller flere elementer mangler kode");
            }
        }
    }

    private void checkForMissingTitles(List<XmlVersionItem> values) throws ImportException {
        for (XmlVersionItem versionItem : values) {
            if (!versionItem.isEmpty()
                    && Strings.isNullOrEmpty(versionItem.getNameNB())
                    && Strings.isNullOrEmpty(versionItem.getNameNN())
                    && Strings.isNullOrEmpty(versionItem.getNameEN())) {
                throw new ImportException("Element med kode " + versionItem.getCode() + " mangler tittel");
            }
        }
    }

    protected XmlVersionExportContainer versionToDto(ClassificationVersion version) {
        List<XmlVersionItem> list = version.getAllClassificationItems()
                .stream()
                .map(XmlVersionItem::new)
                .sorted((o1, o2) -> o1.getCode().compareTo(o2.getCode()))
                .collect(Collectors.toCollection(LinkedList::new));

        XmlVersionExportContainer container = new XmlVersionExportContainer(list);
        container.setSchemaBaseUrl(SchemaBaseUrl);
        return container;
    }

    private void checkForExistingCodes(ClassificationVersion version, List<XmlVersionItem> values)
            throws VersionImportException {
        List<XmlVersionItem> existing = values.stream()
                .filter(xmlVersionItem -> version.hasClassificationItem(xmlVersionItem.getCode()))
                .collect(Collectors.toList());

        if (!existing.isEmpty()) {
            throw new VersionImportException("Følgende koder finnes fra før:", existing);
        }
    }

    @Override
    protected ClassificationItem createClassificationItem(XmlVersionItem xmlItem, ClassificationVersion owner) {
        return new ConcreteClassificationItem(xmlItem.getCode(),
                new Translatable(xmlItem.getNameNB(), xmlItem.getNameNN(), xmlItem.getNameEN()),
                new Translatable(xmlItem.getShortNameNB(), xmlItem.getShortNameNN(), xmlItem.getShortNameEN()),
                new Translatable(xmlItem.getNotesNB(), xmlItem.getNotesNN(), xmlItem.getNotesEN()));
    }
}
