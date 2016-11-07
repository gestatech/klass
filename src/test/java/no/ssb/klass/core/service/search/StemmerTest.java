package no.ssb.klass.core.service.search;

import org.junit.Test;

import no.ssb.klass.core.model.Language;

public class StemmerTest {
    @Test
    public void stemmerForAllAvailableLanguages() {
        for (Language language : Language.values()) {
            Stemmer.newInstance(language);
        }
    }
}
