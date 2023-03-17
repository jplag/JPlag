package de.jplag.cpp2;

import static de.jplag.cpp2.CPPTokenType.APPLY;
import static de.jplag.cpp2.CPPTokenType.ASSIGN;
import static de.jplag.cpp2.CPPTokenType.BRACED_INIT_BEGIN;
import static de.jplag.cpp2.CPPTokenType.BRACED_INIT_END;
import static de.jplag.cpp2.CPPTokenType.BREAK;
import static de.jplag.cpp2.CPPTokenType.CASE;
import static de.jplag.cpp2.CPPTokenType.CATCH_BEGIN;
import static de.jplag.cpp2.CPPTokenType.CATCH_END;
import static de.jplag.cpp2.CPPTokenType.CLASS_BEGIN;
import static de.jplag.cpp2.CPPTokenType.CLASS_END;
import static de.jplag.cpp2.CPPTokenType.CONTINUE;
import static de.jplag.cpp2.CPPTokenType.DEFAULT;
import static de.jplag.cpp2.CPPTokenType.DO_BEGIN;
import static de.jplag.cpp2.CPPTokenType.DO_END;
import static de.jplag.cpp2.CPPTokenType.ELSE;
import static de.jplag.cpp2.CPPTokenType.ENUM_BEGIN;
import static de.jplag.cpp2.CPPTokenType.ENUM_END;
import static de.jplag.cpp2.CPPTokenType.FOR_BEGIN;
import static de.jplag.cpp2.CPPTokenType.FOR_END;
import static de.jplag.cpp2.CPPTokenType.FUNCTION_BEGIN;
import static de.jplag.cpp2.CPPTokenType.FUNCTION_END;
import static de.jplag.cpp2.CPPTokenType.GENERIC;
import static de.jplag.cpp2.CPPTokenType.GOTO;
import static de.jplag.cpp2.CPPTokenType.IF_BEGIN;
import static de.jplag.cpp2.CPPTokenType.IF_END;
import static de.jplag.cpp2.CPPTokenType.NEWARRAY;
import static de.jplag.cpp2.CPPTokenType.NEWCLASS;
import static de.jplag.cpp2.CPPTokenType.QUESTIONMARK;
import static de.jplag.cpp2.CPPTokenType.RETURN;
import static de.jplag.cpp2.CPPTokenType.STATIC_ASSERT;
import static de.jplag.cpp2.CPPTokenType.STRUCT_BEGIN;
import static de.jplag.cpp2.CPPTokenType.STRUCT_END;
import static de.jplag.cpp2.CPPTokenType.SWITCH_BEGIN;
import static de.jplag.cpp2.CPPTokenType.SWITCH_END;
import static de.jplag.cpp2.CPPTokenType.THROW;
import static de.jplag.cpp2.CPPTokenType.TRY;
import static de.jplag.cpp2.CPPTokenType.UNION_BEGIN;
import static de.jplag.cpp2.CPPTokenType.UNION_END;
import static de.jplag.cpp2.CPPTokenType.VARDEF;
import static de.jplag.cpp2.CPPTokenType.WHILE_BEGIN;
import static de.jplag.cpp2.CPPTokenType.WHILE_END;
import static de.jplag.cpp2.grammar.CPP14Parser.RULE_selectionStatement;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import de.jplag.TokenType;
import de.jplag.cpp2.grammar.CPP14Parser.AssignmentOperatorContext;
import de.jplag.cpp2.grammar.CPP14Parser.BraceOrEqualInitializerContext;
import de.jplag.cpp2.grammar.CPP14Parser.BracedInitListContext;
import de.jplag.cpp2.grammar.CPP14Parser.ClassSpecifierContext;
import de.jplag.cpp2.grammar.CPP14Parser.ConditionalExpressionContext;
import de.jplag.cpp2.grammar.CPP14Parser.EnumSpecifierContext;
import de.jplag.cpp2.grammar.CPP14Parser.EnumeratorDefinitionContext;
import de.jplag.cpp2.grammar.CPP14Parser.FunctionBodyContext;
import de.jplag.cpp2.grammar.CPP14Parser.FunctionDefinitionContext;
import de.jplag.cpp2.grammar.CPP14Parser.HandlerContext;
import de.jplag.cpp2.grammar.CPP14Parser.IterationStatementContext;
import de.jplag.cpp2.grammar.CPP14Parser.JumpStatementContext;
import de.jplag.cpp2.grammar.CPP14Parser.LabeledStatementContext;
import de.jplag.cpp2.grammar.CPP14Parser.MemberdeclarationContext;
import de.jplag.cpp2.grammar.CPP14Parser.NewExpressionContext;
import de.jplag.cpp2.grammar.CPP14Parser.NewTypeIdContext;
import de.jplag.cpp2.grammar.CPP14Parser.NoPointerDeclaratorContext;
import de.jplag.cpp2.grammar.CPP14Parser.ParameterDeclarationContext;
import de.jplag.cpp2.grammar.CPP14Parser.PostfixExpressionContext;
import de.jplag.cpp2.grammar.CPP14Parser.SelectionStatementContext;
import de.jplag.cpp2.grammar.CPP14Parser.SimpleDeclarationContext;
import de.jplag.cpp2.grammar.CPP14Parser.SimpleTypeSpecifierContext;
import de.jplag.cpp2.grammar.CPP14Parser.StatementContext;
import de.jplag.cpp2.grammar.CPP14Parser.StaticAssertDeclarationContext;
import de.jplag.cpp2.grammar.CPP14Parser.TemplateArgumentContext;
import de.jplag.cpp2.grammar.CPP14Parser.TemplateDeclarationContext;
import de.jplag.cpp2.grammar.CPP14Parser.ThrowExpressionContext;
import de.jplag.cpp2.grammar.CPP14Parser.TryBlockContext;
import de.jplag.cpp2.grammar.CPP14Parser.UnaryExpressionContext;
import de.jplag.cpp2.grammar.CPP14ParserBaseListener;

