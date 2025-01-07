package de.jplag.kotlin;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Provides tests for the kotlin language module
 */
public class KotlinLanguageTest extends LanguageModuleTest {
    public KotlinLanguageTest() {
        super(new KotlinLanguage(), KotlinTokenAttribute.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("Complete.kt").testCoverages();
        collector.testFile("Game.kt").testSourceCoverage();

        collector.testFile("HelloWorld.kt").testSourceCoverage().testTokenSequence(KotlinTokenAttribute.PACKAGE, KotlinTokenAttribute.FUNCTION,
                KotlinTokenAttribute.FUNCTION_BODY_BEGIN, KotlinTokenAttribute.FUNCTION_INVOCATION, KotlinTokenAttribute.FUNCTION_BODY_END);

        collector.inlineSource("package de.jplag.kotlin\n").testSourceCoverage().testContainedTokens(KotlinTokenAttribute.PACKAGE);
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreMultipleLines("/*", "*/");
    }
}
