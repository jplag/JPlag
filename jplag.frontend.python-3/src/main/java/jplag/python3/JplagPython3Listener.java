package jplag.python3;

import jplag.python3.grammar.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class JplagPython3Listener implements Python3Listener, Python3TokenConstants {

    private jplag.python3.Parser jplagParser;

    public JplagPython3Listener(jplag.python3.Parser jplag) {
        jplagParser = jplag;
    }

    @Override
    public void enterTestlist(@NotNull Python3Parser.TestlistContext ctx) {
    }

    @Override
    public void exitTestlist(@NotNull Python3Parser.TestlistContext ctx) {
    }

    @Override
    public void enterAssert_stmt(@NotNull Python3Parser.Assert_stmtContext ctx) {
        jplagParser.add(ASSERT, ctx.getStart());
    }

    @Override
    public void exitAssert_stmt(@NotNull Python3Parser.Assert_stmtContext ctx) {
    }

    @Override
    public void enterArgument(@NotNull Python3Parser.ArgumentContext ctx) {
    }

    @Override
    public void exitArgument(@NotNull Python3Parser.ArgumentContext ctx) {
    }

    @Override
    public void enterNot_test(@NotNull Python3Parser.Not_testContext ctx) {
    }

    @Override
    public void exitNot_test(@NotNull Python3Parser.Not_testContext ctx) {
    }

    @Override
    public void enterFile_input(@NotNull Python3Parser.File_inputContext ctx) {
    }

    @Override
    public void exitFile_input(@NotNull Python3Parser.File_inputContext ctx) {
    }

    @Override
    public void enterXor_expr(@NotNull Python3Parser.Xor_exprContext ctx) {
    }

    @Override
    public void exitXor_expr(@NotNull Python3Parser.Xor_exprContext ctx) {
    }

    @Override
    public void enterImport_from(@NotNull Python3Parser.Import_fromContext ctx) {
    }

    @Override
    public void exitImport_from(@NotNull Python3Parser.Import_fromContext ctx) {
    }

    @Override
    public void enterSingle_input(@NotNull Python3Parser.Single_inputContext ctx) {
    }

    @Override
    public void exitSingle_input(@NotNull Python3Parser.Single_inputContext ctx) {
    }

    @Override
    public void enterDecorated(@NotNull Python3Parser.DecoratedContext ctx) {
        jplagParser.add(DEC_BEGIN, ctx.getStart());
    }

    @Override
    public void exitDecorated(@NotNull Python3Parser.DecoratedContext ctx) {
        jplagParser.addEnd(DEC_END, ctx.getStart());
    }

    @Override
    public void enterWith_item(@NotNull Python3Parser.With_itemContext ctx) {
    }

    @Override
    public void exitWith_item(@NotNull Python3Parser.With_itemContext ctx) {
    }

    @Override
    public void enterRaise_stmt(@NotNull Python3Parser.Raise_stmtContext ctx) {
        jplagParser.add(RAISE, ctx.getStart());
    }

    @Override
    public void exitRaise_stmt(@NotNull Python3Parser.Raise_stmtContext ctx) {
    }

    @Override
    public void enterImport_as_name(@NotNull Python3Parser.Import_as_nameContext ctx) {
    }

    @Override
    public void exitImport_as_name(@NotNull Python3Parser.Import_as_nameContext ctx) {
    }

    @Override
    public void enterExcept_clause(@NotNull Python3Parser.Except_clauseContext ctx) {
        jplagParser.add(EXCEPT_BEGIN, ctx.getStart());
    }

    @Override
    public void exitExcept_clause(@NotNull Python3Parser.Except_clauseContext ctx) {
        jplagParser.addEnd(EXCEPT_END, ctx.getStart());
    }

    @Override
    public void enterCompound_stmt(@NotNull Python3Parser.Compound_stmtContext ctx) {
    }

    @Override
    public void exitCompound_stmt(@NotNull Python3Parser.Compound_stmtContext ctx) {
    }

    @Override
    public void enterAnd_expr(@NotNull Python3Parser.And_exprContext ctx) {
    }

    @Override
    public void exitAnd_expr(@NotNull Python3Parser.And_exprContext ctx) {
    }

    @Override
    public void enterLambdef_nocond(@NotNull Python3Parser.Lambdef_nocondContext ctx) {
    }

    @Override
    public void exitLambdef_nocond(@NotNull Python3Parser.Lambdef_nocondContext ctx) {
    }

    @Override
    public void enterDictorsetmaker(@NotNull Python3Parser.DictorsetmakerContext ctx) {
        jplagParser.add(ARRAY, ctx.getStart());
    }

    @Override
    public void exitDictorsetmaker(@NotNull Python3Parser.DictorsetmakerContext ctx) {
    }

    @Override
    public void enterReturn_stmt(@NotNull Python3Parser.Return_stmtContext ctx) {
        jplagParser.add(RETURN, ctx.getStart());
    }

    @Override
    public void exitReturn_stmt(@NotNull Python3Parser.Return_stmtContext ctx) {
    }

    @Override
    public void enterDotted_name(@NotNull Python3Parser.Dotted_nameContext ctx) {
    }

    @Override
    public void exitDotted_name(@NotNull Python3Parser.Dotted_nameContext ctx) {
    }

    @Override
    public void enterFlow_stmt(@NotNull Python3Parser.Flow_stmtContext ctx) {
    }

    @Override
    public void exitFlow_stmt(@NotNull Python3Parser.Flow_stmtContext ctx) {
    }

    @Override
    public void enterWhile_stmt(@NotNull Python3Parser.While_stmtContext ctx) {
        jplagParser.add(WHILE_BEGIN, ctx.getStart());
    }

    @Override
    public void exitWhile_stmt(@NotNull Python3Parser.While_stmtContext ctx) {
        jplagParser.addEnd(WHILE_END, ctx.getStart());
    }

    @Override
    public void enterOr_test(@NotNull Python3Parser.Or_testContext ctx) {
    }

    @Override
    public void exitOr_test(@NotNull Python3Parser.Or_testContext ctx) {
    }

    @Override
    public void enterComparison(@NotNull Python3Parser.ComparisonContext ctx) {
    }

    @Override
    public void exitComparison(@NotNull Python3Parser.ComparisonContext ctx) {
    }

    @Override
    public void enterTest(@NotNull Python3Parser.TestContext ctx) {
    }

    @Override
    public void exitTest(@NotNull Python3Parser.TestContext ctx) {
    }

    @Override
    public void enterSubscript(@NotNull Python3Parser.SubscriptContext ctx) {
    }

    @Override
    public void exitSubscript(@NotNull Python3Parser.SubscriptContext ctx) {
    }

    @Override
    public void enterComp_for(@NotNull Python3Parser.Comp_forContext ctx) {
    }

    @Override
    public void exitComp_for(@NotNull Python3Parser.Comp_forContext ctx) {
    }

    @Override
    public void enterYield_arg(@NotNull Python3Parser.Yield_argContext ctx) {
        jplagParser.add(YIELD, ctx.getStart());
    }

    @Override
    public void exitYield_arg(@NotNull Python3Parser.Yield_argContext ctx) {
    }

    @Override
    public void enterYield_expr(@NotNull Python3Parser.Yield_exprContext ctx) {
    }

    @Override
    public void exitYield_expr(@NotNull Python3Parser.Yield_exprContext ctx) {
    }

    @Override
    public void enterImport_stmt(@NotNull Python3Parser.Import_stmtContext ctx) {
        jplagParser.add(IMPORT, ctx.getStart());
    }

    @Override
    public void exitImport_stmt(@NotNull Python3Parser.Import_stmtContext ctx) {
    }

    @Override
    public void enterShift_expr(@NotNull Python3Parser.Shift_exprContext ctx) {
    }

    @Override
    public void exitShift_expr(@NotNull Python3Parser.Shift_exprContext ctx) {
    }

    @Override
    public void enterLambdef(@NotNull Python3Parser.LambdefContext ctx) {
        jplagParser.add(LAMBDA, ctx.getStart());
    }

    @Override
    public void exitLambdef(@NotNull Python3Parser.LambdefContext ctx) {
    }

    @Override
    public void enterAnd_test(@NotNull Python3Parser.And_testContext ctx) {
    }

    @Override
    public void exitAnd_test(@NotNull Python3Parser.And_testContext ctx) {
    }

    @Override
    public void enterGlobal_stmt(@NotNull Python3Parser.Global_stmtContext ctx) {
    }

    @Override
    public void exitGlobal_stmt(@NotNull Python3Parser.Global_stmtContext ctx) {
    }

    @Override
    public void enterImport_as_names(@NotNull Python3Parser.Import_as_namesContext ctx) {
    }

    @Override
    public void exitImport_as_names(@NotNull Python3Parser.Import_as_namesContext ctx) {
    }

    @Override
    public void enterDecorators(@NotNull Python3Parser.DecoratorsContext ctx) {
    }

    @Override
    public void exitDecorators(@NotNull Python3Parser.DecoratorsContext ctx) {
    }

    @Override
    public void enterTry_stmt(@NotNull Python3Parser.Try_stmtContext ctx) {
        jplagParser.add(TRY_BEGIN, ctx.getStart());
    }

    @Override
    public void exitTry_stmt(@NotNull Python3Parser.Try_stmtContext ctx) {
    }

    @Override
    public void enterComp_op(@NotNull Python3Parser.Comp_opContext ctx) {
    }

    @Override
    public void exitComp_op(@NotNull Python3Parser.Comp_opContext ctx) {
    }

    @Override
    public void enterStar_expr(@NotNull Python3Parser.Star_exprContext ctx) {
    }

    @Override
    public void exitStar_expr(@NotNull Python3Parser.Star_exprContext ctx) {
    }

    @Override
    public void enterBreak_stmt(@NotNull Python3Parser.Break_stmtContext ctx) {
        jplagParser.add(BREAK, ctx.getStart());
    }

    @Override
    public void exitBreak_stmt(@NotNull Python3Parser.Break_stmtContext ctx) {
    }

    @Override
    public void enterParameters(@NotNull Python3Parser.ParametersContext ctx) {
    }

    @Override
    public void exitParameters(@NotNull Python3Parser.ParametersContext ctx) {
    }

    @Override
    public void enterDecorator(@NotNull Python3Parser.DecoratorContext ctx) {
    }

    @Override
    public void exitDecorator(@NotNull Python3Parser.DecoratorContext ctx) {
    }

    @Override
    public void enterTfpdef(@NotNull Python3Parser.TfpdefContext ctx) {
    }

    @Override
    public void exitTfpdef(@NotNull Python3Parser.TfpdefContext ctx) {
    }

    @Override
    public void enterTestlist_comp(@NotNull Python3Parser.Testlist_compContext ctx) {
        if (ctx.getText().contains(",")) {
            jplagParser.add(ARRAY, ctx.getStart());
        }
    }

    @Override
    public void exitTestlist_comp(@NotNull Python3Parser.Testlist_compContext ctx) {
    }

    @Override
    public void enterIf_stmt(@NotNull Python3Parser.If_stmtContext ctx) {
        jplagParser.add(IF_BEGIN, ctx.getStart());
    }

    @Override
    public void exitIf_stmt(@NotNull Python3Parser.If_stmtContext ctx) {
        jplagParser.addEnd(IF_END, ctx.getStart());
    }

    @Override
    public void enterWith_stmt(@NotNull Python3Parser.With_stmtContext ctx) {
        jplagParser.add(WITH_BEGIN, ctx.getStart());
    }

    @Override
    public void exitWith_stmt(@NotNull Python3Parser.With_stmtContext ctx) {
        jplagParser.addEnd(WITH_END, ctx.getStart());
    }

    @Override
    public void enterClassdef(@NotNull Python3Parser.ClassdefContext ctx) {
        jplagParser.add(CLASS_BEGIN, ctx.getStart());
    }

    @Override
    public void exitClassdef(@NotNull Python3Parser.ClassdefContext ctx) {
        jplagParser.addEnd(CLASS_END, ctx.getStart());
    }

    @Override
    public void enterExprlist(@NotNull Python3Parser.ExprlistContext ctx) {
    }

    @Override
    public void exitExprlist(@NotNull Python3Parser.ExprlistContext ctx) {
    }

    @Override
    public void enterSmall_stmt(@NotNull Python3Parser.Small_stmtContext ctx) {
    }

    @Override
    public void exitSmall_stmt(@NotNull Python3Parser.Small_stmtContext ctx) {
    }

    @Override
    public void enterTrailer(@NotNull Python3Parser.TrailerContext ctx) {
        if (ctx.getText().charAt(0)=='(') {
            jplagParser.add(APPLY, ctx.getStart());
        } else {
            jplagParser.add(ARRAY, ctx.getStart());
        }
    }

    @Override
    public void exitTrailer(@NotNull Python3Parser.TrailerContext ctx) {
    }

    @Override
    public void enterDotted_as_names(@NotNull Python3Parser.Dotted_as_namesContext ctx) {
    }

    @Override
    public void exitDotted_as_names(@NotNull Python3Parser.Dotted_as_namesContext ctx) {
    }

    @Override
    public void enterArith_expr(@NotNull Python3Parser.Arith_exprContext ctx) {
    }

    @Override
    public void exitArith_expr(@NotNull Python3Parser.Arith_exprContext ctx) {
    }

    @Override
    public void enterArglist(@NotNull Python3Parser.ArglistContext ctx) {
    }

    @Override
    public void exitArglist(@NotNull Python3Parser.ArglistContext ctx) {
    }

    @Override
    public void enterSimple_stmt(@NotNull Python3Parser.Simple_stmtContext ctx) {
    }

    @Override
    public void exitSimple_stmt(@NotNull Python3Parser.Simple_stmtContext ctx) {
    }

    @Override
    public void enterTypedargslist(@NotNull Python3Parser.TypedargslistContext ctx) {
    }

    @Override
    public void exitTypedargslist(@NotNull Python3Parser.TypedargslistContext ctx) {
    }

    @Override
    public void enterExpr(@NotNull Python3Parser.ExprContext ctx) {
    }

    @Override
    public void exitExpr(@NotNull Python3Parser.ExprContext ctx) {
    }

    @Override
    public void enterTerm(@NotNull Python3Parser.TermContext ctx) {
    }

    @Override
    public void exitTerm(@NotNull Python3Parser.TermContext ctx) {
    }

    @Override
    public void enterPower(@NotNull Python3Parser.PowerContext ctx) {
    }

    @Override
    public void exitPower(@NotNull Python3Parser.PowerContext ctx) {
    }

    @Override
    public void enterDotted_as_name(@NotNull Python3Parser.Dotted_as_nameContext ctx) {
    }

    @Override
    public void exitDotted_as_name(@NotNull Python3Parser.Dotted_as_nameContext ctx) {
    }

    @Override
    public void enterFactor(@NotNull Python3Parser.FactorContext ctx) {
    }

    @Override
    public void exitFactor(@NotNull Python3Parser.FactorContext ctx) {
    }

    @Override
    public void enterSliceop(@NotNull Python3Parser.SliceopContext ctx) {
    }

    @Override
    public void exitSliceop(@NotNull Python3Parser.SliceopContext ctx) {
    }

    @Override
    public void enterFuncdef(@NotNull Python3Parser.FuncdefContext ctx) {
        jplagParser.add(METHOD_BEGIN, ctx.getStart());
    }

    @Override
    public void exitFuncdef(@NotNull Python3Parser.FuncdefContext ctx) {
        jplagParser.addEnd(METHOD_END, ctx.getStart());
    }

    @Override
    public void enterSubscriptlist(@NotNull Python3Parser.SubscriptlistContext ctx) {
    }

    @Override
    public void exitSubscriptlist(@NotNull Python3Parser.SubscriptlistContext ctx) {
    }

    @Override
    public void enterTest_nocond(@NotNull Python3Parser.Test_nocondContext ctx) {
    }

    @Override
    public void exitTest_nocond(@NotNull Python3Parser.Test_nocondContext ctx) {
    }

    @Override
    public void enterComp_iter(@NotNull Python3Parser.Comp_iterContext ctx) {
    }

    @Override
    public void exitComp_iter(@NotNull Python3Parser.Comp_iterContext ctx) {
    }

    @Override
    public void enterNonlocal_stmt(@NotNull Python3Parser.Nonlocal_stmtContext ctx) {
    }

    @Override
    public void exitNonlocal_stmt(@NotNull Python3Parser.Nonlocal_stmtContext ctx) {
    }

    @Override
    public void enterEval_input(@NotNull Python3Parser.Eval_inputContext ctx) {
    }

    @Override
    public void exitEval_input(@NotNull Python3Parser.Eval_inputContext ctx) {
    }

    @Override
    public void enterVfpdef(@NotNull Python3Parser.VfpdefContext ctx) {
    }

    @Override
    public void exitVfpdef(@NotNull Python3Parser.VfpdefContext ctx) {
    }

    @Override
    public void enterImport_name(@NotNull Python3Parser.Import_nameContext ctx) {
    }

    @Override
    public void exitImport_name(@NotNull Python3Parser.Import_nameContext ctx) {
    }

    @Override
    public void enterComp_if(@NotNull Python3Parser.Comp_ifContext ctx) {
    }

    @Override
    public void exitComp_if(@NotNull Python3Parser.Comp_ifContext ctx) {
    }

    @Override
    public void enterAugassign(@NotNull Python3Parser.AugassignContext ctx) {
        jplagParser.add(ASSIGN, ctx.getStart());
    }

    @Override
    public void exitAugassign(@NotNull Python3Parser.AugassignContext ctx) {
    }

    @Override
    public void enterPass_stmt(@NotNull Python3Parser.Pass_stmtContext ctx) {
    }

    @Override
    public void exitPass_stmt(@NotNull Python3Parser.Pass_stmtContext ctx) {
    }

    @Override
    public void enterExpr_stmt(@NotNull Python3Parser.Expr_stmtContext ctx) {
    }

    @Override
    public void exitExpr_stmt(@NotNull Python3Parser.Expr_stmtContext ctx) {
    }

    @Override
    public void enterYield_stmt(@NotNull Python3Parser.Yield_stmtContext ctx) {
        jplagParser.add(YIELD, ctx.getStart());
    }

    @Override
    public void exitYield_stmt(@NotNull Python3Parser.Yield_stmtContext ctx) {
    }

    @Override
    public void enterSuite(@NotNull Python3Parser.SuiteContext ctx) {
    }

    @Override
    public void exitSuite(@NotNull Python3Parser.SuiteContext ctx) {
    }

    @Override
    public void enterContinue_stmt(@NotNull Python3Parser.Continue_stmtContext ctx) {
        jplagParser.add(CONTINUE, ctx.getStart());
    }

    @Override
    public void exitContinue_stmt(@NotNull Python3Parser.Continue_stmtContext ctx) {
    }

    @Override
    public void enterTestlist_star_expr(@NotNull Python3Parser.Testlist_star_exprContext ctx) {
    }

    @Override
    public void exitTestlist_star_expr(@NotNull Python3Parser.Testlist_star_exprContext ctx) {
    }

    @Override
    public void enterVarargslist(@NotNull Python3Parser.VarargslistContext ctx) {
    }

    @Override
    public void exitVarargslist(@NotNull Python3Parser.VarargslistContext ctx) {
    }

    @Override
    public void enterFor_stmt(@NotNull Python3Parser.For_stmtContext ctx) {
        jplagParser.add(FOR_BEGIN, ctx.getStart());
    }

    @Override
    public void exitFor_stmt(@NotNull Python3Parser.For_stmtContext ctx) {
        jplagParser.addEnd(FOR_END, ctx.getStart());
    }

    @Override
    public void enterDel_stmt(@NotNull Python3Parser.Del_stmtContext ctx) {
        jplagParser.add(DEL, ctx.getStart());
    }

    @Override
    public void exitDel_stmt(@NotNull Python3Parser.Del_stmtContext ctx) {
    }

    @Override
    public void enterAtom(@NotNull Python3Parser.AtomContext ctx) {
    }

    @Override
    public void exitAtom(@NotNull Python3Parser.AtomContext ctx) {
    }

    @Override
    public void enterStmt(@NotNull Python3Parser.StmtContext ctx) {
    }

    @Override
    public void exitStmt(@NotNull Python3Parser.StmtContext ctx) {
    }

    @Override
    public void enterEveryRule(@NotNull ParserRuleContext ctx) {
    }

    @Override
    public void exitEveryRule(@NotNull ParserRuleContext ctx) {
    }

    @Override
    public void visitTerminal(@NotNull TerminalNode node) {
        if (node.getText().equals("=")) {
            jplagParser.add(ASSIGN, node.getSymbol());
        } else if (node.getText().equals("finally")) {
            jplagParser.add(FINALLY, node.getSymbol());
        }
    }

    @Override
    public void visitErrorNode(@NotNull ErrorNode node) {
    }

	@Override
	public void enterAnnassign(Python3Parser.AnnassignContext ctx) {
	}

	@Override
	public void exitAnnassign(Python3Parser.AnnassignContext ctx) {
	}

	@Override
	public void enterEncoding_decl(Python3Parser.Encoding_declContext ctx) {
	}

	@Override
	public void exitEncoding_decl(Python3Parser.Encoding_declContext ctx) {
	}

	@Override
	public void enterAtom_expr(Python3Parser.Atom_exprContext ctx) {
	}

	@Override
	public void exitAtom_expr(Python3Parser.Atom_exprContext ctx) {
	}

	@Override
	public void enterAsync_funcdef(Python3Parser.Async_funcdefContext ctx) {
	}

	@Override
	public void exitAsync_funcdef(Python3Parser.Async_funcdefContext ctx) {
	}

	@Override
	public void enterAsync_stmt(Python3Parser.Async_stmtContext ctx) {
	}

	@Override
	public void exitAsync_stmt(Python3Parser.Async_stmtContext ctx) {
	}
}
