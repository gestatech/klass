package no.ssb.klass.core.service.search;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.search.Stemmer.NewNorwegianStemmer;

public class NewNorwegianStemmerTest {
    private NewNorwegianStemmer subject;

    @Before
    public void setup() {
        subject = (NewNorwegianStemmer) Stemmer.newInstance(Language.NN);
    }

    @Test
    public void stem() {
        verify("kommun", "kommune", "kommunen", "kommunar", "kommunane");
        verify("jent", "jente", "jenta", "jenter", "jentene");
        verify("hus", "hus", "huset", "hus", "husa");
        verify("saks", "saks", "saksen", "saksar", "saksane");
        verify("lær", "lærar", "læraren", "lærare", "lærarane");
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
