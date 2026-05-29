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

import java.util.List;
import java.util.Objects;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.rust.grammar.RustParser;
import de.jplag.rust.grammar.RustParser.ArrayElementsContext;
import de.jplag.rust.grammar.RustParser.ArrayExpressionContext;
import de.jplag.rust.grammar.RustParser.AttrInputContext;
import de.jplag.rust.grammar.RustParser.BreakExpressionContext;
import de.jplag.rust.grammar.RustParser.CallExpressionContext;
import de.jplag.rust.grammar.RustParser.CallParamsContext;
import de.jplag.rust.grammar.RustParser.ClosureExpressionContext;
import de.jplag.rust.grammar.RustParser.ClosureParamContext;
import de.jplag.rust.grammar.RustParser.CompoundAssignmentExpressionContext;
import de.jplag.rust.grammar.RustParser.DelimTokenTreeContext;
import de.jplag.rust.grammar.RustParser.EnumItemContext;
import de.jplag.rust.grammar.RustParser.EnumItemTupleContext;
import de.jplag.rust.grammar.RustParser.EnumerationContext;
import de.jplag.rust.grammar.RustParser.ExpressionContext;
import de.jplag.rust.grammar.RustParser.ExpressionStatementContext;
import de.jplag.rust.grammar.RustParser.ExpressionWithBlockContext;
import de.jplag.rust.grammar.RustParser.ExternBlockContext;
import de.jplag.rust.grammar.RustParser.ExternCrateContext;
import de.jplag.rust.grammar.RustParser.ExternalItemContext;
import de.jplag.rust.grammar.RustParser.ForLifetimesContext;
import de.jplag.rust.grammar.RustParser.FunctionParamContext;
import de.jplag.rust.grammar.RustParser.Function_Context;
import de.jplag.rust.grammar.RustParser.GenericArgContext;
import de.jplag.rust.grammar.RustParser.GenericArgsBindingContext;
import de.jplag.rust.grammar.RustParser.GenericArgsTypesContext;
import de.jplag.rust.grammar.RustParser.GenericParamContext;
import de.jplag.rust.grammar.RustParser.IfExpressionContext;
import de.jplag.rust.grammar.RustParser.IfLetExpressionContext;
import de.jplag.rust.grammar.RustParser.ImplementationContext;
import de.jplag.rust.grammar.RustParser.InfiniteLoopExpressionContext;
import de.jplag.rust.grammar.RustParser.InnerAttributeContext;
import de.jplag.rust.grammar.RustParser.IteratorLoopExpressionContext;
import de.jplag.rust.grammar.RustParser.LoopLabelContext;
import de.jplag.rust.grammar.RustParser.MacroInvocationContext;
import de.jplag.rust.grammar.RustParser.MacroInvocationSemiContext;
import de.jplag.rust.grammar.RustParser.MacroPunctuationTokenContext;
import de.jplag.rust.grammar.RustParser.MacroRuleContext;
import de.jplag.rust.grammar.RustParser.MacroRulesDefinitionContext;
import de.jplag.rust.grammar.RustParser.MatchArmContext;
import de.jplag.rust.grammar.RustParser.MatchArmGuardContext;
import de.jplag.rust.grammar.RustParser.MatchExpressionContext;
import de.jplag.rust.grammar.RustParser.ModuleContext;
import de.jplag.rust.grammar.RustParser.OuterAttributeContext;
import de.jplag.rust.grammar.RustParser.PathExprSegmentContext;
import de.jplag.rust.grammar.RustParser.PathInExpressionContext;
import de.jplag.rust.grammar.RustParser.PatternContext;
import de.jplag.rust.grammar.RustParser.PredicateLoopExpressionContext;
import de.jplag.rust.grammar.RustParser.PredicatePatternLoopExpressionContext;
import de.jplag.rust.grammar.RustParser.ReturnExpressionContext;
import de.jplag.rust.grammar.RustParser.SelfParamContext;
import de.jplag.rust.grammar.RustParser.SimplePathContext;
import de.jplag.rust.grammar.RustParser.StaticItemContext;
import de.jplag.rust.grammar.RustParser.StructExprFieldContext;
import de.jplag.rust.grammar.RustParser.StructExpressionContext;
import de.jplag.rust.grammar.RustParser.StructExpression_Context;
import de.jplag.rust.grammar.RustParser.StructFieldContext;
import de.jplag.rust.grammar.RustParser.StructPatternContext;
import de.jplag.rust.grammar.RustParser.StructPatternFieldContext;
import de.jplag.rust.grammar.RustParser.Struct_Context;
import de.jplag.rust.grammar.RustParser.Trait_Context;
import de.jplag.rust.grammar.RustParser.TupleElementsContext;
import de.jplag.rust.grammar.RustParser.TupleExpressionContext;
import de.jplag.rust.grammar.RustParser.TupleFieldContext;
import de.jplag.rust.grammar.RustParser.TuplePatternContext;
import de.jplag.rust.grammar.RustParser.TuplePatternItemsContext;
import de.jplag.rust.grammar.RustParser.TupleStructItemsContext;
import de.jplag.rust.grammar.RustParser.TupleStructPatternContext;
import de.jplag.rust.grammar.RustParser.TypeAliasContext;
import de.jplag.rust.grammar.RustParser.TypeParamContext;
import de.jplag.rust.grammar.RustParser.Type_Context;
import de.jplag.rust.grammar.RustParser.Union_Context;
import de.jplag.rust.grammar.RustParser.UseDeclarationContext;
import de.jplag.rust.grammar.RustParser.UseTreeContext;

