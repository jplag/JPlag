package de.jplag.java;

import de.jplag.TokenType;

public enum JavaTokenType implements TokenType {
    J_PACKAGE("PACKAGE"), // check
    J_IMPORT("IMPORT"), // check
    J_CLASS_BEGIN("CLASS{"), // check
    J_CLASS_END("}CLASS"), // check
    J_METHOD_BEGIN("METHOD{"), // check
    J_METHOD_END("}METHOD"), // check
    J_VARDEF("VARDEF"), // check
    J_SYNC_BEGIN("SYNC{"), // check
    J_SYNC_END("}SYNC"), // check
    J_DO_BEGIN("DO{"), // check
    J_DO_END("}DO"), // check
    J_WHILE_BEGIN("WHILE{"), // check
    J_WHILE_END("}WHILE"), // check
    J_FOR_BEGIN("FOR{"), // check
    J_FOR_END("}FOR"), // check
    J_SWITCH_BEGIN("SWITCH{"), // check
    J_SWITCH_END("}SWITCH"), // check
    J_CASE("CASE"), // check
    J_TRY_BEGIN("TRY{"), // check
    J_TRY_END("}TRY"), // check
    J_CATCH_BEGIN("CATCH{"), // check
    J_CATCH_END("}CATCH"), // check
    J_FINALLY_BEGIN("FINALLY{"), // check
    J_FINALLY_END("}FINALLY"), // check
    J_IF_BEGIN("IF{"), // check
    J_ELSE("ELSE"), // check
    J_IF_END("}IF"), // check
    J_COND("COND"), // check
    J_BREAK("BREAK"), // check
    J_CONTINUE("CONTINUE"), // check
    J_RETURN("RETURN"), // check
    J_THROW("THROW"), // check
    J_IN_CLASS_BEGIN("INCLASS{"), //
    J_IN_CLASS_END("}INCLASS"), //
    J_APPLY("APPLY"), // check
    J_NEWCLASS("NEWCLASS"), // check
    J_NEWARRAY("NEWARRAY"), // check
    J_ASSIGN("ASSIGN"), // check
    J_INTERFACE_BEGIN("INTERF{"), // check
    J_INTERFACE_END("}INTERF"), // check
    J_CONSTR_BEGIN("CONSTR{"), //
    J_CONSTR_END("}CONSTR"), //
    J_VOID("VOID"), //
    J_ARRAY_INIT_BEGIN("ARRINIT{"), // check
    J_ARRAY_INIT_END("}ARRINIT"), // check

    // new in 1.5
    J_ENUM_BEGIN("ENUM"), // check
    J_ENUM_CLASS_BEGIN("ENUM_CLA"), // ?? doesn't exist in JAVAC
    J_ENUM_END("}ENUM"), // check
    J_GENERIC("GENERIC"), // check
    J_ASSERT("ASSERT"), // check
    J_ANNO("ANNO"), // check
    J_ANNO_MARKER("ANNOMARK"), // ??
    J_ANNO_M_BEGIN("ANNO_M{"), // ??
    J_ANNO_M_END("}ANNO_M"), // ??
    J_ANNO_T_BEGIN("ANNO_T{"), // check
    J_ANNO_T_END("}ANNO_T"), // check
    J_ANNO_C_BEGIN("ANNO_C{"), // ??
    J_ANNO_C_END("}ANNO_C"), // ??

    // new in 1.9
    J_REQUIRES("REQUIRES"), // check
    J_PROVIDES("PROVIDES"), // check
    J_EXPORTS("EXPORTS"), // check
    J_MODULE_BEGIN("MODULE{"), // check
    J_MODULE_END("}MODULE"), // check

    // new in 13
    J_YIELD("YIELD"),

    // new in 17
    J_DEFAULT("DEFAULT"),
    J_RECORD_BEGIN("RECORD{"),
    J_RECORD_END("}RECORD");

    private final String description;

    public String getDescription() {
        return this.description;
    }

    JavaTokenType(String description) {
        this.description = description;
    }
}
