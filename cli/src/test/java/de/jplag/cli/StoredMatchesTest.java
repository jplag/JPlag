package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;

import de.jplag.options.JPlagOptions;

class StoredMatchesTest extends CommandLineInterfaceTest {

    @Test
    void testDefault() throws CliException {
        buildOptionsFromCLI(defaultArguments());
        assertEquals(JPlagOptions.DEFAULT_SHOWN_COMPARISONS, options.maximumNumberOfComparisons());
    }

    @Test
    void testValidThreshold() throws CliException {
        int expectedValue = 999;
        buildOptionsFromCLI(defaultArguments().shownComparisons(expectedValue));
        assertEquals(expectedValue, options.maximumNumberOfComparisons());
    }

    @Test
    void testAll() throws CliException {
        int expectedValue = JPlagOptions.SHOW_ALL_COMPARISONS;
        buildOptionsFromCLI(defaultArguments().shownComparisons(expectedValue));
        assertEquals(expectedValue, options.maximumNumberOfComparisons());
    }

    @Test
    void testLowerBound() throws CliException {
        buildOptionsFromCLI(defaultArguments().shownComparisons(-2));
        assertEquals(JPlagOptions.SHOW_ALL_COMPARISONS, options.maximumNumberOfComparisons());
    }

    @Test
    void testInvalidThreshold() {
        assertThrowsExactly(CliException.class, () -> buildOptionsFromCLI(defaultArguments().shownComparisons("Not an integer...")));
    }
}
