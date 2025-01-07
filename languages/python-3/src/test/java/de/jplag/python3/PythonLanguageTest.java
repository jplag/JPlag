package de.jplag.python3;

import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Tests for the python language module. The following tests are included:
 * <p>
 * <ul>
 * <li>test all tokens occur in test_utils.py.</li>
 * <li>test exact sequences for log.py and unicode.py.</li>
 * <li>test that all relevant lines are covered in all test files.</li>
 * </ul>
 */
public class PythonLanguageTest extends LanguageModuleTest {
    public PythonLanguageTest() {
        super(new PythonLanguage(), Python3TokenAttribute.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.testFile("test_utils.py").testCoverages();

        collector.testFile("base_features.py", "streams.py").testSourceCoverage();

        collector.testFile("log.py").testSourceCoverage().testTokenSequence(Python3TokenAttribute.IMPORT, Python3TokenAttribute.ASSIGN,
                Python3TokenAttribute.ARRAY, Python3TokenAttribute.APPLY);

        collector.testFile("unicode.py").testSourceCoverage().testTokenSequence(Python3TokenAttribute.ASSIGN);
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
