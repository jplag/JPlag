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
import de.jplag.rlang.grammar.RParser.Access_packageContext;
import de.jplag.rlang.grammar.RParser.Assign_func_declarationContext;
import de.jplag.rlang.grammar.RParser.Assign_valueContext;
import de.jplag.rlang.grammar.RParser.Assign_value_listContext;
import de.jplag.rlang.grammar.RParser.Break_statementContext;
import de.jplag.rlang.grammar.RParser.Compound_statementContext;
import de.jplag.rlang.grammar.RParser.Constant_boolContext;
import de.jplag.rlang.grammar.RParser.Constant_numberContext;
import de.jplag.rlang.grammar.RParser.Constant_stringContext;
import de.jplag.rlang.grammar.RParser.For_statementContext;
import de.jplag.rlang.grammar.RParser.Function_callContext;
import de.jplag.rlang.grammar.RParser.Function_definitionContext;
import de.jplag.rlang.grammar.RParser.HelpContext;
import de.jplag.rlang.grammar.RParser.If_statementContext;
import de.jplag.rlang.grammar.RParser.Index_statementContext;
import de.jplag.rlang.grammar.RParser.Next_statementContext;
import de.jplag.rlang.grammar.RParser.Repeat_statementContext;
import de.jplag.rlang.grammar.RParser.While_statementContext;

/**
 * Contains mapping for RLang to create JPlag tokens from ANTLR tokens
 */
public class RListener extends AbstractAntlrListener {
    public RListener() {
        header();
        functions();
        literals();
        controlStructures();
        assignments();
    }

    private void header() {
        visit(Index_statementContext.class).mapRange(INDEX);
        visit(Access_packageContext.class).map(PACKAGE);
    }

    private void functions() {
        visit(Function_definitionContext.class).map(BEGIN_FUNCTION, END_FUNCTION);
        visit(Function_callContext.class).mapRange(FUNCTION_CALL);

        visit(Compound_statementContext.class).map(COMPOUND_BEGIN, COMPOUND_END);
        visit(HelpContext.class).map(HELP);
    }

    private void literals() {
        visit(Constant_numberContext.class).map(NUMBER);
        visit(Constant_stringContext.class).map(STRING);
        visit(Constant_boolContext.class).map(BOOL);
    }

    private void controlStructures() {
        visit(If_statementContext.class).map(IF_BEGIN, IF_END);
        visit(For_statementContext.class).map(FOR_BEGIN, FOR_END);
        visit(While_statementContext.class).map(WHILE_BEGIN, WHILE_END);
        visit(Repeat_statementContext.class).map(REPEAT_BEGIN, REPEAT_END);

        visit(Next_statementContext.class).map(NEXT);
        visit(Break_statementContext.class).map(BREAK);
    }

    private void assignments() {
        visit(Assign_valueContext.class).map(ASSIGN);
        visit(Assign_func_declarationContext.class).map(ASSIGN_FUNC);
        visit(Assign_value_listContext.class).map(ASSIGN_LIST);
    }
}
