package de.jplag.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.CommandLineArgument;
import de.jplag.options.JPlagOptions;

public class StoredMatchesTest extends CommandLineInterfaceTest {

    @Test
    public void testDefault() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(JPlagOptions.DEFAULT_SHOWN_COMPARISONS, options.getMaximumNumberOfComparisons());
    }

    @Test
    public void testValidThreshold() {
        int expectedValue = 999;
        String argument = buildArgument(CommandLineArgument.SHOWN_COMPARISONS, Integer.toString(expectedValue));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(expectedValue, options.getMaximumNumberOfComparisons());
    }

    @Test
    public void testAll() {
        int expectedValue = -1;
        String argument = buildArgument(CommandLineArgument.SHOWN_COMPARISONS, Integer.toString(expectedValue));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(expectedValue, options.getMaximumNumberOfComparisons());
    }

    @Test
    public void testLowerBound() {
        String argument = buildArgument(CommandLineArgument.SHOWN_COMPARISONS, Integer.toString(-2));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(-1, options.getMaximumNumberOfComparisons());
    }

    @Test
    public void testInvalidThreshold() throws Exception {
        String argument = buildArgument(CommandLineArgument.SHOWN_COMPARISONS, "Not an integer...");
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1, statusCode);
    }
}
