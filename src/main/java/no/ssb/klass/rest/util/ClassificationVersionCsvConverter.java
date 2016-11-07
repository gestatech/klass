package no.ssb.klass.rest.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.springframework.http.HttpOutputMessage;

import com.fasterxml.jackson.databind.ObjectWriter;

import no.ssb.klass.rest.dto.hal.ClassificationItemResource;
import no.ssb.klass.rest.dto.hal.ClassificationVersionResource;

public class ClassificationVersionCsvConverter extends AbstractCsvConverter<ClassificationVersionResource> {

    @Override
    protected boolean supports(Class<?> clazz) {
        return ClassificationVersionResource.class.equals(clazz);
    }

    @Override
    protected void writeInternal(ClassificationVersionResource version, HttpOutputMessage outputMessage)
            throws IOException {
        Charset charset = selectCharsetAndUpdateOutput(outputMessage);
        ObjectWriter writer = createWriter(ClassificationItemResource.class);
        writer.writeValue(new OutputStreamWriter(outputMessage.getBody(), charset), version.getClassificationItems());
    }
}