/**
 * Extracts tokens from the ANTLR parse tree. Token extraction is built to be similar to the Java language module. In
 * some cases, the grammar is ambiguous and requires surrounding context to extract the correct token. Those cases are
 * covered by {@link #enterSimpleTypeSpecifier(SimpleTypeSpecifierContext)} and
 * {@link #enterSimpleDeclaration(SimpleDeclarationContext)}.
 */
public class CPPTokenListener extends CPP14ParserBaseListener {

    private final CPPParserAdapter parser;
    private final Deque<TokenType> trackedState = new ArrayDeque<>();
    private Token lastElseToken;

    /**
     * Constructs a new token listener that will extract tokens to the given {@link CPPParserAdapter}.
     * @param parser the adapter to pass extracted tokens to.
     */
    public CPPTokenListener(CPPParserAdapter parser) {
        this.parser = parser;
    }

    private static final List<Extraction<ClassSpecifierContext>> CLASS_SPECIFIER_TOKENS = List.of(
            Extraction.of(context -> context.classHead().Union(), UNION_BEGIN, UNION_END),
            Extraction.of(context -> context.classHead().classKey().Class(), CLASS_BEGIN, CLASS_END),
            Extraction.of(context -> context.classHead().classKey().Struct(), STRUCT_BEGIN, STRUCT_END));

    @Override
    public void enterClassSpecifier(ClassSpecifierContext context) {
        extractFirstNonNullStartToken(context, context.getStart(), CLASS_SPECIFIER_TOKENS);
    }

    @Override
    public void exitClassSpecifier(ClassSpecifierContext context) {
        extractFirstNonNullEndToken(context, context.getStop(), CLASS_SPECIFIER_TOKENS);
    }

    @Override
    public void enterEnumSpecifier(EnumSpecifierContext context) {
        addEnter(ENUM_BEGIN, context.getStart());
    }

    @Override
    public void exitEnumSpecifier(EnumSpecifierContext context) {
        addExit(ENUM_END, context.getStop());
    }

    @Override
    public void enterFunctionDefinition(FunctionDefinitionContext context) {
        addEnter(FUNCTION_BEGIN, context.getStart());
    }

    @Override
    public void exitFunctionDefinition(FunctionDefinitionContext context) {
        addExit(FUNCTION_END, context.getStop());
    }

    private static final List<Extraction<IterationStatementContext>> ITERATION_STATEMENT_TOKENS = List.of(
            Extraction.of(IterationStatementContext::Do, DO_BEGIN, DO_END), Extraction.of(IterationStatementContext::For, FOR_BEGIN, FOR_END),
            Extraction.of(IterationStatementContext::While, WHILE_BEGIN, WHILE_END));

    @Override
    public void enterIterationStatement(IterationStatementContext context) {
        extractFirstNonNullStartToken(context, context.getStart(), ITERATION_STATEMENT_TOKENS);
    }

