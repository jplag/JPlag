package de.jplag.cpp;

import static de.jplag.cpp.CPPTokenType.APPLY;
import static de.jplag.cpp.CPPTokenType.ASSIGN;
import static de.jplag.cpp.CPPTokenType.BRACED_INIT_BEGIN;
import static de.jplag.cpp.CPPTokenType.BRACED_INIT_END;
import static de.jplag.cpp.CPPTokenType.BREAK;
import static de.jplag.cpp.CPPTokenType.CASE;
import static de.jplag.cpp.CPPTokenType.CATCH_BEGIN;
import static de.jplag.cpp.CPPTokenType.CATCH_END;
import static de.jplag.cpp.CPPTokenType.CLASS_BEGIN;
import static de.jplag.cpp.CPPTokenType.CLASS_END;
import static de.jplag.cpp.CPPTokenType.CONTINUE;
import static de.jplag.cpp.CPPTokenType.DEFAULT;
import static de.jplag.cpp.CPPTokenType.DO_BEGIN;
import static de.jplag.cpp.CPPTokenType.DO_END;
import static de.jplag.cpp.CPPTokenType.ELSE;
import static de.jplag.cpp.CPPTokenType.ENUM_BEGIN;
import static de.jplag.cpp.CPPTokenType.ENUM_END;
import static de.jplag.cpp.CPPTokenType.FOR_BEGIN;
import static de.jplag.cpp.CPPTokenType.FOR_END;
import static de.jplag.cpp.CPPTokenType.FUNCTION_BEGIN;
import static de.jplag.cpp.CPPTokenType.FUNCTION_END;
import static de.jplag.cpp.CPPTokenType.GENERIC;
import static de.jplag.cpp.CPPTokenType.GOTO;
import static de.jplag.cpp.CPPTokenType.IF_BEGIN;
import static de.jplag.cpp.CPPTokenType.IF_END;
import static de.jplag.cpp.CPPTokenType.NEWARRAY;
import static de.jplag.cpp.CPPTokenType.NEWCLASS;
import static de.jplag.cpp.CPPTokenType.QUESTIONMARK;
import static de.jplag.cpp.CPPTokenType.RETURN;
import static de.jplag.cpp.CPPTokenType.STATIC_ASSERT;
import static de.jplag.cpp.CPPTokenType.STRUCT_BEGIN;
import static de.jplag.cpp.CPPTokenType.STRUCT_END;
import static de.jplag.cpp.CPPTokenType.SWITCH_BEGIN;
import static de.jplag.cpp.CPPTokenType.SWITCH_END;
import static de.jplag.cpp.CPPTokenType.THROW;
import static de.jplag.cpp.CPPTokenType.TRY_BEGIN;
import static de.jplag.cpp.CPPTokenType.TRY_END;
import static de.jplag.cpp.CPPTokenType.UNION_BEGIN;
import static de.jplag.cpp.CPPTokenType.UNION_END;
import static de.jplag.cpp.CPPTokenType.VARDEF;
import static de.jplag.cpp.CPPTokenType.WHILE_BEGIN;
import static de.jplag.cpp.CPPTokenType.WHILE_END;

