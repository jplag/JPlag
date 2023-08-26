package de.jplag.cpp2;

import static de.jplag.cpp2.CPPTokenType.*;

import java.util.function.Function;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.TokenType;
import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.cpp2.grammar.CPP14Parser;
import de.jplag.cpp2.grammar.CPP14Parser.*;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableRegistry;
import de.jplag.semantics.VariableScope;

/**
 * Extracts tokens from the ANTLR parse tree. Token extraction is built to be similar to the Java language module. In
 * some cases, the grammar is ambiguous and requires surrounding context to extract the correct token.
 * <p>
 * Those cases are covered by {@link SimpleTypeSpecifierContext} and {@link SimpleDeclarationContext}
 */
class CPPListener extends AbstractAntlrListener {

    CPPListener() {
        super();

        visit(UnqualifiedIdContext.class).withSemantics(CodeSemantics::new);

        visit(ClassSpecifierContext.class, rule -> rule.classHead().Union() != null).map(UNION_BEGIN, UNION_END).withSemantics(CodeSemantics::new);

        mapClass(ClassKeyContext::Class, CLASS_BEGIN, CLASS_END);
        mapClass(ClassKeyContext::Struct, STRUCT_BEGIN, STRUCT_END);  // structs are basically just classes
        visit(EnumSpecifierContext.class).map(ENUM_BEGIN, ENUM_END).withSemantics(CodeSemantics::createControl);

        visit(FunctionDefinitionContext.class).map(FUNCTION_BEGIN, FUNCTION_END).addLocalScope().withSemantics(CodeSemantics::createControl);

        visit(IterationStatementContext.class, rule -> rule.Do() != null).map(DO_BEGIN, DO_END).addLocalScope().withLoopSemantics();
        visit(IterationStatementContext.class, rule -> rule.For() != null).map(FOR_BEGIN, FOR_END).addLocalScope().withLoopSemantics();
        visit(IterationStatementContext.class, rule -> rule.While() != null && rule.Do() == null).map(WHILE_BEGIN, WHILE_END).addLocalScope()
                .withLoopSemantics();

        visit(SelectionStatementContext.class, rule -> rule.Switch() != null).map(SWITCH_BEGIN, SWITCH_END).addLocalScope()
                .withSemantics(CodeSemantics::createControl);
        visit(SelectionStatementContext.class, rule -> rule.If() != null).map(IF_BEGIN, IF_END).addLocalScope()
                .withSemantics(CodeSemantics::createControl);
        visit(CPP14Parser.Else).map(ELSE).withSemantics(CodeSemantics::createControl);  // todo check interaction with if, variable not visible in
                                                                                        // else!

        visit(LabeledStatementContext.class, rule -> rule.Case() != null).map(CASE).withSemantics(CodeSemantics::createControl);
        visit(LabeledStatementContext.class, rule -> rule.Default() != null).map(DEFAULT).withSemantics(CodeSemantics::createControl);

        visit(TryBlockContext.class).map(TRY_BEGIN, TRY_END).addLocalScope().withSemantics(CodeSemantics::createControl);
        visit(HandlerContext.class).map(CATCH_BEGIN, CATCH_END).addLocalScope().withSemantics(CodeSemantics::createControl);

        visit(JumpStatementContext.class, rule -> rule.Break() != null).map(BREAK).withSemantics(CodeSemantics::createControl);
        visit(JumpStatementContext.class, rule -> rule.Continue() != null).map(CONTINUE).withSemantics(CodeSemantics::createControl);
        visit(JumpStatementContext.class, rule -> rule.Goto() != null).map(GOTO).withSemantics(CodeSemantics::createControl);
        visit(JumpStatementContext.class, rule -> rule.Return() != null).map(RETURN).withSemantics(CodeSemantics::createControl);

        visit(ThrowExpressionContext.class).map(THROW).withSemantics(CodeSemantics::createControl);

        visit(NewExpressionContext.class, rule -> rule.newInitializer() != null).map(NEWCLASS).withSemantics(CodeSemantics::new);
        visit(NewExpressionContext.class, rule -> rule.newInitializer() == null).map(NEWARRAY).withSemantics(CodeSemantics::new);

        visit(TemplateDeclarationContext.class).map(GENERIC).withSemantics(CodeSemantics::new);

        visit(AssignmentOperatorContext.class).map(ASSIGN).withSemantics(CodeSemantics::new);  // todo variables
        visit(BraceOrEqualInitializerContext.class, rule -> rule.Assign() != null).map(ASSIGN).withSemantics(CodeSemantics::new);
        visit(UnaryExpressionContext.class, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null).map(ASSIGN)
                .withSemantics(CodeSemantics::new);

        visit(StaticAssertDeclarationContext.class).map(STATIC_ASSERT).withSemantics(CodeSemantics::createControl);
        visit(EnumeratorDefinitionContext.class).map(VARDEF).withSemantics(CodeSemantics::new);
        visit(BracedInitListContext.class).map(BRACED_INIT_BEGIN, BRACED_INIT_END).withSemantics(CodeSemantics::new);

        visit(SimpleTypeSpecifierContext.class, rule -> {
            if (hasAncestor(rule, MemberdeclarationContext.class, FunctionDefinitionContext.class)) {
                return true;
            }
            SimpleDeclarationContext parent = getAncestor(rule, SimpleDeclarationContext.class, TemplateArgumentContext.class,
                    FunctionDefinitionContext.class);
            if (parent == null)
                return false;
            NoPointerDeclaratorContext noPointerDecl = getDescendant(parent, NoPointerDeclaratorContext.class);
            return !noPointerInFunctionCallContext(noPointerDecl) && !hasAncestor(rule, NewTypeIdContext.class);
        }).map(VARDEF).withSemantics(CodeSemantics::new).onEnter((context, variableRegistry) -> {
            SimpleDeclarationContext parent = getAncestor(context, SimpleDeclarationContext.class);
            if (parent == null)  // at this point we know parent exists
                throw new IllegalStateException();
            boolean typeMutable = context.theTypeName() != null; // block is duplicate to member variable register
            for (var decl : parent.initDeclaratorList().initDeclarator()) {
                PointerDeclaratorContext pd = decl.declarator().pointerDeclarator();
                String name = decl.declarator().getText();
                boolean mutable = typeMutable || !pd.pointerOperator().isEmpty();
                variableRegistry.registerVariable(name, VariableScope.LOCAL, mutable);
            }
        });

        visit(SimpleDeclarationContext.class, rule -> {
            if (!hasAncestor(rule, FunctionBodyContext.class)) {
                return false;
            }

            NoPointerDeclaratorContext noPointerDecl = getDescendant(rule, NoPointerDeclaratorContext.class);
            return noPointerInFunctionCallContext(noPointerDecl);
        }).map(APPLY).withSemantics(CodeSemantics::createControl);

        visit(InitDeclaratorContext.class, rule -> rule.initializer() != null && rule.initializer().LeftParen() != null).map(APPLY)
                .withSemantics(CodeSemantics::createControl);
        visit(ParameterDeclarationContext.class).map(VARDEF).withSemantics(CodeSemantics::new);
        visit(ConditionalExpressionContext.class, rule -> rule.Question() != null).map(QUESTIONMARK).withSemantics(CodeSemantics::new);

        visit(PostfixExpressionContext.class, rule -> rule.LeftParen() != null).map(APPLY).withSemantics(CodeSemantics::createControl);
        visit(PostfixExpressionContext.class, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null).map(ASSIGN)
                .withSemantics(CodeSemantics::new);
    }

