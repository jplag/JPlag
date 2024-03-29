package de.jplag.golang;

import de.jplag.TokenType;

public enum GoTokenType implements TokenType {
    // TOP LEVEL STRUCTURES

    PACKAGE("PACKAGE"),
    IMPORT_CLAUSE("IMPORT"),
    IMPORT_CLAUSE_BEGIN("IMPORT("),
    IMPORT_CLAUSE_END(")IMPORT"),
    IMPORT_DECLARATION("IMPORT_DECL"),
    ARRAY_BODY_BEGIN("ARRAY{"),
    ARRAY_BODY_END("}ARRAY"),
    STRUCT_DECLARATION("STRUCT"),
    STRUCT_BODY_BEGIN("STRUCT{"),
    STRUCT_BODY_END("}STRUCT"),
    INTERFACE_DECLARATION("INTERFACE"),
    INTERFACE_BLOCK_BEGIN("INTERFACE{"),
    INTERFACE_BLOCK_END("}INTERFACE"),
    INTERFACE_METHOD("INTERFACE_METHOD"),
    TYPE_CONSTRAINT("TYPE_CONSTRAINT"),
    TYPE_ASSERTION("TYPE_ASSERTION"),
    MAP_BODY_BEGIN("MAP{"),
    MAP_BODY_END("}MAP"),
    SLICE_BODY_BEGIN("SLICE{"),
    SLICE_BODY_END("}SLICE"),
    NAMED_TYPE_BODY_BEGIN("CTYPE{"),
    NAMED_TYPE_BODY_END("}CTYPE"),
    MEMBER_DECLARATION("FIELD"),

    // FUNCTIONS AND METHODS

    FUNCTION_DECLARATION("FUNC"),
    RECEIVER("RECEIVER"),
    FUNCTION_PARAMETER("PARAM"),
    FUNCTION_BODY_BEGIN("FUNC{"),
    FUNCTION_BODY_END("}FUNC"),

    // CONTROL FLOW STATEMENTS

    IF_STATEMENT("IF"),
    IF_BLOCK_BEGIN("IF{"),
    IF_BLOCK_END("}IF"),
    ELSE_BLOCK_BEGIN("ELSE{"),
    ELSE_BLOCK_END("}ELSE"),
    FOR_STATEMENT("FOR"),
    FOR_BLOCK_BEGIN("FOR{"),
    FOR_BLOCK_END("}FOR"),
    SWITCH_STATEMENT("SWITCH"),
    SWITCH_BLOCK_BEGIN("SWITCH{"),
    SWITCH_BLOCK_END("}SWITCH"),
    SWITCH_CASE("CASE"),
    SELECT_STATEMENT("SELECT"),
    SELECT_BLOCK_BEGIN("SELECT{"),
    SELECT_BLOCK_END("}SELECT"),
    CASE_BLOCK_BEGIN("CASE{"),
    CASE_BLOCK_END("}CASE"),

    // STATEMENTS

    VARIABLE_DECLARATION("VAR_DECL"),
    FUNCTION_LITERAL("FUNC_LIT"),
    ASSIGNMENT("ASSIGN"),
    SEND_STATEMENT("SEND"),
    RECEIVE_STATEMENT("RECV"),
    INVOCATION("INVOC"),
    ARGUMENT("ARG"),
    STATEMENT_BLOCK_BEGIN("INNER{"),
    STATEMENT_BLOCK_END("}INNER"),

    // OBJECT CREATION

    ARRAY_ELEMENT("ARRAY_ELEM"),
    MAP_ELEMENT("MAP_ELEM"),
    SLICE_ELEMENT("SLICE_ELEM"),
    NAMED_TYPE_ELEMENT("CTYPE_ELEM"),
    ARRAY_CONSTRUCTOR("ARRAY()"),
    MAP_CONSTRUCTOR("MAP()"),
    SLICE_CONSTRUCTOR("SLICE()"),
    NAMED_TYPE_CONSTRUCTOR("CTYPE()"),

    // CONTROL FLOW KEYWORDS

    RETURN("RETURN"),
    BREAK("BREAK"),
    CONTINUE("CONTINUE"),
    FALLTHROUGH("FALLTHROUGH"),
    GOTO("GOTO"),
    GO("GO"),
    DEFER("DEFER");

    private final String description;

    @Override
    public String getDescription() {
        return this.description;
    }

    GoTokenType(String description) {
        this.description = description;
    }
}
