package de.jplag.cpp;

import static de.jplag.cpp.CPPTokenAttribute.CLASS_BEGIN;
import static de.jplag.cpp.CPPTokenAttribute.CLASS_END;
import static de.jplag.cpp.CPPTokenAttribute.GENERIC;
import static de.jplag.cpp.CPPTokenAttribute.UNION_BEGIN;
import static de.jplag.cpp.CPPTokenAttribute.UNION_END;
import static de.jplag.tokentypes.ArraySyntaxTokenTypes.NEW_ARRAY;
import static de.jplag.tokentypes.ExceptionHandlingTokenTypes.TRY;
import static de.jplag.tokentypes.ImperativeTokenAttribute.ASSIGNMENT;
import static de.jplag.tokentypes.ImperativeTokenAttribute.CALL;
import static de.jplag.tokentypes.ImperativeTokenAttribute.FUNCTION_DEFINITION;
import static de.jplag.tokentypes.ImperativeTokenAttribute.IF;
import static de.jplag.tokentypes.ImperativeTokenAttribute.LOOP;
import static de.jplag.tokentypes.ImperativeTokenAttribute.LOOP_END;
import static de.jplag.tokentypes.ImperativeTokenAttribute.STRUCTURE_DEFINITION;
import static de.jplag.tokentypes.ImperativeTokenAttribute.STRUCTURE_END;
import static de.jplag.tokentypes.ImperativeTokenAttribute.SWITCH;
import static de.jplag.tokentypes.ImperativeTokenAttribute.VARIABLE_DEFINITION;
import static de.jplag.tokentypes.InlineIfTokenTypes.CONDITION;
import static de.jplag.tokentypes.ObjectOrientationTokens.ENUM_DEF;
import static de.jplag.tokentypes.ObjectOrientationTokens.ENUM_END;
import static de.jplag.tokentypes.ObjectOrientationTokens.NEW;

import java.util.List;
import java.util.function.Function;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.TokenAttribute;
import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.ContextVisitor;
import de.jplag.cpp.grammar.CPP14Parser;
import de.jplag.cpp.grammar.CPP14Parser.AssignmentOperatorContext;
import de.jplag.cpp.grammar.CPP14Parser.BraceOrEqualInitializerContext;
import de.jplag.cpp.grammar.CPP14Parser.ClassKeyContext;
import de.jplag.cpp.grammar.CPP14Parser.ClassSpecifierContext;
import de.jplag.cpp.grammar.CPP14Parser.ConditionalExpressionContext;
import de.jplag.cpp.grammar.CPP14Parser.DeclaratorContext;
import de.jplag.cpp.grammar.CPP14Parser.EnumSpecifierContext;
import de.jplag.cpp.grammar.CPP14Parser.EnumeratorDefinitionContext;
import de.jplag.cpp.grammar.CPP14Parser.FunctionBodyContext;
import de.jplag.cpp.grammar.CPP14Parser.FunctionDefinitionContext;
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
import de.jplag.tokentypes.ExceptionHandlingTokenTypes;
import de.jplag.tokentypes.ImperativeTokenAttribute;

/**
 * Extracts tokens from the ANTLR parse tree. Token extraction is built to be similar to the Java language module. In
 * some cases, the grammar is ambiguous and requires surrounding context to extract the correct token.
 * <p>
 * Those cases are covered by {@link SimpleTypeSpecifierContext} and {@link SimpleDeclarationContext}
 */
class CPPListener extends AbstractAntlrListener {

