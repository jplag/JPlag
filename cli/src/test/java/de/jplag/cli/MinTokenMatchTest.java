package de.jplag.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MinTokenMatchTest extends CommandLineInterfaceTest {

    @Test
    void testLanguageDefault() {
        // Language defaults not set yet:
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertNotNull(options.language());
        assertEquals(options.language().minimumTokenMatch(), options.minimumTokenMatch().intValue());
    }

    @Test
    void testInvalidInput() throws Exception {
        String argument = buildArgument(CommandLineArgument.MIN_TOKEN_MATCH, "Not an integer...");
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1, statusCode);
    }

    @Test
    void testUpperBound() throws Exception {
        String argument = buildArgument(CommandLineArgument.MIN_TOKEN_MATCH, "2147483648"); // max value plus one
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1, statusCode);
    }

    @Test
    void testLowerBound() {
        String argument = buildArgument(CommandLineArgument.MIN_TOKEN_MATCH, Integer.toString(-1));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(1, options.minimumTokenMatch().intValue());
    }

    @Test
    void testValidThreshold() {
        int expectedValue = 50;
        String argument = buildArgument(CommandLineArgument.MIN_TOKEN_MATCH, Integer.toString(expectedValue));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(expectedValue, options.minimumTokenMatch().intValue());
    }
}
