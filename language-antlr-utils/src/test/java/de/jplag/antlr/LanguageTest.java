package de.jplag.antlr;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;
import de.jplag.antlr.testLanguage.TestLanguage;

/**
 * Some tests for the abstract antlr language.
 */
class LanguageTest {

    @Test
    void testLanguageWithStaticParser() throws ParsingException {
        TestLanguage lang = new TestLanguage();
        Assertions.assertEquals(0, lang.parse(List.of(), false).size());
    }
}