    CPPListener() {
        visit(ClassSpecifierContext.class, rule -> rule.classHead().Union() != null)
                .map(List.of(STRUCTURE_DEFINITION, UNION_BEGIN), List.of(STRUCTURE_END, UNION_END)).addClassScope() // TODO
                .withSemantics(CodeSemantics::createControl);
        mapClass(ClassKeyContext::Class, List.of(STRUCTURE_DEFINITION, CLASS_BEGIN), List.of(CLASS_END, STRUCTURE_END));
        mapClass(ClassKeyContext::Struct, List.of(STRUCTURE_DEFINITION), List.of(STRUCTURE_END));  // structs are basically just classes
        visit(EnumSpecifierContext.class).map(List.of(STRUCTURE_DEFINITION, ENUM_DEF), List.of(STRUCTURE_END, ENUM_END)).addClassScope()
                .withSemantics(CodeSemantics::createControl);

        visit(FunctionDefinitionContext.class).map(FUNCTION_DEFINITION, ImperativeTokenAttribute.FUNCTION_END).addLocalScope()
                .withSemantics(CodeSemantics::createControl);

        statementRules();

        visit(TryBlockContext.class).map(TRY, ExceptionHandlingTokenTypes.TRY_END).addLocalScope().withSemantics(CodeSemantics::createControl);
        visit(CPP14Parser.HandlerSeqContext.class).map(TRY, ExceptionHandlingTokenTypes.TRY_END).addLocalScope()
                .withSemantics(CodeSemantics::createControl);

        visit(ThrowExpressionContext.class).map(ExceptionHandlingTokenTypes.THROW).withSemantics(CodeSemantics::createControl);

        visit(NewExpressionContext.class, rule -> rule.newInitializer() != null).map(NEW).withSemantics(CodeSemantics::new);
        visit(NewExpressionContext.class, rule -> rule.newInitializer() == null).map(NEW_ARRAY).withSemantics(CodeSemantics::new);

        // TODO
        visit(TemplateDeclarationContext.class).map(GENERIC).withSemantics(CodeSemantics::new);

        visit(AssignmentOperatorContext.class).map(ASSIGNMENT).withSemantics(CodeSemantics::new)
                .onEnter((rule, varReg) -> varReg.setNextVariableAccessType(VariableAccessType.WRITE));
        visit(UnaryExpressionContext.class, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null).map(ASSIGNMENT)
                .withSemantics(CodeSemantics::new).onEnter((rule, varReg) -> varReg.setNextVariableAccessType(VariableAccessType.READ_WRITE));
        visit(CPP14Parser.MemInitializerContext.class).mapRange(ASSIGNMENT).withSemantics(CodeSemantics::new)
                .onEnter((rule, varReg) -> varReg.setNextVariableAccessType(VariableAccessType.WRITE));

        visit(EnumeratorDefinitionContext.class).map(VARIABLE_DEFINITION).withSemantics(CodeSemantics::new)
                .onEnter((rule, varReg) -> varReg.setNextVariableAccessType(VariableAccessType.WRITE));
        // Removed because constructor parameters are generally ignored
        // visit(BracedInitListContext.class).map(BRACED_INIT_BEGIN, BRACED_INIT_END).withSemantics(CodeSemantics::new);

        typeSpecifierRule();
        declarationRules();
        expressionRules();
        idRules();
        javaCompatibility();
    }

