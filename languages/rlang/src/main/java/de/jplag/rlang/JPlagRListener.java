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

import org.antlr.v4.runtime.Token;

import de.jplag.rlang.grammar.RParser;

/**
 * Listener class for visiting the R ANTLR parse tree. Transforms the visited ANTLR token into JPlag tokens. Based on an
 * R module for JPlag v2.15 by Olmo Kramer, see their
 * <a href="https://github.com/CodeGra-de/jplag/tree/master/jplag.frontend.R">JPlag fork</a>.
 * @author Robin Maisch
 */
public class JPlagRListener extends RCombinedBaseListener {

    private final RParserAdapter parserAdapter;

    /**
     * Creates the listener.
     * @param parserAdapter the JPlag parser adapter which receives the transformed tokens.
     */
    public JPlagRListener(RParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
    }

    /**
     * Transforms an ANTLR Token into a JPlag token and transfers it to the token adapter.
     * @param targetType the type of the JPlag token to be created.
     * @param token the ANTLR token.
     */
    private void transformToken(RTokenType targetType, Token token) {
        parserAdapter.addToken(targetType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    private void transformToken(RTokenType targetType, Token start, Token end) {
        parserAdapter.addToken(targetType, start.getLine(), start.getCharPositionInLine() + 1, end.getStopIndex() - start.getStartIndex() + 1);
    }

    @Override
    public void enterIndex_statement(RParser.Index_statementContext context) {
        transformToken(INDEX, context.getStart(), context.getStop());
    }

    @Override
    public void enterAccess_package(RParser.Access_packageContext context) {
        transformToken(PACKAGE, context.getStart());
    }

    @Override
    public void enterFunction_definition(RParser.Function_definitionContext context) {
        transformToken(BEGIN_FUNCTION, context.getStart());
    }

    @Override
    public void exitFunction_definition(RParser.Function_definitionContext context) {
        transformToken(END_FUNCTION, context.getStop());
    }

    @Override
    public void enterFunction_call(RParser.Function_callContext context) {
        transformToken(FUNCTION_CALL, context.getStart(), context.getStop());
    }

    @Override
    public void enterConstant_number(RParser.Constant_numberContext context) {
        transformToken(NUMBER, context.getStart());
    }

    @Override
    public void enterConstant_string(RParser.Constant_stringContext context) {
        transformToken(STRING, context.getStart());
    }

    @Override
    public void enterConstant_bool(RParser.Constant_boolContext context) {
        transformToken(BOOL, context.getStart());
    }

    @Override
    public void enterHelp(RParser.HelpContext context) {
        transformToken(HELP, context.getStart());
    }

    @Override
    public void enterIf_statement(RParser.If_statementContext context) {
        transformToken(IF_BEGIN, context.getStart());
    }

    @Override
    public void exitIf_statement(RParser.If_statementContext context) {
        transformToken(IF_END, context.getStop());
    }

    @Override
    public void enterFor_statement(RParser.For_statementContext context) {
        transformToken(FOR_BEGIN, context.getStart());
    }

    @Override
    public void exitFor_statement(RParser.For_statementContext context) {
        transformToken(FOR_END, context.getStop());
    }

    @Override
    public void enterWhile_statement(RParser.While_statementContext context) {
        transformToken(WHILE_BEGIN, context.getStart());
    }

    @Override
    public void exitWhile_statement(RParser.While_statementContext context) {
        transformToken(WHILE_END, context.getStop());
    }

    @Override
    public void enterRepeat_statement(RParser.Repeat_statementContext context) {
        transformToken(REPEAT_BEGIN, context.getStart());
    }

    @Override
    public void exitRepeat_statement(RParser.Repeat_statementContext context) {
        transformToken(REPEAT_END, context.getStop());
    }

    @Override
    public void enterNext_statement(RParser.Next_statementContext context) {
        transformToken(NEXT, context.getStart());
    }

    @Override
    public void enterBreak_statement(RParser.Break_statementContext context) {
        transformToken(BREAK, context.getStart());
    }

    @Override
    public void enterCompound_statement(RParser.Compound_statementContext context) {
        transformToken(COMPOUND_BEGIN, context.getStart());
    }

    @Override
    public void exitCompound_statement(RParser.Compound_statementContext context) {
        transformToken(COMPOUND_END, context.getStop());
    }

    @Override
    public void enterAssign_value(RParser.Assign_valueContext context) {
        transformToken(ASSIGN, context.getStart());
    }

    @Override
    public void enterAssign_func_declaration(RParser.Assign_func_declarationContext context) {
        transformToken(ASSIGN_FUNC, context.getStart());
    }

    @Override
    public void enterAssign_value_list(RParser.Assign_value_listContext context) {
        transformToken(ASSIGN_LIST, context.getStart());
    }

}