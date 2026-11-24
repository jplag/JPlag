package de.jplag.scalaantlr;

import static de.jplag.scalaantlr.grammar.Scala3Parser.ASSIGN;
import static de.jplag.scalaantlr.grammar.Scala3Parser.CaseClauseContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.CatchesContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.DefDefContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.DefTypeParamContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.Def_Context;
import static de.jplag.scalaantlr.grammar.Scala3Parser.ELSE;
import static de.jplag.scalaantlr.grammar.Scala3Parser.Expr1Context;
import static de.jplag.scalaantlr.grammar.Scala3Parser.ExprContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.ExprInParensContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.ExtMethodContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.FINALLY;
import static de.jplag.scalaantlr.grammar.Scala3Parser.ForExprContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.Guard_Context;
import static de.jplag.scalaantlr.grammar.Scala3Parser.IF;
import static de.jplag.scalaantlr.grammar.Scala3Parser.IMPORT;
import static de.jplag.scalaantlr.grammar.Scala3Parser.InfixExprContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.MatchClauseContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.ModifierContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.PACKAGE;
import static de.jplag.scalaantlr.grammar.Scala3Parser.ParArgumentExprsContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.PatDefContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.RETURN;
import static de.jplag.scalaantlr.grammar.Scala3Parser.RefineDclContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.SimpleExprContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.THROW;
import static de.jplag.scalaantlr.grammar.Scala3Parser.TmplDefContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.YIELD;

import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.scalaantlr.grammar.Scala3Parser;
import de.jplag.scalaantlr.grammar.Scala3Parser.BlockContext;
import de.jplag.scalaantlr.grammar.Scala3Parser.ClassConstrContext;
import de.jplag.scalaantlr.grammar.Scala3Parser.ClsTypeParamContext;
import de.jplag.scalaantlr.grammar.Scala3Parser.ConstrExprContext;
import de.jplag.scalaantlr.grammar.Scala3Parser.IndentContext;
import de.jplag.scalaantlr.grammar.Scala3Parser.OutdentContext;
import de.jplag.scalaantlr.grammar.Scala3Parser.ParamContext;

/**
 * Contains the token creation rules for scala
 */
public class ScalaListener extends AbstractAntlrListener {
    /**
     * New instance
     */
    public ScalaListener() {
        registerControlBlocks();
        registerInstructions();
        registerStructure();
    }

    private void registerStructure() {
        visit(PACKAGE).map(ScalaTokenType.PACKAGE);
        visit(IMPORT).map(ScalaTokenType.IMPORT);

        visit(TmplDefContext.class, context -> context.CLASS() != null).map(ScalaTokenType.CLASS_BEGIN, ScalaTokenType.CLASS_END);
        visit(TmplDefContext.class, context -> context.OBJECT() != null).map(ScalaTokenType.OBJECT_BEGIN, ScalaTokenType.OBJECT_END);
        visit(TmplDefContext.class, context -> context.TRAIT() != null).map(ScalaTokenType.TRAIT_BEGIN, ScalaTokenType.TRAIT_END);

        visit(Def_Context.class, context -> context.TYPE() != null).map(ScalaTokenType.TYPE);
        visit(RefineDclContext.class, context -> context.TYPE() != null).map(ScalaTokenType.TYPE);
        visit(Def_Context.class, context -> context.VAR() != null || context.VAL() != null).map(ScalaTokenType.VARIABLE_DEFINITION);
        visit(RefineDclContext.class, context -> context.VAR() != null || context.VAL() != null).map(ScalaTokenType.VARIABLE_DEFINITION);

        visit(DefTypeParamContext.class).map(ScalaTokenType.TYPE_PARAMETER);
        visit(ClsTypeParamContext.class).map(ScalaTokenType.TYPE_PARAMETER);
        visit(ParamContext.class).map(ScalaTokenType.PARAMETER);

        visit(Def_Context.class, context -> context.DEF() != null && devIsMethod(context.defDef())).mapEnter(ScalaTokenType.METHOD_DEF);
        visit(ExtMethodContext.class, context -> context.DEF() != null && devIsMethod(context.defDef())).mapEnter(ScalaTokenType.METHOD_DEF);
        visit(ExprContext.class, context -> {
            if (context.getParent() instanceof DefDefContext parent) {
                return devIsMethod(parent) && parent.constrExpr() == null;
            }
            return false;
        }).map(ScalaTokenType.METHOD_BEGIN, ScalaTokenType.METHOD_END);
        visit(BlockContext.class, context -> {
            if (context.getParent() instanceof DefDefContext parent) {
                return devIsMethod(parent) && parent.constrExpr() == null;
            }
            return false;
        }).map(ScalaTokenType.METHOD_BEGIN, ScalaTokenType.METHOD_END);
        visit(ConstrExprContext.class).map(ScalaTokenType.CONSTRUCTOR_BEGIN, ScalaTokenType.CONSTRUCTOR_END);
        visit(ClassConstrContext.class, context -> context.clsParamClauses() != null && !context.clsParamClauses().clsParamClause().isEmpty())
                .map(ScalaTokenType.CONSTRUCTOR_BEGIN, ScalaTokenType.CONSTRUCTOR_END);
    }