    private void statementRules() {
        visit(IterationStatementContext.class, rule -> rule.Do() != null).map(LOOP, LOOP_END).addLocalScope().withLoopSemantics();
        visit(IterationStatementContext.class, rule -> rule.For() != null).map(LOOP, LOOP_END).addLocalScope().withLoopSemantics();
        visit(IterationStatementContext.class, rule -> rule.While() != null && rule.Do() == null).map(LOOP, LOOP_END).addLocalScope()
                .withLoopSemantics();

        visit(SelectionStatementContext.class, rule -> rule.Switch() != null).map(SWITCH, ImperativeTokenAttribute.SWITCH_END).addLocalScope()
                .withSemantics(CodeSemantics::createControl);
        visit(SelectionStatementContext.class, rule -> rule.If() != null).map(IF, ImperativeTokenAttribute.IF_END).addLocalScope()
                .withSemantics(CodeSemantics::createControl);
        // possible problem: variable from if visible in else, but in reality is not -- doesn't really matter
        visit(CPP14Parser.Else).map(ImperativeTokenAttribute.ELSE).withSemantics(CodeSemantics::createControl);

        visit(LabeledStatementContext.class, rule -> rule.Case() != null).map(ImperativeTokenAttribute.CASE)
                .withSemantics(CodeSemantics::createControl);
        visit(LabeledStatementContext.class, rule -> rule.Default() != null).map(ImperativeTokenAttribute.DEFAULT)
                .withSemantics(CodeSemantics::createControl);

        visit(JumpStatementContext.class, rule -> rule.Break() != null).map(ImperativeTokenAttribute.BREAK)
                .withSemantics(CodeSemantics::createControl);
        visit(JumpStatementContext.class, rule -> rule.Continue() != null).map(ImperativeTokenAttribute.CONTINUE)
                .withSemantics(CodeSemantics::createControl);
        visit(JumpStatementContext.class, rule -> rule.Goto() != null).map(ImperativeTokenAttribute.GOTO).withSemantics(CodeSemantics::createControl);
        visit(JumpStatementContext.class, rule -> rule.Return() != null).map(ImperativeTokenAttribute.RETURN)
                .withSemantics(CodeSemantics::createControl);
    }

