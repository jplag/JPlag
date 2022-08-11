package de.jplag.rust;

import static de.jplag.rust.RustTokenConstants.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.*;

import de.jplag.rust.grammar.RustParser;
import de.jplag.rust.grammar.RustParserBaseListener;

public class JplagRustListener extends RustParserBaseListener implements ParseTreeListener {

    private final RustParserAdapter parserAdapter;

    private final ParserState<RustContext> state = new ParserState<>();

    public JplagRustListener(RustParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
        state.enter(RustContext.FILE);
    }

    private void transformToken(int targetType, Token token) {
        parserAdapter.addToken(targetType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    private void transformToken(int targetType, Token start, Token end) {
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
        state.leave(RustContext.USE_TREE);
        super.exitUseTree(context);
    }

    @Override
    public void enterSimplePath(RustParser.SimplePathContext context) {
        if (state.getCurrent() == RustContext.USE_TREE) {
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
        state.enter(RustContext.STRUCT_BODY);
        super.enterStruct_(context);
    }

    @Override
    public void exitStruct_(RustParser.Struct_Context context) {
        state.leave(RustContext.STRUCT_BODY);
        super.exitStruct_(context);
    }

    @Override
    public void enterStructExpression(RustParser.StructExpressionContext context) {
        transformToken(STRUCT, context.getStart());
        state.enter(RustContext.STRUCT_BODY);
        super.enterStructExpression(context);
    }

    @Override
    public void exitStructExpression(RustParser.StructExpressionContext context) {
        state.leave(RustContext.STRUCT_BODY);
        super.exitStructExpression(context);
    }

    @Override
    public void enterStructField(RustParser.StructFieldContext context) {
        transformToken(STRUCT_FIELD, context.getStart());
        super.enterStructField(context);
    }

    @Override
    public void enterStructExprField(RustParser.StructExprFieldContext context) {
        transformToken(STRUCT_FIELD, context.getStart());
        super.enterStructExprField(context);
    }

    @Override
    public void enterStructPattern(RustParser.StructPatternContext context) {
        transformToken(STRUCT, context.getStart());
        state.enter(RustContext.STRUCT_BODY);
        super.enterStructPattern(context);
    }

    @Override
    public void exitStructPattern(RustParser.StructPatternContext context) {
        state.leave(RustContext.STRUCT_BODY);
        super.exitStructPattern(context);
    }

    @Override
    public void enterStructPatternField(RustParser.StructPatternFieldContext context) {
        transformToken(STRUCT_FIELD, context.getStart());
        super.enterStructPatternField(context);
    }

    @Override
    public void enterTupleElements(RustParser.TupleElementsContext context) {
        if (context.getChildCount() <= 2)
            state.enter(RustContext.REDUNDANT_TUPLE);
        super.enterTupleElements(context);
    }

    @Override
    public void exitTupleElements(RustParser.TupleElementsContext context) {
        state.maybeLeave(RustContext.REDUNDANT_TUPLE);
        super.exitTupleElements(context);
    }

    @Override
    public void enterTupleField(RustParser.TupleFieldContext context) {
        if (state.getCurrent() != RustContext.REDUNDANT_TUPLE) {
            transformToken(TUPLE_ELEMENT, context.getStart());
        }
        super.enterTupleField(context);
    }

    @Override
    public void enterTupleStructPattern(RustParser.TupleStructPatternContext context) {
        transformToken(STRUCT, context.getStart());
        state.enter(RustContext.STRUCT_BODY);
        super.enterTupleStructPattern(context);
    }

    @Override
    public void exitTupleStructPattern(RustParser.TupleStructPatternContext context) {
        state.leave(RustContext.STRUCT_BODY);
        super.exitTupleStructPattern(context);
    }

    @Override
    public void enterTupleStructItems(RustParser.TupleStructItemsContext context) {
        state.enter(RustContext.TUPLE_STRUCT_PATTERN);
        if (context.getChildCount() <= 2)
            state.enter(RustContext.REDUNDANT_TUPLE);
        super.enterTupleStructItems(context);
    }

    @Override
    public void exitTupleStructItems(RustParser.TupleStructItemsContext context) {
        state.maybeLeave(RustContext.REDUNDANT_TUPLE);
        state.leave(RustContext.TUPLE_STRUCT_PATTERN);
        super.exitTupleStructItems(context);
    }

    @Override
    public void enterTuplePatternItems(RustParser.TuplePatternItemsContext context) {
        state.enter(RustContext.TUPLE_PATTERN);
        super.enterTuplePatternItems(context);
    }

    @Override
    public void exitTuplePatternItems(RustParser.TuplePatternItemsContext context) {
        state.leave(RustContext.TUPLE_PATTERN);
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
        state.leave(RustContext.UNION_BODY);
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
        state.leave(RustContext.TRAIT_BODY);
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
        state.leave(RustContext.IMPLEMENTATION_BODY);
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
        state.leave(RustContext.ENUM_BODY);
        super.exitEnumeration(context);
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
        state.leave(RustContext.MACRO_RULES_DEFINITION_BODY);
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
        state.leave(RustContext.MACRO_RULE_BODY);
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
        state.leave(RustContext.MACRO_INVOCATION_BODY);
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
        state.leave(RustContext.MACRO_INVOCATION_BODY);
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
        state.leave(RustContext.EXTERN_BLOCK);
        super.exitExternBlock(context);
    }

    @Override
    public void enterExternCrate(RustParser.ExternCrateContext context) {
        transformToken(EXTERN_CRATE, context.getStart());
        super.enterExternCrate(context);
    }

    @Override
    public void enterStaticItem(RustParser.StaticItemContext context) {
        transformToken(STATIC_ITEM, context.getStart());
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
        state.leave(RustContext.FUNCTION_BODY, RustContext.PROCEDURE_BODY);
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
        transformToken(TYPE_PARAMETER, context.getStart(), context.getStop());
        super.enterGenericParam(context);
    }

    @Override
    public void enterExpressionWithBlock(RustParser.ExpressionWithBlockContext context) {
        state.enter(RustContext.INNER_BLOCK);
        super.enterExpressionWithBlock(context);
    }

    @Override
    public void exitExpressionWithBlock(RustParser.ExpressionWithBlockContext context) {
        state.leave(RustContext.INNER_BLOCK);
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
        state.maybeLeave(RustContext.ELSE_BODY);
        state.leave(RustContext.IF_BODY, RustContext.ELSE_BODY);
        super.exitIfExpression(context);
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
        state.leave(RustContext.LOOP_BODY);
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
        state.leave(RustContext.LOOP_BODY);
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
        state.leave(RustContext.LOOP_BODY);
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
        state.leave(RustContext.FOR_BODY);
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
        state.leave(RustContext.MATCH_BODY);
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
    public void enterRangeExpression(RustParser.RangeExpressionContext context) {
        // Ranges are ignored for now.
        super.enterRangeExpression(context);
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
        state.leave(RustContext.CALL);
        super.exitCallExpression(ctx);
    }

    @Override
    public void enterMethodCallExpression(RustParser.MethodCallExpressionContext ctx) {
        state.enter(RustContext.CALL);
        super.enterMethodCallExpression(ctx);
    }

    @Override
    public void exitMethodCallExpression(RustParser.MethodCallExpressionContext ctx) {
        state.leave(RustContext.CALL);
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
        state.leave(RustContext.TUPLE);
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
        state.leave(RustContext.CLOSURE_BODY);
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
                && (state.getCurrent() == RustContext.FUNCTION_BODY) && !(context.getChild(0) instanceof RustParser.ReturnExpressionContext);

        if (isImplicitReturnValue) {
            transformToken(RETURN, context.getStart());
        }
        super.enterExpressionStatement(context);
    }

    @Override
    public void enterPattern(RustParser.PatternContext context) {
        switch (state.getCurrent()) {
            case TUPLE_STRUCT_PATTERN -> transformToken(STRUCT_FIELD, context.getStart());
            case TUPLE_PATTERN -> transformToken(TUPLE_ELEMENT, context.getStart());
        }
        super.enterPattern(context);
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
            case "=" -> {
                if (!(node.getParent() instanceof RustParser.AttrInputContext || node.getParent() instanceof RustParser.TypeParamContext
                        || node.getParent() instanceof RustParser.GenericArgsBindingContext)) {
                    transformToken(ASSIGNMENT, token);
                }
            }
            case "{" -> {
                int startType = state.getCurrent().getStartType();
                if (startType != NONE) {
                    transformToken(startType, token);
                }
                switch (state.getCurrent()) {
                    case MACRO_RULES_DEFINITION_BODY, MACRO_INVOCATION_BODY, MACRO_INNER -> state.enter(RustContext.MACRO_INNER);
                }

            }
            case "}" -> {
                int endType = state.getCurrent().getEndType();
                if (endType != NONE) {
                    transformToken(endType, token);
                }

                if (state.getCurrent() == RustContext.MACRO_INNER) {
                    // maybe this is the end of a macro invocation/definition
                    state.leave(RustContext.MACRO_INNER);
                    if (state.getCurrent() == RustContext.MACRO_INVOCATION_BODY) {
                        transformToken(MACRO_INVOCATION_BODY_END, token);
                    } else if (state.getCurrent() == RustContext.MACRO_RULES_DEFINITION_BODY) {
                        transformToken(MACRO_RULES_DEFINITION_BODY_END, token);
                    }
                }
            }
            case "(" -> {
                switch (state.getCurrent()) {
                    case STRUCT_BODY -> transformToken(RustContext.STRUCT_BODY.getStartType(), token);
                    case TUPLE -> transformToken(RustContext.TUPLE.getStartType(), token);
                    case MACRO_INVOCATION_BODY -> {
                        transformToken(MACRO_INVOCATION_BODY_START, token);
                        state.enter(RustContext.MACRO_INNER);
                    }
                    case MACRO_INNER -> state.enter(RustContext.MACRO_INNER);
                    case CALL -> transformToken(APPLY, token);
                }
            }
            case ")" -> {
                switch (state.getCurrent()) {
                    case STRUCT_BODY -> transformToken(RustContext.STRUCT_BODY.getEndType(), token);
                    case TUPLE -> transformToken(RustContext.TUPLE.getEndType(), token);
                    case MACRO_INVOCATION_BODY -> {
                        /* do nothing */ }
                    case MACRO_INNER -> {
                        state.leave(RustContext.MACRO_INNER);
                        if (state.getCurrent() == RustContext.MACRO_INVOCATION_BODY) {
                            transformToken(MACRO_INVOCATION_BODY_END, token);
                        }
                    }

                }
            }
            case "else" -> {
                if (state.getCurrent() == RustContext.IF_BODY) {
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
        if (context.parent instanceof RustParser.GenericArgsTypesContext) {
            transformToken(TYPE_ARGUMENT, context.getStart());
        }

        state.enter(RustContext.TYPE);
        super.enterType_(context);
    }

    @Override
    public void exitType_(RustParser.Type_Context context) {
        state.leave(RustContext.TYPE);
        super.exitType_(context);
    }

    @Override
    public void enterGenericArg(RustParser.GenericArgContext context) {
        transformToken(TYPE_ARGUMENT, context.getStart());
        super.enterGenericArg(context);
    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext context) {
        // ExpressionContext gets no own enter/exit method
        // used in various 'lists' of elements
        if (context instanceof RustParser.ExpressionContext expression) {
            if (context.parent instanceof RustParser.ArrayElementsContext) {
                transformToken(ARRAY_ELEMENT, expression.getStart());
            } else if (context.parent instanceof RustParser.CallParamsContext) {
                transformToken(ARGUMENT, expression.getStart());
            } else if (context.parent instanceof RustParser.TuplePatternItemsContext || context.parent instanceof RustParser.TupleElementsContext) {
                if (state.getCurrent() == RustContext.REDUNDANT_TUPLE)
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
        if (context instanceof RustParser.ExpressionContext) {
            if (context.parent instanceof RustParser.ClosureExpressionContext) {
                transformToken(CLOSURE_BODY_END, context.getStop());
            }
        }
    }

    /**
     * Implementation of Context for the Rust language
     */
    enum RustContext implements ParserState.Context {
        /** This is used to make sure that the stack is not empty -> getCurrent() != null **/
        FILE(NONE, NONE),

        /**
         * These contexts are used to assign the correct tokens to '{' and '}' terminals.
         **/
        FUNCTION_BODY(FUNCTION_BODY_START, FUNCTION_BODY_END),
        PROCEDURE_BODY(FUNCTION_BODY_START, FUNCTION_BODY_END),
        STRUCT_BODY(STRUCT_BODY_BEGIN, STRUCT_BODY_END),
        IF_BODY(IF_BODY_START, IF_BODY_END),
        ELSE_BODY(ELSE_BODY_START, ELSE_BODY_END),
        LOOP_BODY(LOOP_BODY_START, LOOP_BODY_END),
        INNER_BLOCK(INNER_BLOCK_START, INNER_BLOCK_END),
        TRAIT_BODY(TRAIT_BODY_START, TRAIT_BODY_END),
        ENUM_BODY(ENUM_BODY_START, ENUM_BODY_END),
        MACRO_RULES_DEFINITION_BODY(MACRO_RULES_DEFINITION_BODY_START, MACRO_RULES_DEFINITION_BODY_END),
        MACRO_RULE_BODY(MACRO_RULE_BODY_START, MACRO_RULE_BODY_END),
        MACRO_INVOCATION_BODY(MACRO_INVOCATION_BODY_START, NONE),
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
        TYPE(NONE, NONE),

        /**
         * These are to identify expressions as elements of tuples.
         */
        TUPLE_STRUCT_PATTERN(NONE, NONE),
        TUPLE_PATTERN(NONE, NONE),

        /**
         * This is used so that cascades of tuples like '((((1),2),(3)))' generate only as many tokens as necessary.
         */
        REDUNDANT_TUPLE(NONE, NONE),

        /**
         * This is used to be able to correctly assign MACRO_INVOCATION_BODY_END to a '}' symbol.
         */
        MACRO_INNER(NONE, NONE),

        /**
         * In this context, leaves are USE_ITEMS.
         */
        USE_TREE(NONE, NONE),

        /**
         * In this context, '(' should be assigned an APPLY token.
         */
        CALL(NONE, NONE);

        private final int startType;
        private final int endType;

        RustContext(int startType, int endType) {
            this.startType = startType;
            this.endType = endType;
        }

        @Override
        public int getStartType() {
            return startType;
        }

        @Override
        public int getEndType() {
            return endType;
        }
    }
}
