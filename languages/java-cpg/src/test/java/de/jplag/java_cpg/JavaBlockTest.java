package de.jplag.java_cpg;

import de.jplag.ParsingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Test cases regarding the extraction edge implicit vs. explicit blocks in Java code.
 */
class JavaBlockTest extends AbstractJavaCpgLanguageTest {
    @ParameterizedTest
    @MethodSource("provideSrcDirectories")
    @DisplayName("Test pairs of classes with explicit vs. implicit blocks.")
    void testJavaClassPair(String dir) throws ParsingException {
        parseJavaFile(dir);
    }

    /**
     * Argument source for the test case {@code testJavaClassPair}.
     */
    private static Stream<Arguments> provideSrcDirectories() {
        return Stream.of(
                Arguments.of("if"), // just if conditions
                Arguments.of("verbosity")
        ); // complex case with different blocks
    }

}
