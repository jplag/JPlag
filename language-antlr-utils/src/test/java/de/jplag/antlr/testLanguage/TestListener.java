package de.jplag.antlr.testLanguage;

import static de.jplag.antlr.testLanguage.TestTokenType.*;

import java.io.File;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.TestParser;
import de.jplag.antlr.TestParser.*;
import de.jplag.antlr.TokenCollector;

public class TestListener extends AbstractAntlrListener {
    /**
     * New instance
     * @param collector The token collector
     * @param currentFile The currently processed file
     */
    public TestListener(TokenCollector collector, File currentFile) {
        super(collector, currentFile);

        createRangeMapping(CalcExpressionContext.class, ADDITION, rule -> rule.operator() != null && rule.operator().PLUS() != null);
        createRangeMapping(OperatorContext.class, SUBTRACTION, rule -> rule.MINUS() != null);
        createStartStopMapping(SubExpressionContext.class, SUB_EXPRESSION_BEGIN, SUB_EXPRESSION_END);
        createTerminalMapping(TestParser.NUMBER, NUMBER);
    }
}