    private void registerControlBlocks() {
        visit(Expr1Context.class, context -> context.WHILE() != null).mapEnter(ScalaTokenType.WHILE);
        visit(Expr1Context.class, context -> context.WHILE() != null).delegateContext(context -> context.expr(1)).map(ScalaTokenType.WHILE_BODY_BEGIN,
                ScalaTokenType.WHILE_BODY_END);

        visit(ForExprContext.class).mapEnter(ScalaTokenType.FOR);
        visit(YIELD).map(ScalaTokenType.YIELD);
        visit(ExprContext.class, context -> context.getParent() instanceof ForExprContext).map(ScalaTokenType.FOR_BODY_BEGIN,
                ScalaTokenType.FOR_BODY_END);
        visit(IndentContext.class, context -> context.getParent() instanceof ForExprContext).map(ScalaTokenType.FOR_BODY_BEGIN);
        visit(OutdentContext.class, context -> context.getParent() instanceof ForExprContext).map(ScalaTokenType.FOR_BODY_END);

        visit(Expr1Context.class, context -> context.TRY() != null).mapEnter(ScalaTokenType.TRY_BEGIN);
        visit(CatchesContext.class).map(ScalaTokenType.CATCH_BEGIN, ScalaTokenType.CATCH_END);
        visit(FINALLY).map(ScalaTokenType.FINALLY);

        visit(MatchClauseContext.class).map(ScalaTokenType.MATCH_BEGIN, ScalaTokenType.MATCH_END);
        visit(CaseClauseContext.class).map(ScalaTokenType.CASE_STATEMENT);
        visit(BlockContext.class, context -> context.getParent() instanceof CaseClauseContext).map(ScalaTokenType.CASE_BEGIN,
                ScalaTokenType.CASE_END);

        visit(ExprContext.class, context -> context.getParent() instanceof PatDefContext && context.funParams() != null)
                .map(ScalaTokenType.FUNCTION_BEGIN, ScalaTokenType.FUNCTION_END);

        visit(ExprContext.class, context -> {
            if (context.getParent() instanceof DefDefContext parent) {
                return !devIsMethod(parent) && parent.constrExpr() == null;
            }
            return false;
        }).map(ScalaTokenType.MACRO_BEGIN, ScalaTokenType.MACRO_END);
        visit(BlockContext.class, context -> {
            if (context.getParent() instanceof DefDefContext parent) {
                return !devIsMethod(parent) && parent.constrExpr() == null;
            }
            return false;
        }).map(ScalaTokenType.MACRO_BEGIN, ScalaTokenType.MACRO_END);
        visit(Def_Context.class, context -> context.DEF() != null && !devIsMethod(context.defDef())).mapEnter(ScalaTokenType.MACRO);
        visit(ExtMethodContext.class, context -> context.DEF() != null && !devIsMethod(context.defDef())).mapEnter(ScalaTokenType.MACRO);

        visit(IF, token -> token.getParent() instanceof Expr1Context).map(ScalaTokenType.IF);
        visit(ExprContext.class, context -> {
            if (context.getParent() instanceof Expr1Context exprContext) {
                return exprContext.IF() != null && exprContext.expr(1) == context;
            }
            return false;
        }).map(ScalaTokenType.IF_BEGIN, ScalaTokenType.IF_END);
        visit(ELSE).map(ScalaTokenType.ELSE);
        visit(Scala3Parser.ExprContext.class, context -> {
            if (context.getParent() instanceof Expr1Context exprContext) {
                return exprContext.ELSE() != null && exprContext.expr(2) == context;
            }
            return false;
        }).map(ScalaTokenType.ELSE_BEGIN, ScalaTokenType.ELSE_END);
    }

    private void registerInstructions() {
        visit(ASSIGN).map(ScalaTokenType.ASSIGN);

        visit(InfixExprContext.class, context -> context.id() != null).delegateContext(InfixExprContext::id).map(ScalaTokenType.APPLY);
        visit(SimpleExprContext.class, context -> context.argumentExprs() != null).delegateContext(SimpleExprContext::simpleExpr)
                .mapRange(ScalaTokenType.APPLY);
        visit(SimpleExprContext.class, context -> context.NEW() != null).map(ScalaTokenType.NEW_CREATION_BEGIN, ScalaTokenType.NEW_CREATION_END);
        visit(ExprInParensContext.class, context -> context.getParent().getParent() instanceof ParArgumentExprsContext)
                .mapRange(ScalaTokenType.ARGUMENT);

        visit(Scala3Parser.Type_Context.class, context -> context.getParent().getParent() instanceof Scala3Parser.TypeArgsContext)
                .map(ScalaTokenType.TYPE_ARGUMENT);

        visit(InfixExprContext.class, context -> context.id() != null).delegateContext(context -> context.infixExpr(0)).map(ScalaTokenType.ARGUMENT);
        visit(InfixExprContext.class, context -> context.id() != null).delegateContext(context -> context.infixExpr(1)).map(ScalaTokenType.ARGUMENT);

        visit(Guard_Context.class).map(ScalaTokenType.GUARD);

        visit(RETURN).map(ScalaTokenType.RETURN);
        visit(THROW).map(ScalaTokenType.THROW);
    }

    private boolean devIsMethod(DefDefContext context) {
        ParserRuleContext parent = context.getParent();
        if (parent instanceof Def_Context) {
            parent = parent.getParent();
        }
        ModifierContext modifier = parent.getChild(ModifierContext.class, 0);
        if (modifier == null) {
            return true;
        }
        return !(modifier.localModifier() != null && modifier.localModifier().INLINE() != null);
    }
}
