package de.jplag.typescript;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.TokenCollector;
import de.jplag.typescript.grammar.TypeScriptParser;
import de.jplag.typescript.grammar.TypeScriptParserBaseListener;
import static de.jplag.typescript.grammar.TypeScriptParser.*;

import java.io.File;

import static de.jplag.typescript.TypeScriptTokenType.*;

public class TypeScriptListener extends AbstractAntlrListener {

    public TypeScriptListener(TokenCollector collector, File currentFile) {
        super(collector, currentFile);

        this.createRangeMapping(ImportStatementContext.class, IMPORT);
        this.createRangeMapping(ExportStatementContext.class, EXPORT);
        this.createStartStopMapping(NamespaceDeclarationContext.class, NAMESPACE_BEGIN, NAMESPACE_END);

        this.createStartStopMapping(ClassDeclarationContext.class, CLASS_BEGIN, CLASS_END);
        this.createStartStopMapping(InterfaceDeclarationContext.class, INTERFACE_BEGIN, INTERFACE_END);
        this.createStartStopMapping(EnumDeclarationContext.class, ENUM_BEGIN, ENUM_END);
        this.createRangeMapping(VariableDeclarationContext.class, ASSIGNMENT, it -> it.Assign() != null);
        this.createStartStopMapping(IfStatementContext.class, IF_BEGIN, IF_END);
        this.createStartStopMapping(SwitchStatementContext.class, SWITCH_BEGIN, SWITCH_END);
        this.createRangeMapping(CaseClauseContext.class, SWITCH_CASE);
        this.createRangeMapping(DefaultClauseContext.class, SWITCH_CASE);
        this.createStartStopMapping(MethodDeclarationExpressionContext.class, METHOD_BEGIN, METHOD_END);
        // For why both are needed is needed see testFile: methods
        this.createStartStopMapping(FunctionDeclarationContext.class, METHOD_BEGIN, METHOD_END);
        this.createRangeMapping(FunctionDeclarationContext.class, ASSIGNMENT);

        this.createStartStopMapping(ArrowFunctionDeclarationContext.class, METHOD_BEGIN, METHOD_END);
        this.createStartStopMapping(FunctionExpressionDeclarationContext.class, METHOD_BEGIN, METHOD_END);
        this.createStartStopMapping(GetterSetterDeclarationExpressionContext.class, METHOD_BEGIN, METHOD_END);
        this.createRangeMapping(PropertyDeclarationExpressionContext.class, ASSIGNMENT, it -> it.initializer() != null);
        this.createRangeMapping(PropertySetterContext.class, ASSIGNMENT);
        this.createStartStopMapping(WhileStatementContext.class, WHILE_BEGIN, WHILE_END);
        this.createStartStopMapping(ForStatementContext.class, FOR_BEGIN, FOR_END);
        this.createStartStopMapping(ForVarStatementContext.class, FOR_BEGIN, FOR_END);
        this.createStartStopMapping(ForInStatementContext.class, FOR_BEGIN, FOR_END);
        this.createRangeMapping(TryStatementContext.class, TRY_BEGIN);
        this.createStartStopMapping(CatchProductionContext.class, CATCH_BEGIN, CATCH_END);
        this.createStartStopMapping(FinallyProductionContext.class, FINALLY_BEGIN, FINALLY_END);


        this.createRangeMapping(BreakStatementContext.class, BREAK);
        this.createRangeMapping(ReturnStatementContext.class, RETURN);
        this.createRangeMapping(ContinueStatementContext.class, CONTINUE);
        this.createRangeMapping(ThrowStatementContext.class, THROW);
        this.createRangeMapping(EnumMemberContext.class, ENUM_MEMBER);

        // this.createRangeMapping(CallSignatureContext.class, FUNCTION_CALL);
    }

}
