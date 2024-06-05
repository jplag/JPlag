package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class StoredMatchesTest extends CliTest {
    @Test
    void testDefault() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions();
        assertEquals(JPlagOptions.DEFAULT_SHOWN_COMPARISONS, options.maximumNumberOfComparisons());
    }

    @Test
    void testValidThreshold() throws ExitException, IOException {
        int expectedValue = 999;
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SHOWN_COMPARISONS, expectedValue));
        assertEquals(expectedValue, options.maximumNumberOfComparisons());
    }

    @Test
    void testAll() throws ExitException, IOException {
        int expectedValue = JPlagOptions.SHOW_ALL_COMPARISONS;
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SHOWN_COMPARISONS, expectedValue));
        assertEquals(expectedValue, options.maximumNumberOfComparisons());
    }

    @Test
    void testLowerBound() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SHOWN_COMPARISONS, -2));
        assertEquals(JPlagOptions.SHOW_ALL_COMPARISONS, options.maximumNumberOfComparisons());
    }

    @Test
    void testInvalidThreshold() {
        assertThrowsExactly(CliException.class, () -> {
            runCliForOptions(args -> args.withInvalid(CliArgument.SHOWN_COMPARISONS, "Not an integer..."));
        });
    }
}
