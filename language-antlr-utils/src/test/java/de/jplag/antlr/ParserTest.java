package de.jplag.antlr;

import static de.jplag.antlr.testLanguage.TestTokenType.ADDITION;
import static de.jplag.antlr.testLanguage.TestTokenType.NUMBER;
import static de.jplag.antlr.testLanguage.TestTokenType.SUBTRACTION;
import static de.jplag.antlr.testLanguage.TestTokenType.SUB_EXPRESSION_BEGIN;
import static de.jplag.antlr.testLanguage.TestTokenType.SUB_EXPRESSION_END;

import de.jplag.antlr.testLanguage.TestLanguage;
import de.jplag.antlr.testLanguage.TestTokenType;
import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Test cases for the ANTLR parser.
 */
public class ParserTest extends LanguageModuleTest {

    /**
     * Creates the test suite.
     */
    public ParserTest() {
        super(new TestLanguage(), TestTokenType.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.inlineSource("(1 + 3)").testTokenSequence(SUB_EXPRESSION_BEGIN, ADDITION, NUMBER, NUMBER, SUB_EXPRESSION_END);

        collector.inlineSource("(1 - 3)").testTokenSequence(SUB_EXPRESSION_BEGIN, NUMBER, SUBTRACTION, NUMBER, SUB_EXPRESSION_END);

        collector.inlineSource("1").testTokenSequence(NUMBER);
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        // No ignored lines.
    }
}
