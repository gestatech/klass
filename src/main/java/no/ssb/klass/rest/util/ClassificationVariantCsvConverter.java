package no.ssb.klass.rest.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.springframework.http.HttpOutputMessage;

import com.fasterxml.jackson.databind.ObjectWriter;

import no.ssb.klass.rest.dto.hal.ClassificationItemResource;
import no.ssb.klass.rest.dto.hal.ClassificationVariantResource;

public class ClassificationVariantCsvConverter extends AbstractCsvConverter<ClassificationVariantResource> {

    @Override
    protected boolean supports(Class<?> clazz) {
        return ClassificationVariantResource.class.equals(clazz);
    }

    @Override
    protected void writeInternal(ClassificationVariantResource variant, HttpOutputMessage outputMessage)
            throws IOException {
        Charset charset = selectCharsetAndUpdateOutput(outputMessage);
        ObjectWriter writer = createWriter(ClassificationItemResource.class);
        writer.writeValue(new OutputStreamWriter(outputMessage.getBody(), charset), variant.getClassificationItems());
    }
}
