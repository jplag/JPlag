package de.jplag.swift;

import de.jplag.TokenType;

public enum SwiftTokenType implements TokenType {
    IMPORT("IMPORT"),
    CLASS_DECLARATION("CLASS"),
    STRUCT_DECLARATION("STRUCT"),
    ENUM_DECLARATION("ENUM"),
    PROTOCOL_DECLARATION("PROTOCOL"),
    CLASS_BODY_BEGIN("CLASS{"),
    CLASS_BODY_END("}CLASS"),
    STRUCT_BODY_BEGIN("STRUCT{"),
    STRUCT_BODY_END("}STRUCT"),
    ENUM_BODY_BEGIN("ENUM{"),
    ENUM_BODY_END("}ENUM"),
    ENUM_LITERAL("ENUM CASE"),
    PROTOCOL_BODY_BEGIN("PROTOCOL{"),
    PROTOCOL_BODY_END("}PROTOCOL"),
    PROPERTY_DECLARATION("PROPERTY"),
    PROPERTY_ACCESSOR_BEGIN("ACCESSOR{"),
    PROPERTY_ACCESSOR_END("}ACCESSOR"),
    FUNCTION("FUNCTION"),
    FUNCTION_PARAMETER("PARAMETER"),
    FUNCTION_BODY_BEGIN("FUNCTION{"),
    FUNCTION_BODY_END("}FUNCTION"),
    CLOSURE_BODY_BEGIN("CLOSURE{"),
    CLOSURE_BODY_END("}CLOSURE"),
    FOR_BODY_BEGIN("FOR{"),
    FOR_BODY_END("}FOR"),
    IF_BODY_BEGIN("IF{"),
    IF_BODY_END("}IF"),
    SWITCH_BODY_BEGIN("SWITCH{"),
    SWITCH_BODY_END("}SWITCH"),
    SWITCH_CASE("CASE"),
    WHILE_BODY_BEGIN("WHILE{"),
    WHILE_BODY_END("}WHILE"),
    REPEAT_WHILE_BODY_BEGIN("REPEAT{"),
    REPEAT_WHILE_BODY_END("}REPEAT"),
    DEFER_BODY_BEGIN("DEFER{"),
    DEFER_BODY_END("}DEFER"),
    DO_TRY_BODY_BEGIN("DO{"),
    DO_TRY_BODY_END("}DO"),
    CATCH_BODY_BEGIN("CATCH{"),
    CATCH_BODY_END("}CATCH"),
    THROW("THROW"),
    RETURN("RETURN"),
    CONTINUE("CONTINUE"),
    BREAK("BREAK"),
    FALLTHROUGH("FALLTHROUGH"),
    ASSIGNMENT("ASSIGN"),
    FUNCTION_CALL("CALL");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }

    SwiftTokenType(String description) {
        this.description = description;
    }
}
