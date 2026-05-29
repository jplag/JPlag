package de.jplag.c;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Configures the tests for the c language module.
 */
public class CLanguageModuleTests extends LanguageModuleTest {
    /**
     * New instance.
     */
    public CLanguageModuleTests() {
        super(new CLanguage(), CTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("minimal.c", "progpedia-00081_00003.c").testSourceCoverage();
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreLinesByPrefix("#");
        collector.ignoreLinesByContains("//ignore");
    }
}
