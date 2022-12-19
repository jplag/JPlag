package de.jplag.cpp2;

import de.jplag.TokenType;

public enum CPPTokenType implements TokenType {
    C_CLASS_BEGIN("CLASS{"),
    C_CLASS_END("}CLASS"),
    C_STRUCT_BEGIN("STRUCT{"),
    C_STRUCT_END("}STRUCT"),
    C_ENUM_BEGIN("ENUM{"),
    C_ENUM_END("}ENUM"),
    C_BLOCK_BEGIN("INIT{"),
    C_BLOCK_END("}INIT"),
    C_FUNCTION_BEGIN("FUNCTION{"),
    C_FUNCTION_END("}FUNCTION"),
    C_DO_BEGIN("DO{"),
    C_DO_END("}DO"),
    C_WHILE_BEGIN("WHILE{"),
    C_WHILE_END("}WHILE"),
    C_FOR_BEGIN("FOR{"),
    C_FOR_END("}FOR"),
    C_SWITCH_BEGIN("SWITCH{"),
    C_SWITCH_END("}SWITCH"),
    C_CASE("CASE"),
    C_TRY("TRY"),
    C_CATCH_BEGIN("CATCH{"),
    C_CATCH_END("}CATCH"),
    C_IF_BEGIN("IF{"),
    C_IF_END("}IF"),
    C_ELSE("ELSE"),
    C_BREAK("BREAK"),
    C_CONTINUE("CONTINUE"),
    C_GOTO("GOTO"),
    C_RETURN("RETURN"),
    C_THROW("THROW"),
    C_NEWCLASS("NEWCLASS"),
    C_GENERIC("GENERIC"),
    C_NEWARRAY("NEWARRAY"),
    C_ARRAYINIT_BEGIN("ARRAYINIT{"),
    C_ARRAYINIT_END("}ARRAYINIT"),
    C_ASSIGN("ASSIGN"),
    C_STATIC_ASSERT("STATIC_ASSERT"),
    C_VARDEF("VARDEF"),
    C_QUESTIONMARK("COND"),
    C_ATTRIBUTE("ATTRIBUTE"),
    C_DEFAULT("DEFAULT"),
    ;

    private final String description;

    public String getDescription() {
        return this.description;
    }

    CPPTokenType(String description) {
        this.description = description;
    }
}