    /**
     * @return true of this context represents a function call
     */
    private static boolean noPointerInFunctionCallContext(NoPointerDeclaratorContext context) {
        return context != null && (context.parametersAndQualifiers() != null || context.LeftParen() != null);
    }

    private void mapClass(Function<ClassKeyContext, TerminalNode> getTerminal, TokenType beginTokenType, TokenType endTokenType) {
        visit(ClassSpecifierContext.class, rule -> {
            ClassKeyContext classKey = rule.classHead().classKey();
            return classKey != null && getTerminal.apply(classKey) != null;
        }).map(beginTokenType, endTokenType).addClassScope().withSemantics(CodeSemantics::createControl).onEnter(this::registerClassVariables);
    }

    private void registerClassVariables(ClassSpecifierContext context, VariableRegistry variableRegistry) {
        for (MemberdeclarationContext member : context.memberSpecification().memberdeclaration()) {
            if (member.memberDeclaratorList() != null) {
                // I don't even know man
                SimpleTypeSpecifierContext ugh = member.declSpecifierSeq().declSpecifier().get(0).typeSpecifier().trailingTypeSpecifier()
                        .simpleTypeSpecifier();
                boolean typeMutable = ugh.theTypeName() != null;
                for (var decl : member.memberDeclaratorList().memberDeclarator()) {
                    // decl.declarator().noPointerDeclarator() may alternatively be used, todo
                    PointerDeclaratorContext pd = decl.declarator().pointerDeclarator();
                    String name = pd.noPointerDeclarator().getText();
                    boolean mutable = typeMutable || !pd.pointerOperator().isEmpty();
                    variableRegistry.registerVariable(name, VariableScope.CLASS, mutable);
                }
            }
        }
    }
}
