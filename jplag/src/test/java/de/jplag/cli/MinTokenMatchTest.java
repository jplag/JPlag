package de.jplag.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import de.jplag.CommandLineArgument;
import de.jplag.JPlag;
import de.jplag.exceptions.ExitException;

public class MinTokenMatchTest extends CommandLineInterfaceTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testLanguageDefault() {
        // Language defaults not set yet:
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertNull(options.getMinimumTokenMatch());

        JPlag jplag = null;
        // Init JPlag:
        try {
            jplag = new JPlag(options);
        } catch (ExitException e) {
            e.printStackTrace();
            fail("JPlag intialization failed!");
        }

        // Now the language is set:
        assertNotNull(jplag.getLanguage());
        assertEquals(jplag.getLanguage().minimumTokenMatch(), options.getMinimumTokenMatch().intValue());
    }

    @Test
    public void testInvalidInput() {
        exit.expectSystemExitWithStatus(1);
        String argument = buildArgument(CommandLineArgument.MIN_TOKEN_MATCH, "Not an integer...");
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
    }

    @Test
    public void testUpperBound() {
        exit.expectSystemExitWithStatus(1);
        String argument = buildArgument(CommandLineArgument.MIN_TOKEN_MATCH, "2147483648"); // max value plus one
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
    }

    @Test
    public void testLowerBound() {
        String argument = buildArgument(CommandLineArgument.MIN_TOKEN_MATCH, Integer.toString(-1));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(1, options.getMinimumTokenMatch().intValue());
    }

    @Test
    public void testValidThreshold() {
        int expectedValue = 50;
        String argument = buildArgument(CommandLineArgument.MIN_TOKEN_MATCH, Integer.toString(expectedValue));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(expectedValue, options.getMinimumTokenMatch().intValue());
    }
}
