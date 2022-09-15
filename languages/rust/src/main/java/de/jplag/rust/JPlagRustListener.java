package de.jplag.rust;

import static de.jplag.rust.RustTokenType.APPLY;
import static de.jplag.rust.RustTokenType.ARGUMENT;
import static de.jplag.rust.RustTokenType.ARRAY_BODY_END;
import static de.jplag.rust.RustTokenType.ARRAY_BODY_START;
import static de.jplag.rust.RustTokenType.ARRAY_ELEMENT;
import static de.jplag.rust.RustTokenType.ASSIGNMENT;
import static de.jplag.rust.RustTokenType.BREAK;
import static de.jplag.rust.RustTokenType.CLOSURE;
import static de.jplag.rust.RustTokenType.CLOSURE_BODY_END;
import static de.jplag.rust.RustTokenType.CLOSURE_BODY_START;
import static de.jplag.rust.RustTokenType.ELSE_BODY_END;
import static de.jplag.rust.RustTokenType.ELSE_BODY_START;
import static de.jplag.rust.RustTokenType.ELSE_STATEMENT;
import static de.jplag.rust.RustTokenType.ENUM;
import static de.jplag.rust.RustTokenType.ENUM_BODY_END;
import static de.jplag.rust.RustTokenType.ENUM_BODY_START;
import static de.jplag.rust.RustTokenType.ENUM_ITEM;
import static de.jplag.rust.RustTokenType.EXTERN_BLOCK;
import static de.jplag.rust.RustTokenType.EXTERN_BLOCK_END;
import static de.jplag.rust.RustTokenType.EXTERN_BLOCK_START;
import static de.jplag.rust.RustTokenType.EXTERN_CRATE;
import static de.jplag.rust.RustTokenType.FOR_BODY_END;
import static de.jplag.rust.RustTokenType.FOR_BODY_START;
import static de.jplag.rust.RustTokenType.FOR_STATEMENT;
import static de.jplag.rust.RustTokenType.FUNCTION;
import static de.jplag.rust.RustTokenType.FUNCTION_BODY_END;
import static de.jplag.rust.RustTokenType.FUNCTION_BODY_START;
import static de.jplag.rust.RustTokenType.FUNCTION_PARAMETER;
import static de.jplag.rust.RustTokenType.IF_BODY_END;
import static de.jplag.rust.RustTokenType.IF_BODY_START;
import static de.jplag.rust.RustTokenType.IF_STATEMENT;
import static de.jplag.rust.RustTokenType.IMPLEMENTATION;
import static de.jplag.rust.RustTokenType.IMPLEMENTATION_BODY_END;
import static de.jplag.rust.RustTokenType.IMPLEMENTATION_BODY_START;
import static de.jplag.rust.RustTokenType.INNER_ATTRIBUTE;
import static de.jplag.rust.RustTokenType.INNER_BLOCK_END;
import static de.jplag.rust.RustTokenType.INNER_BLOCK_START;
import static de.jplag.rust.RustTokenType.LABEL;
import static de.jplag.rust.RustTokenType.LOOP_BODY_END;
import static de.jplag.rust.RustTokenType.LOOP_BODY_START;
import static de.jplag.rust.RustTokenType.LOOP_STATEMENT;
import static de.jplag.rust.RustTokenType.MACRO_INVOCATION;
import static de.jplag.rust.RustTokenType.MACRO_INVOCATION_BODY_END;
import static de.jplag.rust.RustTokenType.MACRO_INVOCATION_BODY_START;
import static de.jplag.rust.RustTokenType.MACRO_RULE;
import static de.jplag.rust.RustTokenType.MACRO_RULES_DEFINITION;
import static de.jplag.rust.RustTokenType.MACRO_RULES_DEFINITION_BODY_END;
import static de.jplag.rust.RustTokenType.MACRO_RULES_DEFINITION_BODY_START;
import static de.jplag.rust.RustTokenType.MACRO_RULE_BODY_END;
import static de.jplag.rust.RustTokenType.MACRO_RULE_BODY_START;
import static de.jplag.rust.RustTokenType.MATCH_BODY_END;
import static de.jplag.rust.RustTokenType.MATCH_BODY_START;
import static de.jplag.rust.RustTokenType.MATCH_CASE;
import static de.jplag.rust.RustTokenType.MATCH_EXPRESSION;
import static de.jplag.rust.RustTokenType.MATCH_GUARD;
import static de.jplag.rust.RustTokenType.MODULE;
import static de.jplag.rust.RustTokenType.MODULE_END;
import static de.jplag.rust.RustTokenType.MODULE_START;
import static de.jplag.rust.RustTokenType.OUTER_ATTRIBUTE;
import static de.jplag.rust.RustTokenType.RETURN;
import static de.jplag.rust.RustTokenType.STATIC_ITEM;
import static de.jplag.rust.RustTokenType.STRUCT;
import static de.jplag.rust.RustTokenType.STRUCT_BODY_END;
import static de.jplag.rust.RustTokenType.STRUCT_BODY_START;
import static de.jplag.rust.RustTokenType.STRUCT_FIELD;
import static de.jplag.rust.RustTokenType.STRUCT_INITIALISATION;
import static de.jplag.rust.RustTokenType.TRAIT;
import static de.jplag.rust.RustTokenType.TRAIT_BODY_END;
import static de.jplag.rust.RustTokenType.TRAIT_BODY_START;
import static de.jplag.rust.RustTokenType.TUPLE;
import static de.jplag.rust.RustTokenType.TUPLE_ELEMENT;
import static de.jplag.rust.RustTokenType.TUPLE_END;
import static de.jplag.rust.RustTokenType.TUPLE_START;
import static de.jplag.rust.RustTokenType.TYPE_ALIAS;
import static de.jplag.rust.RustTokenType.TYPE_ARGUMENT;
import static de.jplag.rust.RustTokenType.TYPE_PARAMETER;
import static de.jplag.rust.RustTokenType.UNION;
import static de.jplag.rust.RustTokenType.UNION_BODY_END;
import static de.jplag.rust.RustTokenType.UNION_BODY_START;
import static de.jplag.rust.RustTokenType.USE_DECLARATION;
import static de.jplag.rust.RustTokenType.USE_ITEM;
import static de.jplag.rust.RustTokenType.VARIABLE_DECLARATION;

