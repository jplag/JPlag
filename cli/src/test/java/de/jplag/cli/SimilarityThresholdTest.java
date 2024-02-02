package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;

class SimilarityThresholdTest extends CommandLineInterfaceTest {
    private static final double EXPECTED_DEFAULT_SIMILARITY_THRESHOLD = 0;

    @Test
    void testDefaultThreshold() throws CliException {
        buildOptionsFromCLI(defaultArguments());
        assertEquals(EXPECTED_DEFAULT_SIMILARITY_THRESHOLD, options.similarityThreshold());
    }

    @Test
    void testInvalidThreshold() {
        assertThrowsExactly(CliException.class, () -> buildOptionsFromCLI(defaultArguments().similarityThreshold("Not a Double...")));
    }

    @Test
    void testLowerBound() throws CliException {
        buildOptionsFromCLI(defaultArguments().similarityThreshold(-.01));
        assertEquals(0.0, options.similarityThreshold(), DELTA);
    }

    @Test
    void testUpperBound() throws CliException {
        buildOptionsFromCLI(defaultArguments().similarityThreshold(1.01));
        assertEquals(1.0, options.similarityThreshold(), DELTA);
    }

    @Test
    void testValidThreshold() throws CliException {
        double expectedValue = 0.5;
        buildOptionsFromCLI(defaultArguments().similarityThreshold(expectedValue));
        assertEquals(expectedValue, options.similarityThreshold(), DELTA);
    }
}
