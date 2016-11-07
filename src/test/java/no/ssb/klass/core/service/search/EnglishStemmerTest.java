package no.ssb.klass.core.service.search;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.search.Stemmer.EnglishStemmer;

public class EnglishStemmerTest {
    private EnglishStemmer subject;

    @Before
    public void setup() {
        subject = (EnglishStemmer) Stemmer.newInstance(Language.EN);
    }

    @Test
    public void stem() {
        verify("girl", "girl", "girls");
        verify("hou", "house", "houses");
        verify("bu", "bus", "buses");
    }

    private void verify(String rootForm, String... alternativeForms) {
        for (String alternativeForm : alternativeForms) {
            assertEquals("Failed alternative form: " + alternativeForm, rootForm, stem(alternativeForm));
        }
    }

    private String stem(String word) {
        return subject.stem(new String[] { word }).get(0);
    }
}
