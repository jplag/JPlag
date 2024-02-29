package de.jplag.antlr.testLanguage;

import static de.jplag.antlr.testLanguage.TestTokenType.ADDITION;
import static de.jplag.antlr.testLanguage.TestTokenType.NUMBER;
import static de.jplag.antlr.testLanguage.TestTokenType.SUBTRACTION;
import static de.jplag.antlr.testLanguage.TestTokenType.SUB_EXPRESSION_BEGIN;
import static de.jplag.antlr.testLanguage.TestTokenType.SUB_EXPRESSION_END;
import static de.jplag.antlr.testLanguage.TestTokenType.VARDEF;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.TestParser;
import de.jplag.antlr.TestParser.CalcExpressionContext;
import de.jplag.antlr.TestParser.OperatorContext;
import de.jplag.antlr.TestParser.SubExpressionContext;
import de.jplag.antlr.TestParser.VarDefContext;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableScope;

class TestListener extends AbstractAntlrListener {

    TestListener() {
        visit(VarDefContext.class).map(VARDEF).withSemantics(CodeSemantics::createKeep)
                .onEnter((rule, variableRegistry) -> variableRegistry.registerVariable(rule.VAR_NAME().getText(), VariableScope.FILE, false));
        visit(CalcExpressionContext.class, rule -> rule.operator() != null && rule.operator().PLUS() != null).map(ADDITION).withControlSemantics();
        visit(OperatorContext.class, rule -> rule.MINUS() != null).map(SUBTRACTION).withControlSemantics();
        visit(SubExpressionContext.class).map(SUB_EXPRESSION_BEGIN, SUB_EXPRESSION_END).addLocalScope().withControlSemantics();
        visit(TestParser.NUMBER).map(NUMBER).withSemantics(CodeSemantics::createKeep);
        visit(VarDefContext.class).map(VARDEF).withSemantics(CodeSemantics::createKeep);
    }
}
