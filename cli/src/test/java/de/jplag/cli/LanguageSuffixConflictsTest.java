package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import de.jplag.multilang.MultiLanguageOptions;
import de.jplag.multilang.MultiLanguageParser;

public class LanguageSuffixConflictsTest {
    @Test
    void testNoConflictsBetweenLanguageSuffixes() {
        assertDoesNotThrow(() -> new MultiLanguageParser(new MultiLanguageOptions()),
                "There were conflicts between language suffixes. For Details see error message");
    }
}
