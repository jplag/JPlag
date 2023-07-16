package de.jplag.cpp2;

import static de.jplag.cpp2.CPPTokenType.*;

import java.io.File;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.TokenCollector;
import de.jplag.cpp2.grammar.CPP14Parser;
import de.jplag.cpp2.grammar.CPP14Parser.*;

/**
 * Extracts tokens from the ANTLR parse tree. Token extraction is built to be similar to the Java language module. In
 * some cases, the grammar is ambiguous and requires surrounding context to extract the correct token.
 * <p>
 * Those cases are covered by {@link SimpleTypeSpecifierContext} and {@link SimpleDeclarationContext}
 */
public class CPPListener extends AbstractAntlrListener {
    /**
     * New instance
     * @param collector The token collector the token will be added to
     * @param currentFile The currently processed file
     */
    public CPPListener(TokenCollector collector, File currentFile) {
        super(collector, currentFile);

        mapEnterExit(ClassSpecifierContext.class, UNION_BEGIN, UNION_END, rule -> rule.classHead().Union() != null);
        mapEnterExit(ClassSpecifierContext.class, CLASS_BEGIN, CLASS_END,
                rule -> rule.classHead().classKey() != null && rule.classHead().classKey().Class() != null);
        mapEnterExit(ClassSpecifierContext.class, STRUCT_BEGIN, STRUCT_END,
                rule -> rule.classHead().classKey() != null && rule.classHead().classKey().Struct() != null);
        mapEnterExit(EnumSpecifierContext.class, ENUM_BEGIN, ENUM_END);

        mapEnterExit(FunctionDefinitionContext.class, FUNCTION_BEGIN, FUNCTION_END);

        mapEnterExit(IterationStatementContext.class, DO_BEGIN, DO_END, rule -> rule.Do() != null);
        mapEnterExit(IterationStatementContext.class, FOR_BEGIN, FOR_END, rule -> rule.For() != null);
        mapEnterExit(IterationStatementContext.class, WHILE_BEGIN, WHILE_END, rule -> rule.While() != null && rule.Do() == null);

        mapEnterExit(SelectionStatementContext.class, SWITCH_BEGIN, SWITCH_END, rule -> rule.Switch() != null);
        mapEnterExit(SelectionStatementContext.class, IF_BEGIN, IF_END, rule -> rule.If() != null);
        mapTerminal(CPP14Parser.Else, ELSE);

        mapEnter(LabeledStatementContext.class, CASE, rule -> rule.Case() != null);
        mapEnter(LabeledStatementContext.class, DEFAULT, rule -> rule.Default() != null);

        mapEnter(TryBlockContext.class, TRY);
        mapEnterExit(HandlerContext.class, CATCH_BEGIN, CATCH_END);

        mapEnter(JumpStatementContext.class, BREAK, rule -> rule.Break() != null);
        mapEnter(JumpStatementContext.class, CONTINUE, rule -> rule.Continue() != null);
        mapEnter(JumpStatementContext.class, GOTO, rule -> rule.Goto() != null);
        mapEnter(JumpStatementContext.class, RETURN, rule -> rule.Return() != null);

        mapEnter(ThrowExpressionContext.class, THROW);

        mapEnter(NewExpressionContext.class, NEWCLASS, rule -> rule.newInitializer() != null);
        mapEnter(NewExpressionContext.class, NEWARRAY, rule -> rule.newInitializer() == null);

        mapEnter(TemplateDeclarationContext.class, GENERIC);

        mapEnter(AssignmentOperatorContext.class, ASSIGN);
        mapEnter(BraceOrEqualInitializerContext.class, ASSIGN, rule -> rule.Assign() != null);
        mapEnter(UnaryExpressionContext.class, ASSIGN, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null);

        mapEnter(StaticAssertDeclarationContext.class, STATIC_ASSERT);
        mapEnter(EnumeratorDefinitionContext.class, VARDEF);
        mapEnterExit(BracedInitListContext.class, BRACED_INIT_BEGIN, BRACED_INIT_END);

        mapEnter(SimpleTypeSpecifierContext.class, VARDEF, rule -> {
            if (hasAncestor(rule, MemberdeclarationContext.class, FunctionDefinitionContext.class)) {
                return true;
            }

            if (hasAncestor(rule, SimpleDeclarationContext.class, TemplateArgumentContext.class, FunctionDefinitionContext.class)) {
                SimpleDeclarationContext parent = getAncestor(rule, SimpleDeclarationContext.class);
                NoPointerDeclaratorContext noPointerDecl = getDescendant(parent, NoPointerDeclaratorContext.class);

                return (!noPointerInFunctionCallContext(noPointerDecl)) && !hasAncestor(rule, NewTypeIdContext.class);
            }

            return false;
        });

        mapEnter(SimpleDeclarationContext.class, APPLY, rule -> {
            if (!hasAncestor(rule, FunctionBodyContext.class)) {
                return false;
            }

            NoPointerDeclaratorContext noPointerDecl = getDescendant(rule, NoPointerDeclaratorContext.class);
            return noPointerInFunctionCallContext(noPointerDecl);
        });

        mapEnter(InitDeclaratorContext.class, APPLY, rule -> rule.initializer() != null && rule.initializer().LeftParen() != null);
        mapEnter(ParameterDeclarationContext.class, VARDEF);
        mapEnter(ConditionalExpressionContext.class, QUESTIONMARK, rule -> rule.Question() != null);

        mapEnter(PostfixExpressionContext.class, APPLY, rule -> rule.LeftParen() != null);
        mapEnter(PostfixExpressionContext.class, ASSIGN, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null);
    }

    /**
     * @return true of this context represents a function call
     */
    private static boolean noPointerInFunctionCallContext(NoPointerDeclaratorContext context) {
        return context != null && (context.parametersAndQualifiers() != null || context.LeftParen() != null);
    }
}
