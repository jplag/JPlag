package de.jplag.cpp2;

import java.util.ArrayDeque;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
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

    public CPPTokenListener(CPPParserAdapter parser) {
        this.parser = parser;
    }

    @Override
    public void enterCompoundStatement(CPP14Parser.CompoundStatementContext context) {
        addEnter(CPPTokenType.BLOCK_BEGIN, context.getStart());
    }

    @Override
    public void exitCompoundStatement(CPP14Parser.CompoundStatementContext context) {
        addExit(CPPTokenType.BLOCK_END, context.getStop());
    }

    @Override
    public void enterClassSpecifier(CPP14Parser.ClassSpecifierContext context) {
        CPP14Parser.ClassKeyContext classKey = context.classHead().classKey();
        if (classKey.Class() != null) {
            addEnter(CPPTokenType.CLASS_BEGIN, context.getStart());
        } else if (classKey.Struct() != null) {
            addEnter(CPPTokenType.STRUCT_BEGIN, context.getStart());
        }
    }

    @Override
    public void exitClassSpecifier(CPP14Parser.ClassSpecifierContext context) {
        CPP14Parser.ClassKeyContext classKey = context.classHead().classKey();
        if (classKey.Class() != null) {
            addExit(CPPTokenType.CLASS_END, context.getStop());
        } else if (classKey.Struct() != null) {
            addExit(CPPTokenType.STRUCT_END, context.getStop());
        }
    }

    @Override
    public void enterEnumSpecifier(CPP14Parser.EnumSpecifierContext context) {
        addEnter(CPPTokenType.ENUM_BEGIN, context.getStart());
    }

    @Override
    public void exitEnumSpecifier(CPP14Parser.EnumSpecifierContext context) {
        addExit(CPPTokenType.ENUM_END, context.getStop());
    }

    @Override
    public void enterFunctionDefinition(CPP14Parser.FunctionDefinitionContext context) {
        addEnter(CPPTokenType.FUNCTION_BEGIN, context.getStart());
    }

    @Override
    public void exitFunctionDefinition(CPP14Parser.FunctionDefinitionContext context) {
        addExit(CPPTokenType.FUNCTION_END, context.getStop());
    }

    @Override
    public void enterIterationStatement(CPP14Parser.IterationStatementContext context) {
        if (context.Do() != null) {
            addEnter(CPPTokenType.DO_BEGIN, context.getStart());
        } else if (context.For() != null) {
            addEnter(CPPTokenType.FOR_BEGIN, context.getStart());
        } else if (context.While() != null) {
            addEnter(CPPTokenType.WHILE_BEGIN, context.getStart());
        }
    }

    @Override
    public void exitIterationStatement(CPP14Parser.IterationStatementContext context) {
        if (context.Do() != null) {
            addEnter(CPPTokenType.DO_END, context.getStop());
        } else if (context.For() != null) {
            addEnter(CPPTokenType.FOR_END, context.getStop());
        } else if (context.While() != null) {
            addEnter(CPPTokenType.WHILE_END, context.getStop());
        }
    }

    @Override
    public void enterSelectionStatement(CPP14Parser.SelectionStatementContext context) {
        if (context.Switch() != null) {
            addEnter(CPPTokenType.SWITCH_BEGIN, context.getStart());
        } else if (context.If() != null) {
            addEnter(CPPTokenType.IF_BEGIN, context.getStart());
            if (context.Else() != null) {
                addEnter(CPPTokenType.ELSE, context.Else().getSymbol());
            }
        }
    }

    @Override
    public void exitSelectionStatement(CPP14Parser.SelectionStatementContext context) {
        if (context.Switch() != null) {
            addEnter(CPPTokenType.SWITCH_END, context.getStop());
        } else if (context.If() != null) {
            addEnter(CPPTokenType.IF_END, context.getStop());
        }
    }

    @Override
    public void enterLabeledStatement(CPP14Parser.LabeledStatementContext context) {
        if (context.Case() != null) {
            addEnter(CPPTokenType.CASE, context.getStart());
        } else if (context.Default() != null) {
            addEnter(CPPTokenType.DEFAULT, context.getStart());
        }
    }

    @Override
    public void enterTryBlock(CPP14Parser.TryBlockContext context) {
        addEnter(CPPTokenType.TRY, context.getStart());
    }

    @Override
    public void enterHandler(CPP14Parser.HandlerContext context) {
        addEnter(CPPTokenType.CATCH_BEGIN, context.getStart());
    }

    @Override
    public void exitHandler(CPP14Parser.HandlerContext context) {
        addEnter(CPPTokenType.CATCH_END, context.getStop());
    }

    @Override
    public void enterJumpStatement(CPP14Parser.JumpStatementContext context) {
        if (context.Break() != null) {
            addEnter(CPPTokenType.BREAK, context.getStart());
        } else if (context.Continue() != null) {
            addEnter(CPPTokenType.CONTINUE, context.getStart());
        } else if (context.Goto() != null) {
            addEnter(CPPTokenType.GOTO, context.getStart());
        } else if (context.Return() != null) {
            addEnter(CPPTokenType.RETURN, context.getStart());
        }
    }

    @Override
    public void enterThrowExpression(CPP14Parser.ThrowExpressionContext context) {
        addEnter(CPPTokenType.THROW, context.getStart());
    }

    @Override
    public void enterNewExpression(CPP14Parser.NewExpressionContext context) {
        // TODO NEWARRAY, ARRAYINIT
        if (context.newInitializer() == null) {
            addEnter(CPPTokenType.NEWARRAY, context.getStart());
        } else {
            addEnter(CPPTokenType.NEWCLASS, context.getStart());
        }
    }

    @Override
    public void enterTemplateDeclaration(CPP14Parser.TemplateDeclarationContext context) {
        addEnter(CPPTokenType.GENERIC, context.getStart());
    }

    @Override
    public void enterAssignmentOperator(CPP14Parser.AssignmentOperatorContext context) {
        /*
         does not cover ++, --, this is done via UnaryExpressionContext and PostfixExpressionContext
         does not cover all =, this is done via BraceOrEqualInitializerContext
        */
        addEnter(CPPTokenType.ASSIGN, context.getStart());
    }

    @Override
    public void enterBraceOrEqualInitializer(CPP14Parser.BraceOrEqualInitializerContext context) {
        if (context.Assign() != null) {
            addEnter(CPPTokenType.ASSIGN, context.getStart());
        }
    }

    @Override
    public void enterUnaryExpression(CPP14Parser.UnaryExpressionContext context) {
        if (context.PlusPlus() != null || context.MinusMinus() != null) {
            addEnter(CPPTokenType.ASSIGN, context.getStart());
        }
    }

    @Override
    public void enterStaticAssertDeclaration(CPP14Parser.StaticAssertDeclarationContext context) {
        addEnter(CPPTokenType.STATIC_ASSERT, context.getStart());
    }

    @Override
    public void enterEnumeratorDefinition(CPP14Parser.EnumeratorDefinitionContext context) {
        addEnter(CPPTokenType.VARDEF, context.getStart());
    }

    /**
     * @param context the parse tree
     */
    @Override
    public void enterSimpleTypeSpecifier(CPP14Parser.SimpleTypeSpecifierContext context) {
        if (hasAncestor(context, CPP14Parser.MemberdeclarationContext.class, CPP14Parser.FunctionDefinitionContext.class)) {
            addEnter(CPPTokenType.VARDEF, context.getStart());
        } else if (hasAncestor(context, CPP14Parser.SimpleDeclarationContext.class, CPP14Parser.TemplateArgumentContext.class,
                CPP14Parser.FunctionDefinitionContext.class)) {
            /*
             part of a SimpleDeclaration without being part of
             - a TemplateArgument (vector<HERE> v)
             - a FunctionDefinition (return type, parameters) (parameters are extracted in enterParameterDeclaration as VARDEF)
             first.
            */
            CPP14Parser.SimpleDeclarationContext parent = getAncestor(context, CPP14Parser.SimpleDeclarationContext.class);
            assert parent != null; // already checked by hasAncestor
            CPP14Parser.NoPointerDeclaratorContext noPointerDecl = getDescendant(parent, CPP14Parser.NoPointerDeclaratorContext.class);
            if ((!noPointerInFunctionCallContext(noPointerDecl)) && !hasAncestor(context, CPP14Parser.NewTypeIdContext.class)) {
                // 'new <Type>' does not declare a new variable
                addEnter(CPPTokenType.VARDEF, context.getStart());
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
            addEnter(CPPTokenType.APPLY, noPointerDecl.getStart());
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
        addEnter(CPPTokenType.VARDEF, context.getStart());
    }

    @Override
    public void enterConditionalExpression(CPP14Parser.ConditionalExpressionContext context) {
        if (context.Question() != null) {
            addEnter(CPPTokenType.QUESTIONMARK, context.getStart());
        }
    }

    @Override
    public void enterPostfixExpression(CPP14Parser.PostfixExpressionContext context) {
        // additional function calls are handled in SimpleDeclarationContext
        if (context.LeftParen() != null) {
            addEnter(CPPTokenType.APPLY, context.getStart());
        } else if (context.PlusPlus() != null || context.MinusMinus() != null) {
            addEnter(CPPTokenType.ASSIGN, context.getStart());
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

    private void addEnter(TokenType type, org.antlr.v4.runtime.Token token) {
        int column = token.getCharPositionInLine() + 1;
        parser.addToken(type, column, token.getLine(), token.getText().length());
    }

    private void addExit(TokenType type, org.antlr.v4.runtime.Token token) {
        this.parser.addToken(type, CPPParserAdapter.USE_PREVIOUS_COLUMN, token.getLine(), 1);
    }
}