/**
 * Contains the token extraction rules for the rust language module.
 */
public class RustListener extends AbstractAntlrListener {
    /**
     * New instance.
     */
    public RustListener() {
        structureRules();
        imperativeRules();
        curlyRules();
        parenthesisRules();
        expressionRules();
    }

    private void structureRules() {
        visit(InnerAttributeContext.class).mapRange(INNER_ATTRIBUTE);
        visit(OuterAttributeContext.class).mapRange(OUTER_ATTRIBUTE);
        visit(UseDeclarationContext.class).map(USE_DECLARATION);
        visit(SimplePathContext.class,
                (c) -> hasScope(c, Scope.USE_TREE) && !(c.parent.getChildCount() > 1 && "::".equals(c.parent.getChild(1).getText())))
                        .mapRange(USE_ITEM);
        visit(RustParser.STAR, node -> node.getParent() instanceof UseTreeContext).map(USE_ITEM);

        visit(ModuleContext.class).map(MODULE);
        visit(Struct_Context.class).map(STRUCT);
        visit(StructExpression_Context.class).map(STRUCT_INITIALISATION);
        visit(StructExpressionContext.class).map(STRUCT_INITIALISATION);
        visit(StructFieldContext.class).map(STRUCT_FIELD);
        visit(StructPatternContext.class).map(STRUCT);
        visit(StructPatternFieldContext.class).map(STRUCT_FIELD);
        visit(TupleStructPatternContext.class).map(STRUCT_INITIALISATION);
        visit(Union_Context.class).map(UNION);
        visit(TypeAliasContext.class).map(TYPE_ALIAS);
        visit(ImplementationContext.class).map(IMPLEMENTATION);
        visit(EnumerationContext.class).map(ENUM);
        visit(EnumItemContext.class).map(ENUM_ITEM);

        visit(Type_Context.class,
                c -> c.getParent() instanceof GenericArgsTypesContext && c.getParent().getParent().getParent() instanceof PathExprSegmentContext)
                        .mapRange(TYPE_ARGUMENT);
        visit(GenericArgContext.class, c -> c.getParent().getParent() instanceof PathInExpressionContext).mapRange(TYPE_ARGUMENT);
    }

