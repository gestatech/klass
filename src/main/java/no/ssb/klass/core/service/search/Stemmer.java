package no.ssb.klass.core.service.search;

import java.util.ArrayList;
import java.util.List;

import no.ssb.klass.core.model.Language;

/**
 * Simple algorithm to get root form of a word (called stemming in search terminology). Does not handle irregular forms
 * (e.g mouse/mice), and is only trying to stem nouns. Consider using stemming from a library such as lucene or
 * snowball.
 */
abstract class Stemmer {

    /**
     * Get root form of input words
     */
    public List<String> stem(String[] words) {
        List<String> baseWords = new ArrayList<>();
        for (String word : words) {
            for (String ending : getLanguageSpecificEndings()) {
                if (word.endsWith(ending)) {
                    word = word.substring(0, word.length() - ending.length());
                    // Remove only one ending
                    break;
                }
            }
            if (word.length() != 0) {
                baseWords.add(word);
            }
        }
        return baseWords;
    }

    protected abstract String[] getLanguageSpecificEndings();

    public static Stemmer newInstance(Language language) {
        switch (language) {
        case NB:
            return new NorwegianStemmer();
        case NN:
            return new NewNorwegianStemmer();
        case EN:
            return new EnglishStemmer();
        default:
            throw new IllegalArgumentException("No stemmer available for language: " + language);
        }
    }

    static class EnglishStemmer extends Stemmer {
        @Override
        protected String[] getLanguageSpecificEndings() {
            return new String[] { "ses", "se", "es", "s" };
        }
    }

    static class NorwegianStemmer extends Stemmer {
        @Override
        protected String[] getLanguageSpecificEndings() {
            return new String[] { "erene", "eren", "ere", "er", "ene", "en", "e", "a", "et" };
        }
    }

    static class NewNorwegianStemmer extends Stemmer {
        @Override
        protected String[] getLanguageSpecificEndings() {
            return new String[] { "arane", "arar", "aren", "are", "ar", "er", "ane", "ene", "en", "e", "a", "et" };
        }
    }
}
