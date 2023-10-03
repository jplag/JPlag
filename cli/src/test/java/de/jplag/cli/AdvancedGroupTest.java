package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class AdvancedGroupTest extends CommandLineInterfaceTest {
    private static final String SUFFIXES = ".sc,.scala";

    private static final double SIMILARITY_THRESHOLD = 0.5;

    /**
     * Verify that it is possible to set multiple options in the "advanced" options group.
     */
    @Test
    void testNotExclusive() throws CliException {
        buildOptionsFromCLI(defaultArguments().suffixes(SUFFIXES).similarityThreshold(SIMILARITY_THRESHOLD));
        assertEquals(Arrays.stream(SUFFIXES.split(",")).toList(), options.fileSuffixes());
        assertEquals(0.5, options.similarityThreshold());
    }
}
