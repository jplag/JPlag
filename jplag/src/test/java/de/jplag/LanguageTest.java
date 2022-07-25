package de.jplag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This class contains integration tests for {@link Language} and {@link LanguageLoader}.
 * @author Dominik Fuchss
 */
class LanguageTest {
    @Test
    void testLoading() {
        var languages = LanguageLoader.loadLanguages();
        Assertions.assertEquals(9, languages.size());
    }
}
