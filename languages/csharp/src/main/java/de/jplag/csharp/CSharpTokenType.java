package de.jplag.csharp;

import de.jplag.TokenType;

/**
 * Based on the legacy C# 1.2 constants.
 */
public enum CSharpTokenType implements TokenType {
    INVOCATION("INVOC"),
    OBJECT_CREATION("OBJECT"),
    ARRAY_CREATION("ARRAY"),
    ASSIGNMENT("ASSIGN"),
    FIELD("FIELD"),
    CONSTANT("CONST"),
    LOCAL_VARIABLE("LOCAL_VAR"),
    IF("IF"),
    SWITCH_BEGIN("SWITCH{"),
    SWITCH_END("}SWITCH"),
    CASE("CASE"),
    DO("DO"),
    WHILE("WHILE"),
    FOR("FOR"),
    FOREACH("FOREACH"),
    BREAK("BREAK"),
    CONTINUE("CONTINUE"),
    GOTO("GOTO"),
    RETURN("RETURN"),
    THROW("THROW"),
    CHECKED("CHECKED"),
    UNCHECKED("UNCHECKED"),
    LOCK("LOCK"),
    USING("USING"),
    TRY("TRY"),
    CATCH("CATCH"),
    FINALLY("FINALLY"),
    NAMESPACE_BEGIN("NAMESPACE{"),
    NAMESPACE_END("}NAMESPACE"),
    USING_DIRECTIVE("USING"),
    CLASS_BEGIN("CLASS{"),
    CLASS_END("}CLASS"),
    METHOD("METHOD"),
    PROPERTY("PROPERTY"),
    EVENT("EVENT"),
    INDEXER("INDEXER"),
    OPERATOR("OPERATOR"),
    CONSTRUCTOR("CONSTR"),
    DESTRUCTOR("DESTRUCTOR"),
    STRUCT_BEGIN("STRUCT{"),
    STRUCT_END("}STRUCT"),
    INTERFACE_BEGIN("INTERFACE{"),
    INTERFACE_END("}INTERFACE"),
    ENUM("ENUM"),
    DELEGATE("DELEGATE"),
    ATTRIBUTE("ATTRIBUTE"),
    IF_END("}IF"),
    UNSAFE("UNSAFE"),
    FIXED("FIXED"),
    METHOD_BEGIN("METHOD{"),
    METHOD_END("}METHOD"),
    STRUCT("STRUCT"),
    IF_BEGIN("IF{"),
    CLASS("CLASS"),
    INTERFACE("INTERFACE"),
    ENUM_BEGIN("ENUM{"),
    ENUM_END("}ENUM"),
    ENUMERAL("ENUMERAL"),
    ACCESSORS_BEGIN("ACCESSORS{"),
    ACCESSORS_END("}ACCESSORS"),
    ACCESSOR_BEGIN("ACCESSOR{"),
    ACCESSOR_END("}ACCESSOR");

    private final String description;

    public String getDescription() {
        return this.description;
    }

    CSharpTokenType(String description) {
        this.description = description;
    }
}
