
package de.jplag.java;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;

/**
 * Test cases regarding the extraction from try vs. try with resource.
 */
class JavaTryTest extends AbstractJavaLanguageTest {
    @Test
    @DisplayName("Test difference between try block and try-with-resource block.")
    void testJavaClassPair() throws ParsingException {
        assertIterableEquals(parseJavaFile("Try.java"), parseJavaFile("TryWithResource.java"));
    }
}
