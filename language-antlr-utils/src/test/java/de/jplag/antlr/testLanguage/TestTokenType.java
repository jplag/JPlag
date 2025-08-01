package de.jplag.antlr.testLanguage;

import de.jplag.TokenType;

/**
 * Artificial token types for testing.
 */
public enum TestTokenType implements TokenType {
    ADDITION("PLUS("),
    SUBTRACTION("MINUS("),
    SUB_EXPRESSION_BEGIN("SUB {"),
    SUB_EXPRESSION_END("} SUB"),
    NUMBER("NUM"),
    VARDEF("VARDEF"),
    VARREF("VARREF");

    private final String description;

    TestTokenType(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
