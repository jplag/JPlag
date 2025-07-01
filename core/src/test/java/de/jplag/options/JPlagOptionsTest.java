package de.jplag.options;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import de.jplag.java.JavaLanguage;

class JPlagOptionsTest {
    @Test
    void testWithLanguageOption() {
        JavaLanguage lang = new JavaLanguage();
        JPlagOptions options = new JPlagOptions(null, Set.of(), Set.of());
        options = options.withLanguage(lang);

        assertEquals(lang, options.language());
    }
}
