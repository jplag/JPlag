
package de.jplag.java;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.ParsingException;

/**
 * Test cases regarding the extraction from if and else conditions.
 */
class JavaIfElseTest extends AbstractJavaLanguageTest {
    @ParameterizedTest
    @MethodSource("provideClassPairs")
    @DisplayName("Test difference between if-else, if-if and if-else-if.")
    void testJavaClassPair(String fileName1, String fileName2) throws ParsingException {
        assertIterableEquals(parseJavaFile(fileName1), parseJavaFile(fileName1));
    }

    /**
     * Argument source for the test case {@link testJavaClassPair(String, String)).
     */
    private static Stream<Arguments> provideClassPairs() {
        return Stream.of(Arguments.of("IfElse.java", "IfIf.java"), // if instead of else
                Arguments.of("IfElse.java", "IfElseIf.java"),// add if to else
                Arguments.of("IfElseIf.java", "IfIf.java")); // removal of else
    }

}
