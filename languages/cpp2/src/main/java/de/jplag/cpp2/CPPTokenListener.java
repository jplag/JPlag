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

import java.util.ArrayDeque;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import de.jplag.TokenType;
import de.jplag.cpp2.grammar.CPP14Parser;
import de.jplag.cpp2.grammar.CPP14ParserBaseListener;

/**
 * Extracts tokens from the ANTLR parse tree. Token extraction is built to be similar to the Java language module. In
 * some cases, the grammar is ambiguous and requires surrounding context to extract the correct token. Those cases are
 * covered by {@link #enterSimpleTypeSpecifier(CPP14Parser.SimpleTypeSpecifierContext)} and
 * {@link #enterSimpleDeclaration(CPP14Parser.SimpleDeclarationContext)}.
 */
public class CPPTokenListener extends CPP14ParserBaseListener {

    private final CPPParserAdapter parser;

    /**
     * Constructs a new token listener that will extract tokens to the given {@link CPPParserAdapter}.
     * @param parser the adapter to pass extracted tokens to.
     */
    public CPPTokenListener(CPPParserAdapter parser) {
        this.parser = parser;
    }

    @Override
    public void enterClassSpecifier(CPP14Parser.ClassSpecifierContext context) {
        if (context.classHead().Union() != null) {
            addEnter(UNION_BEGIN, context.getStart());
        } else {
            CPP14Parser.ClassKeyContext classKey = context.classHead().classKey();
            if (classKey.Class() != null) {
                addEnter(CLASS_BEGIN, context.getStart());
            } else if (classKey.Struct() != null) {
                addEnter(STRUCT_BEGIN, context.getStart());
            }
        }
    }

    @Override
    public void exitClassSpecifier(CPP14Parser.ClassSpecifierContext context) {
        if (context.classHead().Union() != null) {
            addExit(UNION_END, context.getStop());
        } else {
            CPP14Parser.ClassKeyContext classKey = context.classHead().classKey();
            if (classKey.Class() != null) {
                addExit(CLASS_END, context.getStop());
            } else if (classKey.Struct() != null) {
                addExit(STRUCT_END, context.getStop());
            }
        }
    }

    @Override
    public void enterEnumSpecifier(CPP14Parser.EnumSpecifierContext context) {
        addEnter(ENUM_BEGIN, context.getStart());
    }

    @Override
    public void exitEnumSpecifier(CPP14Parser.EnumSpecifierContext context) {
        addExit(ENUM_END, context.getStop());
    }

    @Override
    public void enterFunctionDefinition(CPP14Parser.FunctionDefinitionContext context) {
        addEnter(FUNCTION_BEGIN, context.getStart());
    }

    @Override
    public void exitFunctionDefinition(CPP14Parser.FunctionDefinitionContext context) {
        addExit(FUNCTION_END, context.getStop());
    }

    @Override
    public void enterIterationStatement(CPP14Parser.IterationStatementContext context) {
        if (context.Do() != null) {
            addEnter(DO_BEGIN, context.getStart());
        } else if (context.For() != null) {
            addEnter(FOR_BEGIN, context.getStart());
        } else if (context.While() != null) {
            addEnter(WHILE_BEGIN, context.getStart());
        }
    }

    @Override
    public void exitIterationStatement(CPP14Parser.IterationStatementContext context) {
        if (context.Do() != null) {
            addEnter(DO_END, context.getStop());
        } else if (context.For() != null) {
            addEnter(FOR_END, context.getStop());
        } else if (context.While() != null) {
            addEnter(WHILE_END, context.getStop());
        }
    }

    @Override
    public void enterSelectionStatement(CPP14Parser.SelectionStatementContext context) {
        if (context.Switch() != null) {
            addEnter(SWITCH_BEGIN, context.getStart());
        } else if (context.If() != null) {
            addEnter(IF_BEGIN, context.getStart());
            if (context.Else() != null) {
                addEnter(ELSE, context.Else().getSymbol());
            }
        }
    }

    @Override
    public void exitSelectionStatement(CPP14Parser.SelectionStatementContext context) {
        if (context.Switch() != null) {
            addEnter(SWITCH_END, context.getStop());
        } else if (context.If() != null) {
            addEnter(IF_END, context.getStop());
        }
    }

    @Override
    public void enterLabeledStatement(CPP14Parser.LabeledStatementContext context) {
        if (context.Case() != null) {
            addEnter(CASE, context.getStart());
        } else if (context.Default() != null) {
            addEnter(DEFAULT, context.getStart());
        }
    }

    @Override
    public void enterTryBlock(CPP14Parser.TryBlockContext context) {
        addEnter(TRY, context.getStart());
    }

    @Override
    public void enterHandler(CPP14Parser.HandlerContext context) {
        addEnter(CATCH_BEGIN, context.getStart());
    }

    @Override
    public void exitHandler(CPP14Parser.HandlerContext context) {
        addEnter(CATCH_END, context.getStop());
    }

    @Override
    public void enterJumpStatement(CPP14Parser.JumpStatementContext context) {
        if (context.Break() != null) {
            addEnter(BREAK, context.getStart());
        } else if (context.Continue() != null) {
            addEnter(CONTINUE, context.getStart());
        } else if (context.Goto() != null) {
            addEnter(GOTO, context.getStart());
        } else if (context.Return() != null) {
            addEnter(RETURN, context.getStart());
        }
    }

