package de.jplag.kotlin;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Provides tests for the kotlin language module.
 */
public class KotlinLanguageTest extends LanguageModuleTest {
    public KotlinLanguageTest() {
        super(new KotlinLanguage(), KotlinTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("Complete.kt").testCoverages();
        collector.testFile("Game.kt").testSourceCoverage();

        collector.testFile("HelloWorld.kt").testSourceCoverage().testTokenSequence(KotlinTokenType.PACKAGE, KotlinTokenType.FUNCTION,
                KotlinTokenType.FUNCTION_BODY_BEGIN, KotlinTokenType.FUNCTION_INVOCATION, KotlinTokenType.FUNCTION_BODY_END);

        collector.inlineSource("package de.jplag.kotlin\n").testSourceCoverage().testContainedTokens(KotlinTokenType.PACKAGE);
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreMultipleLines("/*", "*/");
    }
}
