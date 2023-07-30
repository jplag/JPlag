package de.jplag.typescript;

import static de.jplag.typescript.TypeScriptTokenType.*;
import static de.jplag.typescript.grammar.TypeScriptParser.*;

import java.io.File;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.TokenCollector;

public class TypeScriptListener extends AbstractAntlrListener {

    public TypeScriptListener(TokenCollector collector, File currentFile) {
        super(collector, currentFile);

        this.mapRange(ImportStatementContext.class, IMPORT);
        this.mapTerminal(Export, EXPORT);
        this.mapEnterExit(NamespaceDeclarationContext.class, NAMESPACE_BEGIN, NAMESPACE_END);

        this.mapEnterExit(ClassDeclarationContext.class, CLASS_BEGIN, CLASS_END);
        this.mapEnterExit(InterfaceDeclarationContext.class, INTERFACE_BEGIN, INTERFACE_END);
        this.mapRange(PropertySignaturContext.class, DECLARATION);
        this.mapEnterExit(EnumDeclarationContext.class, ENUM_BEGIN, ENUM_END);
        this.mapRange(VariableDeclarationContext.class, DECLARATION);
        this.mapRange(VariableDeclarationContext.class, ASSIGNMENT, it -> it.Assign() != null);
        this.mapEnterExit(IfStatementContext.class, IF_BEGIN, IF_END);
        this.mapTerminal(Else, IF_BEGIN);
        this.mapEnterExit(SwitchStatementContext.class, SWITCH_BEGIN, SWITCH_END);
        this.mapRange(CaseClauseContext.class, SWITCH_CASE);
        this.mapRange(DefaultClauseContext.class, SWITCH_CASE);
        this.mapEnterExit(MethodDeclarationExpressionContext.class, METHOD_BEGIN, METHOD_END);

        this.mapRange(FunctionDeclarationContext.class, DECLARATION);
        this.mapRange(FunctionDeclarationContext.class, ASSIGNMENT);
        this.mapEnterExit(FunctionDeclarationContext.class, METHOD_BEGIN, METHOD_END);

        this.mapEnterExit(ArrowFunctionDeclarationContext.class, METHOD_BEGIN, METHOD_END);
        this.mapEnterExit(FunctionExpressionDeclarationContext.class, METHOD_BEGIN, METHOD_END);
        this.mapEnterExit(GetterSetterDeclarationExpressionContext.class, METHOD_BEGIN, METHOD_END);
        this.mapRange(PropertyDeclarationExpressionContext.class, DECLARATION);
        this.mapRange(PropertyDeclarationExpressionContext.class, ASSIGNMENT, it -> it.initializer() != null);
        this.mapRange(PropertySetterContext.class, ASSIGNMENT);
        this.mapEnterExit(WhileStatementContext.class, WHILE_BEGIN, WHILE_END);
        this.mapEnterExit(ForStatementContext.class, FOR_BEGIN, FOR_END);
        this.mapEnterExit(ForVarStatementContext.class, FOR_BEGIN, FOR_END);
        this.mapEnterExit(ForInStatementContext.class, FOR_BEGIN, FOR_END);
        this.mapRange(TryStatementContext.class, TRY_BEGIN);
        this.mapEnterExit(CatchProductionContext.class, CATCH_BEGIN, CATCH_END);
        this.mapEnterExit(FinallyProductionContext.class, FINALLY_BEGIN, FINALLY_END);

        this.mapRange(BreakStatementContext.class, BREAK);
        this.mapRange(ReturnStatementContext.class, RETURN);
        this.mapRange(ContinueStatementContext.class, CONTINUE);
        this.mapRange(ThrowStatementContext.class, THROW);
        this.mapRange(EnumMemberContext.class, ENUM_MEMBER);

        this.mapEnterExit(ConstructorDeclarationContext.class, CONSTRUCTOR_BEGIN, CONSTRUCTOR_END);

        this.mapRange(AssignmentExpressionContext.class, ASSIGNMENT);

        this.mapRange(PostDecreaseExpressionContext.class, ASSIGNMENT);
        this.mapRange(PreDecreaseExpressionContext.class, ASSIGNMENT);
        this.mapRange(PostIncrementExpressionContext.class, ASSIGNMENT);
        this.mapRange(PreIncrementExpressionContext.class, ASSIGNMENT);

        this.mapRange(ArgumentsContext.class, FUNCTION_CALL);
    }

}
