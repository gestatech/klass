package no.ssb.klass.rest.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public abstract class AbstractCsvConverter<T> extends AbstractHttpMessageConverter<T> {
    private static final Charset DEFAULT_CHARACTER_SET = StandardCharsets.ISO_8859_1;
    private static final char DEFAULT_CSV_SEPARATOR = ';';

    public AbstractCsvConverter() {
        super(new MediaType("text", "csv"));
    }

    protected ObjectWriter createWriter(Class<?> clazz) {
        return createWriter(clazz, DEFAULT_CSV_SEPARATOR);
    }

    protected ObjectWriter createWriter(Class<?> clazz, char csvSeparator) {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(clazz).withHeader().withColumnSeparator(csvSeparator);
        return mapper.writer(schema);
    }

    protected Charset selectCharsetAndUpdateOutput(HttpOutputMessage outputMessage) {
        Charset charset = getContentTypeCharset(outputMessage.getHeaders().getContentType());
        outputMessage.getHeaders().setContentType(new MediaType("text", "csv", charset));
        return charset;
    }

    private Charset getContentTypeCharset(MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        } else {
            return DEFAULT_CHARACTER_SET;
        }
    }

    @Override
    protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) {
        throw new UnsupportedOperationException("Reading csv file is not supported");
    }
}