    @Override
    public void exitIterationStatement(IterationStatementContext context) {
        extractFirstNonNullEndToken(context, context.getStop(), ITERATION_STATEMENT_TOKENS);
    }

    /**
     * Extract tokens for {@code if} and {@code switch}. To extract {@link CPPTokenType#ELSE} after the tokens inside the if
     * block but before the tokens in the else block, {@link #trackedState} works as a stack of the current state.
     * {@link CPPTokenType#IF_END} is only extracted after the whole tree element (including else), to be consistent with
     * the Java language module.
     * @param context the selection statement.
     */
    @Override
    public void enterSelectionStatement(SelectionStatementContext context) {
        if (context.Switch() != null) {
            addEnter(SWITCH_BEGIN, context.getStart());
            this.trackedState.add(CPPTokenType.SWITCH_END);
        } else if (context.If() != null) {
            addEnter(IF_BEGIN, context.getStart());
            if (context.Else() != null) {
                this.trackedState.add(ELSE);
                this.lastElseToken = context.Else().getSymbol();
            }
            this.trackedState.add(CPPTokenType.IF_END);
        }
    }

    @Override
    public void enterStatement(StatementContext context) {
        if (context.getParent().getRuleIndex() == RULE_selectionStatement && this.trackedState.peekLast() == CPPTokenType.ELSE) {
            addEnter(trackedState.removeLast(), this.lastElseToken);
        }
    }

    @Override
    public void exitStatement(StatementContext context) {
        if (context.getParent().getRuleIndex() == RULE_selectionStatement && this.trackedState.peekLast() == CPPTokenType.IF_END) {
            // drop if end token from state, but do not add it yet (see exitSelectionStatement)
            trackedState.removeLast();
        }
    }

    @Override
    public void exitSelectionStatement(SelectionStatementContext context) {
        if (context.Switch() != null) {
            addEnter(SWITCH_END, context.getStop());
        } else if (context.If() != null) {
            addEnter(IF_END, context.getStop());
        }
    }

    private static final List<Extraction<LabeledStatementContext>> LABELED_STATEMENT_TOKES = List
            .of(Extraction.of(LabeledStatementContext::Case, CASE), Extraction.of(LabeledStatementContext::Default, DEFAULT));

    @Override
    public void enterLabeledStatement(LabeledStatementContext context) {
        extractFirstNonNullStartToken(context, context.start, LABELED_STATEMENT_TOKES);
    }

    @Override
    public void enterTryBlock(TryBlockContext context) {
        addEnter(TRY, context.getStart());
    }

    @Override
    public void enterHandler(HandlerContext context) {
        addEnter(CATCH_BEGIN, context.getStart());
    }

    @Override
    public void exitHandler(HandlerContext context) {
        addEnter(CATCH_END, context.getStop());
    }

    private static final List<Extraction<JumpStatementContext>> JUMP_STATEMENT_TOKENS = List.of(Extraction.of(JumpStatementContext::Break, BREAK),
            Extraction.of(JumpStatementContext::Continue, CONTINUE), Extraction.of(JumpStatementContext::Goto, GOTO),
            Extraction.of(JumpStatementContext::Return, RETURN));

    @Override
    public void enterJumpStatement(JumpStatementContext context) {
        extractFirstNonNullStartToken(context, context.getStart(), JUMP_STATEMENT_TOKENS);
    }

    @Override
    public void enterThrowExpression(ThrowExpressionContext context) {
        addEnter(THROW, context.getStart());
    }

    private static final List<Extraction<NewExpressionContext>> NEW_EXPRESSION_TOKENS = List
            .of(Extraction.of(NewExpressionContext::newInitializer, NEWCLASS), Extraction.fallback(NEWARRAY));

    @Override
    public void enterNewExpression(NewExpressionContext context) {
        extractFirstNonNullStartToken(context, context.getStart(), NEW_EXPRESSION_TOKENS);
    }

    @Override
    public void enterTemplateDeclaration(TemplateDeclarationContext context) {
        addEnter(GENERIC, context.getStart());
    }

    @Override
    public void enterAssignmentOperator(AssignmentOperatorContext context) {
        // does not cover ++, --, this is done via UnaryExpressionContext and PostfixExpressionContext
        // does not cover all =, this is done via BraceOrEqualInitializerContext
        addEnter(ASSIGN, context.getStart());
    }

