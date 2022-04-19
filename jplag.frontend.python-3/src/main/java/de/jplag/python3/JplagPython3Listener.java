package de.jplag.python3;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.python3.grammar.Python3BaseListener;
import de.jplag.python3.grammar.Python3Parser;

public class JplagPython3Listener extends Python3BaseListener implements Python3TokenConstants {

    private final Parser parser;

    public JplagPython3Listener(Parser parser) {
        this.parser = parser;
    }

    @Override
    public void enterAssert_stmt(Python3Parser.Assert_stmtContext ctx) {
        parser.add(ASSERT, ctx.getStart());
    }

    @Override
    public void enterDecorated(Python3Parser.DecoratedContext ctx) {
        parser.add(DEC_BEGIN, ctx.getStart());
    }

    @Override
    public void exitDecorated(Python3Parser.DecoratedContext ctx) {
        parser.addEnd(DEC_END, ctx.getStart());
    }

    @Override
    public void enterRaise_stmt(Python3Parser.Raise_stmtContext ctx) {
        parser.add(RAISE, ctx.getStart());
    }

    @Override
    public void enterExcept_clause(Python3Parser.Except_clauseContext ctx) {
        parser.add(EXCEPT_BEGIN, ctx.getStart());
    }

    @Override
    public void exitExcept_clause(Python3Parser.Except_clauseContext ctx) {
        parser.addEnd(EXCEPT_END, ctx.getStart());
    }

    @Override
    public void enterDictorsetmaker(Python3Parser.DictorsetmakerContext ctx) {
        parser.add(ARRAY, ctx.getStart());
    }

    @Override
    public void enterReturn_stmt(Python3Parser.Return_stmtContext ctx) {
        parser.add(RETURN, ctx.getStart());
    }

    @Override
    public void enterWhile_stmt(Python3Parser.While_stmtContext ctx) {
        parser.add(WHILE_BEGIN, ctx.getStart());
    }

    @Override
    public void exitWhile_stmt(Python3Parser.While_stmtContext ctx) {
        parser.addEnd(WHILE_END, ctx.getStart());
    }

    @Override
    public void enterYield_arg(Python3Parser.Yield_argContext ctx) {
        parser.add(YIELD, ctx.getStart());
    }

    @Override
    public void enterImport_stmt(Python3Parser.Import_stmtContext ctx) {
        parser.add(IMPORT, ctx.getStart());
    }

    @Override
    public void enterLambdef(Python3Parser.LambdefContext ctx) {
        parser.add(LAMBDA, ctx.getStart());
    }

    @Override
    public void enterTry_stmt(Python3Parser.Try_stmtContext ctx) {
        parser.add(TRY_BEGIN, ctx.getStart());
    }

    @Override
    public void enterBreak_stmt(Python3Parser.Break_stmtContext ctx) {
        parser.add(BREAK, ctx.getStart());
    }

    @Override
    public void enterTestlist_comp(Python3Parser.Testlist_compContext ctx) {
        if (ctx.getText().contains(",")) {
            parser.add(ARRAY, ctx.getStart());
        }
    }

    @Override
    public void enterIf_stmt(Python3Parser.If_stmtContext ctx) {
        parser.add(IF_BEGIN, ctx.getStart());
    }

    @Override
    public void exitIf_stmt(Python3Parser.If_stmtContext ctx) {
        parser.addEnd(IF_END, ctx.getStart());
    }

    @Override
    public void enterWith_stmt(Python3Parser.With_stmtContext ctx) {
        parser.add(WITH_BEGIN, ctx.getStart());
    }

    @Override
    public void exitWith_stmt(Python3Parser.With_stmtContext ctx) {
        parser.addEnd(WITH_END, ctx.getStart());
    }

    @Override
    public void enterClassdef(Python3Parser.ClassdefContext ctx) {
        parser.add(CLASS_BEGIN, ctx.getStart());
    }

    @Override
    public void exitClassdef(Python3Parser.ClassdefContext ctx) {
        parser.addEnd(CLASS_END, ctx.getStart());
    }

    @Override
    public void enterTrailer(Python3Parser.TrailerContext ctx) {
        if (ctx.getText().charAt(0) == '(') {
            parser.add(APPLY, ctx.getStart());
        } else {
            parser.add(ARRAY, ctx.getStart());
        }
    }

    @Override
    public void enterFuncdef(Python3Parser.FuncdefContext ctx) {
        parser.add(METHOD_BEGIN, ctx.getStart());
    }

    @Override
    public void exitFuncdef(Python3Parser.FuncdefContext ctx) {
        parser.addEnd(METHOD_END, ctx.getStart());
    }

    @Override
    public void enterAugassign(Python3Parser.AugassignContext ctx) {
        parser.add(ASSIGN, ctx.getStart());
    }

    @Override
    public void enterYield_stmt(Python3Parser.Yield_stmtContext ctx) {
        parser.add(YIELD, ctx.getStart());
    }

    @Override
    public void enterContinue_stmt(Python3Parser.Continue_stmtContext ctx) {
        parser.add(CONTINUE, ctx.getStart());
    }

    @Override
    public void enterFor_stmt(Python3Parser.For_stmtContext ctx) {
        parser.add(FOR_BEGIN, ctx.getStart());
    }

    @Override
    public void exitFor_stmt(Python3Parser.For_stmtContext ctx) {
        parser.addEnd(FOR_END, ctx.getStart());
    }

    @Override
    public void enterDel_stmt(Python3Parser.Del_stmtContext ctx) {
        parser.add(DEL, ctx.getStart());
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        if (node.getText().equals("=")) {
            parser.add(ASSIGN, node.getSymbol());
        } else if (node.getText().equals("finally")) {
            parser.add(FINALLY, node.getSymbol());
        }
    }
}
