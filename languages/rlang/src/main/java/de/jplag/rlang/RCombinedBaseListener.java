package de.jplag.rlang;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.rlang.grammar.RFilter;
import de.jplag.rlang.grammar.RFilterListener;
import de.jplag.rlang.grammar.RListener;
import de.jplag.rlang.grammar.RParser;

/**
 * Empty base implementation for {@link RListener} and {@link RFilterListener}.
 */
public abstract class RCombinedBaseListener implements RListener, RFilterListener {
    @Override
    public void enterStream(RFilter.StreamContext context) {

    }

    @Override
    public void exitStream(RFilter.StreamContext context) {

    }

    @Override
    public void enterEat(RFilter.EatContext context) {

    }

    @Override
    public void exitEat(RFilter.EatContext context) {

    }

    @Override
    public void enterElement(RFilter.ElementContext context) {

    }

    @Override
    public void exitElement(RFilter.ElementContext context) {

    }

    @Override
    public void enterAtom(RFilter.AtomContext context) {

    }

    @Override
    public void exitAtom(RFilter.AtomContext context) {

    }

    @Override
    public void enterOp(RFilter.OpContext context) {

    }

    @Override
    public void exitOp(RFilter.OpContext context) {

    }

    @Override
    public void enterProg(RParser.ProgContext context) {

    }

    @Override
    public void exitProg(RParser.ProgContext context) {

    }

    @Override
    public void enterExpr(RParser.ExprContext context) {

    }

    @Override
    public void exitExpr(RParser.ExprContext context) {

    }

    @Override
    public void enterIndex_statement(RParser.Index_statementContext context) {

    }

    @Override
    public void exitIndex_statement(RParser.Index_statementContext context) {

    }

    @Override
    public void enterAccess_package(RParser.Access_packageContext context) {

    }

    @Override
    public void exitAccess_package(RParser.Access_packageContext context) {

    }

    @Override
    public void enterFunction_definition(RParser.Function_definitionContext context) {

    }

    @Override
    public void exitFunction_definition(RParser.Function_definitionContext context) {

    }

    @Override
    public void enterFunction_call(RParser.Function_callContext context) {

    }

    @Override
    public void exitFunction_call(RParser.Function_callContext context) {

    }

    @Override
    public void enterConstant(RParser.ConstantContext context) {

    }

    @Override
    public void exitConstant(RParser.ConstantContext context) {

    }

    @Override
    public void enterConstant_number(RParser.Constant_numberContext context) {

    }

    @Override
    public void exitConstant_number(RParser.Constant_numberContext context) {

    }

    @Override
    public void enterConstant_string(RParser.Constant_stringContext context) {

    }

    @Override
    public void exitConstant_string(RParser.Constant_stringContext context) {

    }

    @Override
    public void enterConstant_bool(RParser.Constant_boolContext context) {

    }

    @Override
    public void exitConstant_bool(RParser.Constant_boolContext context) {

    }

    @Override
    public void enterHelp(RParser.HelpContext context) {

    }

    @Override
    public void exitHelp(RParser.HelpContext context) {

    }

    @Override
    public void enterIf_statement(RParser.If_statementContext context) {

    }

    @Override
    public void exitIf_statement(RParser.If_statementContext context) {

    }

    @Override
    public void enterFor_statement(RParser.For_statementContext context) {

    }

    @Override
    public void exitFor_statement(RParser.For_statementContext context) {

    }

    @Override
    public void enterWhile_statement(RParser.While_statementContext context) {

    }

    @Override
    public void exitWhile_statement(RParser.While_statementContext context) {

    }

    @Override
    public void enterRepeat_statement(RParser.Repeat_statementContext context) {

    }

    @Override
    public void exitRepeat_statement(RParser.Repeat_statementContext context) {

    }

    @Override
    public void enterNext_statement(RParser.Next_statementContext context) {

    }

    @Override
    public void exitNext_statement(RParser.Next_statementContext context) {

    }

    @Override
    public void enterBreak_statement(RParser.Break_statementContext context) {

    }

    @Override
    public void exitBreak_statement(RParser.Break_statementContext context) {

    }

    @Override
    public void enterCompound_statement(RParser.Compound_statementContext context) {

    }

    @Override
    public void exitCompound_statement(RParser.Compound_statementContext context) {

    }

    @Override
    public void enterExprlist(RParser.ExprlistContext context) {

    }

    @Override
    public void exitExprlist(RParser.ExprlistContext context) {

    }

    @Override
    public void enterFormlist(RParser.FormlistContext context) {

    }

    @Override
    public void exitFormlist(RParser.FormlistContext context) {

    }

    @Override
    public void enterForm(RParser.FormContext context) {

    }

    @Override
    public void exitForm(RParser.FormContext context) {

    }

    @Override
    public void enterSublist(RParser.SublistContext context) {

    }

    @Override
    public void exitSublist(RParser.SublistContext context) {

    }

    @Override
    public void enterSub(RParser.SubContext context) {

    }

    @Override
    public void exitSub(RParser.SubContext context) {

    }

    @Override
    public void enterAssign_value(RParser.Assign_valueContext context) {

    }

    @Override
    public void exitAssign_value(RParser.Assign_valueContext context) {

    }

    @Override
    public void enterAssign_func_declaration(RParser.Assign_func_declarationContext context) {

    }

    @Override
    public void exitAssign_func_declaration(RParser.Assign_func_declarationContext context) {

    }

    @Override
    public void enterAssign_value_list(RParser.Assign_value_listContext context) {

    }

    @Override
    public void exitAssign_value_list(RParser.Assign_value_listContext context) {

    }

    @Override
    public void visitTerminal(TerminalNode node) {

    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext context) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext context) {

    }
}
