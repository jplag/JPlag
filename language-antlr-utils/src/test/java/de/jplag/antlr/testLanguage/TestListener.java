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

        mapEnter(VarDefContext.class, VARDEF).addAsVariable(VariableScope.FILE, false, rule -> rule.VAR_NAME().getText())
                .withSemantics(CodeSemantics.createKeep());

        mapRange(CalcExpressionContext.class, ADDITION, rule -> rule.operator() != null && rule.operator().PLUS() != null).withControlSemantics();
        mapRange(OperatorContext.class, SUBTRACTION, rule -> rule.MINUS() != null).withControlSemantics();
        mapEnterExit(SubExpressionContext.class, SUB_EXPRESSION_BEGIN, SUB_EXPRESSION_END)
                // .addEndSemanticHandler(registry -> registry.addAllNonLocalVariablesAsReads())
                // does not work here, because there is no class context. Is still here as an example.
                .addLocalScope().withControlSemantics();
        mapTerminal(TestParser.NUMBER, NUMBER).withSemantics(CodeSemantics.createKeep());
        mapEnter(VarRefContext.class, VARREF).withSemantics(CodeSemantics.createKeep());
    }
}
