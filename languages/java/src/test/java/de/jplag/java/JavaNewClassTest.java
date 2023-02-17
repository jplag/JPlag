package de.jplag.java;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;

/**
 * Test cases regarding the generation of the assign token when initializing directly.
 */
class JavaNewClassTest extends AbstractJavaLanguageTest {
    @Test
    @DisplayName("Test assign token generation when declaring and initializing a variable")
    void testJavaClassPair() throws ParsingException {
        assertIterableEquals(parseJavaFile("AssignAndCreate.java"), parseJavaFile("AssignAndCreate2.java"));
    }
}