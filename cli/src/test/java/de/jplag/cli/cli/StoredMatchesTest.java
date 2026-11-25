package de.jplag.cli.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.io.IOException;

import de.jplag.options.ReportingOptions;
import org.junit.jupiter.api.Test;

import de.jplag.cli.CliException;
import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class StoredMatchesTest extends CliTest {
    @Test
    void testDefault() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions();
        assertEquals(ReportingOptions.DEFAULT_SHOWN_COMPARISONS, options.reportingOptions().maximumNumberOfComparisons());
    }

    @Test
    void testValidThreshold() throws ExitException, IOException {
        int expectedValue = 999;
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SHOWN_COMPARISONS, expectedValue));
        assertEquals(expectedValue, options.reportingOptions().maximumNumberOfComparisons());
    }

    @Test
    void testAll() throws ExitException, IOException {
        int expectedValue = ReportingOptions.SHOW_ALL_COMPARISONS;
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SHOWN_COMPARISONS, expectedValue));
        assertEquals(expectedValue, options.reportingOptions().maximumNumberOfComparisons());
    }

    @Test
    void testLowerBound() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SHOWN_COMPARISONS, -2));
        assertEquals(ReportingOptions.SHOW_ALL_COMPARISONS, options.reportingOptions().maximumNumberOfComparisons());
    }

    @Test
    void testInvalidThreshold() {
        assertThrowsExactly(CliException.class, () -> {
            runCliForOptions(args -> args.withInvalid(CliArgument.SHOWN_COMPARISONS, "Not an integer..."));
        });
    }
}