import java.util.function.Function;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.TokenType;
import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.ContextVisitor;
import de.jplag.cpp.grammar.CPP14Parser;
import de.jplag.cpp.grammar.CPP14Parser.AssignmentOperatorContext;
import de.jplag.cpp.grammar.CPP14Parser.BraceOrEqualInitializerContext;
import de.jplag.cpp.grammar.CPP14Parser.BracedInitListContext;
import de.jplag.cpp.grammar.CPP14Parser.ClassKeyContext;
import de.jplag.cpp.grammar.CPP14Parser.ClassSpecifierContext;
import de.jplag.cpp.grammar.CPP14Parser.ConditionalExpressionContext;
import de.jplag.cpp.grammar.CPP14Parser.DeclaratorContext;
import de.jplag.cpp.grammar.CPP14Parser.EnumSpecifierContext;
import de.jplag.cpp.grammar.CPP14Parser.EnumeratorDefinitionContext;
import de.jplag.cpp.grammar.CPP14Parser.FunctionBodyContext;
import de.jplag.cpp.grammar.CPP14Parser.FunctionDefinitionContext;
import de.jplag.cpp.grammar.CPP14Parser.HandlerContext;
import de.jplag.cpp.grammar.CPP14Parser.InitDeclaratorContext;
import de.jplag.cpp.grammar.CPP14Parser.IterationStatementContext;
import de.jplag.cpp.grammar.CPP14Parser.JumpStatementContext;
import de.jplag.cpp.grammar.CPP14Parser.LabeledStatementContext;
import de.jplag.cpp.grammar.CPP14Parser.MemberDeclaratorContext;
import de.jplag.cpp.grammar.CPP14Parser.MemberSpecificationContext;
import de.jplag.cpp.grammar.CPP14Parser.MemberdeclarationContext;
import de.jplag.cpp.grammar.CPP14Parser.NewExpressionContext;
import de.jplag.cpp.grammar.CPP14Parser.NewTypeIdContext;
import de.jplag.cpp.grammar.CPP14Parser.NoPointerDeclaratorContext;
import de.jplag.cpp.grammar.CPP14Parser.ParameterDeclarationContext;
import de.jplag.cpp.grammar.CPP14Parser.PostfixExpressionContext;
import de.jplag.cpp.grammar.CPP14Parser.SelectionStatementContext;
import de.jplag.cpp.grammar.CPP14Parser.SimpleDeclarationContext;
import de.jplag.cpp.grammar.CPP14Parser.SimpleTypeSpecifierContext;
import de.jplag.cpp.grammar.CPP14Parser.StaticAssertDeclarationContext;
import de.jplag.cpp.grammar.CPP14Parser.TemplateArgumentContext;
import de.jplag.cpp.grammar.CPP14Parser.TemplateDeclarationContext;
import de.jplag.cpp.grammar.CPP14Parser.ThrowExpressionContext;
import de.jplag.cpp.grammar.CPP14Parser.TryBlockContext;
import de.jplag.cpp.grammar.CPP14Parser.UnaryExpressionContext;
import de.jplag.cpp.grammar.CPP14Parser.UnqualifiedIdContext;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableAccessType;
import de.jplag.semantics.VariableRegistry;
import de.jplag.semantics.VariableScope;

/**
 * Extracts tokens from the ANTLR parse tree. Token extraction is built to be similar to the Java language module. In
 * some cases, the grammar is ambiguous and requires surrounding context to extract the correct token.
 * <p>
 * Those cases are covered by {@link SimpleTypeSpecifierContext} and {@link SimpleDeclarationContext}.
 */
class CPPListener extends AbstractAntlrListener {

    CPPListener() {
        visit(ClassSpecifierContext.class, rule -> rule.classHead().Union() != null).map(UNION_BEGIN, UNION_END).addClassScope()
                .withSemantics(CodeSemantics::createControl);
        mapClass(ClassKeyContext::Class, CLASS_BEGIN, CLASS_END);
        mapClass(ClassKeyContext::Struct, STRUCT_BEGIN, STRUCT_END);  // structs are basically just classes
        visit(EnumSpecifierContext.class).map(ENUM_BEGIN, ENUM_END).addClassScope().withSemantics(CodeSemantics::createControl);

        visit(FunctionDefinitionContext.class).map(FUNCTION_BEGIN, FUNCTION_END).addLocalScope().withSemantics(CodeSemantics::createControl);

        statementRules();

        visit(TryBlockContext.class).map(TRY_BEGIN, TRY_END).addLocalScope().withSemantics(CodeSemantics::createControl);
        visit(HandlerContext.class).map(CATCH_BEGIN, CATCH_END).addLocalScope().withSemantics(CodeSemantics::createControl);

        visit(ThrowExpressionContext.class).map(THROW).withSemantics(CodeSemantics::createControl);

        visit(NewExpressionContext.class, rule -> rule.newInitializer() != null).map(NEWCLASS).withSemantics(CodeSemantics::new);
        visit(NewExpressionContext.class, rule -> rule.newInitializer() == null).map(NEWARRAY).withSemantics(CodeSemantics::new);

        visit(TemplateDeclarationContext.class).map(GENERIC).withSemantics(CodeSemantics::new);

        visit(AssignmentOperatorContext.class).map(ASSIGN).withSemantics(CodeSemantics::new)
                .onEnter((rule, varReg) -> varReg.setNextVariableAccessType(VariableAccessType.WRITE));
        visit(UnaryExpressionContext.class, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null).map(ASSIGN)
                .withSemantics(CodeSemantics::new).onEnter((rule, varReg) -> varReg.setNextVariableAccessType(VariableAccessType.READ_WRITE));

        visit(StaticAssertDeclarationContext.class).map(STATIC_ASSERT).withSemantics(CodeSemantics::createControl);
        visit(EnumeratorDefinitionContext.class).map(VARDEF).withSemantics(CodeSemantics::new)
                .onEnter((rule, varReg) -> varReg.setNextVariableAccessType(VariableAccessType.WRITE));
        visit(BracedInitListContext.class).map(BRACED_INIT_BEGIN, BRACED_INIT_END).withSemantics(CodeSemantics::new);

        typeSpecifierRule();
        declarationRules();
        expressionRules();
        idRules();
    }

