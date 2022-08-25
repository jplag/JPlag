package de.jplag.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.CommandLineArgument;
import de.jplag.options.JPlagOptions;

public class SimiliarityThresholdTest extends CommandLineInterfaceTest {

    @Test
    public void testDefaultThreshold() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(JPlagOptions.DEFAULT_SIMILARITY_THRESHOLD, options.getSimilarityThreshold(), DELTA);
    }

    @Test
    public void testInvalidThreshold() throws Exception {
        String argument = buildArgument(CommandLineArgument.SIMILARITY_THRESHOLD, "Not a float...");
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1, statusCode);
    }

    @Test
    public void testLowerBound() {
        String argument = buildArgument(CommandLineArgument.SIMILARITY_THRESHOLD, Float.toString(-1f));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(0f, options.getSimilarityThreshold(), DELTA);
    }

    @Test
    public void testUpperBound() {
        String argument = buildArgument(CommandLineArgument.SIMILARITY_THRESHOLD, Float.toString(101f));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(100f, options.getSimilarityThreshold(), DELTA);
    }

    @Test
    public void testValidThreshold() {
        float expectedValue = 50f;
        String argument = buildArgument(CommandLineArgument.SIMILARITY_THRESHOLD, Float.toString(expectedValue));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(expectedValue, options.getSimilarityThreshold(), DELTA);
    }
}
