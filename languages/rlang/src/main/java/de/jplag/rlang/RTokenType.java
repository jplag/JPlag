package de.jplag.rlang;

import de.jplag.TokenType;

/**
 * Tokens in R that are deemed important when comparing submissions for plagiarisms. Based on an R module for JPlag
 * v2.15 by Olmo Kramer, see their <a href="https://github.com/CodeGra-de/jplag/tree/master/jplag.frontend.R">JPlag
 * fork</a>.
 */
public enum RTokenType implements TokenType {
    BEGIN_FUNCTION("FUNCTION{"),
    END_FUNCTION("}FUNCTION"),
    FUNCTION_CALL("FUNCTION()"),
    NUMBER("NUMBER"),
    STRING("STRING"),
    BOOL("BOOL"),
    ASSIGN("ASSIGN"),
    ASSIGN_FUNC("ASSIGN_FUNC"),
    ASSIGN_LIST("ASSIGN_LIST"),
    HELP("HELP"),
    INDEX("INDEX"),
    PACKAGE("PACKAGE"),
    IF_BEGIN("IF{"),
    IF_END("}IF-ELSE"),
    FOR_BEGIN("FOR{"),
    FOR_END("}FOR"),
    WHILE_BEGIN("WHILE{"),
    WHILE_END("}WHILE"),
    REPEAT_BEGIN("REPEAT{"),
    REPEAT_END("}REPEAT"),
    NEXT("NEXT"),
    BREAK("BREAK"),
    COMPOUND_BEGIN("COMPOUND{"),
    COMPOUND_END("}COMPOUND");

    private final String description;

    public String getDescription() {
        return this.description;
    }

    RTokenType(String description) {
        this.description = description;
    }
}
