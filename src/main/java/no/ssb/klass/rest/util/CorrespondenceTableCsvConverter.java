package no.ssb.klass.rest.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.springframework.http.HttpOutputMessage;

import com.fasterxml.jackson.databind.ObjectWriter;

import no.ssb.klass.rest.dto.hal.CorrespondenceMapResource;
import no.ssb.klass.rest.dto.hal.CorrespondenceTableResource;

public class CorrespondenceTableCsvConverter extends AbstractCsvConverter<CorrespondenceTableResource> {

    @Override
    protected boolean supports(Class<?> clazz) {
        return CorrespondenceTableResource.class.equals(clazz);
    }

    @Override
    protected void writeInternal(CorrespondenceTableResource correspondenceTable, HttpOutputMessage outputMessage)
            throws IOException {
        Charset charset = selectCharsetAndUpdateOutput(outputMessage);
        ObjectWriter writer = createWriter(CorrespondenceMapResource.class);
        writer.writeValue(new OutputStreamWriter(outputMessage.getBody(), charset), correspondenceTable
                .getCorrespondenceMaps());
    }
}
