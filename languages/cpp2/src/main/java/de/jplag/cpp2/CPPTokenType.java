package de.jplag.cpp2;

import de.jplag.TokenType;

public enum CPPTokenType implements TokenType {
    CLASS_BEGIN("CLASS{"),
    CLASS_END("}CLASS"),
    STRUCT_BEGIN("STRUCT{"),
    STRUCT_END("}STRUCT"),
    ENUM_BEGIN("ENUM{"),
    ENUM_END("}ENUM"),
    UNION_BEGIN("UNION{"),
    UNION_END("}UNION"),
    BLOCK_BEGIN("INIT{"),
    BLOCK_END("}INIT"),
    FUNCTION_BEGIN("FUNCTION{"),
    FUNCTION_END("}FUNCTION"),
    DO_BEGIN("DO{"),
    DO_END("}DO"),
    WHILE_BEGIN("WHILE{"),
    WHILE_END("}WHILE"),
    FOR_BEGIN("FOR{"),
    FOR_END("}FOR"),
    SWITCH_BEGIN("SWITCH{"),
    SWITCH_END("}SWITCH"),
    CASE("CASE"),
    TRY("TRY"),
    CATCH_BEGIN("CATCH{"),
    CATCH_END("}CATCH"),
    IF_BEGIN("IF{"),
    IF_END("}IF"),
    ELSE("ELSE"),
    BREAK("BREAK"),
    CONTINUE("CONTINUE"),
    GOTO("GOTO"),
    RETURN("RETURN"),
    THROW("THROW"),
    NEWCLASS("NEWCLASS"),
    GENERIC("GENERIC"),
    NEWARRAY("NEWARRAY"),
    ARRAYINIT_BEGIN("ARRAYINIT{"),
    ARRAYINIT_END("}ARRAYINIT"),
    ASSIGN("ASSIGN"),
    STATIC_ASSERT("STATIC_ASSERT"),
    VARDEF("VARDEF"),
    QUESTIONMARK("COND"),
    ATTRIBUTE("ATTRIBUTE"),
    DEFAULT("DEFAULT"),
    APPLY("APPLY");

    private final String description;

    public String getDescription() {
        return this.description;
    }

    CPPTokenType(String description) {
        this.description = description;
    }
}
