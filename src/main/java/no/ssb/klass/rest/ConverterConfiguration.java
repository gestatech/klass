package no.ssb.klass.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import no.ssb.klass.rest.dto.CodeChangeList;
import no.ssb.klass.rest.dto.CodeList;
import no.ssb.klass.rest.dto.CorrespondenceItemList;
import no.ssb.klass.rest.dto.hal.ClassificationVariantResource;
import no.ssb.klass.rest.dto.hal.ClassificationVersionResource;
import no.ssb.klass.rest.dto.hal.CorrespondenceTableResource;
import no.ssb.klass.rest.util.ClassificationVariantCsvConverter;
import no.ssb.klass.rest.util.ClassificationVersionCsvConverter;
import no.ssb.klass.rest.util.CodeChangeListCsvConverter;
import no.ssb.klass.rest.util.CodeListCsvConverter;
import no.ssb.klass.rest.util.CorrespondenceItemListCsvConverter;
import no.ssb.klass.rest.util.CorrespondenceTableCsvConverter;

@Configuration
public class ConverterConfiguration {

    @Bean
    public HttpMessageConverter<CorrespondenceTableResource> correspondenceTableCsvConverter() {
        return new CorrespondenceTableCsvConverter();
    }

    @Bean
    public HttpMessageConverter<ClassificationVersionResource> classificationVersionCsvConverter() {
        return new ClassificationVersionCsvConverter();
    }

    @Bean
    public HttpMessageConverter<ClassificationVariantResource> classificationVariantCsvConverter() {
        return new ClassificationVariantCsvConverter();
    }

    @Bean
    public HttpMessageConverter<CodeList> codeListCsvConverter() {
        return new CodeListCsvConverter();
    }

    @Bean
    public HttpMessageConverter<CodeChangeList> codeChangeListCsvConverter() {
        return new CodeChangeListCsvConverter();
    }

    @Bean
    public HttpMessageConverter<CorrespondenceItemList> correspondenceItemListCsvConverter() {
        return new CorrespondenceItemListCsvConverter();
    }
}