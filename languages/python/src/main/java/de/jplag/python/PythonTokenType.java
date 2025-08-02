package de.jplag.python;

import de.jplag.TokenType;

public enum PythonTokenType implements TokenType {
    // Python 3.6
    IMPORT("IMPORT"),
    CLASS_BEGIN("CLASS{"),
    CLASS_END("}CLASS"),
    METHOD_BEGIN("METHOD{"),
    METHOD_END("}METHOD"),
    ASSIGN("ASSIGN"),
    WHILE_BEGIN("WHILE{"),
    WHILE_END("}WHILE"),
    FOR_BEGIN("FOR{"),
    FOR_END("}FOR"),
    TRY_BEGIN("TRY{"),
    TRY_END("}TRY"),
    EXCEPT_BEGIN("EXCEPT{"),
    EXCEPT_END("}EXCEPT"),
    FINALLY_BEGIN("FINALLY{"),
    FINALLY_END("}FINALLY"),
    IF_BEGIN("IF{"),
    IF_END("}IF"),
    APPLY("APPLY"),
    BREAK("BREAK"),
    CONTINUE("CONTINUE"),
    RETURN("RETURN"),
    RAISE("RAISE"),
    DECORATOR_BEGIN("DECOR{"),
    DECORATOR_END("}DECOR"),
    LAMBDA("LAMBDA"),
    ASSERT("ASSERT"),
    YIELD("YIELD"),
    DEL("DEL"),
    WITH_BEGIN("WITH{"),
    WITH_END("}WITH"),
    ASYNC("ASYNC"),
    AWAIT("AWAIT"),
    PASS("PASS"),
    GLOBAL("GLOBAL"),
    NONLOCAL("NONLOCAL"),
    LIST("LIST"),
    SET("SET"),
    DICTIONARY("DICTIONARY"),

    // Python 3.8
    NAMED_EXPR("NAMED"), // ":=" named expression (PEP 572)

    // Python 3.10
    MATCH_BEGIN("MATCH{"),
    MATCH_END("}MATCH"),
    CASE("CASE"),

    // Python 3.11
    EXCEPT_GROUP_BEGIN("EXCEPT*{"),
    EXCEPT_GROUP_END("}EXCEPT*"),

    // Python 3.12
    TYPE_ALIAS("TYPE"); // type_alias_statement (PEP 695)

    private final String description;

    @Override
    public String getDescription() {
        return this.description;
    }

    PythonTokenType(String description) {
        this.description = description;
    }
}
