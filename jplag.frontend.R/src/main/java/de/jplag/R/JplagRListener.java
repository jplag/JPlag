package de.jplag.R;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.R.grammar.RFilter;
import de.jplag.R.grammar.RFilterListener;
import de.jplag.R.grammar.RListener;
import de.jplag.R.grammar.RParser;

/**
 * Listener class for visiting the R ANTLR parse tree. Transforms the visited ANTLR token into JPlag tokens.
 * Based on an R frontend for JPlag v2.15 by Olmo Kramer, see their <a href="https://github.com/CodeGra-de/jplag/tree/master/jplag.frontend.R">JPlag fork</a>.
 * @author Robin Maisch
 */
public class JplagRListener implements RListener, RFilterListener, RTokenConstants {

    private final RParserAdapter parserAdapter;

    /**
     * Creates the listener.
     * @param parserAdapter the JPlag parser adapter which receives the transformed tokens.
     */
    public JplagRListener(RParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
    }

    /**
     * Transforms an ANTLR Token into a JPlag token and transfers it to the token adapter.
     * @param targetType the type of the JPlag token to be created.
     * @param token the ANTLR token.
     */
    private void transformToken(int targetType, Token token) {
        parserAdapter.addToken(targetType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    private void transformToken(int targetType, Token start, Token end) {
        parserAdapter.addToken(targetType, start.getLine(), start.getCharPositionInLine() + 1, end.getStopIndex() - start.getStartIndex() + 1);
    }

    @Override
    public void enterProg(RParser.ProgContext ctx) {

    }

    @Override
    public void exitProg(RParser.ProgContext ctx) {

    }

    @Override
    public void enterExpr(RParser.ExprContext ctx) {

    }

    @Override
    public void exitExpr(RParser.ExprContext ctx) {

    }

    @Override
    public void enterIndex_statement(RParser.Index_statementContext ctx) {
        transformToken(INDEX, ctx.getStart());
    }

    @Override
    public void exitIndex_statement(RParser.Index_statementContext ctx) {

    }

    @Override
    public void enterAccess_package(RParser.Access_packageContext ctx) {
        transformToken(PACKAGE, ctx.getStart());
    }

    @Override
    public void exitAccess_package(RParser.Access_packageContext ctx) {

    }

    @Override
    public void enterFunction_definition(RParser.Function_definitionContext ctx) {
        transformToken(BEGIN_FUNCTION, ctx.getStart());
    }

    @Override
    public void exitFunction_definition(RParser.Function_definitionContext ctx) {
        transformToken(END_FUNCTION, ctx.getStop());
    }

    @Override
    public void enterFunction_call(RParser.Function_callContext ctx) {
        transformToken(FUNCTION_CALL, ctx.getStart(), ctx.getStop());
    }

    @Override
    public void exitFunction_call(RParser.Function_callContext ctx) {

    }

    @Override
    public void enterConstant(RParser.ConstantContext ctx) {

    }

    @Override
    public void exitConstant(RParser.ConstantContext ctx) {

    }

    @Override
    public void enterConstant_number(RParser.Constant_numberContext ctx) {
        transformToken(NUMBER, ctx.getStart());
    }

    @Override
    public void exitConstant_number(RParser.Constant_numberContext ctx) {

    }

    @Override
    public void enterConstant_string(RParser.Constant_stringContext ctx) {
        transformToken(STRING, ctx.getStart());
    }

    @Override
    public void exitConstant_string(RParser.Constant_stringContext ctx) {

    }

    @Override
    public void enterConstant_bool(RParser.Constant_boolContext ctx) {
        transformToken(BOOL, ctx.getStart());
    }

    @Override
    public void exitConstant_bool(RParser.Constant_boolContext ctx) {

    }

    @Override
    public void enterHelp(RParser.HelpContext ctx) {
        transformToken(HELP, ctx.getStart());
    }

    @Override
    public void exitHelp(RParser.HelpContext ctx) {

    }

    @Override
    public void enterIf_statement(RParser.If_statementContext ctx) {
        transformToken(IF_BEGIN, ctx.getStart());
    }

    @Override
    public void exitIf_statement(RParser.If_statementContext ctx) {
        transformToken(IF_END, ctx.getStop());
    }

    @Override
    public void enterFor_statement(RParser.For_statementContext ctx) {
        transformToken(FOR_BEGIN, ctx.getStart());
    }

    @Override
    public void exitFor_statement(RParser.For_statementContext ctx) {
        transformToken(FOR_END, ctx.getStop());
    }

    @Override
    public void enterWhile_statement(RParser.While_statementContext ctx) {
       transformToken(WHILE_BEGIN, ctx.getStart());
    }

    @Override
    public void exitWhile_statement(RParser.While_statementContext ctx) {
        transformToken(WHILE_END, ctx.getStop());
    }

    @Override
    public void enterRepeat_statement(RParser.Repeat_statementContext ctx) {
        transformToken(REPEAT_BEGIN, ctx.getStart());
    }

    @Override
    public void exitRepeat_statement(RParser.Repeat_statementContext ctx) {
        transformToken(REPEAT_END, ctx.getStop());
    }

    @Override
    public void enterNext_statement(RParser.Next_statementContext ctx) {
        transformToken(NEXT, ctx.getStart());
    }

    @Override
    public void exitNext_statement(RParser.Next_statementContext ctx) {

    }

    @Override
    public void enterBreak_statement(RParser.Break_statementContext ctx) {
        transformToken(BREAK, ctx.getStart());
    }

    @Override
    public void exitBreak_statement(RParser.Break_statementContext ctx) {

    }

    @Override
    public void enterCompound_statement(RParser.Compound_statementContext ctx) {
        transformToken(COMPOUND_BEGIN, ctx.getStart());
    }

    @Override
    public void exitCompound_statement(RParser.Compound_statementContext ctx) {
        transformToken(COMPOUND_END, ctx.getStop());
    }

    @Override
    public void enterExprlist(RParser.ExprlistContext ctx) {

    }

    @Override
    public void exitExprlist(RParser.ExprlistContext ctx) {

    }

    @Override
    public void enterFormlist(RParser.FormlistContext ctx) {

    }

    @Override
    public void exitFormlist(RParser.FormlistContext ctx) {

    }

    @Override
    public void enterForm(RParser.FormContext ctx) {

    }

    @Override
    public void exitForm(RParser.FormContext ctx) {

    }

    @Override
    public void enterSublist(RParser.SublistContext ctx) {

    }

    @Override
    public void exitSublist(RParser.SublistContext ctx) {

    }

    @Override
    public void enterSub(RParser.SubContext ctx) {

    }

    @Override
    public void exitSub(RParser.SubContext ctx) {

    }

    @Override
    public void enterAssign_value(RParser.Assign_valueContext ctx) {
        transformToken(ASSIGN, ctx.getStart());
    }

    @Override
    public void exitAssign_value(RParser.Assign_valueContext ctx) {

    }

    @Override
    public void enterAssign_func_declaration(RParser.Assign_func_declarationContext ctx) {
        transformToken(ASSIGN_FUNC, ctx.getStart());
    }

    @Override
    public void exitAssign_func_declaration(RParser.Assign_func_declarationContext ctx) {

    }

    @Override
    public void enterAssign_value_list(RParser.Assign_value_listContext ctx) {
        transformToken(ASSIGN_LIST, ctx.getStart());
    }

    @Override
    public void exitAssign_value_list(RParser.Assign_value_listContext ctx) {

    }

    @Override
    public void enterStream(RFilter.StreamContext ctx) {

    }

    @Override
    public void exitStream(RFilter.StreamContext ctx) {

    }

    @Override
    public void enterEat(RFilter.EatContext ctx) {

    }

    @Override
    public void exitEat(RFilter.EatContext ctx) {

    }

    @Override
    public void enterElement(RFilter.ElementContext ctx) {

    }

    @Override
    public void exitElement(RFilter.ElementContext ctx) {

    }

    @Override
    public void enterAtom(RFilter.AtomContext ctx) {

    }

    @Override
    public void exitAtom(RFilter.AtomContext ctx) {

    }

    @Override
    public void enterOp(RFilter.OpContext ctx) {

    }

    @Override
    public void exitOp(RFilter.OpContext ctx) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode node) {

    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }
}