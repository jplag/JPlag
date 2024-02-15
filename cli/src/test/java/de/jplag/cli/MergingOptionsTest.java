package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.merging.MergingOptions;

/**
 * Test cases for the options of the match merging mechanism.
 */
class MergingOptionsTest extends CommandLineInterfaceTest {

    @Test
    @DisplayName("Test if default values are used when creating merging options from CLI")
    void testMergingDefault() throws CliException {
        buildOptionsFromCLI(defaultArguments());
        assertNotNull(options.mergingOptions());
        assertEquals(MergingOptions.DEFAULT_ENABLED, options.mergingOptions().enabled());
        assertEquals(MergingOptions.DEFAULT_NEIGHBOR_LENGTH, options.mergingOptions().minimumNeighborLength());
        assertEquals(MergingOptions.DEFAULT_GAP_SIZE, options.mergingOptions().maximumGapSize());
    }
}
