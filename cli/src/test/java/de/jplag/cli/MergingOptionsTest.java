package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.merging.MergingOptions;

/**
 * Test cases for the options of the match merging mechanism.
 */
public class MergingOptionsTest extends CommandLineInterfaceTest {

    @Test
    @DisplayName("Test if default values are used when creating merging options from CLI")
    void testMergingDefault() throws CliException {
        buildOptionsFromCLI(defaultArguments());
        assertNotNull(options.mergingOptions());
        assertEquals(options.mergingOptions().enabled(), MergingOptions.DEFAULT_ENABLED);
        assertEquals(options.mergingOptions().minimumNeighborLength(), MergingOptions.DEFAULT_NEIGHBOR_LENGTH);
        assertEquals(options.mergingOptions().maximumGapSize(), MergingOptions.DEFAULT_GAP_SIZE);
    }
}
