package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class SimilarityThresholdTest extends CliTest {
    private static final double EXPECTED_DEFAULT_SIMILARITY_THRESHOLD = 0;

    @Test
    void testDefaultThreshold() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions();
        assertEquals(EXPECTED_DEFAULT_SIMILARITY_THRESHOLD, options.similarityThreshold());
    }

    @Test
    void testInvalidThreshold() {
        assertThrowsExactly(CliException.class, () -> {
            runCli(args -> args.withInvalid(CliArgument.SIMILARITY_THRESHOLD, "Not a Double..."));
        });
    }

    @Test
    void testLowerBound() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SIMILARITY_THRESHOLD, -.01));
        assertEquals(0.0, options.similarityThreshold(), DELTA);
    }

    @Test
    void testUpperBound() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SIMILARITY_THRESHOLD, 1.01));
        assertEquals(1.0, options.similarityThreshold(), DELTA);
    }

    @Test
    void testValidThreshold() throws ExitException, IOException {
        double expectedValue = 0.5;
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SIMILARITY_THRESHOLD, expectedValue));
        assertEquals(expectedValue, options.similarityThreshold(), DELTA);
    }
}
