package de.jplag.scala;

import de.jplag.TokenType;

public enum ScalaTokenType implements TokenType {
    PACKAGE("PACKAGE"),
    IMPORT("IMPORT"),
    CLASS_BEGIN("CLASS{"),
    CLASS_END("}CLASS"),
    METHOD_DEF("METHOD"),
    METHOD_BEGIN("METHOD{"),
    METHOD_END("}METHOD"),
    VARIABLE_DEFINITION("VAR_DEF"),
    DO_WHILE("DO-WHILE"),
    DO_WHILE_END("END-DO-WHILE"),
    DO_BODY_BEGIN("DO{"),
    DO_BODY_END("}DO"),
    WHILE("WHILE"),
    WHILE_BODY_BEGIN("WHILE{"),
    WHILE_BODY_END("}WHILE"),
    FOR("FOR"),
    FOR_BODY_BEGIN("FOR{"),
    FOR_BODY_END("}FOR"),
    CASE_STATEMENT("CASE"),
    CASE_BEGIN("CASE{"),
    CASE_END("}CASE"),
    TRY_BEGIN("TRY{"),
    CATCH_BEGIN("CATCH{"),
    CATCH_END("}CATCH"),
    FINALLY("FINALLY"),
    IF("IF"),
    IF_BEGIN("IF{"),
    IF_END("}IF"),
    ELSE("ELSE"),
    ELSE_BEGIN("ELSE{"),
    ELSE_END("}ELSE"),
    RETURN("RETURN"),
    THROW("THROW"),
    NEW_CREATION_BEGIN("NEW{"),
    NEW_CREATION_END("}NEW"),
    APPLY("APPLY"),
    ASSIGN("ASSIGN"),
    TRAIT_BEGIN("TRAIT{"),
    TRAIT_END("}TRAIT"),
    CONSTRUCTOR_BEGIN("CONSTR{"),
    CONSTRUCTOR_END("}CONSTR"),
    MATCH_BEGIN("MATCH{"),
    MATCH_END("}MATCH"),
    GUARD("GUARD"),
    OBJECT_BEGIN("OBJECT{"),
    OBJECT_END("}OBJECT"),
    MACRO("MACRO"),
    MACRO_BEGIN("MACRO{"),
    MACRO_END("}MACRO"),
    TYPE("TYPE"),

    FUNCTION_BEGIN("FUNC{"),
    FUNCTION_END("}FUNC"),
    PARTIAL_FUNCTION_BEGIN("PFUNC{"),
    PARTIAL_FUNCTION_END("}PFUNC"),

    YIELD("YIELD"),

    PARAMETER("PARAM"),
    ARGUMENT("ARG"),
    NEW_OBJECT("NEW(),"),
    SELF_TYPE("SELF"),
    TYPE_PARAMETER("T_PARAM"),
    TYPE_ARGUMENT("T_ARG"),
    BLOCK_START("{"),
    BLOCK_END("}"),
    ENUM_GENERATOR("ENUMERATE"),
    MEMBER("MEMBER");

    private final String description;

    public String getDescription() {
        return description;
    }

    ScalaTokenType(String description) {
        this.description = description;
    }
}