    private void imperativeRules() {
        visit(MacroRulesDefinitionContext.class).map(MACRO_RULES_DEFINITION);
        visit(MacroRuleContext.class).map(MACRO_RULE);
        visit(MacroInvocationSemiContext.class).map(MACRO_INVOCATION);
        visit(MacroInvocationContext.class).map(MACRO_INVOCATION);
        visit(ExternBlockContext.class).map(EXTERN_BLOCK);
        visit(ExternCrateContext.class).map(EXTERN_CRATE);
        visit(StaticItemContext.class, c -> c.getParent() instanceof ExternalItemContext).map(STATIC_ITEM);
        visit(StaticItemContext.class, c -> !(c.getParent() instanceof ExternalItemContext)).map(VARIABLE_DECLARATION);
        visit(Function_Context.class).delegateTerminal(c -> c.getChild(TerminalNodeImpl.class, 0)).map(FUNCTION);
        visit(SelfParamContext.class).mapRange(FUNCTION_PARAMETER);
        visit(FunctionParamContext.class).mapRange(FUNCTION_PARAMETER);
        visit(GenericParamContext.class, c -> !(c.getParent().getParent() instanceof ForLifetimesContext)).mapRange(TYPE_PARAMETER);
        visit(IfExpressionContext.class).mapRange(IF_STATEMENT);
        visit(IfLetExpressionContext.class).map(IF_STATEMENT);
        visit(RustParser.KW_ELSE).map(ELSE_STATEMENT);
        visit(LoopLabelContext.class).mapRange(LABEL);
        visit(InfiniteLoopExpressionContext.class).delegateTerminal(c -> c.getChild(TerminalNodeImpl.class, 0)).map(LOOP_STATEMENT);
        visit(PredicateLoopExpressionContext.class).delegateTerminal(c -> c.getChild(TerminalNodeImpl.class, 0)).map(LOOP_STATEMENT);
        visit(PredicatePatternLoopExpressionContext.class).delegateTerminal(c -> c.getChild(TerminalNodeImpl.class, 0)).map(LOOP_STATEMENT);
        visit(IteratorLoopExpressionContext.class).delegateTerminal(c -> c.getChild(TerminalNodeImpl.class, 0)).map(FOR_STATEMENT);
        visit(BreakExpressionContext.class).mapRange(BREAK);
        visit(MatchExpressionContext.class).map(MATCH_EXPRESSION);
        visit(MatchArmContext.class).map(MATCH_CASE);
        visit(MatchArmGuardContext.class).map(MATCH_GUARD);
        visit(CompoundAssignmentExpressionContext.class).map(ASSIGNMENT);
        visit(ArrayExpressionContext.class).map(ARRAY_BODY_START, ARRAY_BODY_END);
        visit(TuplePatternContext.class).map(TUPLE);
        visit(ClosureExpressionContext.class).map(CLOSURE);
        visit(ClosureParamContext.class).map(FUNCTION_PARAMETER);
        visit(ReturnExpressionContext.class).map(RETURN);
        visit(RustParser.KW_LET, node -> !hasScope(node, Scope.MACRO_INNER)).map(VARIABLE_DECLARATION);
        visit(RustParser.EQ, node -> !hasParentType(node, AttrInputContext.class, MacroPunctuationTokenContext.class, TypeParamContext.class,
                GenericArgsBindingContext.class) && !hasScope(node, Scope.MACRO_INNER)).map(ASSIGNMENT);

        visit(StructExprFieldContext.class).mapRange(ARGUMENT);
        visit(TupleFieldContext.class, (c) -> !hasScope(c, Scope.REDUNDANT_TUPLE)).map(TUPLE_ELEMENT);
        visit(Trait_Context.class).map(TRAIT);

        visit(PatternContext.class, c -> hasScope(c, Scope.TUPLE_STRUCT_PATTERN)).mapRange(ARGUMENT);
        visit(PatternContext.class, c -> hasScope(c, Scope.TUPLE_PATTERN)).map(TUPLE_ELEMENT);
    }

    private void expressionRules() {
        visit(ExpressionContext.class, c -> c.getParent() instanceof ArrayElementsContext).map(ARRAY_ELEMENT);
        visit(ExpressionContext.class, c -> c.getParent() instanceof CallParamsContext).mapRange(ARGUMENT);
        visit(ExpressionContext.class, c -> (c.getParent() instanceof TuplePatternItemsContext || c.getParent() instanceof TupleElementsContext)
                && !hasScope(c, Scope.REDUNDANT_TUPLE)).map(TUPLE_ELEMENT);
        visit(ExpressionContext.class, c -> c.getParent() instanceof ClosureExpressionContext).map(CLOSURE_BODY_START, CLOSURE_BODY_END);
        visit(ExpressionContext.class, c -> c.getParent() instanceof ClosureExpressionContext).map(RETURN);

        visit(ExpressionStatementContext.class, c -> {
            RuleContext maybeFunctionBlock = c.parent.parent;
            return maybeFunctionBlock instanceof RustParser.StatementsContext && maybeFunctionBlock.getChildCount() == 1
                    && hasScope(c, Scope.FUNCTION_BODY) && !(c.getChild(0) instanceof RustParser.ReturnExpressionContext);
        }).map(RETURN);
    }

    private void parenthesisRules() {
        visit(RustParser.LPAREN, node -> hasScope(node, Scope.STRUCT_DECLARATION_BODY)).map(STRUCT_BODY_START);
        visit(RustParser.LPAREN, node -> hasScope(node, Scope.TUPLE)).map(TUPLE_START);
        visit(RustParser.LPAREN, node -> hasScope(node, Scope.MACRO_INVOCATION_BODY)).map(MACRO_INVOCATION_BODY_START);
        visit(RustParser.LPAREN, node -> hasScope(node, Scope.CALL)).map(APPLY);
        visit(RustParser.LSQUAREBRACKET, node -> hasScope(node, Scope.MACRO_INVOCATION_BODY)).map(MACRO_INVOCATION_BODY_START);

        visit(RustParser.RPAREN, node -> hasScope(node, Scope.STRUCT_DECLARATION_BODY)).map(STRUCT_BODY_END);
        visit(RustParser.RPAREN, node -> hasScope(node, Scope.TUPLE)).map(TUPLE_END);
        visit(RustParser.RPAREN, node -> hasScope(node, Scope.MACRO_INVOCATION_BODY)).map(MACRO_INVOCATION_BODY_END);
        visit(RustParser.RSQUAREBRACKET, node -> hasScope(node, Scope.MACRO_INVOCATION_BODY)).map(MACRO_INVOCATION_BODY_END);
    }

