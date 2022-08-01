package de.jplag.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.jplag.CommandLineArgument;
import de.jplag.JPlag;

public class MinTokenMatchTest extends CommandLineInterfaceTest {

    @Test
    public void testLanguageDefault() {
        // Language defaults not set yet:
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertNull(options.getMinimumTokenMatch());
        assertNull(options.getLanguage());

        // Init JPlag:
        new JPlag(options);

        // Now the language is set:
        assertNotNull(options.getLanguage());
        assertEquals(options.getLanguage().minimumTokenMatch(), options.getMinimumTokenMatch().intValue());
    }

    @Test
    public void testInvalidInput() throws Exception {
        String argument = buildArgument(CommandLineArgument.MIN_TOKEN_MATCH, "Not an integer...");
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1, statusCode);
    }

    @Test
    public void testUpperBound() throws Exception {
        String argument = buildArgument(CommandLineArgument.MIN_TOKEN_MATCH, "2147483648"); // max value plus one
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1, statusCode);
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
