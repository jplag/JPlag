package de.jplag.cli;

import static de.jplag.cli.test.CliArgument.GAP_SIZE;
import static de.jplag.cli.test.CliArgument.MERGING_ENABLED;
import static de.jplag.cli.test.CliArgument.NEIGHBOR_LENGTH;
import static de.jplag.cli.test.CliArgument.REQUIRED_MERGES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.merging.MergingOptions;
import de.jplag.options.JPlagOptions;

/**
 * Test cases for the options of the match merging mechanism.
 */
class MergingOptionsTest extends CliTest {

    @Test
    @DisplayName("Test if default values are used when creating merging options from CLI")
    void testMergingDefault() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions();

        assertNotNull(options.mergingOptions());
        assertEquals(MergingOptions.DEFAULT_ENABLED, options.mergingOptions().enabled());
        assertEquals(MergingOptions.DEFAULT_NEIGHBOR_LENGTH, options.mergingOptions().minimumNeighborLength());
        assertEquals(MergingOptions.DEFAULT_GAP_SIZE, options.mergingOptions().maximumGapSize());
        assertEquals(MergingOptions.DEFAULT_REQUIRED_MERGES, options.mergingOptions().minimumRequiredMerges());
    }

    @Test
    @DisplayName("Test if custom values are correctly propagated when creating merging options from CLI")
    void testMergingCustom() throws ExitException, IOException {
        int customNumber = 7;

        JPlagOptions options = runCliForOptions(args -> args.with(MERGING_ENABLED).with(NEIGHBOR_LENGTH, customNumber).with(GAP_SIZE, customNumber)
                .with(REQUIRED_MERGES, customNumber));

        assertNotNull(options.mergingOptions());
        assertEquals(true, options.mergingOptions().enabled());
        assertEquals(customNumber, options.mergingOptions().minimumNeighborLength());
        assertEquals(customNumber, options.mergingOptions().maximumGapSize());
        assertEquals(customNumber, options.mergingOptions().minimumRequiredMerges());
    }

}
