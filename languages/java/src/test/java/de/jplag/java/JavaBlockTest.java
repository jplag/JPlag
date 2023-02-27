package de.jplag.java;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.ParsingException;

/**
 * Test cases regarding the extraction from implicit vs. explicit blocks in Java code.
 */
class JavaBlockTest extends AbstractJavaLanguageTest {
    @ParameterizedTest
    @MethodSource("provideClassPairs")
    @DisplayName("Test pairs of classes with explicit vs. implicit blocks.")
    void testJavaClassPair(String fileName1, String fileName2) throws ParsingException {
        assertIterableEquals(parseJavaFile(fileName1), parseJavaFile(fileName2));
    }

    /**
     * Argument source for the test case {@link testJavaClassPair(String, String)).
     */
    private static Stream<Arguments> provideClassPairs() {
        return Stream.of(Arguments.of("IfWithBraces.java", "IfWithoutBraces.java"), // just if conditions
                Arguments.of("Verbose.java", "Compact.java")); // complex case with different blocks
    }

}