import java.util.Objects;
import java.util.Optional;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import de.jplag.rust.grammar.RustParser;
import de.jplag.rust.grammar.RustParserBaseListener;

public class JPlagRustListener extends RustParserBaseListener implements ParseTreeListener {

    private final RustParserAdapter parserAdapter;

    private final ParserState<RustContext> state = new ParserState<>();

    public JPlagRustListener(RustParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
        state.enter(RustContext.FILE);
    }

    private void transformToken(Optional<RustTokenType> targetType, Token token) {
        targetType.ifPresent(type -> transformToken(type, token));
    }

    private void transformToken(RustTokenType targetType, Token token) {
        parserAdapter.addToken(targetType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    private void transformToken(RustTokenType targetType, Token start, Token end) {
        parserAdapter.addToken(targetType, start.getLine(), start.getCharPositionInLine() + 1, end.getStopIndex() - start.getStartIndex() + 1);
    }

    @Override
    public void enterInnerAttribute(RustParser.InnerAttributeContext context) {
        transformToken(INNER_ATTRIBUTE, context.getStart(), context.getStop());
        super.enterInnerAttribute(context);
    }

    @Override
    public void enterOuterAttribute(RustParser.OuterAttributeContext context) {
        transformToken(OUTER_ATTRIBUTE, context.getStart(), context.getStop());
        super.enterOuterAttribute(context);
    }

    @Override
    public void enterUseDeclaration(RustParser.UseDeclarationContext context) {
        transformToken(USE_DECLARATION, context.getStart());
        super.enterUseDeclaration(context);
    }

    @Override
    public void enterUseTree(RustParser.UseTreeContext context) {
        state.enter(RustContext.USE_TREE);
        super.enterUseTree(context);
    }

    @Override
    public void exitUseTree(RustParser.UseTreeContext context) {
        state.leaveAsserted(RustContext.USE_TREE);
        super.exitUseTree(context);
    }

    @Override
    public void enterSimplePath(RustParser.SimplePathContext context) {
        if (state.getCurrentContext() == RustContext.USE_TREE) {
            if (context.parent.getChildCount() > 1 && context.parent.getChild(1).getText().equals("::")) {
                // Not a leaf
                return;
            }

            transformToken(USE_ITEM, context.getStart(), context.getStop());
        }
        super.enterSimplePath(context);
    }

    @Override
    public void enterModule(RustParser.ModuleContext context) {
        transformToken(MODULE, context.getStart());
        state.enter(RustContext.MODULE_BODY);
        super.enterModule(context);
    }

    @Override
    public void enterStruct_(RustParser.Struct_Context context) {
        transformToken(STRUCT, context.getStart());
        state.enter(RustContext.STRUCT_DECLARATION_BODY);
        super.enterStruct_(context);
    }

    @Override
    public void exitStruct_(RustParser.Struct_Context context) {
        state.leaveAsserted(RustContext.STRUCT_DECLARATION_BODY);
        super.exitStruct_(context);
    }

    @Override
    public void enterStructExpression(RustParser.StructExpressionContext context) {
        transformToken(STRUCT_INITIALISATION, context.getStart());
        state.enter(RustContext.STRUCT_INITIALISATION);
        super.enterStructExpression(context);
    }

    @Override
    public void exitStructExpression(RustParser.StructExpressionContext context) {
        state.leaveAsserted(RustContext.STRUCT_INITIALISATION);
        super.exitStructExpression(context);
    }

    @Override
    public void enterStructField(RustParser.StructFieldContext context) {
        transformToken(STRUCT_FIELD, context.getStart());
        super.enterStructField(context);
    }

    @Override
    public void enterStructExprField(RustParser.StructExprFieldContext context) {
        transformToken(ARGUMENT, context.getStart(), context.getStop());
        super.enterStructExprField(context);
    }

    @Override
    public void enterStructPattern(RustParser.StructPatternContext context) {
        transformToken(STRUCT, context.getStart());
        state.enter(RustContext.STRUCT_DECLARATION_BODY);
        super.enterStructPattern(context);
    }

    @Override
    public void exitStructPattern(RustParser.StructPatternContext context) {
        state.leaveAsserted(RustContext.STRUCT_DECLARATION_BODY);
        super.exitStructPattern(context);
    }

    @Override
    public void enterStructPatternField(RustParser.StructPatternFieldContext context) {
        transformToken(STRUCT_FIELD, context.getStart());
        super.enterStructPatternField(context);
    }

    @Override
    public void enterTupleExpression(RustParser.TupleExpressionContext context) {
        state.enter(RustContext.TUPLE);

        var elements = context.getChild(RustParser.TupleElementsContext.class, 0);
        // one child = exactly one subtree and no trailing comma
        if (Objects.nonNull(elements) && elements.getChildCount() == 1) {
            state.enter(RustContext.REDUNDANT_TUPLE);
        }

        super.enterTupleExpression(context);
    }

    @Override
    public void exitTupleExpression(RustParser.TupleExpressionContext ctx) {
        state.leaveIfInContext(RustContext.REDUNDANT_TUPLE);
        state.leaveAsserted(RustContext.TUPLE);
        super.exitTupleExpression(ctx);
    }

    @Override
    public void enterTupleField(RustParser.TupleFieldContext context) {
        if (state.getCurrentContext() != RustContext.REDUNDANT_TUPLE) {
            transformToken(TUPLE_ELEMENT, context.getStart());
        }
        super.enterTupleField(context);
    }

    @Override
    public void enterTupleStructPattern(RustParser.TupleStructPatternContext context) {
        transformToken(STRUCT_INITIALISATION, context.getStart());
        state.enter(RustContext.STRUCT_INITIALISATION);
        super.enterTupleStructPattern(context);
    }

    @Override
    public void exitTupleStructPattern(RustParser.TupleStructPatternContext context) {
        state.leaveAsserted(RustContext.STRUCT_INITIALISATION);
        super.exitTupleStructPattern(context);
    }

    @Override
    public void enterTupleStructItems(RustParser.TupleStructItemsContext context) {
        state.enter(RustContext.TUPLE_STRUCT_PATTERN);
        super.enterTupleStructItems(context);
    }

    @Override
    public void exitTupleStructItems(RustParser.TupleStructItemsContext context) {
        state.leaveAsserted(RustContext.TUPLE_STRUCT_PATTERN);
        super.exitTupleStructItems(context);
    }

    @Override
    public void enterTuplePatternItems(RustParser.TuplePatternItemsContext context) {
        state.enter(RustContext.TUPLE_PATTERN);
        super.enterTuplePatternItems(context);
    }

    @Override
    public void exitTuplePatternItems(RustParser.TuplePatternItemsContext context) {
        state.leaveAsserted(RustContext.TUPLE_PATTERN);
        super.exitTuplePatternItems(context);
    }

    @Override
    public void enterUnion_(RustParser.Union_Context context) {
        transformToken(UNION, context.getStart());
        state.enter(RustContext.UNION_BODY);
        super.enterUnion_(context);
    }

    @Override
    public void exitUnion_(RustParser.Union_Context context) {
        state.leaveAsserted(RustContext.UNION_BODY);
        super.exitUnion_(context);
    }

    @Override
    public void enterTrait_(RustParser.Trait_Context context) {
        transformToken(TRAIT, context.getStart());
        state.enter(RustContext.TRAIT_BODY);
        super.enterTrait_(context);
    }

    @Override
    public void exitTrait_(RustParser.Trait_Context context) {
        state.leaveAsserted(RustContext.TRAIT_BODY);
        super.exitTrait_(context);
    }

    @Override
    public void enterTypeAlias(RustParser.TypeAliasContext context) {
        transformToken(TYPE_ALIAS, context.getStart());
        super.enterTypeAlias(context);
    }

    @Override
    public void enterImplementation(RustParser.ImplementationContext context) {
        transformToken(IMPLEMENTATION, context.getStart());
        state.enter(RustContext.IMPLEMENTATION_BODY);
        super.enterImplementation(context);
    }

    @Override
    public void exitImplementation(RustParser.ImplementationContext context) {
        state.leaveAsserted(RustContext.IMPLEMENTATION_BODY);
        super.exitImplementation(context);
    }

    @Override
    public void enterEnumeration(RustParser.EnumerationContext context) {
        transformToken(ENUM, context.getStart());
        state.enter(RustContext.ENUM_BODY);
        super.enterEnumeration(context);
    }

    @Override
    public void exitEnumeration(RustParser.EnumerationContext context) {
        state.leaveAsserted(RustContext.ENUM_BODY);
        super.exitEnumeration(context);
    }

    @Override
    public void enterEnumItemTuple(RustParser.EnumItemTupleContext ctx) {
        state.enter(RustContext.TUPLE);
        super.enterEnumItemTuple(ctx);
    }

    @Override
    public void exitEnumItemTuple(RustParser.EnumItemTupleContext ctx) {
        state.leaveAsserted(RustContext.TUPLE);
        super.exitEnumItemTuple(ctx);
    }

    @Override
    public void enterEnumItem(RustParser.EnumItemContext context) {
        transformToken(ENUM_ITEM, context.getStart());
        super.enterEnumItem(context);
    }

    @Override
    public void enterMacroRulesDefinition(RustParser.MacroRulesDefinitionContext context) {
        transformToken(MACRO_RULES_DEFINITION, context.getStart());
        state.enter(RustContext.MACRO_RULES_DEFINITION_BODY);
        super.enterMacroRulesDefinition(context);
    }

    @Override
    public void exitMacroRulesDefinition(RustParser.MacroRulesDefinitionContext context) {
        state.leaveAsserted(RustContext.MACRO_RULES_DEFINITION_BODY);
        super.exitMacroRulesDefinition(context);
    }

    @Override
    public void enterMacroRule(RustParser.MacroRuleContext context) {
        transformToken(MACRO_RULE, context.getStart());
        state.enter(RustContext.MACRO_RULE_BODY);
        super.enterMacroRule(context);
    }

    @Override
    public void exitMacroRule(RustParser.MacroRuleContext context) {
        state.leaveAsserted(RustContext.MACRO_RULE_BODY);
        super.exitMacroRule(context);
    }

    @Override
    public void enterMacroInvocationSemi(RustParser.MacroInvocationSemiContext context) {
        transformToken(MACRO_INVOCATION, context.getStart());
        state.enter(RustContext.MACRO_INVOCATION_BODY);
        super.enterMacroInvocationSemi(context);
    }

    @Override
    public void exitMacroInvocationSemi(RustParser.MacroInvocationSemiContext context) {
        state.leaveAsserted(RustContext.MACRO_INVOCATION_BODY);
        super.exitMacroInvocationSemi(context);
    }

    @Override
    public void enterMacroInvocation(RustParser.MacroInvocationContext context) {
        transformToken(MACRO_INVOCATION, context.getStart());
        state.enter(RustContext.MACRO_INVOCATION_BODY);
        super.enterMacroInvocation(context);
    }

    @Override
    public void exitMacroInvocation(RustParser.MacroInvocationContext context) {
        state.leaveAsserted(RustContext.MACRO_INVOCATION_BODY);
        super.exitMacroInvocation(context);
    }

    @Override
    public void enterExternBlock(RustParser.ExternBlockContext context) {
        transformToken(EXTERN_BLOCK, context.getStart());
        state.enter(RustContext.EXTERN_BLOCK);
        super.enterExternBlock(context);
    }

    @Override
    public void exitExternBlock(RustParser.ExternBlockContext context) {
        state.leaveAsserted(RustContext.EXTERN_BLOCK);
        super.exitExternBlock(context);
    }

    @Override
    public void enterExternCrate(RustParser.ExternCrateContext context) {
        transformToken(EXTERN_CRATE, context.getStart());
        super.enterExternCrate(context);
    }

    @Override
    public void enterStaticItem(RustParser.StaticItemContext context) {
        RustTokenType tokenType = context.getParent() instanceof RustParser.ExternalItemContext ? STATIC_ITEM : VARIABLE_DECLARATION;
        transformToken(tokenType, context.getStart());
        super.enterStaticItem(context);
    }

    @Override
    public void enterFunction_(RustParser.Function_Context context) {
        Token fn = context.getChild(TerminalNodeImpl.class, 0).getSymbol();
        transformToken(FUNCTION, fn);
        boolean hasReturnType = context.getChild(RustParser.FunctionReturnTypeContext.class, 0) != null;
        state.enter(hasReturnType ? RustContext.FUNCTION_BODY : RustContext.PROCEDURE_BODY);
        super.enterFunction_(context);
    }

    @Override
    public void exitFunction_(RustParser.Function_Context context) {
        state.leaveAsserted(RustContext.FUNCTION_BODY, RustContext.PROCEDURE_BODY);
        super.exitFunction_(context);
    }

    @Override
    public void enterSelfParam(RustParser.SelfParamContext context) {
        transformToken(FUNCTION_PARAMETER, context.getStart(), context.getStop());
        super.enterSelfParam(context);
    }

    @Override
    public void enterFunctionParam(RustParser.FunctionParamContext context) {
        transformToken(FUNCTION_PARAMETER, context.getStart(), context.getStop());
        super.enterFunctionParam(context);
    }

    @Override
    public void enterGenericParam(RustParser.GenericParamContext context) {
        if (!(context.getParent().getParent() instanceof RustParser.ForLifetimesContext)) {
            transformToken(TYPE_PARAMETER, context.getStart(), context.getStop());
        }
        super.enterGenericParam(context);
    }

    @Override
    public void enterExpressionWithBlock(RustParser.ExpressionWithBlockContext context) {
        state.enter(RustContext.INNER_BLOCK);
        super.enterExpressionWithBlock(context);
    }

    @Override
    public void exitExpressionWithBlock(RustParser.ExpressionWithBlockContext context) {
        state.leaveAsserted(RustContext.INNER_BLOCK);
        super.exitExpressionWithBlock(context);
    }

    @Override
    public void enterIfExpression(RustParser.IfExpressionContext context) {
        transformToken(IF_STATEMENT, context.getStart());
        state.enter(RustContext.IF_BODY);
        super.enterIfExpression(context);
    }

    @Override
    public void exitIfExpression(RustParser.IfExpressionContext context) {
        state.leaveIfInContext(RustContext.ELSE_BODY);
        state.leaveAsserted(RustContext.IF_BODY, RustContext.ELSE_BODY);
        super.exitIfExpression(context);
    }

    @Override
    public void enterIfLetExpression(RustParser.IfLetExpressionContext ctx) {
        transformToken(IF_STATEMENT, ctx.getStart());
        state.enter(RustContext.IF_BODY);
        super.enterIfLetExpression(ctx);
    }

    @Override
    public void exitIfLetExpression(RustParser.IfLetExpressionContext ctx) {
        state.leaveIfInContext(RustContext.ELSE_BODY);
        state.leaveAsserted(RustContext.IF_BODY);
        super.exitIfLetExpression(ctx);
    }

    @Override
    public void enterLoopLabel(RustParser.LoopLabelContext context) {
        transformToken(LABEL, context.getStart());
        super.enterLoopLabel(context);
    }

    @Override
    public void enterInfiniteLoopExpression(RustParser.InfiniteLoopExpressionContext context) {
        Token loopKeyword = context.getChild(TerminalNodeImpl.class, 0).getSymbol();
        transformToken(LOOP_STATEMENT, loopKeyword);
        state.enter(RustContext.LOOP_BODY);
        super.enterInfiniteLoopExpression(context);
    }

    @Override
    public void exitInfiniteLoopExpression(RustParser.InfiniteLoopExpressionContext context) {
        state.leaveAsserted(RustContext.LOOP_BODY);
        super.exitInfiniteLoopExpression(context);
    }

    @Override
    public void enterPredicateLoopExpression(RustParser.PredicateLoopExpressionContext context) {
        Token whileKeyword = context.getChild(TerminalNodeImpl.class, 0).getSymbol();
        transformToken(LOOP_STATEMENT, whileKeyword);
        state.enter(RustContext.LOOP_BODY);
        super.enterPredicateLoopExpression(context);
    }

    @Override
    public void exitPredicateLoopExpression(RustParser.PredicateLoopExpressionContext context) {
        state.leaveAsserted(RustContext.LOOP_BODY);
        super.exitPredicateLoopExpression(context);
    }

    @Override
    public void enterPredicatePatternLoopExpression(RustParser.PredicatePatternLoopExpressionContext context) {
        Token whileKeyword = context.getChild(TerminalNodeImpl.class, 0).getSymbol();
        transformToken(LOOP_STATEMENT, whileKeyword);
        state.enter(RustContext.LOOP_BODY);
        super.enterPredicatePatternLoopExpression(context);
    }

    @Override
    public void exitPredicatePatternLoopExpression(RustParser.PredicatePatternLoopExpressionContext context) {
        state.leaveAsserted(RustContext.LOOP_BODY);
        super.exitPredicatePatternLoopExpression(context);
    }

    @Override
    public void enterIteratorLoopExpression(RustParser.IteratorLoopExpressionContext context) {
        Token forKeyword = context.getChild(TerminalNodeImpl.class, 0).getSymbol();
        transformToken(FOR_STATEMENT, forKeyword);
        state.enter(RustContext.FOR_BODY);
        super.enterIteratorLoopExpression(context);
    }

    @Override
    public void exitIteratorLoopExpression(RustParser.IteratorLoopExpressionContext context) {
        state.leaveAsserted(RustContext.FOR_BODY);
        super.exitIteratorLoopExpression(context);
    }

    @Override
    public void enterBreakExpression(RustParser.BreakExpressionContext context) {
        transformToken(BREAK, context.getStart());
        super.enterBreakExpression(context);
    }

    @Override
    public void enterMatchExpression(RustParser.MatchExpressionContext context) {
        transformToken(MATCH_EXPRESSION, context.getStart());
        state.enter(RustContext.MATCH_BODY);
        super.enterMatchExpression(context);
    }

    @Override
    public void exitMatchExpression(RustParser.MatchExpressionContext context) {
        state.leaveAsserted(RustContext.MATCH_BODY);
        super.exitMatchExpression(context);
    }

    @Override
    public void enterMatchArm(RustParser.MatchArmContext context) {
        transformToken(MATCH_CASE, context.getStart());
        super.enterMatchArm(context);
    }

    @Override
    public void enterMatchArmGuard(RustParser.MatchArmGuardContext context) {
        transformToken(MATCH_GUARD, context.getStart());
        super.enterMatchArmGuard(context);
    }

    @Override
    public void enterCompoundAssignOperator(RustParser.CompoundAssignOperatorContext context) {
        transformToken(ASSIGNMENT, context.getStart());
        super.enterCompoundAssignOperator(context);
    }

    @Override
    public void enterCallExpression(RustParser.CallExpressionContext ctx) {
        state.enter(RustContext.CALL);
        super.enterCallExpression(ctx);
    }

    @Override
    public void exitCallExpression(RustParser.CallExpressionContext ctx) {
        state.leaveAsserted(RustContext.CALL);
        super.exitCallExpression(ctx);
    }

    @Override
    public void enterMethodCallExpression(RustParser.MethodCallExpressionContext context) {
        state.enter(RustContext.CALL);
        super.enterMethodCallExpression(context);
    }

    @Override
    public void exitMethodCallExpression(RustParser.MethodCallExpressionContext ctx) {
        state.leaveAsserted(RustContext.CALL);
        super.exitMethodCallExpression(ctx);
    }

    @Override
    public void enterConstantItem(RustParser.ConstantItemContext context) {
        transformToken(VARIABLE_DECLARATION, context.getStart());
        super.enterConstantItem(context);
    }

    @Override
    public void enterArrayExpression(RustParser.ArrayExpressionContext context) {
        transformToken(ARRAY_BODY_START, context.getStart());
        super.enterArrayExpression(context);
    }

    @Override
    public void exitArrayExpression(RustParser.ArrayExpressionContext context) {
        transformToken(ARRAY_BODY_END, context.getStop());
        super.exitArrayExpression(context);
    }

    @Override
    public void enterTuplePattern(RustParser.TuplePatternContext context) {
        transformToken(TUPLE, context.getStart());
        state.enter(RustContext.TUPLE);
        super.enterTuplePattern(context);
    }

    @Override
    public void exitTuplePattern(RustParser.TuplePatternContext context) {
        state.leaveAsserted(RustContext.TUPLE);
        super.exitTuplePattern(context);
    }

    @Override
    public void enterClosureExpression(RustParser.ClosureExpressionContext context) {
        transformToken(CLOSURE, context.getStart());
        state.enter(RustContext.CLOSURE_BODY);
        super.enterClosureExpression(context);
    }

    @Override
    public void exitClosureExpression(RustParser.ClosureExpressionContext context) {
        state.leaveAsserted(RustContext.CLOSURE_BODY);
        super.exitClosureExpression(context);
    }

    @Override
    public void enterClosureParam(RustParser.ClosureParamContext context) {
        transformToken(FUNCTION_PARAMETER, context.getStart());
        super.enterClosureParam(context);
    }

    @Override
    public void enterReturnExpression(RustParser.ReturnExpressionContext context) {
        transformToken(RETURN, context.getStart());
        super.enterReturnExpression(context);
    }

    @Override
    public void enterExpressionStatement(RustParser.ExpressionStatementContext context) {
        // may be return value
        RuleContext maybeFunctionBlock = context.parent.parent;
        boolean isImplicitReturnValue = maybeFunctionBlock instanceof RustParser.StatementsContext && (maybeFunctionBlock.getChildCount() == 1)
                && (state.getCurrentContext() == RustContext.FUNCTION_BODY) && !(context.getChild(0) instanceof RustParser.ReturnExpressionContext);

        if (isImplicitReturnValue) {
            transformToken(RETURN, context.getStart());
        }
        super.enterExpressionStatement(context);
    }

    @Override
    public void enterPattern(RustParser.PatternContext context) {
        switch (state.getCurrentContext()) {
            case TUPLE_STRUCT_PATTERN -> transformToken(ARGUMENT, context.getStart(), context.getStop());
            case TUPLE_PATTERN -> transformToken(TUPLE_ELEMENT, context.getStart());
            default -> {
                // do nothing
            }
        }
        super.enterPattern(context);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        final Token token = node.getSymbol();
        final ParseTree parentNode = node.getParent();
        RustContext stateContext = state.getCurrentContext();
        switch (node.getText()) {
            case "*" -> {
                if (parentNode instanceof RustParser.UseTreeContext) {
                    transformToken(USE_ITEM, token);
                }
            }
            case "let" -> {
                if (stateContext != RustContext.MACRO_INNER) {
                    transformToken(VARIABLE_DECLARATION, token);
                }
            }
            case "=" -> {
                if (!(parentNode instanceof RustParser.AttrInputContext || parentNode instanceof RustParser.MacroPunctuationTokenContext
                        || parentNode instanceof RustParser.TypeParamContext || parentNode instanceof RustParser.GenericArgsBindingContext)
                        && stateContext != RustContext.MACRO_INNER) {
                    transformToken(ASSIGNMENT, token);
                }
            }
            case "{" -> {
                Optional<RustTokenType> startType = stateContext.getStartType();
                startType.ifPresent(type -> transformToken(type, token));
                switch (stateContext) {
                    case MACRO_RULE_BODY, MACRO_INVOCATION_BODY, MACRO_INNER -> state.enter(RustContext.MACRO_INNER);
                    default -> {
                        // do nothing
                    }
                }

            }
            case "}" -> {
                Optional<RustTokenType> endType = stateContext.getEndType();
                endType.ifPresent(type -> transformToken(type, token));

                if (stateContext == RustContext.MACRO_INNER) {
                    // maybe this is the end of a macro invocation/definition
                    state.leaveAsserted(RustContext.MACRO_INNER);
                    stateContext = state.getCurrentContext();
                    if (stateContext == RustContext.MACRO_INVOCATION_BODY || stateContext == RustContext.MACRO_RULE_BODY) {
                        transformToken(stateContext.getEndType(), token);
                    }
                }
            }
            case "(" -> {
                switch (stateContext) {
                    case STRUCT_DECLARATION_BODY -> transformToken(RustContext.STRUCT_DECLARATION_BODY.getStartType(), token);
                    case TUPLE -> transformToken(RustContext.TUPLE.getStartType(), token);
                    case MACRO_INVOCATION_BODY -> {
                        transformToken(MACRO_INVOCATION_BODY_START, token);
                        state.enter(RustContext.MACRO_INNER);
                    }
                    case MACRO_INNER -> state.enter(RustContext.MACRO_INNER);
                    case CALL -> transformToken(APPLY, token);
                    default -> {
                        // do nothing
                    }
                }
            }
            case ")", "]" -> {
                switch (stateContext) {
                    case STRUCT_DECLARATION_BODY -> transformToken(RustContext.STRUCT_DECLARATION_BODY.getEndType(), token);
                    case TUPLE -> transformToken(RustContext.TUPLE.getEndType(), token);
                    case MACRO_INVOCATION_BODY -> {
                        /* do nothing */
                    }
                    case MACRO_INNER -> {
                        state.leaveAsserted(RustContext.MACRO_INNER);
                        stateContext = state.getCurrentContext();
                        if (stateContext == RustContext.MACRO_INVOCATION_BODY) {
                            transformToken(MACRO_INVOCATION_BODY_END, token);
                        }
                    }
                    default -> {
                        // do nothing
                    }

                }
            }
            case "[" -> {
                switch (stateContext) {
                    case MACRO_INVOCATION_BODY -> {
                        transformToken(MACRO_INVOCATION_BODY_START, token);
                        state.enter(RustContext.MACRO_INNER);
                    }
                    case MACRO_INNER -> state.enter(RustContext.MACRO_INNER);
                    default -> {
                        // do nothing
                    }
                }
            }

            case "else" -> {
                if (stateContext == RustContext.IF_BODY) {
                    transformToken(ELSE_STATEMENT, token);
                    state.enter(RustContext.ELSE_BODY);
                }
            }
            default -> {
                // do nothing
            }
        }
    }

    @Override
    public void enterType_(RustParser.Type_Context context) {
        if (context.getParent() instanceof RustParser.GenericArgsTypesContext
                && context.getParent().getParent().getParent() instanceof RustParser.PathExprSegmentContext) {
            transformToken(TYPE_ARGUMENT, context.getStart(), context.getStop());
        }
        state.enter(RustContext.TYPE);
        super.enterType_(context);
    }

    @Override
    public void exitType_(RustParser.Type_Context context) {
        state.leaveAsserted(RustContext.TYPE);
        super.exitType_(context);
    }

    @Override
    public void enterGenericArg(RustParser.GenericArgContext context) {
        // Only type arguments for methods, not for type expressions
        if (context.getParent().getParent() instanceof RustParser.PathInExpressionContext) {
            transformToken(TYPE_ARGUMENT, context.getStart(), context.getStop());
        }
        super.enterGenericArg(context);
    }

    @Override
    public void enterEveryRule(ParserRuleContext context) {
        // ExpressionContext gets no own enter/exit method
        // used in various 'lists' of elements
        if (context instanceof RustParser.ExpressionContext expression) {
            if (context.parent instanceof RustParser.ArrayElementsContext) {
                transformToken(ARRAY_ELEMENT, expression.getStart());
            } else if (context.parent instanceof RustParser.CallParamsContext) {
                transformToken(ARGUMENT, expression.getStart(), expression.getStop());
            } else if (context.parent instanceof RustParser.TuplePatternItemsContext || context.parent instanceof RustParser.TupleElementsContext) {
                if (state.getCurrentContext() == RustContext.REDUNDANT_TUPLE)
                    return;
                transformToken(TUPLE_ELEMENT, expression.getStart());
            } else if (context.parent instanceof RustParser.ClosureExpressionContext) {
                transformToken(CLOSURE_BODY_START, context.getStart());
                transformToken(RETURN, expression.getStart());
            }
        }
    }

    @Override
    public void exitEveryRule(ParserRuleContext context) {
        if (context instanceof RustParser.ExpressionContext && context.parent instanceof RustParser.ClosureExpressionContext) {
            transformToken(CLOSURE_BODY_END, context.getStop());

        }
    }

    /**
     * Implementation of Context for the Rust language
     */
    enum RustContext implements ParserState.Context {

        /**
         * This is used to make sure that the stack is not empty -> getCurrent() != null
         **/
        FILE(),

        /**
         * These contexts are used to assign the correct tokens to '{' and '}' terminals.
         **/
        FUNCTION_BODY(FUNCTION_BODY_START, FUNCTION_BODY_END),
        PROCEDURE_BODY(FUNCTION_BODY_START, FUNCTION_BODY_END),
        STRUCT_DECLARATION_BODY(STRUCT_BODY_START, STRUCT_BODY_END),
        IF_BODY(IF_BODY_START, IF_BODY_END),
        ELSE_BODY(ELSE_BODY_START, ELSE_BODY_END),
        LOOP_BODY(LOOP_BODY_START, LOOP_BODY_END),
        INNER_BLOCK(INNER_BLOCK_START, INNER_BLOCK_END),
        TRAIT_BODY(TRAIT_BODY_START, TRAIT_BODY_END),
        ENUM_BODY(ENUM_BODY_START, ENUM_BODY_END),
        MACRO_RULES_DEFINITION_BODY(MACRO_RULES_DEFINITION_BODY_START, MACRO_RULES_DEFINITION_BODY_END),
        MACRO_RULE_BODY(MACRO_RULE_BODY_START, MACRO_RULE_BODY_END),
        MACRO_INVOCATION_BODY(MACRO_INVOCATION_BODY_START, MACRO_INVOCATION_BODY_END),
        IMPLEMENTATION_BODY(IMPLEMENTATION_BODY_START, IMPLEMENTATION_BODY_END),
        EXTERN_BLOCK(EXTERN_BLOCK_START, EXTERN_BLOCK_END),
        MODULE_BODY(MODULE_START, MODULE_END),
        UNION_BODY(UNION_BODY_START, UNION_BODY_END),
        CLOSURE_BODY(CLOSURE_BODY_START, CLOSURE_BODY_END),
        MATCH_BODY(MATCH_BODY_START, MATCH_BODY_END),
        FOR_BODY(FOR_BODY_START, FOR_BODY_END),
        TUPLE(TUPLE_START, TUPLE_END),

        /**
         * This is to avoid the empty type `()` being parsed as an empty tuple etc.
         **/
        TYPE(),

        /**
         * These are to identify expressions as elements of tuples.
         */
        TUPLE_STRUCT_PATTERN(),
        TUPLE_PATTERN(),

        /**
         * This is used so that cascades of tuples like '((((1),2),(3)))' generate only as many tokens as necessary.
         */
        REDUNDANT_TUPLE(),

        /**
         * This is used to be able to correctly assign MACRO_INVOCATION_BODY_END to a '}' symbol.
         */
        MACRO_INNER(),

        /**
         * In this context, leaves are USE_ITEMS.
         */
        USE_TREE(),

        /**
         * In this context, '(' should be assigned an APPLY token.
         */
        CALL(),

        /**
         * This context should behave like a function call: No tokens for parentheses.
         */
        STRUCT_INITIALISATION();

        private final Optional<RustTokenType> startType;
        private final Optional<RustTokenType> endType;

        RustContext() {
            this.startType = Optional.empty();
            this.endType = Optional.empty();
        }

        RustContext(RustTokenType startType, RustTokenType endType) {
            this.startType = Optional.of(startType);
            this.endType = Optional.of(endType);
        }

        @Override
        public Optional<RustTokenType> getStartType() {
            return startType;
        }

        @Override
        public Optional<RustTokenType> getEndType() {
            return endType;
        }
    }
}