    @Override
    public void enterBraceOrEqualInitializer(BraceOrEqualInitializerContext context) {
        if (context.Assign() != null) {
            addEnter(ASSIGN, context.getStart());
        }
    }

    @Override
    public void enterUnaryExpression(UnaryExpressionContext context) {
        if (context.PlusPlus() != null || context.MinusMinus() != null) {
            addEnter(ASSIGN, context.getStart());
        }
    }

    @Override
    public void enterStaticAssertDeclaration(StaticAssertDeclarationContext context) {
        addEnter(STATIC_ASSERT, context.getStart());
    }

    @Override
    public void enterEnumeratorDefinition(EnumeratorDefinitionContext context) {
        addEnter(VARDEF, context.getStart());
    }

    @Override
    public void enterBracedInitList(BracedInitListContext context) {
        addEnter(BRACED_INIT_BEGIN, context.getStart());
    }

    @Override
    public void exitBracedInitList(BracedInitListContext context) {
        addExit(BRACED_INIT_END, context.getStop());
    }

    /**
     * Covers {@link CPPTokenType#VARDEF} extraction. The grammar is ambiguous here, so inspecting the surrounding tree
     * elements is required to not extract {@link CPPTokenType#VARDEF} in places of type declarations, function calls and
     * template arguments.
     */
    @Override
    public void enterSimpleTypeSpecifier(SimpleTypeSpecifierContext context) {
        if (hasAncestor(context, MemberdeclarationContext.class, FunctionDefinitionContext.class)) {
            addEnter(VARDEF, context.getStart());
        } else if (hasAncestor(context, SimpleDeclarationContext.class, TemplateArgumentContext.class, FunctionDefinitionContext.class)) {
            // part of a SimpleDeclaration without being part of
            // - a TemplateArgument (vector<HERE> v)
            // - a FunctionDefinition (return type, parameters) (parameters are extracted in enterParameterDeclaration as VARDEF)
            // first.
            SimpleDeclarationContext parent = getAncestor(context, SimpleDeclarationContext.class);
            assert parent != null; // already checked by hasAncestor
            NoPointerDeclaratorContext noPointerDecl = getDescendant(parent, NoPointerDeclaratorContext.class);
            if ((!noPointerInFunctionCallContext(noPointerDecl)) && !hasAncestor(context, NewTypeIdContext.class)) {
                // 'new <Type>' does not declare a new variable
                addEnter(VARDEF, context.getStart());
            }
        }
    }

    @Override
    public void enterSimpleDeclaration(SimpleDeclarationContext context) {
        if (!hasAncestor(context, FunctionBodyContext.class)) {
            // not in a context where a function call can appear, assume it's a function definition
            return;
        }
        NoPointerDeclaratorContext noPointerDecl = getDescendant(context, NoPointerDeclaratorContext.class);
        if (noPointerInFunctionCallContext(noPointerDecl)) {
            // method calls like A::b(), b()
            addEnter(APPLY, noPointerDecl.getStart());
        }
    }

    /**
     * {@return true of this context represents a function call}
     */
    private static boolean noPointerInFunctionCallContext(NoPointerDeclaratorContext context) {
        return context != null && (context.parametersAndQualifiers() != null || context.LeftParen() != null);
    }

    @Override
    public void enterParameterDeclaration(ParameterDeclarationContext context) {
        addEnter(VARDEF, context.getStart());
    }

    @Override
    public void enterConditionalExpression(ConditionalExpressionContext context) {
        if (context.Question() != null) {
            addEnter(QUESTIONMARK, context.getStart());
        }
    }

    private static final List<Extraction<PostfixExpressionContext>> POSTFIX_EXPRESSION_TOKENS = List.of(
            Extraction.of(PostfixExpressionContext::LeftParen, APPLY), Extraction.of(PostfixExpressionContext::PlusPlus, ASSIGN),
            Extraction.of(PostfixExpressionContext::MinusMinus, ASSIGN));

    @Override
    public void enterPostfixExpression(PostfixExpressionContext context) {
        // additional function calls are handled in SimpleDeclarationContext
        extractFirstNonNullStartToken(context, context.getStart(), POSTFIX_EXPRESSION_TOKENS);
    }