    private void statementRules() {
        visit(IterationStatementContext.class, rule -> rule.Do() != null).map(DO_BEGIN, DO_END).addLocalScope().withLoopSemantics();
        visit(IterationStatementContext.class, rule -> rule.For() != null).map(FOR_BEGIN, FOR_END).addLocalScope().withLoopSemantics();
        visit(IterationStatementContext.class, rule -> rule.While() != null && rule.Do() == null).map(WHILE_BEGIN, WHILE_END).addLocalScope()
                .withLoopSemantics();

        visit(SelectionStatementContext.class, rule -> rule.Switch() != null).map(SWITCH_BEGIN, SWITCH_END).addLocalScope()
                .withSemantics(CodeSemantics::createControl);
        visit(SelectionStatementContext.class, rule -> rule.If() != null).map(IF_BEGIN, IF_END).addLocalScope()
                .withSemantics(CodeSemantics::createControl);
        // possible problem: variable from if visible in else, but in reality is not -- doesn't really matter
        visit(CPP14Parser.Else).map(ELSE).withSemantics(CodeSemantics::createControl);

        visit(LabeledStatementContext.class, rule -> rule.Case() != null).map(CASE).withSemantics(CodeSemantics::createControl);
        visit(LabeledStatementContext.class, rule -> rule.Default() != null).map(DEFAULT).withSemantics(CodeSemantics::createControl);

        visit(JumpStatementContext.class, rule -> rule.Break() != null).map(BREAK).withSemantics(CodeSemantics::createControl);
        visit(JumpStatementContext.class, rule -> rule.Continue() != null).map(CONTINUE).withSemantics(CodeSemantics::createControl);
        visit(JumpStatementContext.class, rule -> rule.Goto() != null).map(GOTO).withSemantics(CodeSemantics::createControl);
        visit(JumpStatementContext.class, rule -> rule.Return() != null).map(RETURN).withSemantics(CodeSemantics::createControl);
    }

    private void typeSpecifierRule() {
        visit(SimpleTypeSpecifierContext.class, rule -> {
            if (hasAncestor(rule, MemberdeclarationContext.class, FunctionDefinitionContext.class)) {
                return true;
            }
            SimpleDeclarationContext parent = getAncestor(rule, SimpleDeclarationContext.class, TemplateArgumentContext.class,
                    FunctionDefinitionContext.class);
            if (parent == null) {
                return false;
            }
            NoPointerDeclaratorContext noPointerDecl = getDescendant(parent, NoPointerDeclaratorContext.class);
            return !noPointerInFunctionCallContext(noPointerDecl) && !hasAncestor(rule, NewTypeIdContext.class);
        }).map(VARDEF).withSemantics(CodeSemantics::new).onEnter((context, variableRegistry) -> {
            SimpleDeclarationContext parent = getAncestor(context, SimpleDeclarationContext.class);
            if (parent == null) { // at this point we know parent exists
                throw new IllegalStateException();
            }
            // boolean typeMutable = context.theTypeName() != null; // block is duplicate to member variable register
            // possible issue: what if multiple variables are declared in the same line?
            variableRegistry.setNextVariableAccessType(VariableAccessType.WRITE);
            if (parent.initDeclaratorList() == null) {
                return;
            }
            for (InitDeclaratorContext dec : parent.initDeclaratorList().initDeclarator()) {
                String name = dec.declarator().pointerDeclarator().noPointerDeclarator().getText();
                VariableScope scope = variableRegistry.inLocalScope() ? VariableScope.LOCAL : VariableScope.FILE;
                variableRegistry.registerVariable(name, scope, true);
            }
        });
    }

