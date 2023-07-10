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

        createStartMapping(VarDefContext.class, VARDEF).addAsVariable(VariableScope.FILE, false, rule -> rule.VAR_NAME().getText())
                .withSemantics(CodeSemantics.createKeep());

        createRangeMapping(CalcExpressionContext.class, ADDITION, rule -> rule.operator() != null && rule.operator().PLUS() != null)
                .withControlSemantics();
        createRangeMapping(OperatorContext.class, SUBTRACTION, rule -> rule.MINUS() != null).withControlSemantics();
        createStartStopMapping(SubExpressionContext.class, SUB_EXPRESSION_BEGIN, SUB_EXPRESSION_END)
                // .addEndSemanticHandler(registry -> registry.addAllNonLocalVariablesAsReads())
                // does not work here, because there is no class context. Is still here as an example.
                .addLocalScope().withControlSemantics();
        createTerminalMapping(TestParser.NUMBER, NUMBER).withSemantics(CodeSemantics.createKeep());
        createStartMapping(VarRefContext.class, VARREF).withSemantics(CodeSemantics.createKeep());
    }
}
