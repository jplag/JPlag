package de.jplag.python3;

import de.jplag.TokenAttribute;

public enum Python3TokenAttribute implements TokenAttribute {
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
    EXCEPT_BEGIN("CATCH{"),
    EXCEPT_END("}CATCH"),
    FINALLY("FINALLY"),
    IF_BEGIN("IF{"),
    IF_END("}IF"),
    APPLY("APPLY"),
    BREAK("BREAK"),
    CONTINUE("CONTINUE"),
    RETURN("RETURN"),
    RAISE("RAISE"),
    DEC_BEGIN("DECOR{"),
    DEC_END("}DECOR"),
    LAMBDA("LAMBDA"),
    ARRAY("ARRAY"),
    ASSERT("ASSERT"),
    YIELD("YIELD"),
    DEL("DEL"),
    WITH_BEGIN("WITH}"),
    WITH_END("}WITH"),
    ASYNC("ASYNC"),
    AWAIT("AWAIT");

    private final String description;

    @Override
    public String getDescription() {
        return this.description;
    }

    Python3TokenAttribute(String description) {
        this.description = description;
    }
}