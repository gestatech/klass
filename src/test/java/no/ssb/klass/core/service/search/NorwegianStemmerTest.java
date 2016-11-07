package no.ssb.klass.core.service.search;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.search.Stemmer.NorwegianStemmer;

public class NorwegianStemmerTest {
    private NorwegianStemmer subject;

    @Before
    public void setup() {
        subject = (NorwegianStemmer) Stemmer.newInstance(Language.NB);
    }

    @Test
    public void stem() {
        verify("kommun", "kommune", "kommunen", "kommuner", "kommunene");
        verify("jent", "jente", "jenta", "jenter", "jentene");
        verify("hus", "hus", "huset", "hus", "husene");
        verify("saks", "saks", "saksen", "sakser", "saksene");
        verify("lær", "lærer", "læreren", "lærere", "lærerene");
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
