package de.jplag.cpp2;

import static de.jplag.cpp2.CPPTokenType.*;

import de.jplag.antlr.AbstractAntlrListener;
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
     */
    public CPPListener() {
        super();

        visit(ClassSpecifierContext.class, rule -> rule.classHead().Union() != null).map(UNION_BEGIN, UNION_END);
        visit(ClassSpecifierContext.class, rule -> rule.classHead().classKey() != null && rule.classHead().classKey().Class() != null)
                .map(CLASS_BEGIN, CLASS_END);
        visit(ClassSpecifierContext.class, rule -> rule.classHead().classKey() != null && rule.classHead().classKey().Struct() != null)
                .map(STRUCT_BEGIN, STRUCT_END);
        visit(EnumSpecifierContext.class).map(ENUM_BEGIN, ENUM_END);

        visit(FunctionDefinitionContext.class).map(FUNCTION_BEGIN, FUNCTION_END);

        visit(IterationStatementContext.class, rule -> rule.Do() != null).map(DO_BEGIN, DO_END);
        visit(IterationStatementContext.class, rule -> rule.For() != null).map(FOR_BEGIN, FOR_END);
        visit(IterationStatementContext.class, rule -> rule.While() != null && rule.Do() == null).map(WHILE_BEGIN, WHILE_END);

        visit(SelectionStatementContext.class, rule -> rule.Switch() != null).map(SWITCH_BEGIN, SWITCH_END);
        visit(SelectionStatementContext.class, rule -> rule.If() != null).map(IF_BEGIN, IF_END);
        visit(CPP14Parser.Else).map(ELSE);

        visit(LabeledStatementContext.class, rule -> rule.Case() != null).map(CASE);
        visit(LabeledStatementContext.class, rule -> rule.Default() != null).map(DEFAULT);

        visit(TryBlockContext.class).map(TRY);
        visit(HandlerContext.class).map(CATCH_BEGIN, CATCH_END);

        visit(JumpStatementContext.class, rule -> rule.Break() != null).map(BREAK);
        visit(JumpStatementContext.class, rule -> rule.Continue() != null).map(CONTINUE);
        visit(JumpStatementContext.class, rule -> rule.Goto() != null).map(GOTO);
        visit(JumpStatementContext.class, rule -> rule.Return() != null).map(RETURN);

        visit(ThrowExpressionContext.class).map(THROW);

        visit(NewExpressionContext.class, rule -> rule.newInitializer() != null).map(NEWCLASS);
        visit(NewExpressionContext.class, rule -> rule.newInitializer() == null).map(NEWARRAY);

        visit(TemplateDeclarationContext.class).map(GENERIC);

        visit(AssignmentOperatorContext.class).map(ASSIGN);
        visit(BraceOrEqualInitializerContext.class, rule -> rule.Assign() != null).map(ASSIGN);
        visit(UnaryExpressionContext.class, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null).map(ASSIGN);

        visit(StaticAssertDeclarationContext.class).map(STATIC_ASSERT);
        visit(EnumeratorDefinitionContext.class).map(VARDEF);
        visit(BracedInitListContext.class).map(BRACED_INIT_BEGIN, BRACED_INIT_END);

        visit(SimpleTypeSpecifierContext.class, rule -> {
            if (hasAncestor(rule, MemberdeclarationContext.class, FunctionDefinitionContext.class)) {
                return true;
            }

            if (hasAncestor(rule, SimpleDeclarationContext.class, TemplateArgumentContext.class, FunctionDefinitionContext.class)) {
                SimpleDeclarationContext parent = getAncestor(rule, SimpleDeclarationContext.class);
                NoPointerDeclaratorContext noPointerDecl = getDescendant(parent, NoPointerDeclaratorContext.class);

                return (!noPointerInFunctionCallContext(noPointerDecl)) && !hasAncestor(rule, NewTypeIdContext.class);
            }

            return false;
        }).map(VARDEF);

        visit(SimpleDeclarationContext.class, rule -> {
            if (!hasAncestor(rule, FunctionBodyContext.class)) {
                return false;
            }

            NoPointerDeclaratorContext noPointerDecl = getDescendant(rule, NoPointerDeclaratorContext.class);
            return noPointerInFunctionCallContext(noPointerDecl);
        }).map(APPLY);

        visit(InitDeclaratorContext.class, rule -> rule.initializer() != null && rule.initializer().LeftParen() != null).map(APPLY);
        visit(ParameterDeclarationContext.class).map(VARDEF);
        visit(ConditionalExpressionContext.class, rule -> rule.Question() != null).map(QUESTIONMARK);

        visit(PostfixExpressionContext.class, rule -> rule.LeftParen() != null).map(APPLY);
        visit(PostfixExpressionContext.class, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null).map(ASSIGN);
    }

    /**
     * @return true of this context represents a function call
     */
    private static boolean noPointerInFunctionCallContext(NoPointerDeclaratorContext context) {
        return context != null && (context.parametersAndQualifiers() != null || context.LeftParen() != null);
    }
}
