package de.jplag.options;

import de.jplag.java.JavaLanguage;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JPlagOptionsTest {
    @Test
    void testWithLanguageOption() {
        JavaLanguage lang = new JavaLanguage();
        JPlagOptions options = new JPlagOptions(null, Set.of(), Set.of());
        options = options.withLanguageOption(lang);

        assertEquals(lang, options.language());
    }
}
