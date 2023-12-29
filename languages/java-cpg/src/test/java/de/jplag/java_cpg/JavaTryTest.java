package de.jplag.java_cpg;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;

/**
 * Test cases regarding the extraction edge try vs. try with resource.
 */
class JavaTryTest extends AbstractJavaCpgLanguageTest {
    @Test
    @DisplayName("Test difference between try block and try-with-resource block.")
    void testJavaClassPair() throws ParsingException {
        assertIterableEquals(parseJavaFile("try/Try.java"), parseJavaFile("try/TryWithResource.java"));
    }
}
