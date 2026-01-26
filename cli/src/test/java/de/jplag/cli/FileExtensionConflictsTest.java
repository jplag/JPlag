package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import de.jplag.multilang.MultiLanguageOptions;
import de.jplag.multilang.MultiLanguageParser;

class FileExtensionConflictsTest {
    @Test
    void testNoConflictsBetweenFileExtensions() {
        assertDoesNotThrow(() -> new MultiLanguageParser(new MultiLanguageOptions()),
                "There is conflict regarding file extensions between two or more language modules. Ensure exactly one module has priority");
    }
}
