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
import static de.jplag.typescript.grammar.TypeScriptParser.Else;
import static de.jplag.typescript.grammar.TypeScriptParser.Export;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.typescript.grammar.TypeScriptParser.ArgumentsContext;
import de.jplag.typescript.grammar.TypeScriptParser.ArrowFunctionDeclarationContext;
import de.jplag.typescript.grammar.TypeScriptParser.AssignmentExpressionContext;
import de.jplag.typescript.grammar.TypeScriptParser.BreakStatementContext;
import de.jplag.typescript.grammar.TypeScriptParser.CaseClauseContext;
import de.jplag.typescript.grammar.TypeScriptParser.CatchProductionContext;
import de.jplag.typescript.grammar.TypeScriptParser.ClassDeclarationContext;
import de.jplag.typescript.grammar.TypeScriptParser.ConstructorDeclarationContext;
import de.jplag.typescript.grammar.TypeScriptParser.ContinueStatementContext;
import de.jplag.typescript.grammar.TypeScriptParser.DefaultClauseContext;
import de.jplag.typescript.grammar.TypeScriptParser.EnumDeclarationContext;
import de.jplag.typescript.grammar.TypeScriptParser.EnumMemberContext;
import de.jplag.typescript.grammar.TypeScriptParser.FinallyProductionContext;
import de.jplag.typescript.grammar.TypeScriptParser.ForInStatementContext;
import de.jplag.typescript.grammar.TypeScriptParser.ForStatementContext;
import de.jplag.typescript.grammar.TypeScriptParser.ForVarStatementContext;
import de.jplag.typescript.grammar.TypeScriptParser.FunctionDeclarationContext;
import de.jplag.typescript.grammar.TypeScriptParser.FunctionExpressionDeclarationContext;
import de.jplag.typescript.grammar.TypeScriptParser.GetterSetterDeclarationExpressionContext;
import de.jplag.typescript.grammar.TypeScriptParser.IfStatementContext;
import de.jplag.typescript.grammar.TypeScriptParser.ImportStatementContext;
import de.jplag.typescript.grammar.TypeScriptParser.InterfaceDeclarationContext;
import de.jplag.typescript.grammar.TypeScriptParser.MethodDeclarationExpressionContext;
import de.jplag.typescript.grammar.TypeScriptParser.NamespaceDeclarationContext;
import de.jplag.typescript.grammar.TypeScriptParser.PostDecreaseExpressionContext;
import de.jplag.typescript.grammar.TypeScriptParser.PostIncrementExpressionContext;
import de.jplag.typescript.grammar.TypeScriptParser.PreDecreaseExpressionContext;
import de.jplag.typescript.grammar.TypeScriptParser.PreIncrementExpressionContext;
import de.jplag.typescript.grammar.TypeScriptParser.PropertyDeclarationExpressionContext;
import de.jplag.typescript.grammar.TypeScriptParser.PropertySetterContext;
import de.jplag.typescript.grammar.TypeScriptParser.PropertySignaturContext;
import de.jplag.typescript.grammar.TypeScriptParser.ReturnStatementContext;
import de.jplag.typescript.grammar.TypeScriptParser.SwitchStatementContext;
import de.jplag.typescript.grammar.TypeScriptParser.ThrowStatementContext;
import de.jplag.typescript.grammar.TypeScriptParser.TryStatementContext;
import de.jplag.typescript.grammar.TypeScriptParser.VariableDeclarationContext;
import de.jplag.typescript.grammar.TypeScriptParser.WhileStatementContext;

/**
 * This class is responsible for mapping parsed TypeScript to the internal Token structure.
 */
public class TypeScriptListener extends AbstractAntlrListener {

    /**
     * Creates the listener.
     */
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
