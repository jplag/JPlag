package de.jplag.typescript;

import static de.jplag.typescript.TypeScriptTokenType.ASSIGNMENT;
import static de.jplag.typescript.TypeScriptTokenType.BREAK;
import static de.jplag.typescript.TypeScriptTokenType.CATCH_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.CATCH_END;
import static de.jplag.typescript.TypeScriptTokenType.CLASS_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.CLASS_END;
import static de.jplag.typescript.TypeScriptTokenType.CONSTRUCTOR_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.CONSTRUCTOR_END;
import static de.jplag.typescript.TypeScriptTokenType.CONTINUE;
import static de.jplag.typescript.TypeScriptTokenType.DECLARATION;
import static de.jplag.typescript.TypeScriptTokenType.ENUM_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.ENUM_END;
import static de.jplag.typescript.TypeScriptTokenType.ENUM_MEMBER;
import static de.jplag.typescript.TypeScriptTokenType.EXPORT;
import static de.jplag.typescript.TypeScriptTokenType.FINALLY_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.FINALLY_END;
import static de.jplag.typescript.TypeScriptTokenType.FOR_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.FOR_END;
import static de.jplag.typescript.TypeScriptTokenType.FUNCTION_CALL;
import static de.jplag.typescript.TypeScriptTokenType.IF_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.IF_END;
import static de.jplag.typescript.TypeScriptTokenType.IMPORT;
import static de.jplag.typescript.TypeScriptTokenType.INTERFACE_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.INTERFACE_END;
import static de.jplag.typescript.TypeScriptTokenType.METHOD_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.METHOD_END;
import static de.jplag.typescript.TypeScriptTokenType.NAMESPACE_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.NAMESPACE_END;
import static de.jplag.typescript.TypeScriptTokenType.RETURN;
import static de.jplag.typescript.TypeScriptTokenType.SWITCH_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.SWITCH_CASE;
import static de.jplag.typescript.TypeScriptTokenType.SWITCH_END;
import static de.jplag.typescript.TypeScriptTokenType.THROW;
import static de.jplag.typescript.TypeScriptTokenType.TRY_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.WHILE_BEGIN;
import static de.jplag.typescript.TypeScriptTokenType.WHILE_END;

import static de.jplag.typescript.grammar.TypeScriptParser.ImportStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.Export;
import static de.jplag.typescript.grammar.TypeScriptParser.NamespaceDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ClassDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.InterfaceDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PropertySignaturContext;
import static de.jplag.typescript.grammar.TypeScriptParser.EnumDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.VariableDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.IfStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.Else;
import static de.jplag.typescript.grammar.TypeScriptParser.SwitchStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.CaseClauseContext;
import static de.jplag.typescript.grammar.TypeScriptParser.DefaultClauseContext;
import static de.jplag.typescript.grammar.TypeScriptParser.MethodDeclarationExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ArgumentsContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PreIncrementExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PostIncrementExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PreDecreaseExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PostDecreaseExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.EnumMemberContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ThrowStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ContinueStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ReturnStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.BreakStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.FinallyProductionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.CatchProductionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.TryStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ForInStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ForVarStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ForStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.WhileStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PropertySetterContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PropertyDeclarationExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.GetterSetterDeclarationExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.FunctionExpressionDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ArrowFunctionDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.FunctionDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ConstructorDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.AssignmentExpressionContext;

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
        this.mapEnterExit(GetterSetterDeclarationExpressionContext.class, METHOD_BEGIN, METHOD_END);
        this.mapRange(PropertyDeclarationExpressionContext.class, DECLARATION);
        this.mapRange(PropertyDeclarationExpressionContext.class, ASSIGNMENT, it -> it.initializer() != null);
        this.mapRange(PropertySetterContext.class, ASSIGNMENT);
        this.mapRange(PropertySignaturContext.class, DECLARATION);

        this.mapEnterExit(InterfaceDeclarationContext.class, INTERFACE_BEGIN, INTERFACE_END);
        this.mapEnterExit(ConstructorDeclarationContext.class, CONSTRUCTOR_BEGIN, CONSTRUCTOR_END);


        this.mapEnterExit(EnumDeclarationContext.class, ENUM_BEGIN, ENUM_END);
        this.mapRange(EnumMemberContext.class, ENUM_MEMBER);

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

        this.mapRange(AssignmentExpressionContext.class, ASSIGNMENT);
        this.mapRange(PostDecreaseExpressionContext.class, ASSIGNMENT);
        this.mapRange(PreDecreaseExpressionContext.class, ASSIGNMENT);
        this.mapRange(PostIncrementExpressionContext.class, ASSIGNMENT);
        this.mapRange(PreIncrementExpressionContext.class, ASSIGNMENT);

        this.mapRange(ArgumentsContext.class, FUNCTION_CALL);
    }

}
