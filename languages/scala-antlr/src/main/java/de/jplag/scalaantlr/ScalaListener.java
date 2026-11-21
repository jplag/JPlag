package de.jplag.scalaantlr;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.scalaantlr.grammar.Scala3Parser;
import org.antlr.v4.runtime.ParserRuleContext;

import static de.jplag.scalaantlr.grammar.Scala3Parser.ASSIGN;
import static de.jplag.scalaantlr.grammar.Scala3Parser.BlockExprContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.CaseClauseContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.CatchesContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.DefDefContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.DefTermParamContext;
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
import static de.jplag.scalaantlr.grammar.Scala3Parser.TemplateContext;
import static de.jplag.scalaantlr.grammar.Scala3Parser.TmplDefContext;

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

        visit(TemplateContext.class).map(ScalaTokenType.CONSTRUCTOR_BEGIN, ScalaTokenType.CONSTRUCTOR_END);

        visit(Guard_Context.class).map(ScalaTokenType.GUARD);
    }

    private void registerControlBlocks() {
        visit(Expr1Context.class, context -> context.WHILE() != null).mapEnter(ScalaTokenType.WHILE);
        visit(Expr1Context.class, context -> context.WHILE() != null).delegateContext(context -> context.expr(1)).map(ScalaTokenType.WHILE_BODY_BEGIN,
                ScalaTokenType.WHILE_BODY_END);

        visit(ForExprContext.class).mapEnter(ScalaTokenType.FOR);
        visit(ForExprContext.class, context -> context.expr() != null && context.YIELD() != null).delegateContext(ForExprContext::expr)
                .mapExit(ScalaTokenType.YIELD);
        visit(ForExprContext.class, context -> context.expr() != null).delegateContext(ForExprContext::expr).map(ScalaTokenType.FOR_BODY_BEGIN,
                ScalaTokenType.FOR_BODY_END);
        visit(ForExprContext.class, context -> context.expr() == null).delegateContext(ForExprContext::indent).map(ScalaTokenType.FOR_BODY_BEGIN);
        visit(ForExprContext.class, context -> context.expr() == null && context.YIELD() != null).delegateContext(ForExprContext::outdent)
                .map(ScalaTokenType.YIELD);
        visit(ForExprContext.class, context -> context.expr() == null).delegateContext(ForExprContext::outdent).map(ScalaTokenType.FOR_BODY_END);

        visit(Expr1Context.class, context -> context.TRY() != null).mapEnter(ScalaTokenType.TRY_BEGIN);
        visit(CatchesContext.class).map(ScalaTokenType.CATCH_BEGIN, ScalaTokenType.CATCH_END);
        visit(FINALLY).map(ScalaTokenType.FINALLY);

        visit(MatchClauseContext.class).map(ScalaTokenType.MATCH_BEGIN, ScalaTokenType.MATCH_END);
        visit(CaseClauseContext.class).map(ScalaTokenType.CASE_STATEMENT);
        visit(CaseClauseContext.class).delegateContext(CaseClauseContext::block).map(ScalaTokenType.CASE_BEGIN, ScalaTokenType.CASE_END);

        visit(BlockExprContext.class, context -> getAncestor(context, ExprContext.class).getParent() instanceof PatDefContext)
                .map(ScalaTokenType.FUNCTION_BEGIN, ScalaTokenType.FUNCTION_END);

        visit(DefDefContext.class, this::devIsMethod).map(ScalaTokenType.METHOD_BEGIN, ScalaTokenType.METHOD_END);
        visit(Def_Context.class, context -> context.DEF() != null && devIsMethod(context.defDef())).mapEnter(ScalaTokenType.METHOD_DEF);
        visit(ExtMethodContext.class, context -> context.DEF() != null && devIsMethod(context.defDef())).mapEnter(ScalaTokenType.METHOD_DEF);

        visit(DefDefContext.class, context -> !devIsMethod(context)).map(ScalaTokenType.MACRO_BEGIN, ScalaTokenType.MACRO_END);
        visit(Def_Context.class, context -> context.DEF() != null && !devIsMethod(context.defDef())).mapEnter(ScalaTokenType.MACRO);
        visit(ExtMethodContext.class, context -> context.DEF() != null && !devIsMethod(context.defDef())).mapEnter(ScalaTokenType.MACRO);

        visit(DefTypeParamContext.class).map(ScalaTokenType.TYPE_PARAMETER);
        visit(DefTermParamContext.class).map(ScalaTokenType.PARAMETER);

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

        visit(InfixExprContext.class, context -> context.id() != null).delegateContext(context -> context.infixExpr(0)).map(ScalaTokenType.ARGUMENT);
        visit(InfixExprContext.class, context -> context.id() != null).delegateContext(context -> context.infixExpr(1)).map(ScalaTokenType.ARGUMENT);

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