    private void curlyRules() {
        curlyOpeningRules();
        curlyClosingRules();
    }

    private void curlyOpeningRules() {
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.FUNCTION_BODY)).map(FUNCTION_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.PROCEDURE_BODY)).map(FUNCTION_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.STRUCT_DECLARATION_BODY)).map(STRUCT_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.IF_BODY)).map(IF_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.ELSE_BODY)).map(ELSE_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.LOOP_BODY)).map(LOOP_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.INNER_BLOCK)).map(INNER_BLOCK_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.TRAIT_BODY)).map(TRAIT_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.ENUM_BODY)).map(ENUM_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.MACRO_RULES_DEFINITION_BODY)).map(MACRO_RULES_DEFINITION_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.MACRO_RULE_BODY)).map(MACRO_RULE_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.MACRO_INVOCATION_BODY)).map(MACRO_INVOCATION_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.IMPLEMENTATION_BODY)).map(IMPLEMENTATION_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.EXTERN_BLOCK)).map(EXTERN_BLOCK_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.MODULE_BODY)).map(MODULE_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.UNION_BODY)).map(UNION_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.CLOSURE_BODY)).map(CLOSURE_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.MATCH_BODY)).map(MATCH_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.FOR_BODY)).map(FOR_BODY_START);
        visit(RustParser.LCURLYBRACE, node -> hasScope(node, Scope.TUPLE)).map(TUPLE_START);
    }

    private void curlyClosingRules() {
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.FUNCTION_BODY)).map(FUNCTION_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.PROCEDURE_BODY)).map(FUNCTION_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.STRUCT_DECLARATION_BODY)).map(STRUCT_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.IF_BODY)).map(IF_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.ELSE_BODY)).map(ELSE_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.LOOP_BODY)).map(LOOP_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.INNER_BLOCK)).map(INNER_BLOCK_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.TRAIT_BODY)).map(TRAIT_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.ENUM_BODY)).map(ENUM_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.MACRO_RULES_DEFINITION_BODY)).map(MACRO_RULES_DEFINITION_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.MACRO_RULE_BODY)).map(MACRO_RULE_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.MACRO_INVOCATION_BODY)).map(MACRO_INVOCATION_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.IMPLEMENTATION_BODY)).map(IMPLEMENTATION_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.EXTERN_BLOCK)).map(EXTERN_BLOCK_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.MODULE_BODY)).map(MODULE_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.UNION_BODY)).map(UNION_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.CLOSURE_BODY)).map(CLOSURE_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.MATCH_BODY)).map(MATCH_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.FOR_BODY)).map(FOR_BODY_END);
        visit(RustParser.RCURLYBRACE, node -> hasScope(node, Scope.TUPLE)).map(TUPLE_END);
    }

    private boolean hasParentType(ParseTree node, Class<? extends ParserRuleContext>... parents) {
        return List.of(parents).stream().anyMatch(type -> type.isAssignableFrom(node.getParent().getClass()));
    }

    private boolean hasScope(ParseTree context, Scope... scopes) {
        return List.of(scopes).contains(determineScopeOf(context));
    }

    private Scope determineScopeOf(ParseTree context) {
        return switch (context) {
            case UseTreeContext _ -> Scope.USE_TREE;
            case ModuleContext _ -> Scope.MODULE_BODY;
            case Struct_Context _ -> Scope.STRUCT_DECLARATION_BODY;
            case StructExpression_Context _ -> Scope.STRUCT_INITIALISATION;
            case StructPatternContext _ -> Scope.STRUCT_DECLARATION_BODY;
            case TupleExpressionContext tuple -> {
                var elements = tuple.getChild(TupleElementsContext.class, 0);
                // one child = exactly one subtree and no trailing comma
                if (Objects.nonNull(elements) && elements.getChildCount() == 1) {
                    yield Scope.REDUNDANT_TUPLE;
                }
                yield Scope.TUPLE;
            }
            case TupleStructPatternContext _ -> Scope.STRUCT_INITIALISATION;
            case TupleStructItemsContext _ -> Scope.TUPLE_STRUCT_PATTERN;
            case TuplePatternItemsContext _ -> Scope.TUPLE_PATTERN;
            case Union_Context _ -> Scope.UNION_BODY;
            case Trait_Context _ -> Scope.TRAIT_BODY;
            case ImplementationContext _ -> Scope.IMPLEMENTATION_BODY;
            case EnumerationContext _ -> Scope.ENUM_BODY;
            case EnumItemTupleContext _ -> Scope.TUPLE;
            case MacroRulesDefinitionContext _ -> Scope.MACRO_RULES_DEFINITION_BODY;
            case MacroRuleContext _ -> Scope.MACRO_RULE_BODY;
            case MacroInvocationSemiContext _,RustParser.MacroInvocationContext _ -> Scope.MACRO_INVOCATION_BODY;
            case ExternBlockContext _ -> Scope.EXTERN_BLOCK;
            case Function_Context functionContext -> {
                if (functionContext.getChild(RustParser.FunctionReturnTypeContext.class, 0) != null) {
                    yield Scope.FUNCTION_BODY;
                } else {
                    yield Scope.PROCEDURE_BODY;
                }
            }
            case ExpressionWithBlockContext _ -> Scope.INNER_BLOCK;
            case IfExpressionContext _,RustParser.IfLetExpressionContext _ -> Scope.IF_BODY;
            case InfiniteLoopExpressionContext _,RustParser.PredicateLoopExpressionContext _,PredicatePatternLoopExpressionContext _ -> Scope.LOOP_BODY;
            case IteratorLoopExpressionContext _ -> Scope.FOR_BODY;
            case MatchExpressionContext _ -> Scope.MATCH_BODY;
            case CallExpressionContext _,RustParser.MethodCallExpressionContext _ -> Scope.CALL;
            case TuplePatternContext _ -> Scope.TUPLE;
            case ClosureExpressionContext _ -> Scope.CLOSURE_BODY;
            case Type_Context _ -> Scope.TYPE;
            case DelimTokenTreeContext delimTokenTreeContext when !(delimTokenTreeContext.getParent() instanceof RustParser.MacroTranscriberContext
                    || delimTokenTreeContext.getParent() instanceof RustParser.MacroInvocationContext) -> Scope.MACRO_INNER;

            default -> {
                if (context.getParent() == null) {
                    yield Scope.FILE;
                }

                if (context.getParent() instanceof ParserRuleContext parent) {
                    int index = parent.children.indexOf(context);
                    if (index > 0) {
                        ParseTree sibling = parent.children.get(index - 1);
                        if (sibling instanceof TerminalNode terminal) {
                            if (terminal.getSymbol().getText().equals("else")) {
                                yield Scope.ELSE_BODY;
                            }
                        }
                    }

                    yield determineScopeOf(parent);
                }

                yield Scope.FILE;
            }
        };
    }

    /**
     * Implementation of Context for the Rust language.
     */
    enum Scope {

        /**
         * This is used to make sure that the stack is not empty -> getCurrent() != null.
         **/
        FILE,

        /**
         * These contexts are used to assign the correct tokens to '{' and '}' terminals.
         **/
        FUNCTION_BODY,
        PROCEDURE_BODY,
        STRUCT_DECLARATION_BODY,
        IF_BODY,
        ELSE_BODY,
        LOOP_BODY,
        INNER_BLOCK,
        TRAIT_BODY,
        ENUM_BODY,
        MACRO_RULES_DEFINITION_BODY,
        MACRO_RULE_BODY,
        MACRO_INVOCATION_BODY,
        IMPLEMENTATION_BODY,
        EXTERN_BLOCK,
        MODULE_BODY,
        UNION_BODY,
        CLOSURE_BODY,
        MATCH_BODY,
        FOR_BODY,
        TUPLE,

        /**
         * This is to avoid the empty type `()` being parsed as an empty tuple etc.
         **/
        TYPE,

        /**
         * These are to identify expressions as elements of tuples.
         */
        TUPLE_STRUCT_PATTERN,
        TUPLE_PATTERN,

        /**
         * This is used so that cascades of tuples like '((((1),2),(3)))' generate only as many tokens as necessary.
         */
        REDUNDANT_TUPLE,

        /**
         * This is used to be able to correctly assign MACRO_INVOCATION_BODY_END to a '}' symbol.
         */
        MACRO_INNER,

        /**
         * In this context, leaves are USE_ITEMS.
         */
        USE_TREE,

        /**
         * In this context, '(' should be assigned an APPLY token.
         */
        CALL,

        /**
         * This context should behave like a function call: No tokens for parentheses.
         */
        STRUCT_INITIALISATION
    }
}
