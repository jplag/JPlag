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

        createStartStopMapping(ClassSpecifierContext.class, UNION_BEGIN, UNION_END, rule -> rule.classHead().Union() != null);
        createStartStopMapping(ClassSpecifierContext.class, CLASS_BEGIN, CLASS_END,
                rule -> rule.classHead().classKey() != null && rule.classHead().classKey().Class() != null);
        createStartStopMapping(ClassSpecifierContext.class, STRUCT_BEGIN, STRUCT_END,
                rule -> rule.classHead().classKey() != null && rule.classHead().classKey().Struct() != null);
        createStartStopMapping(EnumSpecifierContext.class, ENUM_BEGIN, ENUM_END);

        createStartStopMapping(FunctionDefinitionContext.class, FUNCTION_BEGIN, FUNCTION_END);

        createStartStopMapping(IterationStatementContext.class, DO_BEGIN, DO_END, rule -> rule.Do() != null);
        createStartStopMapping(IterationStatementContext.class, FOR_BEGIN, FOR_END, rule -> rule.For() != null);
        createStartStopMapping(IterationStatementContext.class, WHILE_BEGIN, WHILE_END, rule -> rule.While() != null && rule.Do() == null);

        createStartStopMapping(SelectionStatementContext.class, SWITCH_BEGIN, SWITCH_END, rule -> rule.Switch() != null);
        createStartStopMapping(SelectionStatementContext.class, IF_BEGIN, IF_END, rule -> rule.If() != null);
        createTerminalMapping(CPP14Parser.Else, ELSE);

        createStartMapping(LabeledStatementContext.class, CASE, rule -> rule.Case() != null);
        createStartMapping(LabeledStatementContext.class, DEFAULT, rule -> rule.Default() != null);

        createStartMapping(TryBlockContext.class, TRY);
        createStartStopMapping(HandlerContext.class, CATCH_BEGIN, CATCH_END);

        createStartMapping(JumpStatementContext.class, BREAK, rule -> rule.Break() != null);
        createStartMapping(JumpStatementContext.class, CONTINUE, rule -> rule.Continue() != null);
        createStartMapping(JumpStatementContext.class, GOTO, rule -> rule.Goto() != null);
        createStartMapping(JumpStatementContext.class, RETURN, rule -> rule.Return() != null);

        createStartMapping(ThrowExpressionContext.class, THROW);

        createStartMapping(NewExpressionContext.class, NEWCLASS, rule -> rule.newInitializer() != null);
        createStartMapping(NewExpressionContext.class, NEWARRAY, rule -> rule.newInitializer() == null);

        createStartMapping(TemplateDeclarationContext.class, GENERIC);

        createStartMapping(AssignmentOperatorContext.class, ASSIGN);
        createStartMapping(BraceOrEqualInitializerContext.class, ASSIGN, rule -> rule.Assign() != null);
        createStartMapping(UnaryExpressionContext.class, ASSIGN, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null);

        createStartMapping(StaticAssertDeclarationContext.class, STATIC_ASSERT);
        createStartMapping(EnumeratorDefinitionContext.class, VARDEF);
        createStartStopMapping(BracedInitListContext.class, BRACED_INIT_BEGIN, BRACED_INIT_END);

        createStartMapping(SimpleTypeSpecifierContext.class, VARDEF, rule -> {
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

        createStartMapping(SimpleDeclarationContext.class, APPLY, rule -> {
            if (!hasAncestor(rule, FunctionBodyContext.class)) {
                return false;
            }

            NoPointerDeclaratorContext noPointerDecl = getDescendant(rule, NoPointerDeclaratorContext.class);
            return noPointerInFunctionCallContext(noPointerDecl);
        });

        createStartMapping(InitDeclaratorContext.class, APPLY, rule -> rule.initializer() != null && rule.initializer().LeftParen() != null);
        createStartMapping(ParameterDeclarationContext.class, VARDEF);
        createStartMapping(ConditionalExpressionContext.class, QUESTIONMARK, rule -> rule.Question() != null);

        createStartMapping(PostfixExpressionContext.class, APPLY, rule -> rule.LeftParen() != null);
        createStartMapping(PostfixExpressionContext.class, ASSIGN, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null);
    }

    /**
     * @return true of this context represents a function call
     */
    private static boolean noPointerInFunctionCallContext(NoPointerDeclaratorContext context) {
        return context != null && (context.parametersAndQualifiers() != null || context.LeftParen() != null);
    }
}
