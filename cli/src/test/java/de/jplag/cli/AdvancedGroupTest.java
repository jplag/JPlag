package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArg;
import de.jplag.cli.test.CliArgBuilder;
import de.jplag.cli.test.CliResult;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class AdvancedGroupTest extends CliTest {
    private static final String[] SUFFIXES = new String[] {".sc", ".scala"};

    private static final double SIMILARITY_THRESHOLD = 0.5;

    /**
     * Verify that it is possible to set multiple options in the "advanced" options group.
     */
    @Test
    void testNotExclusive() throws ExitException, IOException {
        CliResult result = runCli();
        JPlagOptions options = result.jPlagOptions();

        assertEquals(List.of(SUFFIXES), options.fileSuffixes());
        assertEquals(SIMILARITY_THRESHOLD, options.similarityThreshold());
    }

    @Override
    public void initializeParameters(CliArgBuilder args) {
        addDefaultParameters();
        args.with(CliArg.SIMILARITY_THRESHOLD, SIMILARITY_THRESHOLD);
        args.with(CliArg.SUFFIXES, SUFFIXES);
    }
}
