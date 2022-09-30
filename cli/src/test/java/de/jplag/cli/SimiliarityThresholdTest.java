package de.jplag.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.options.JPlagOptions;

class SimiliarityThresholdTest extends CommandLineInterfaceTest {

    @Test
    void testDefaultThreshold() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(JPlagOptions.DEFAULT_SIMILARITY_THRESHOLD, options.similarityThreshold(), DELTA);
    }

    @Test
    void testInvalidThreshold() throws Exception {
        String argument = buildArgument(CommandLineArgument.SIMILARITY_THRESHOLD, "Not a Double...");
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1.0, statusCode);
    }

    @Test
    void testLowerBound() {
        String argument = buildArgument(CommandLineArgument.SIMILARITY_THRESHOLD, Double.toString(-0.01));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(0.0, options.similarityThreshold(), DELTA);
    }

    @Test
    void testUpperBound() {
        String argument = buildArgument(CommandLineArgument.SIMILARITY_THRESHOLD, Double.toString(1.01));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(1.0, options.similarityThreshold(), DELTA);
    }

    @Test
    void testValidThreshold() {
        double expectedValue = 0.5;
        String argument = buildArgument(CommandLineArgument.SIMILARITY_THRESHOLD, Double.toString(expectedValue));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(expectedValue, options.similarityThreshold(), DELTA);
    }
}
