package de.jplag.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.options.JPlagOptions;

class StoredMatchesTest extends CommandLineInterfaceTest {

    @Test
    void testDefault() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(JPlagOptions.DEFAULT_SHOWN_COMPARISONS, options.maximumNumberOfComparisons());
    }

    @Test
    void testValidThreshold() {
        int expectedValue = 999;
        String argument = buildArgument(CommandLineArgument.SHOWN_COMPARISONS, Integer.toString(expectedValue));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(expectedValue, options.maximumNumberOfComparisons());
    }

    @Test
    void testAll() {
        int expectedValue = JPlagOptions.SHOW_ALL_COMPARISONS;
        String argument = buildArgument(CommandLineArgument.SHOWN_COMPARISONS, Integer.toString(expectedValue));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(expectedValue, options.maximumNumberOfComparisons());
    }

    @Test
    void testLowerBound() {
        String argument = buildArgument(CommandLineArgument.SHOWN_COMPARISONS, Integer.toString(-2));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(JPlagOptions.SHOW_ALL_COMPARISONS, options.maximumNumberOfComparisons());
    }

    @Test
    void testInvalidThreshold() throws Exception {
        String argument = buildArgument(CommandLineArgument.SHOWN_COMPARISONS, "Not an integer...");
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1, statusCode);
    }
}
