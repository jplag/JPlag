package de.jplag.swift;

import static de.jplag.swift.SwiftTokenType.*;

import org.antlr.v4.runtime.Token;

import de.jplag.swift.grammar.Swift5Parser.*;
import de.jplag.swift.grammar.Swift5ParserBaseListener;

public class JPlagSwiftListener extends Swift5ParserBaseListener {
    private final SwiftParserAdapter parserAdapter;

    public JPlagSwiftListener(SwiftParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the grammar's token given by token.
     * @param tokenType the custom token type that occurred.
     * @param token the corresponding grammar's token
     */
    private void transformToken(SwiftTokenType tokenType, Token token) {
        parserAdapter.addToken(tokenType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the current grammatical context given by
     * start and end.
     * @param tokenType the custom token type that occurred.
     * @param start the first Token of the context
     * @param end the last Token of the context
     */
    private void transformToken(SwiftTokenType tokenType, Token start, Token end) {
        parserAdapter.addToken(tokenType, start.getLine(), start.getCharPositionInLine() + 1, end.getStopIndex() - start.getStartIndex() + 1);
    }

    @Override
    public void enterImport_declaration(Import_declarationContext context) {
        transformToken(IMPORT, context.getStart(), context.getStop());
        super.enterImport_declaration(context);
    }

    @Override
    public void enterClass_declaration(Class_declarationContext context) {
        transformToken(CLASS_DECLARATION, context.getStart(), context.getStop());
        super.enterClass_declaration(context);
    }

    @Override
    public void enterClass_body(Class_bodyContext context) {
        transformToken(CLASS_BODY_BEGIN, context.getStart());
        super.enterClass_body(context);
    }

    @Override
    public void exitClass_body(Class_bodyContext context) {
        transformToken(CLASS_BODY_END, context.getStop());
        super.exitClass_body(context);
    }

    @Override
    public void enterStruct_declaration(Struct_declarationContext context) {
        transformToken(STRUCT_DECLARATION, context.getStart(), context.getStop());
        super.enterStruct_declaration(context);
    }

    @Override
    public void enterStruct_body(Struct_bodyContext context) {
        transformToken(STRUCT_BODY_BEGIN, context.getStart());
        super.enterStruct_body(context);
    }

    @Override
    public void exitStruct_body(Struct_bodyContext context) {
        transformToken(STRUCT_BODY_END, context.getStop());
        super.exitStruct_body(context);
    }

    @Override
    public void enterEnum_name(Enum_nameContext context) {
        transformToken(ENUM_DECLARATION, context.getStart(), context.getStop());
        super.enterEnum_name(context);
    }

    @Override
    public void enterRaw_value_style_enum_members(Raw_value_style_enum_membersContext context) {
        transformToken(ENUM_BODY_BEGIN, context.getStart());
        super.enterRaw_value_style_enum_members(context);
    }

    @Override
    public void exitRaw_value_style_enum(Raw_value_style_enumContext context) {
        transformToken(ENUM_BODY_END, context.getStop());
        super.exitRaw_value_style_enum(context);
    }

    @Override
    public void enterRaw_value_style_enum_case(Raw_value_style_enum_caseContext context) {
        transformToken(ENUM_LITERAL, context.getStart(), context.getStop());
        super.enterRaw_value_style_enum_case(context);
    }

    @Override
    public void enterUnion_style_enum_members(Union_style_enum_membersContext context) {
        transformToken(ENUM_BODY_BEGIN, context.getStart());
        super.enterUnion_style_enum_members(context);
    }

    @Override
    public void exitUnion_style_enum(Union_style_enumContext context) {
        transformToken(ENUM_BODY_END, context.getStop());
        super.exitUnion_style_enum(context);
    }

    @Override
    public void enterUnion_style_enum_case(Union_style_enum_caseContext context) {
        transformToken(ENUM_LITERAL, context.getStart(), context.getStop());
        super.enterUnion_style_enum_case(context);
    }

    @Override
    public void enterProtocol_declaration(Protocol_declarationContext context) {
        transformToken(PROTOCOL_DECLARATION, context.getStart(), context.getStop());
        super.enterProtocol_declaration(context);
    }

    @Override
    public void enterProtocol_body(Protocol_bodyContext context) {
        transformToken(PROTOCOL_BODY_BEGIN, context.getStart());
        super.enterProtocol_body(context);
    }

    @Override
    public void exitProtocol_body(Protocol_bodyContext context) {
        transformToken(PROTOCOL_BODY_END, context.getStop());
        super.exitProtocol_body(context);
    }

    @Override
    public void enterVariable_declaration(Variable_declarationContext context) {
        transformToken(PROPERTY_DECLARATION, context.getStart(), context.getStop());
        super.enterVariable_declaration(context);
    }

    @Override
    public void enterProtocol_property_declaration(Protocol_property_declarationContext context) {
        transformToken(PROPERTY_DECLARATION, context.getStart(), context.getStop());
        super.enterProtocol_property_declaration(context);
    }

    @Override
    public void enterConstant_declaration(Constant_declarationContext context) {
        transformToken(PROPERTY_DECLARATION, context.getStart(), context.getStop());
        super.enterConstant_declaration(context);
    }

    @Override
    public void enterGetter_clause(Getter_clauseContext context) {
        transformToken(PROPERTY_ACCESSOR_BEGIN, context.getStart());
        super.enterGetter_clause(context);
    }

    @Override
    public void exitGetter_clause(Getter_clauseContext context) {
        transformToken(PROPERTY_ACCESSOR_END, context.getStop());
        super.exitGetter_clause(context);
    }

    @Override
    public void enterSetter_clause(Setter_clauseContext context) {
        transformToken(PROPERTY_ACCESSOR_BEGIN, context.getStart());
        super.enterSetter_clause(context);
    }

    @Override
    public void exitSetter_clause(Setter_clauseContext context) {
        transformToken(PROPERTY_ACCESSOR_END, context.getStop());
        super.exitSetter_clause(context);
    }

    @Override
    public void enterWillSet_clause(WillSet_clauseContext context) {
        transformToken(PROPERTY_ACCESSOR_BEGIN, context.getStart());
        super.enterWillSet_clause(context);
    }

    @Override
    public void enterGetter_setter_block(Getter_setter_blockContext context) {
        if (isComputedReadOnlyVariableGetterContext(context)) {
            transformToken(PROPERTY_ACCESSOR_BEGIN, context.getStart());
        }
        super.enterGetter_setter_block(context);
    }

    @Override
    public void exitGetter_setter_block(Getter_setter_blockContext context) {
        if (isComputedReadOnlyVariableGetterContext(context)) {
            transformToken(PROPERTY_ACCESSOR_END, context.getStop());
        }
        super.exitGetter_setter_block(context);
    }

    /**
     * Indicates whether the given context encodes a computed read-only variable getter. An example of this is
     * <code>var example: Int { return 1 }</code>.
     * @param context
     * @return
     */
    private boolean isComputedReadOnlyVariableGetterContext(Getter_setter_blockContext context) {
        return context.getChildCount() == 1 && context.getChild(0) instanceof Code_blockContext;
    }

    @Override
    public void exitWillSet_clause(WillSet_clauseContext context) {
        transformToken(PROPERTY_ACCESSOR_END, context.getStop());
        super.exitWillSet_clause(context);
    }

    @Override
    public void enterDidSet_clause(DidSet_clauseContext context) {
        transformToken(PROPERTY_ACCESSOR_BEGIN, context.getStart());
        super.enterDidSet_clause(context);
    }

    @Override
    public void exitDidSet_clause(DidSet_clauseContext context) {
        transformToken(PROPERTY_ACCESSOR_END, context.getStop());
        super.exitDidSet_clause(context);
    }

    @Override
    public void enterInitializer_declaration(Initializer_declarationContext context) {
        transformToken(FUNCTION, context.getStart(), context.getStop());
        super.enterInitializer_declaration(context);
    }

    @Override
    public void enterProtocol_initializer_declaration(Protocol_initializer_declarationContext context) {
        transformToken(FUNCTION, context.getStart(), context.getStop());
        super.enterProtocol_initializer_declaration(context);
    }

    @Override
    public void enterInitializer_body(Initializer_bodyContext context) {
        transformToken(FUNCTION_BODY_BEGIN, context.getStart());
        super.enterInitializer_body(context);
    }

    @Override
    public void exitInitializer_body(Initializer_bodyContext context) {
        transformToken(FUNCTION_BODY_END, context.getStop());
        super.exitInitializer_body(context);
    }

    @Override
    public void enterFunction_name(Function_nameContext context) {
        transformToken(FUNCTION, context.getStart(), context.getStop());
        super.enterFunction_name(context);
    }

    @Override
    public void enterParameter(ParameterContext context) {
        transformToken(FUNCTION_PARAMETER, context.getStart(), context.getStop());
        super.enterParameter(context);
    }

    @Override
    public void enterFunction_result(Function_resultContext context) {
        transformToken(FUNCTION_PARAMETER, context.getStart(), context.getStop());
        super.enterFunction_result(context);
    }

    @Override
    public void enterFunction_body(Function_bodyContext context) {
        transformToken(FUNCTION_BODY_BEGIN, context.getStart());
        super.enterFunction_body(context);
    }

    @Override
    public void exitFunction_body(Function_bodyContext context) {
        transformToken(FUNCTION_BODY_END, context.getStop());
        super.exitFunction_body(context);
    }

    @Override
    public void enterClosure_expression(Closure_expressionContext context) {
        transformToken(CLOSURE_BODY_BEGIN, context.getStart());
        super.enterClosure_expression(context);
    }

    @Override
    public void exitClosure_expression(Closure_expressionContext context) {
        transformToken(CLOSURE_BODY_END, context.getStop());
        super.exitClosure_expression(context);
    }

    @Override
    public void enterFor_in_statement(For_in_statementContext context) {
        transformToken(FOR_BODY_BEGIN, context.getStart());
        super.enterFor_in_statement(context);
    }

    @Override
    public void exitFor_in_statement(For_in_statementContext context) {
        transformToken(FOR_BODY_END, context.getStop());
        super.exitFor_in_statement(context);
    }

    @Override
    public void enterIf_statement(If_statementContext context) {
        transformToken(IF_BODY_BEGIN, context.getStart());
        super.enterIf_statement(context);
    }

    @Override
    public void exitIf_statement(If_statementContext context) {
        transformToken(IF_BODY_END, context.getStop());
        super.exitIf_statement(context);
    }

    @Override
    public void enterElse_clause(Else_clauseContext context) {
        transformToken(IF_BODY_END, context.getStart());
        // Check that current context is not `else if` but rather `else`
        if (!(context.getChild(1) instanceof If_statementContext)) {
            transformToken(IF_BODY_BEGIN, context.getStart());
        }
        super.enterElse_clause(context);
    }

    @Override
    public void enterGuard_statement(Guard_statementContext context) {
        transformToken(IF_BODY_BEGIN, context.getStart());
        super.enterGuard_statement(context);
    }

    @Override
    public void exitGuard_statement(Guard_statementContext context) {
        transformToken(IF_BODY_END, context.getStop());
        super.exitGuard_statement(context);
    }

    @Override
    public void enterSwitch_statement(Switch_statementContext context) {
        transformToken(SWITCH_BODY_BEGIN, context.getStart());
        super.enterSwitch_statement(context);
    }

    @Override
    public void exitSwitch_statement(Switch_statementContext context) {
        transformToken(SWITCH_BODY_END, context.getStop());
        super.exitSwitch_statement(context);
    }

    @Override
    public void enterSwitch_case(Switch_caseContext context) {
        transformToken(SWITCH_CASE, context.getStart());
        super.enterSwitch_case(context);
    }

    @Override
    public void enterWhile_statement(While_statementContext context) {
        transformToken(WHILE_BODY_BEGIN, context.getStart());
        super.enterWhile_statement(context);
    }

    @Override
    public void exitWhile_statement(While_statementContext context) {
        transformToken(WHILE_BODY_END, context.getStop());
        super.exitWhile_statement(context);
    }

    @Override
    public void enterRepeat_while_statement(Repeat_while_statementContext context) {
        transformToken(REPEAT_WHILE_BODY_BEGIN, context.getStart());
        super.enterRepeat_while_statement(context);
    }

    @Override
    public void exitRepeat_while_statement(Repeat_while_statementContext context) {
        transformToken(REPEAT_WHILE_BODY_END, context.getStop());
        super.exitRepeat_while_statement(context);
    }

    @Override
    public void enterDefer_statement(Defer_statementContext context) {
        transformToken(DEFER_BODY_BEGIN, context.getStart());
        super.enterDefer_statement(context);
    }

    @Override
    public void exitDefer_statement(Defer_statementContext context) {
        transformToken(DEFER_BODY_END, context.getStop());
        super.exitDefer_statement(context);
    }

    @Override
    public void enterDo_block(Do_blockContext context) {
        transformToken(DO_TRY_BODY_BEGIN, context.getStart());
        super.enterDo_block(context);
    }

    @Override
    public void exitDo_block(Do_blockContext context) {
        transformToken(DO_TRY_BODY_END, context.getStop());
        super.exitDo_block(context);
    }

    @Override
    public void enterCatch_clause(Catch_clauseContext context) {
        transformToken(CATCH_BODY_BEGIN, context.getStart());
        super.enterCatch_clause(context);
    }

    @Override
    public void exitCatch_clause(Catch_clauseContext context) {
        transformToken(CATCH_BODY_END, context.getStop());
        super.exitCatch_clause(context);
    }

    @Override
    public void enterThrow_statement(Throw_statementContext context) {
        transformToken(THROW, context.getStart(), context.getStop());
        super.enterThrow_statement(context);
    }

    @Override
    public void enterReturn_statement(Return_statementContext context) {
        transformToken(RETURN, context.getStart(), context.getStop());
        super.enterReturn_statement(context);
    }

    @Override
    public void enterContinue_statement(Continue_statementContext context) {
        transformToken(CONTINUE, context.getStart(), context.getStop());
        super.enterContinue_statement(context);
    }

    @Override
    public void enterBreak_statement(Break_statementContext context) {
        transformToken(BREAK, context.getStart(), context.getStop());
        super.enterBreak_statement(context);
    }

    @Override
    public void enterFallthrough_statement(Fallthrough_statementContext context) {
        transformToken(FALLTHROUGH, context.getStart(), context.getStop());
        super.enterFallthrough_statement(context);
    }

    @Override
    public void enterInitializer(InitializerContext context) {
        transformToken(ASSIGNMENT, context.getStart(), context.getStop());
        super.enterInitializer(context);
    }

    @Override
    public void enterRaw_value_assignment(Raw_value_assignmentContext context) {
        transformToken(ASSIGNMENT, context.getStart(), context.getStop());
        super.enterRaw_value_assignment(context);
    }

    @Override
    public void enterBinary_operator(Binary_operatorContext context) {
        String operator = context.getText();
        if (operator.contains("=") && !operator.contains("==")) {
            transformToken(ASSIGNMENT, context.getStart(), context.getStop());
        }
        super.enterBinary_operator(context);
    }

    @Override
    public void enterFunction_call_suffix(Function_call_suffixContext context) {
        transformToken(FUNCTION_CALL, context.getStart(), context.getStop());
        super.enterFunction_call_suffix(context);
    }
}
