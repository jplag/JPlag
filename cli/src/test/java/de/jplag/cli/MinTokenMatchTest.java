package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MinTokenMatchTest extends CommandLineInterfaceTest {

    @Test
    void testLanguageDefault() throws CliException {
        // Language defaults not set yet:
        buildOptionsFromCLI(defaultArguments());
        assertNotNull(options.language());
        assertEquals(options.language().minimumTokenMatch(), options.minimumTokenMatch().intValue());
    }

    @Test
    void testInvalidInput() {
        Assertions.assertThrowsExactly(CliException.class, () -> buildOptionsFromCLI(defaultArguments().minTokens("Not an integer...")));
    }

    @Test
    void testUpperBound() {
        String higherThanMax = String.valueOf(((long) Integer.MAX_VALUE) + 1);

        Assertions.assertThrowsExactly(CliException.class, () -> buildOptionsFromCLI(defaultArguments().minTokens(higherThanMax)));
    }

    @Test
    void testLowerBound() throws CliException {
        buildOptionsFromCLI(defaultArguments().minTokens(-1));
        assertEquals(1, options.minimumTokenMatch().intValue());
    }

    @Test
    void testValidThreshold() throws CliException {
        int expectedValue = 50;
        buildOptionsFromCLI(defaultArguments().minTokens(expectedValue));
        assertEquals(expectedValue, options.minimumTokenMatch().intValue());
    }
}
