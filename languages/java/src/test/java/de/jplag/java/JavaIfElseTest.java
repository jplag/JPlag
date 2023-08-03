
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
    private static final String IF_ELSE_IF = "IfElseIf.java";
    private static final String IF_IF = "IfIf.java";
    private static final String IF_ELSE = "IfElse.java";

    @ParameterizedTest
    @MethodSource("provideClassPairs")
    @DisplayName("Test difference between if-else, if-if and if-else-if.")
    void testJavaClassPair(String fileName1, String fileName2) throws ParsingException {
        assertIterableEquals(parseJavaFile(fileName1), parseJavaFile(fileName2));
    }

    /**
     * Argument source for the test case {@link testJavaClassPair(String, String)).
     */
    private static Stream<Arguments> provideClassPairs() {
        return Stream.of(Arguments.of(IF_ELSE, IF_IF), // if instead of else
                Arguments.of(IF_ELSE, IF_ELSE_IF),// add if to else
                Arguments.of(IF_ELSE_IF, IF_IF)); // removal of else
    }

}
