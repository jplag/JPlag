package de.jplag.rust;

import static de.jplag.rust.RustTokenConstants.*;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.*;

import de.jplag.rust.grammar.RustParser;
import de.jplag.rust.grammar.RustParserBaseListener;

public class JplagRustListener extends RustParserBaseListener implements ParseTreeListener {

    private final RustParserAdapter parserAdapter;
    private final Deque<RustBlockContext> blockContexts;

    public JplagRustListener(RustParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
        this.blockContexts = new LinkedList<>();
    }

    private void transformToken(int targetType, Token token) {
        parserAdapter.addToken(targetType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    private void transformToken(int targetType, Token start, Token end) {
        parserAdapter.addToken(targetType, start.getLine(), start.getCharPositionInLine() + 1, end.getStopIndex() - start.getStartIndex() + 1);
    }

    private void enterBlockContext(RustBlockContext context) {
        blockContexts.push(context);
    }

    private void expectAndLeave(RustBlockContext... contexts) {
        RustBlockContext topContext = blockContexts.pop();
        assert Arrays.stream(contexts).anyMatch(context -> context == topContext);
    }

    @Override
    public void enterInnerAttribute(RustParser.InnerAttributeContext ctx) {
        transformToken(INNER_ATTRIBUTE, ctx.getStart(), ctx.getStop());
        super.enterInnerAttribute(ctx);
    }

    @Override
    public void enterOuterAttribute(RustParser.OuterAttributeContext ctx) {
        transformToken(OUTER_ATTRIBUTE, ctx.getStart(), ctx.getStop());
        super.enterOuterAttribute(ctx);
    }

    @Override
    public void enterUseDeclaration(RustParser.UseDeclarationContext ctx) {
        transformToken(USE_DECLARATION, ctx.getStart());
        super.enterUseDeclaration(ctx);
    }

    @Override
    public void enterUseTree(RustParser.UseTreeContext ctx) {
        enterBlockContext(RustBlockContext.USE_TREE);
        super.enterUseTree(ctx);
    }

    @Override
    public void exitUseTree(RustParser.UseTreeContext ctx) {
        expectAndLeave(RustBlockContext.USE_TREE);
        super.exitUseTree(ctx);
    }

    @Override
    public void enterAttr(RustParser.AttrContext ctx) {
        enterBlockContext(RustBlockContext.ATTRIBUTE_TREE);
        super.enterAttr(ctx);
    }

    @Override
    public void exitAttr(RustParser.AttrContext ctx) {
        expectAndLeave(RustBlockContext.ATTRIBUTE_TREE);
        super.exitAttr(ctx);
    }

    @Override
    public void enterSimplePath(RustParser.SimplePathContext ctx) {
        if (ctx.parent instanceof RustParser.UseTreeContext) {
            if (ctx.parent.getChildCount() > 1 && ctx.parent.getChild(1).getText().equals("::")) {
                // Not a leaf
                return;
            }

            transformToken(USE_ITEM, ctx.getStart(), ctx.getStop());
        }
        super.enterSimplePath(ctx);
    }

    @Override
    public void enterModule(RustParser.ModuleContext ctx) {
        transformToken(MODULE, ctx.getStart());
        enterBlockContext(RustBlockContext.MODULE_BODY);
        super.enterModule(ctx);
    }

    @Override
    public void enterStruct_(RustParser.Struct_Context ctx) {
        transformToken(STRUCT, ctx.getStart());
        enterBlockContext(RustBlockContext.STRUCT_BODY);
        super.enterStruct_(ctx);
    }

    @Override
    public void exitStruct_(RustParser.Struct_Context ctx) {
        expectAndLeave(RustBlockContext.STRUCT_BODY);
        super.exitStruct_(ctx);
    }

    @Override
    public void enterUnion_(RustParser.Union_Context ctx) {
        transformToken(UNION, ctx.getStart());
        enterBlockContext(RustBlockContext.UNION_BODY);
        super.enterUnion_(ctx);
    }

    @Override
    public void exitUnion_(RustParser.Union_Context ctx) {
        expectAndLeave(RustBlockContext.UNION_BODY);
        super.exitUnion_(ctx);
    }

    @Override
    public void enterTrait_(RustParser.Trait_Context ctx) {
        transformToken(TRAIT, ctx.getStart());
        enterBlockContext(RustBlockContext.TRAIT_BODY);
        super.enterTrait_(ctx);
    }

    @Override
    public void exitTrait_(RustParser.Trait_Context ctx) {
        expectAndLeave(RustBlockContext.TRAIT_BODY);
        super.exitTrait_(ctx);
    }

    @Override
    public void enterImplementation(RustParser.ImplementationContext ctx) {
        enterBlockContext(RustBlockContext.IMPL_BODY);
        super.enterImplementation(ctx);
    }

    @Override
    public void enterEnumeration(RustParser.EnumerationContext ctx) {
        transformToken(ENUM, ctx.getStart());
        enterBlockContext(RustBlockContext.ENUM_BODY);
        super.enterEnumeration(ctx);
    }

    @Override
    public void exitEnumeration(RustParser.EnumerationContext ctx) {
        expectAndLeave(RustBlockContext.ENUM_BODY);
        super.exitEnumeration(ctx);
    }

    @Override
    public void enterMacroRulesDefinition(RustParser.MacroRulesDefinitionContext ctx) {
        transformToken(MACRO_RULES_DEFINITION, ctx.getStart());
        enterBlockContext(RustBlockContext.MACRO_RULES_DEFINITION_BODY);
        super.enterMacroRulesDefinition(ctx);
    }

    @Override
    public void exitMacroRulesDefinition(RustParser.MacroRulesDefinitionContext ctx) {
        expectAndLeave(RustBlockContext.MACRO_RULES_DEFINITION_BODY);
        super.exitMacroRulesDefinition(ctx);
    }

    @Override
    public void enterMacroRule(RustParser.MacroRuleContext ctx) {
        transformToken(MACRO_RULE, ctx.getStart());
        enterBlockContext(RustBlockContext.MACRO_RULE_BODY);
        super.enterMacroRule(ctx);
    }

    @Override
    public void exitMacroRule(RustParser.MacroRuleContext ctx) {
        expectAndLeave(RustBlockContext.MACRO_RULE_BODY);
        super.exitMacroRule(ctx);
    }

    @Override
    public void enterMacroInvocationSemi(RustParser.MacroInvocationSemiContext ctx) {
        transformToken(MACRO_INVOCATION, ctx.getStart());
        enterBlockContext(RustBlockContext.MACRO_INVOCATION_BODY);
        super.enterMacroInvocationSemi(ctx);
    }

    @Override
    public void exitMacroInvocationSemi(RustParser.MacroInvocationSemiContext ctx) {
        expectAndLeave(RustBlockContext.MACRO_INVOCATION_BODY);
        super.exitMacroInvocationSemi(ctx);
    }

    @Override
    public void enterExternBlock(RustParser.ExternBlockContext ctx) {
        enterBlockContext(RustBlockContext.EXTERN_BLOCK);
        super.enterExternBlock(ctx);
    }

    @Override
    public void exitExternBlock(RustParser.ExternBlockContext ctx) {
        expectAndLeave(RustBlockContext.EXTERN_BLOCK);
        super.exitExternBlock(ctx);
    }

    @Override
    public void enterFunction_(RustParser.Function_Context ctx) {
        Token fn = ((TerminalNodeImpl) ctx.getChild(1)).getSymbol();
        transformToken(FUNCTION, fn);
        enterBlockContext(RustBlockContext.FUNCTION_BODY);
        super.enterFunction_(ctx);
    }

    @Override
    public void exitFunction_(RustParser.Function_Context ctx) {
        expectAndLeave(RustBlockContext.FUNCTION_BODY);
        super.exitFunction_(ctx);
    }

    @Override
    public void enterSelfParam(RustParser.SelfParamContext ctx) {
        transformToken(FUNCTION_PARAMETER, ctx.getStart(), ctx.getStop());
        super.enterSelfParam(ctx);
    }

    @Override
    public void enterFunctionParam(RustParser.FunctionParamContext ctx) {
        transformToken(FUNCTION_PARAMETER, ctx.getStart(), ctx.getStop());
        super.enterFunctionParam(ctx);
    }

    @Override
    public void enterGenericParam(RustParser.GenericParamContext ctx) {
        transformToken(TYPE_PARAMETER, ctx.getStart(), ctx.getStop());
        super.enterGenericParam(ctx);
    }

    @Override
    public void enterExpressionWithBlock(RustParser.ExpressionWithBlockContext ctx) {
        enterBlockContext(RustBlockContext.INNER_BLOCK);
        super.enterExpressionWithBlock(ctx);
    }

    @Override
    public void exitExpressionWithBlock(RustParser.ExpressionWithBlockContext ctx) {
        expectAndLeave(RustBlockContext.INNER_BLOCK);
        super.exitExpressionWithBlock(ctx);
    }

    @Override
    public void enterCompoundAssignOperator(RustParser.CompoundAssignOperatorContext ctx) {
        transformToken(ASSIGNMENT, ctx.getStart());
        super.enterCompoundAssignOperator(ctx);
    }

    @Override
    public void enterConstantItem(RustParser.ConstantItemContext ctx) {
        transformToken(VARIABLE_DECLARATION, ctx.getStart());
        super.enterConstantItem(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        final Token token = node.getSymbol();
        switch (node.getText()) {
            case "*" -> {
                if (node.getParent() instanceof RustParser.UseTreeContext) {
                    transformToken(USE_ITEM, token);
                }
            }
            case "let" -> transformToken(VARIABLE_DECLARATION, token);
            case "=" -> transformToken(ASSIGNMENT, token);
            case "{" -> {
                int startType = getCurrentContext().getStartType();
                if (startType != NONE) {
                    transformToken(startType, token);
                }
            }
            case "}" -> {
                int endType = getCurrentContext().getEndType();
                if (endType != NONE) {
                    transformToken(endType, token);
                }
            }
            default -> {
                // do nothing
            }
        }
    }

    private RustBlockContext getCurrentContext() {
        return blockContexts.peek();
    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {

    }

    private RustParser.ExpressionContext getAttibutedSubTree(RustParser.ExpressionContext context) {
        RustParser.ExpressionContext tree = context;
        while (tree.getChild(0)instanceof RustParser.AttributedExpressionContext attrExpr) {
            tree = attrExpr.children.stream().dropWhile(subTree -> subTree instanceof RustParser.OuterAttributeContext).findFirst()
                    .map(subTree -> (RustParser.ExpressionContext) subTree).get();
        }
        return tree;
    }

    private enum RustBlockContext {
        FUNCTION_BODY(FUNCTION_BODY_START, FUNCTION_BODY_END),
        STRUCT_BODY(STRUCT_BODY_BEGIN, STRUCT_BODY_END),
        IF_BODY(IF_BODY_START, IF_BODY_END),
        INNER_BLOCK(INNER_BLOCK_START, INNER_BLOCK_END),
        USE_TREE(NONE, NONE),
        ATTRIBUTE_TREE(NONE, NONE),

        TRAIT_BODY(TRAIT_BODY_START, TRAIT_BODY_END),
        ENUM_BODY(ENUM_BODY_START, ENUM_BODY_END),
        MACRO_RULES_DEFINITION_BODY(MACRO_RULES_DEFINITION_BODY_START, MACRO_RULES_DEFINITION_BODY_END),
        MACRO_RULE_BODY(MACRO_RULE_BODY_START, MACRO_RULE_BODY_END),
        MACRO_INVOCATION_BODY(MACRO_INVOCATION_BODY_START, MACRO_INVOCATION_BODY_END),
        IMPL_BODY(IMPL_BODY_START, IMPL_BODY_END),
        EXTERN_BLOCK(EXTERN_BLOCK_START, EXTERN_BLOCK_END),
        MODULE_BODY(MODULE_START, MODULE_END),
        UNION_BODY(UNION_BODY_START, UNION_BODY_END);

        private final int startType;
        private final int endType;

        <T extends ParserRuleContext> RustBlockContext(int startType, int endType) {
            this.startType = startType;
            this.endType = endType;
        }

        public int getStartType() {
            return startType;
        }

        public int getEndType() {
            return endType;
        }
    }
}
