package de.jplag.python3;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

public class PyhtonLanguageTest extends LanguageModuleTest {
    public PyhtonLanguageTest() {
        super(new PythonLanguage(), Python3TokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("test_utils.py").testCoverages();

        collector.testFile("base_features.py", "streams.py").testSourceCoverage();

        collector.testFile("log.py").testSourceCoverage().testTokenSequence(Python3TokenType.IMPORT, Python3TokenType.ASSIGN, Python3TokenType.ARRAY,
                Python3TokenType.APPLY);

        collector.testFile("unicode.py").testSourceCoverage().testTokenSequence(Python3TokenType.ASSIGN);
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        collector.ignoreMultipleLines("\"\"\"");

        collector.ignoreLinesByPrefix("else:");
        collector.ignoreLinesByPrefix("elif");
        collector.ignoreLinesByPrefix("#");
        collector.ignoreLinesByPrefix("pass");
    }
}
