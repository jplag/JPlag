package de.jplag.scala;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Unit test for the Scala language module, verifying tokenization and source coverage. Extends
 * {@link LanguageModuleTest} to provide Scala-specific test files and rules for ignoring irrelevant source lines,
 * including comments, specific patterns, and multi-line blocks.
 */
public class ScalaLanguageTest extends LanguageModuleTest {

    /**
     * Constructs a Scala language test module with the appropriate language and token type.
     */
    public ScalaLanguageTest() {
        super(new ScalaLanguage(), ScalaTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("Complete.scala").testCoverages();
        collector.testFile("Parser.scala").testSourceCoverage();
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreLinesByRegex("\\w*}\\w*");
        collector.ignoreLinesByRegex("\\w*\\)\\w*");
        collector.ignoreLinesByPrefix("//");
        collector.ignoreMultipleLines("/*", "*/");
        collector.ignoreLinesByContains("//ignore line");
    }
}
