package de.jplag.antlr.testLanguage;

import static de.jplag.antlr.testLanguage.TestTokenType.*;

import java.io.File;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.TestParser;
import de.jplag.antlr.TestParser.*;
import de.jplag.antlr.TokenCollector;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableScope;

public class TestListener extends AbstractAntlrListener {
    /**
     * New instance
     * @param collector The token collector
     * @param currentFile The currently processed file
     */
    public TestListener(TokenCollector collector, File currentFile) {
        super(collector, currentFile, true);
        visit(VarDefContext.class).map(VARDEF).withSemantics(CodeSemantics::createKeep)
                .onEnter(rule -> variableRegistry.registerVariable(rule.VAR_NAME().getText(), VariableScope.FILE, false));
        visit(CalcExpressionContext.class, rule -> rule.operator() != null && rule.operator().PLUS() != null).map(ADDITION).withControlSemantics();
        visit(OperatorContext.class, rule -> rule.MINUS() != null).map(SUBTRACTION).withControlSemantics();
        visit(SubExpressionContext.class).map(SUB_EXPRESSION_BEGIN, SUB_EXPRESSION_END).addLocalScope().withControlSemantics();
        visit(TestParser.NUMBER).map(NUMBER).withSemantics(CodeSemantics::createKeep);
        visit(VarDefContext.class).map(VARDEF).withSemantics(CodeSemantics::createKeep);
    }
}
