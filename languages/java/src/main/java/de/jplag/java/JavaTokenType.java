package de.jplag.java;

import de.jplag.TokenType;

/**
 * Token types used for tokenizing Java code.
 */
public enum JavaTokenType implements TokenType {
    J_PACKAGE("PACKAGE"),
    J_IMPORT("IMPORT"),
    J_CLASS_BEGIN("CLASS{"),
    J_CLASS_END("}CLASS"),
    J_METHOD_BEGIN("METHOD{"),
    J_METHOD_END("}METHOD"),
    J_VARDEF("VARDEF"),
    J_SYNC_BEGIN("SYNC{"),
    J_SYNC_END("}SYNC"),
    J_LOOP_BEGIN("LOOP{"),
    J_LOOP_END("}LOOP"),
    J_SWITCH_BEGIN("SWITCH{"),
    J_SWITCH_END("}SWITCH"),
    J_CASE("CASE"),
    J_TRY_BEGIN("TRY{"),
    J_TRY_END("}TRY"),
    J_CATCH_BEGIN("CATCH{"),
    J_CATCH_END("}CATCH"),
    J_FINALLY_BEGIN("FINALLY{"),
    J_FINALLY_END("}FINALLY"),
    J_IF_BEGIN("IF{"),
    J_IF_END("}IF"),
    J_COND("COND"),
    J_BREAK("BREAK"),
    J_CONTINUE("CONTINUE"),
    J_RETURN("RETURN"),
    J_THROW("THROW"),
    J_APPLY("APPLY"),
    J_NEWCLASS("NEWCLASS"),
    J_NEWARRAY("NEWARRAY"),
    J_ASSIGN("ASSIGN"),
    J_INTERFACE_BEGIN("INTERF{"),
    J_INTERFACE_END("}INTERF"),
    J_ARRAY_INIT_BEGIN("ARRINIT{"),
    J_ARRAY_INIT_END("}ARRINIT"),

    // new in 1.5
    J_ENUM_BEGIN("ENUM"),
    J_ENUM_END("}ENUM"),
    J_GENERIC("GENERIC"),
    J_ASSERT("ASSERT"),
    J_ANNO("ANNO"),
    J_ANNO_T_BEGIN("ANNO_T{"),
    J_ANNO_T_END("}ANNO_T"),

    // new in 1.9
    J_REQUIRES("REQUIRES"),
    J_PROVIDES("PROVIDES"),
    J_EXPORTS("EXPORTS"),
    J_MODULE_BEGIN("MODULE{"),
    J_MODULE_END("}MODULE"),

    // new in 13
    J_YIELD("YIELD"),

    // new in 17
    J_DEFAULT("DEFAULT"),
    J_RECORD_BEGIN("RECORD{"),
    J_RECORD_END("}RECORD");

    private final String description;

    @Override
    public String getDescription() {
        return this.description;
    }

    JavaTokenType(String description) {
        this.description = description;
    }
}
