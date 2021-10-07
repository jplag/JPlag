package jplag.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import de.jplag.CommandLineArgument;
import de.jplag.options.JPlagOptions;

public class SimiliarityThresholdTest extends CommandLineInterfaceTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testDefaultThreshold() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(JPlagOptions.DEFAULT_SIMILARITY_THRESHOLD, options.getSimilarityThreshold(), DELTA);
    }

    @Test
    public void testInvalidThreshold() {
        exit.expectSystemExitWithStatus(1);
        String argument = buildArgument(CommandLineArgument.SIMILARITY_THRESHOLD, "Not a float...");
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
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