    private void declarationRules() {
        mapApply(visit(SimpleDeclarationContext.class, rule -> {
            if (!hasAncestor(rule, FunctionBodyContext.class)) {
                return false;
            }

            NoPointerDeclaratorContext noPointerDecl = getDescendant(rule, NoPointerDeclaratorContext.class);
            return noPointerInFunctionCallContext(noPointerDecl);
        }));

        mapApply(visit(InitDeclaratorContext.class, rule -> rule.initializer() != null && rule.initializer().LeftParen() != null));
        visit(DeclaratorContext.class, rule -> {
            ParserRuleContext parent = rule.getParent();
            BraceOrEqualInitializerContext desc = getDescendant(parent, BraceOrEqualInitializerContext.class);
            return desc != null && desc.Assign() != null && (parent == desc.getParent() || parent == desc.getParent().getParent());
        }).map(ASSIGN).withSemantics(CodeSemantics::new).onEnter((ctx, varReg) -> varReg.setNextVariableAccessType(VariableAccessType.WRITE));

        visit(ParameterDeclarationContext.class).map(VARDEF).withSemantics(CodeSemantics::new).onEnter((ctx, varReg) -> {
            // don't register parameters in function declarations, e.g. bc6h_enc lines 117-120
            if (hasAncestor(ctx, FunctionDefinitionContext.class, SimpleDeclarationContext.class) && ctx.declarator() != null) {
                CPP14Parser.PointerDeclaratorContext pd = ctx.declarator().pointerDeclarator();
                String name = pd.noPointerDeclarator().getText();
                varReg.registerVariable(name, VariableScope.LOCAL, true);
                varReg.setNextVariableAccessType(VariableAccessType.WRITE);
            }
        });
    }

    private void expressionRules() {
        visit(ConditionalExpressionContext.class, rule -> rule.Question() != null).map(QUESTIONMARK).withSemantics(CodeSemantics::new);

        mapApply(visit(PostfixExpressionContext.class, rule -> rule.LeftParen() != null));
        visit(PostfixExpressionContext.class, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null).map(ASSIGN)
                .withSemantics(CodeSemantics::new).onEnter((rule, varReg) -> varReg.setNextVariableAccessType(VariableAccessType.READ_WRITE));
    }

    private void idRules() {
        visit(UnqualifiedIdContext.class).onEnter((ctx, varReg) -> {
            ParserRuleContext parentCtx = ctx.getParent().getParent();
            if (!parentCtx.getParent().getParent().getText().contains("(")) {
                boolean isClassVariable = parentCtx.getClass() == PostfixExpressionContext.class // after dot
                        && "this".equals(((PostfixExpressionContext) parentCtx).postfixExpression().getText());
                varReg.registerVariableAccess(ctx.getText(), isClassVariable);
            }
        });
    }

    private void mapApply(ContextVisitor<?> visitor) {
        visitor.onExit((ctx, varReg) -> varReg.setMutableWrite(false)).onEnter((ctx, varReg) -> {
            varReg.addAllNonLocalVariablesAsReads();
            varReg.setMutableWrite(true);
        }).map(APPLY).withControlSemantics();
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
        MemberSpecificationContext members = context.memberSpecification();
        if (members != null) { // is null if class has no members
            for (MemberdeclarationContext member : members.memberdeclaration()) {
                if (member.memberDeclaratorList() != null) {
                    for (MemberDeclaratorContext memberDec : member.memberDeclaratorList().memberDeclarator()) {
                        DeclaratorContext dec = memberDec.declarator();
                        if (dec != null) { // is null in some weird case, see bc6h_enc line 1100
                            String name = dec.pointerDeclarator().noPointerDeclarator().getText();
                            variableRegistry.registerVariable(name, VariableScope.CLASS, true);
                        }
                    }
                }
            }
        }
    }
}
