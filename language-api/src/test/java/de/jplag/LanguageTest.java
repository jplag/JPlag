package de.jplag;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LanguageTest {
    @Test
    public void testInvalidLanguageDoesNotWork() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            InvalidLanguage invalidLanguage = new InvalidLanguage();
            invalidLanguage.parse(Collections.emptySet(), false);
        });
    }

    @Test
    public void testValidLanguageWithNormalization() throws ParsingException {
        Language language = new LanguageWithNormalization();
        language.parse(Collections.emptySet(), false);
    }

    @Test
    public void testValidLanguageWithoutNormalization() throws ParsingException {
        Language language = new LanguageWithoutNormalization();
        language.parse(Collections.emptySet(), false);
    }

    private static abstract class LanguageBase implements Language {
        @Override
        public String[] suffixes() {
            return new String[0];
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getIdentifier() {
            return null;
        }

        @Override
        public int minimumTokenMatch() {
            return 0;
        }
    }

    private static class InvalidLanguage extends LanguageBase {
    }

    private static class LanguageWithNormalization extends LanguageBase {
        @Override
        public List<Token> parse(Set<File> files, boolean normalize) {
            return Collections.emptyList();
        }
    }

    private static class LanguageWithoutNormalization extends LanguageBase {
        @Override
        public List<Token> parse(Set<File> files) {
            return Collections.emptyList();
        }
    }
}