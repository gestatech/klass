package no.ssb.klass.core.converting.xml;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectWriter;

import no.ssb.klass.core.converting.exception.ImportException;
import no.ssb.klass.core.converting.xml.abstracts.AbstractXmlService;
import no.ssb.klass.core.converting.xml.dto.XmlVersionWithChildrenContainer;
import no.ssb.klass.core.converting.xml.fullversionexport.XmlVersionWithChildrenBuilder;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.service.ClassificationService;

/**
 * @author Mads Lundemo, SSB.
 */
@Service
public class FullVersionExportService extends AbstractXmlService<ClassificationVersion> {

    @Autowired
    private ClassificationService classificationService;

    @Override
    public InputStream toXmlStream(ClassificationVersion version) {
        XmlVersionWithChildrenContainer container = versionToDto(version);
        ObjectWriter writer = getObjectWriter(XmlVersionWithChildrenContainer.class);
        return createInputStream(container, writer);

    }

    @Override
    public void fromXmlStreamAndMerge(InputStream stream, ClassificationVersion domainObject) throws ImportException {
        throw new ImportException("Import not supported");
    }

    private XmlVersionWithChildrenContainer versionToDto(ClassificationVersion version) {
        ClassificationVersion classificationVersion = classificationService.getClassificationVersion(version
                .getId());
        XmlVersionWithChildrenBuilder builder =
                new XmlVersionWithChildrenBuilder(classificationService, classificationVersion);
        return builder.build();
    }

}
