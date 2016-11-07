package no.ssb.klass.rest.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.springframework.http.HttpOutputMessage;

import com.fasterxml.jackson.databind.ObjectWriter;

import no.ssb.klass.rest.dto.CodeList;

public class CodeListCsvConverter extends AbstractCsvConverter<CodeList> {
    @Override
    protected boolean supports(Class<?> clazz) {
        return CodeList.class.equals(clazz);
    }

    @Override
    protected void writeInternal(CodeList codeList, HttpOutputMessage outputMessage) throws IOException {
        Charset charset = selectCharsetAndUpdateOutput(outputMessage);
        ObjectWriter writer = createWriter(codeList.codeItemsJavaType(), codeList.getCsvSeparator());
        writer.writeValue(new OutputStreamWriter(outputMessage.getBody(), charset), codeList.getCodes());
    }
}