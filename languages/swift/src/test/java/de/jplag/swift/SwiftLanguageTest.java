package de.jplag.swift;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

public class SwiftLanguageTest extends LanguageModuleTest {
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