    /**
     * Searches a subtree for a descendant of a specific type. Search is done breath-first.
     * @param context the context to search the subtree from.
     * @param descendant the class representing the type to search for.
     * @param <T> the type to search for.
     * @return the first appearance of an element of the given type in the subtree, or null if no such element exists.
     */
    private <T extends ParserRuleContext> T getDescendant(ParserRuleContext context, Class<T> descendant) {
        // simple iterative bfs
        ArrayDeque<ParserRuleContext> queue = new ArrayDeque<>();
        queue.add(context);
        while (!queue.isEmpty()) {
            ParserRuleContext next = queue.removeFirst();
            for (ParseTree tree : next.children) {
                if (tree.getClass() == descendant) {
                    return descendant.cast(tree);
                }
                if (tree instanceof ParserRuleContext parserRuleContext) {
                    queue.addLast(parserRuleContext);
                }
            }
        }
        return null;
    }

    /**
     * Searches the ancestors of an element for an element of the specific type.
     * @param context the current element to start the search from.
     * @param ancestor the class representing the type to search for.
     * @param stops the types of elements to stop the upward search at.
     * @param <T> the type of the element to search for.
     * @return an ancestor of the specified type, or null if not found.
     */
    @SafeVarargs
    private <T extends ParserRuleContext> T getAncestor(ParserRuleContext context, Class<T> ancestor, Class<? extends ParserRuleContext>... stops) {
        ParserRuleContext currentcontext = context;
        Set<Class<? extends ParserRuleContext>> forbidden = Set.of(stops);
        do {
            ParserRuleContext next = currentcontext.getParent();
            if (next == null) {
                return null;
            }
            if (next.getClass() == ancestor) {
                return ancestor.cast(next);
            }
            if (forbidden.contains(next.getClass())) {
                return null;
            }
            currentcontext = next;
        } while (true);
    }

    /**
     * {@return true if an ancestor of the specified type exists}
     * @param context the current element to start the search from.
     * @param parent the class representing the type to search for.
     * @param stops the types of elements to stop the upward search at.
     * @see #getAncestor(ParserRuleContext, Class, Class[])
     */
    @SafeVarargs
    private boolean hasAncestor(ParserRuleContext context, Class<? extends ParserRuleContext> parent, Class<? extends ParserRuleContext>... stops) {
        return getAncestor(context, parent, stops) != null;
    }

    // extraction utilities

    private void addEnter(TokenType type, Token token) {
        addTokenWithLength(type, token, token.getText().length());
    }

    private void addExit(TokenType type, Token token) {
        addTokenWithLength(type, token, 1);
    }

    private void addTokenWithLength(TokenType type, Token token, int length) {
        int column = token.getCharPositionInLine() + 1;
        this.parser.addToken(type, column, token.getLine(), length);
    }

    /**
     * Describes an extraction rule. If the extraction test returns true, one of the given token types is extracted.
     * @param extractionTest the test whether the rule matches.
     * @param startToken the token extracted for this rule in {@code enter*} contexts
     * @param endToken the token extracted for this rule in {@code exit*} contexts
     * @param <T> the input type
     */
    private record Extraction<T>(Predicate<T> extractionTest, TokenType startToken, TokenType endToken) {

        /**
         * Creates an Extraction rule that matches if the value returned by the given function is non-null.
         */
        static <T> Extraction<T> of(Function<T, ?> contextToAnything, TokenType startToken) {
            return of(contextToAnything, startToken, null);
        }

        static <T> Extraction<T> of(Function<T, ?> contextToAnything, TokenType startToken, TokenType endToken) {
            // go from (T -> ?) to (T -> boolean)
            Predicate<T> isNonNull = t -> contextToAnything.andThen(Objects::nonNull).apply(t);
            return new Extraction<>(isNonNull, startToken, endToken);
        }

        static <T> Extraction<T> fallback(TokenType toExtract) {
            return new Extraction<>(t -> true, toExtract, null);
        }
    }

    private <T> void extractFirstNonNullEndToken(T context, Token token, List<Extraction<T>> extractions) {
        extractFirstNonNull(context, token, extractions, false);
    }

    private <T> void extractFirstNonNullStartToken(T context, Token token, List<Extraction<T>> extractions) {
        extractFirstNonNull(context, token, extractions, true);
    }

    private <T> void extractFirstNonNull(T context, Token token, List<Extraction<T>> extractions, boolean start) {
        for (Extraction<? super T> extraction : extractions) {
            if (extraction.extractionTest().test(context)) {
                if (start) {
                    addEnter(extraction.startToken(), token);
                } else {
                    addExit(extraction.endToken(), token);
                }
                return;
            }
        }
    }
}
