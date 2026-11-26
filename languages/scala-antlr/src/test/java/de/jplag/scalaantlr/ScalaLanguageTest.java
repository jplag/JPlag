package de.jplag.scalaantlr;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Tests for the scala language module
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

        collector.addTokenPositionTests("tokenPositions");
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
