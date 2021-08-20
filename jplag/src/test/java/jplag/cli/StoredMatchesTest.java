package jplag.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import jplag.CommandLineArgument;
import jplag.options.JPlagOptions;

public class StoredMatchesTest extends CommandLineInterfaceTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testDefault() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(JPlagOptions.DEFAULT_STORED_MATCHES, options.getMaxNumberOfMatches());
    }

    @Test
    public void testValidThreshold() {
        int expectedValue = 999;
        String argument = buildArgument(CommandLineArgument.STORED_MATCHES, Integer.toString(expectedValue));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(expectedValue, options.getMaxNumberOfMatches());
    }

    @Test
    public void testAll() {
        int expectedValue = -1;
        String argument = buildArgument(CommandLineArgument.STORED_MATCHES, Integer.toString(expectedValue));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(expectedValue, options.getMaxNumberOfMatches());
    }

    @Test
    public void testLowerBound() {
        String argument = buildArgument(CommandLineArgument.STORED_MATCHES, Integer.toString(-2));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(-1, options.getMaxNumberOfMatches());
    }

    @Test
    public void testInvalidThreshold() {
        exit.expectSystemExitWithStatus(1);
        String argument = buildArgument(CommandLineArgument.STORED_MATCHES, "Not an integer...");
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
    }
}
