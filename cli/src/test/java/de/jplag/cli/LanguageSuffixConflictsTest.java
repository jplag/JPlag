package de.jplag.cli;

import org.junit.jupiter.api.Test;

import de.jplag.multilang.MultiLanguageOptions;
import de.jplag.multilang.MultiLanguageParser;

public class LanguageSuffixConflictsTest {
    @Test
    void testNoConflictsBetweenLanguageSuffixes() {
        new MultiLanguageParser(new MultiLanguageOptions()); // triggers internal check for conflicts automatically.
        // For conflicting language, see error message
    }
}
