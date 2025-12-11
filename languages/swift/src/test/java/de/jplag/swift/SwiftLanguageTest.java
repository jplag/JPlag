package de.jplag.swift;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Unit test for the Swift language module, verifying tokenization and source coverage. Extends
 * {@link LanguageModuleTest} to provide Swift-specific test files and rules for ignoring irrelevant source lines.
 */
public class SwiftLanguageTest extends LanguageModuleTest {

    /**
     * Constructs a Swift language test module with the appropriate language and token type.
     */
    public SwiftLanguageTest() {
        super(new SwiftLanguage(), SwiftTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("Complete.swift").testCoverages();
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreLinesByContains("//NO TOKEN");
    }
}
