package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class MinTokenMatchTest extends CliTest {

    @Test
    void testLanguageDefault() throws ExitException, IOException {
        // Language defaults not set yet:
        JPlagOptions options = runCliForOptions();

        assertNotNull(options.language());
        assertEquals(options.language().minimumTokenMatch(), options.minimumTokenMatch().intValue());
    }

    @Test
    void testInvalidInput() {
        Assertions.assertThrowsExactly(CliException.class, () -> {
            runCli(options -> options.withInvalid(CliArgument.MIN_TOKEN_MATCH, "Not an integer..."));
        });
    }

    @Test
    void testUpperBound() {
        String higherThanMax = String.valueOf((long) Integer.MAX_VALUE + 1);

        Assertions.assertThrowsExactly(CliException.class, () -> {
            runCli(options -> options.withInvalid(CliArgument.MIN_TOKEN_MATCH, higherThanMax));
        });
    }

    @Test
    void testLowerBound() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.MIN_TOKEN_MATCH, -1));
        assertEquals(1, options.minimumTokenMatch().intValue());
    }

    @Test
    void testValidThreshold() throws ExitException, IOException {
        int expectedValue = 50;
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.MIN_TOKEN_MATCH, expectedValue));
        assertEquals(expectedValue, options.minimumTokenMatch().intValue());
    }
}
