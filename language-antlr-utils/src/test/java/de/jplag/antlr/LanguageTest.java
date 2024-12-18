package de.jplag.antlr;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;
import de.jplag.antlr.testLanguage.TestLanguage;
import de.jplag.antlr.testLanguage.TestParserAdapter;

/**
 * Some tests for the abstract antlr language
 */
class LanguageTest {
    @Test
    void testExceptionForNoDefinedParser() {
        LanguageWithoutParser lang = new LanguageWithoutParser();
        Set<File> emptySet = Set.of();
        assertThrows(UnsupportedOperationException.class, () -> lang.parse(emptySet, false));
    }

    @Test
    void testLanguageWithStaticParser() throws ParsingException {
        TestLanguage lang = new TestLanguage();
        Assertions.assertEquals(0, lang.parse(Set.of(), false).size());
    }

    @Test
    void testLanguageWithLazyParser() throws ParsingException {
        LanguageWithLazyParser lang = new LanguageWithLazyParser();
        Assertions.assertEquals(0, lang.parse(Set.of(), false).size());
    }

    private static class LanguageWithoutParser extends AbstractAntlrLanguage {
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

    private static class LanguageWithLazyParser extends LanguageWithoutParser {
        @Override
        protected AbstractAntlrParserAdapter<?> initializeParser(boolean normalize) {
            return new TestParserAdapter(this);
        }
    }
}
