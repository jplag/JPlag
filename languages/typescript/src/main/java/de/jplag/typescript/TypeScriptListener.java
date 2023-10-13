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
import static de.jplag.typescript.grammar.TypeScriptParser.ArgumentsContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ArrowFunctionDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.AssignmentExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.BreakStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.CaseClauseContext;
import static de.jplag.typescript.grammar.TypeScriptParser.CatchProductionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ClassDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ConstructorDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ContinueStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.DefaultClauseContext;
import static de.jplag.typescript.grammar.TypeScriptParser.Else;
import static de.jplag.typescript.grammar.TypeScriptParser.EnumDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.EnumMemberContext;
import static de.jplag.typescript.grammar.TypeScriptParser.Export;
import static de.jplag.typescript.grammar.TypeScriptParser.FinallyProductionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ForInStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ForStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ForVarStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.FunctionDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.FunctionExpressionDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.GetterSetterDeclarationExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.IfStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ImportStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.InterfaceDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.MethodDeclarationExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.NamespaceDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PostDecreaseExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PostIncrementExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PreDecreaseExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PreIncrementExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PropertyDeclarationExpressionContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PropertySetterContext;
import static de.jplag.typescript.grammar.TypeScriptParser.PropertySignaturContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ReturnStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.SwitchStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.ThrowStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.TryStatementContext;
import static de.jplag.typescript.grammar.TypeScriptParser.VariableDeclarationContext;
import static de.jplag.typescript.grammar.TypeScriptParser.WhileStatementContext;

import de.jplag.antlr.AbstractAntlrListener;

/**
 * This class is responsible for mapping parsed TypeScript to the internal Token structure
 */
public class TypeScriptListener extends AbstractAntlrListener {

    public TypeScriptListener() {
        visit(ImportStatementContext.class).map(IMPORT);
        visit(Export).map(EXPORT);
        visit(NamespaceDeclarationContext.class).map(NAMESPACE_BEGIN, NAMESPACE_END);

        visit(ClassDeclarationContext.class).map(CLASS_BEGIN, CLASS_END);
        visit(GetterSetterDeclarationExpressionContext.class).map(METHOD_BEGIN, METHOD_END);
        visit(PropertyDeclarationExpressionContext.class).map(DECLARATION);
        visit(PropertyDeclarationExpressionContext.class, it -> it.initializer() != null).map(ASSIGNMENT);
        visit(PropertySetterContext.class).map(ASSIGNMENT);
        visit(PropertySignaturContext.class).map(DECLARATION);

        visit(InterfaceDeclarationContext.class).map(INTERFACE_BEGIN, INTERFACE_END);
        visit(ConstructorDeclarationContext.class).map(CONSTRUCTOR_BEGIN, CONSTRUCTOR_END);

        visit(EnumDeclarationContext.class).map(ENUM_BEGIN, ENUM_END);
        visit(EnumMemberContext.class).map(ENUM_MEMBER);

        visit(VariableDeclarationContext.class).map(DECLARATION);
        visit(VariableDeclarationContext.class, it -> it.Assign() != null).map(ASSIGNMENT);

        visit(IfStatementContext.class).map(IF_BEGIN, IF_END);
        visit(Else).map(IF_BEGIN);

        visit(SwitchStatementContext.class).map(SWITCH_BEGIN, SWITCH_END);
        visit(CaseClauseContext.class).map(SWITCH_CASE);
        visit(DefaultClauseContext.class).map(SWITCH_CASE);

        visit(MethodDeclarationExpressionContext.class).map(METHOD_BEGIN, METHOD_END);

        visit(FunctionDeclarationContext.class).map(DECLARATION);
        visit(FunctionDeclarationContext.class).map(ASSIGNMENT);
        visit(FunctionDeclarationContext.class).map(METHOD_BEGIN, METHOD_END);

        visit(ArrowFunctionDeclarationContext.class).map(METHOD_BEGIN, METHOD_END);
        visit(FunctionExpressionDeclarationContext.class).map(METHOD_BEGIN, METHOD_END);

        visit(WhileStatementContext.class).map(WHILE_BEGIN, WHILE_END);
        visit(ForStatementContext.class).map(FOR_BEGIN, FOR_END);
        visit(ForVarStatementContext.class).map(FOR_BEGIN, FOR_END);
        visit(ForInStatementContext.class).map(FOR_BEGIN, FOR_END);

        visit(TryStatementContext.class).map(TRY_BEGIN);
        visit(CatchProductionContext.class).map(CATCH_BEGIN, CATCH_END);
        visit(FinallyProductionContext.class).map(FINALLY_BEGIN, FINALLY_END);

        visit(BreakStatementContext.class).map(BREAK);
        visit(ReturnStatementContext.class).map(RETURN);
        visit(ContinueStatementContext.class).map(CONTINUE);
        visit(ThrowStatementContext.class).map(THROW);

        visit(AssignmentExpressionContext.class).map(ASSIGNMENT);
        visit(PostDecreaseExpressionContext.class).map(ASSIGNMENT);
        visit(PreDecreaseExpressionContext.class).map(ASSIGNMENT);
        visit(PostIncrementExpressionContext.class).map(ASSIGNMENT);
        visit(PreIncrementExpressionContext.class).map(ASSIGNMENT);

        visit(ArgumentsContext.class).map(FUNCTION_CALL);
    }

}