    @Override
    public void enterThrowExpression(CPP14Parser.ThrowExpressionContext context) {
        addEnter(THROW, context.getStart());
    }

    @Override
    public void enterNewExpression(CPP14Parser.NewExpressionContext context) {
        if (context.newInitializer() == null) {
            addEnter(NEWARRAY, context.getStart());
        } else {
            addEnter(NEWCLASS, context.getStart());
        }
    }

    @Override
    public void enterTemplateDeclaration(CPP14Parser.TemplateDeclarationContext context) {
        addEnter(GENERIC, context.getStart());
    }

    @Override
    public void enterAssignmentOperator(CPP14Parser.AssignmentOperatorContext context) {
        // does not cover ++, --, this is done via UnaryExpressionContext and PostfixExpressionContext
        // does not cover all =, this is done via BraceOrEqualInitializerContext
        addEnter(ASSIGN, context.getStart());
    }

    @Override
    public void enterBraceOrEqualInitializer(CPP14Parser.BraceOrEqualInitializerContext context) {
        if (context.Assign() != null) {
            addEnter(ASSIGN, context.getStart());
        }
    }

    @Override
    public void enterUnaryExpression(CPP14Parser.UnaryExpressionContext context) {
        if (context.PlusPlus() != null || context.MinusMinus() != null) {
            addEnter(ASSIGN, context.getStart());
        }
    }

    @Override
    public void enterStaticAssertDeclaration(CPP14Parser.StaticAssertDeclarationContext context) {
        addEnter(STATIC_ASSERT, context.getStart());
    }

    @Override
    public void enterEnumeratorDefinition(CPP14Parser.EnumeratorDefinitionContext context) {
        addEnter(VARDEF, context.getStart());
    }

    @Override
    public void enterBracedInitList(CPP14Parser.BracedInitListContext context) {
        addEnter(BRACED_INIT_BEGIN, context.getStart());
    }

    @Override
    public void exitBracedInitList(CPP14Parser.BracedInitListContext context) {
        addExit(BRACED_INIT_END, context.getStop());
    }

    /**
     * Covers {@link CPPTokenType#VARDEF} extraction. The grammar is ambiguous here, so inspecting the surrounding tree
     * elements is required to not extract {@link CPPTokenType#VARDEF} in places of type declarations, function calls and
     * template arguments.
     */
    @Override
    public void enterSimpleTypeSpecifier(CPP14Parser.SimpleTypeSpecifierContext context) {
        if (hasAncestor(context, CPP14Parser.MemberdeclarationContext.class, CPP14Parser.FunctionDefinitionContext.class)) {
            addEnter(VARDEF, context.getStart());
        } else if (hasAncestor(context, CPP14Parser.SimpleDeclarationContext.class, CPP14Parser.TemplateArgumentContext.class,
                CPP14Parser.FunctionDefinitionContext.class)) {
            // part of a SimpleDeclaration without being part of
            // - a TemplateArgument (vector<HERE> v)
            // - a FunctionDefinition (return type, parameters) (parameters are extracted in enterParameterDeclaration as VARDEF)
            // first.
            CPP14Parser.SimpleDeclarationContext parent = getAncestor(context, CPP14Parser.SimpleDeclarationContext.class);
            assert parent != null; // already checked by hasAncestor
            CPP14Parser.NoPointerDeclaratorContext noPointerDecl = getDescendant(parent, CPP14Parser.NoPointerDeclaratorContext.class);
            if ((!noPointerInFunctionCallContext(noPointerDecl)) && !hasAncestor(context, CPP14Parser.NewTypeIdContext.class)) {
                // 'new <Type>' does not declare a new variable
                addEnter(VARDEF, context.getStart());
            }
        }
    }

    @Override
    public void enterSimpleDeclaration(CPP14Parser.SimpleDeclarationContext context) {
        if (!hasAncestor(context, CPP14Parser.FunctionBodyContext.class)) {
            // not in a context where a function call can appear, assume it's a function definition
            return;
        }
        CPP14Parser.NoPointerDeclaratorContext noPointerDecl = getDescendant(context, CPP14Parser.NoPointerDeclaratorContext.class);
        if (noPointerInFunctionCallContext(noPointerDecl)) {
            // method calls like A::b(), b()
            addEnter(APPLY, noPointerDecl.getStart());
        }
    }

    /**
     * {@return true of this context represents a function call}
     */
    private static boolean noPointerInFunctionCallContext(CPP14Parser.NoPointerDeclaratorContext context) {
        return context != null && (context.parametersAndQualifiers() != null || context.LeftParen() != null);
    }

    @Override
    public void enterParameterDeclaration(CPP14Parser.ParameterDeclarationContext context) {
        addEnter(VARDEF, context.getStart());
    }

    @Override
    public void enterConditionalExpression(CPP14Parser.ConditionalExpressionContext context) {
        if (context.Question() != null) {
            addEnter(QUESTIONMARK, context.getStart());
        }
    }

    @Override
    public void enterPostfixExpression(CPP14Parser.PostfixExpressionContext context) {
        // additional function calls are handled in SimpleDeclarationContext
        if (context.LeftParen() != null) {
            addEnter(APPLY, context.getStart());
        } else if (context.PlusPlus() != null || context.MinusMinus() != null) {
            addEnter(ASSIGN, context.getStart());
        }
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
}
