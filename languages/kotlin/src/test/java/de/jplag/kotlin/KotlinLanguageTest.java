package de.jplag.kotlin;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Unit test for the Kotlin language module, verifying tokenization and source coverage. Extends
 * {@link LanguageModuleTest} to provide Kotlin-specific test files, token sequences, and rules for ignoring irrelevant
 * source lines such as multi-line comments.
 */
public class KotlinLanguageTest extends LanguageModuleTest {

    /**
     * Constructs a Kotlin language test module with the appropriate language and token type.
     */
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
