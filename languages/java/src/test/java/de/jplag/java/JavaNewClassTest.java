package de.jplag.java;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;

/**
 * Test cases regarding the generation of the assign token when initializing a variable directly. Whether the
 * initialization is done on the same line as the declaration should not affect the extracted tokens.
 */
class JavaNewClassTest extends AbstractJavaLanguageTest {
    @Test
    @DisplayName("Test init and declare on same line vs. different lines")
    void testJavaClassPair() throws ParsingException {
        assertIterableEquals(parseJavaFile("DeclareAndInitialize1.java"), parseJavaFile("DeclareAndInitialize2.java"));
    }
}