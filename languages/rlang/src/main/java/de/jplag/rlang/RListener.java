package de.jplag.rlang;

import static de.jplag.rlang.RTokenType.ASSIGN;
import static de.jplag.rlang.RTokenType.ASSIGN_FUNC;
import static de.jplag.rlang.RTokenType.ASSIGN_LIST;
import static de.jplag.rlang.RTokenType.BEGIN_FUNCTION;
import static de.jplag.rlang.RTokenType.BOOL;
import static de.jplag.rlang.RTokenType.BREAK;
import static de.jplag.rlang.RTokenType.COMPOUND_BEGIN;
import static de.jplag.rlang.RTokenType.COMPOUND_END;
import static de.jplag.rlang.RTokenType.END_FUNCTION;
import static de.jplag.rlang.RTokenType.FOR_BEGIN;
import static de.jplag.rlang.RTokenType.FOR_END;
import static de.jplag.rlang.RTokenType.FUNCTION_CALL;
import static de.jplag.rlang.RTokenType.HELP;
import static de.jplag.rlang.RTokenType.IF_BEGIN;
import static de.jplag.rlang.RTokenType.IF_END;
import static de.jplag.rlang.RTokenType.INDEX;
import static de.jplag.rlang.RTokenType.NEXT;
import static de.jplag.rlang.RTokenType.NUMBER;
import static de.jplag.rlang.RTokenType.PACKAGE;
import static de.jplag.rlang.RTokenType.REPEAT_BEGIN;
import static de.jplag.rlang.RTokenType.REPEAT_END;
import static de.jplag.rlang.RTokenType.STRING;
import static de.jplag.rlang.RTokenType.WHILE_BEGIN;
import static de.jplag.rlang.RTokenType.WHILE_END;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.rlang.grammar.RParser.ArrayAccessContext;
import de.jplag.rlang.grammar.RParser.AssignmentContext;
import de.jplag.rlang.grammar.RParser.BreakContext;
import de.jplag.rlang.grammar.RParser.ComplexContext;
import de.jplag.rlang.grammar.RParser.CompoundStatementContext;
import de.jplag.rlang.grammar.RParser.FalseContext;
import de.jplag.rlang.grammar.RParser.FloatContext;
import de.jplag.rlang.grammar.RParser.ForContext;
import de.jplag.rlang.grammar.RParser.FunctionCallContext;
import de.jplag.rlang.grammar.RParser.FunctionDefinitionContext;
import de.jplag.rlang.grammar.RParser.HelpContext;
import de.jplag.rlang.grammar.RParser.HexContext;
import de.jplag.rlang.grammar.RParser.IfContext;
import de.jplag.rlang.grammar.RParser.IfElseContext;
import de.jplag.rlang.grammar.RParser.IntContext;
import de.jplag.rlang.grammar.RParser.ListAccessContext;
import de.jplag.rlang.grammar.RParser.NamespaceAccessContext;
import de.jplag.rlang.grammar.RParser.NextContext;
import de.jplag.rlang.grammar.RParser.RepeatContext;
import de.jplag.rlang.grammar.RParser.StringContext;
import de.jplag.rlang.grammar.RParser.SubContext;
import de.jplag.rlang.grammar.RParser.TrueContext;
import de.jplag.rlang.grammar.RParser.WhileContext;

/**
 * Contains mapping for RLang to create JPlag tokens from ANTLR tokens.
 */
public class RListener extends AbstractAntlrListener {

    /**
     * Creates the listener.
     */
    public RListener() {
        addHeaderRules();
        addFunctionRules();
        addLiteralRules();
        addControlStructureRules();
        addAssigmentRules();
    }

    private void addHeaderRules() {
        visit(ArrayAccessContext.class).mapRange(INDEX);
        visit(ListAccessContext.class).mapRange(INDEX);
        visit(NamespaceAccessContext.class).map(PACKAGE);
    }

    private void addFunctionRules() {
        visit(FunctionDefinitionContext.class).map(BEGIN_FUNCTION, END_FUNCTION);
        visit(FunctionCallContext.class).mapRange(FUNCTION_CALL);

        visit(CompoundStatementContext.class).map(COMPOUND_BEGIN, COMPOUND_END);
        visit(HelpContext.class).map(HELP);
    }

    private void addLiteralRules() {
        visit(HexContext.class).map(NUMBER);
        visit(IntContext.class).map(NUMBER);
        visit(FloatContext.class).map(NUMBER);
        visit(ComplexContext.class).map(NUMBER);
        visit(StringContext.class).map(STRING);
        visit(TrueContext.class).map(BOOL);
        visit(FalseContext.class).map(BOOL);
    }

    private void addControlStructureRules() {
        visit(IfContext.class).map(IF_BEGIN, IF_END);
        visit(IfElseContext.class).map(IF_BEGIN, IF_END);
        visit(ForContext.class).map(FOR_BEGIN, FOR_END);
        visit(WhileContext.class).map(WHILE_BEGIN, WHILE_END);
        visit(RepeatContext.class).map(REPEAT_BEGIN, REPEAT_END);

        visit(NextContext.class).map(NEXT);
        visit(BreakContext.class).map(BREAK);
    }

    private void addAssigmentRules() {
        visit(AssignmentContext.class, context -> context.ASSIGN() != null).map(ASSIGN);
        visit(AssignmentContext.class, context -> context.EQUALS() != null).map(ASSIGN_FUNC);
        visit(SubContext.class).map(ASSIGN_LIST);
    }
}
