package de.jplag.typescript;

import de.jplag.TokenType;

/**
 * Tokens extracted by the TypeScript language module.
 */
public enum TypeScriptTokenType implements TokenType {

    IMPORT("IMPORT"),
    EXPORT("EXPORT"),
    NAMESPACE_BEGIN("NAMESPACE{"),
    NAMESPACE_END("}NAMESPACE"),
    CLASS_BEGIN("CLASS{"),
    CLASS_END("}CLASS"),
    INTERFACE_BEGIN("INTERFACE{"),
    INTERFACE_END("}INTERFACE"),
    ENUM_BEGIN("ENUM{"),
    ENUM_END("}ENUM"),
    METHOD_BEGIN("METHOD{"),
    METHOD_END("}METHOD"),
    WHILE_BEGIN("WHILE{"),
    WHILE_END("}WHILE"),
    FOR_BEGIN("FOR{"),
    FOR_END("}FOR"),
    ASSIGNMENT("ASSIGN"),
    IF_BEGIN("IF{"),
    IF_END("}IF"),
    SWITCH_BEGIN("SWITCH{"),
    SWITCH_END("}SWITCH"),
    SWITCH_CASE("CASE"),
    TRY_BEGIN("TRY{"),
    CATCH_BEGIN("}CATCH{"),
    CATCH_END("}CATCH"),
    FINALLY_BEGIN("FINALLY{"),
    FINALLY_END("}FINALLY"),
    BREAK("BREAK"),
    RETURN("RETURN"),
    THROW("THROW"),
    CONTINUE("CONTINUE"),
    FUNCTION_CALL("CALL"),
    ENUM_MEMBER("ENUM_MEMBER"),
    CONSTRUCTOR_BEGIN("CONSTRUCT{"),
    CONSTRUCTOR_END("}CONSTRUCT"),
    DECLARATION("DECLARE");

    private final String description;

    @Override
    public String getDescription() {
        return this.description;
    }

    TypeScriptTokenType(String description) {
        this.description = description;
    }
}
