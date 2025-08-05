package de.jplag.scala;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

public class ScalaLanguageTest extends LanguageModuleTest {
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
