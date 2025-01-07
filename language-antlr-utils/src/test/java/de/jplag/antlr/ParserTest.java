package de.jplag.antlr;

import static de.jplag.antlr.testLanguage.TestTokenAttribute.ADDITION;
import static de.jplag.antlr.testLanguage.TestTokenAttribute.NUMBER;
import static de.jplag.antlr.testLanguage.TestTokenAttribute.SUBTRACTION;
import static de.jplag.antlr.testLanguage.TestTokenAttribute.SUB_EXPRESSION_BEGIN;
import static de.jplag.antlr.testLanguage.TestTokenAttribute.SUB_EXPRESSION_END;

import de.jplag.antlr.testLanguage.TestLanguage;
import de.jplag.antlr.testLanguage.TestTokenAttribute;
import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

public class ParserTest extends LanguageModuleTest {
    public ParserTest() {
        super(new TestLanguage(), TestTokenAttribute.class);
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        collector.inlineSource("(1 + 3)").testTokenSequence(SUB_EXPRESSION_BEGIN, ADDITION, NUMBER, NUMBER, SUB_EXPRESSION_END);

        collector.inlineSource("(1 - 3)").testTokenSequence(SUB_EXPRESSION_BEGIN, NUMBER, SUBTRACTION, NUMBER, SUB_EXPRESSION_END);

        collector.inlineSource("1").testTokenSequence(NUMBER);
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {

    }
}