    private void typeSpecifierRule() {
        visit(SimpleTypeSpecifierContext.class, rule -> {
            if (hasAncestor(rule.getParent(), MemberdeclarationContext.class, FunctionDefinitionContext.class, SimpleTypeSpecifierContext.class,
                    TemplateArgumentContext.class)) {
                return true;
            }
            SimpleDeclarationContext parent = getAncestor(rule, SimpleDeclarationContext.class, TemplateArgumentContext.class,
                    FunctionDefinitionContext.class);
            if (parent == null) {
                return false;
            }
            NoPointerDeclaratorContext noPointerDecl = getDescendant(parent, NoPointerDeclaratorContext.class);
            return !noPointerInFunctionCallContext(noPointerDecl) && !hasAncestor(rule, NewTypeIdContext.class);
        }).map(VARIABLE_DEFINITION).withSemantics(CodeSemantics::new).onEnter((context, variableRegistry) -> {
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

        visit(DeclaratorContext.class, rule -> rule.getParent().getClass() == CPP14Parser.ForRangeDeclarationContext.class)
                .mapRange(VARIABLE_DEFINITION).withSemantics(CodeSemantics::new).onEnter((rule, varReg) -> varReg
                        .registerVariable(getDescendant(rule, UnqualifiedIdContext.class).getText(), VariableScope.LOCAL, false));
    }

    private void javaCompatibility() {
        visit(CPP14Parser.LiteralContext.class, rule -> rule.StringLiteral() != null).mapRange(NEW).withSemantics(CodeSemantics::new);
        visit(CPP14Parser.EqualityExpressionContext.class, rule -> !rule.Equal().isEmpty()).delegateTerminal(rule -> rule.Equal(0)).map(CALL)
                .withSemantics(CodeSemantics::new);
    }

    private void declarationRules() {
        mapApply(visit(SimpleDeclarationContext.class, rule -> {
            if (!hasAncestor(rule, FunctionBodyContext.class)) {
                return false;
            }

            NoPointerDeclaratorContext noPointerDecl = getDescendant(rule, NoPointerDeclaratorContext.class);
            return noPointerInFunctionCallContext(noPointerDecl);
        }));

        mapApply(visit(InitDeclaratorContext.class, rule -> rule.initializer() != null && rule.initializer().LeftParen() != null
                && getDescendant(rule.getParent().getParent(), CPP14Parser.ClassNameContext.class) == null));
        visit(CPP14Parser.IdExpressionContext.class, rule -> {
            ParserRuleContext ancestor = rule.getParent().getParent().getParent().getParent().getParent();
            if (ancestor == null || !ancestor.getClass().isAssignableFrom(InitDeclaratorContext.class)) {
                return false;
            }
            InitDeclaratorContext parent = (InitDeclaratorContext) ancestor;

            return parent.initializer() != null && parent.initializer().LeftParen() != null
                    && getDescendant(parent.getParent().getParent(), CPP14Parser.ClassNameContext.class) != null;
        }).map(ASSIGNMENT, NEW).onExit((ctx, varReg) -> varReg.setMutableWrite(false)).onEnter((ctx, varReg) -> {
            varReg.addAllNonLocalVariablesAsReads();
            varReg.setMutableWrite(true);
        }).withControlSemantics();
        visit(DeclaratorContext.class, rule -> {
            ParserRuleContext parent = rule.getParent();
            BraceOrEqualInitializerContext desc = getDescendant(parent, BraceOrEqualInitializerContext.class);
            return (desc != null && desc.Assign() != null && (parent == desc.getParent() || parent == desc.getParent().getParent()));
        }).map(ASSIGNMENT).withSemantics(CodeSemantics::new).onEnter((ctx, varReg) -> varReg.setNextVariableAccessType(VariableAccessType.WRITE));

        visit(CPP14Parser.InitDeclaratorListContext.class, rule -> {
            CPP14Parser.TrailingTypeSpecifierContext specifier = getDescendant(rule.getParent(), CPP14Parser.TrailingTypeSpecifierContext.class);
            return !(rule.initDeclarator().isEmpty() && rule.initDeclarator(0).declarator() == null
                    && rule.initDeclarator(0).declarator().pointerDeclarator() == null) && specifier != null
                    && getDescendant(specifier, CPP14Parser.ClassNameContext.class) != null
                    && getDescendant(rule, CPP14Parser.InitializerContext.class) == null
                    && rule.getParent().getClass() == SimpleDeclarationContext.class;
        }).map(ASSIGNMENT, NEW).withSemantics(CodeSemantics::new);

        visit(ParameterDeclarationContext.class, rule -> !hasAncestor(rule, TemplateArgumentContext.class)).map(VARIABLE_DEFINITION)
                .withSemantics(CodeSemantics::new).onEnter((ctx, varReg) -> {
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
        visit(ConditionalExpressionContext.class, rule -> rule.Question() != null).map(CONDITION).withSemantics(CodeSemantics::new);

        mapApply(visit(PostfixExpressionContext.class, rule -> rule.LeftParen() != null));
        visit(PostfixExpressionContext.class, rule -> rule.PlusPlus() != null || rule.MinusMinus() != null).map(ASSIGNMENT)
                .withSemantics(CodeSemantics::new).onEnter((rule, varReg) -> varReg.setNextVariableAccessType(VariableAccessType.READ_WRITE));
    }

    private void idRules() {
        visit(UnqualifiedIdContext.class).onEnter((ctx, varReg) -> {
            ParserRuleContext parentCtx = ctx.getParent().getParent();
            if (!parentCtx.getParent().getParent().getText().contains("(")) {
                boolean isClassVariable = parentCtx.getClass() == PostfixExpressionContext.class // after dot
                        && ((PostfixExpressionContext) parentCtx).postfixExpression().getText().equals("this");
                varReg.registerVariableAccess(ctx.getText(), isClassVariable);
            }
        });
    }

    private void mapApply(ContextVisitor<?> visitor) {
        visitor.onExit((ctx, varReg) -> varReg.setMutableWrite(false)).onEnter((ctx, varReg) -> {
            varReg.addAllNonLocalVariablesAsReads();
            varReg.setMutableWrite(true);
        }).map(CALL).withControlSemantics();
    }

    /**
     * @return true of this context represents a function call
     */
    private static boolean noPointerInFunctionCallContext(NoPointerDeclaratorContext context) {
        return context != null && (context.parametersAndQualifiers() != null || context.LeftParen() != null);
    }

    private void mapClass(Function<ClassKeyContext, TerminalNode> getTerminal, List<TokenAttribute> beginTokenType,
            List<TokenAttribute> endTokenType) {
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
