package de.jplag.antlr.testLanguage;

import de.jplag.TokenAttribute;

public enum TestTokenAttribute implements TokenAttribute {
    ADDITION("PLUS("),
    SUBTRACTION("MINUS("),
    SUB_EXPRESSION_BEGIN("SUB {"),
    SUB_EXPRESSION_END("} SUB"),
    NUMBER("NUM"),
    VARDEF("VARDEF"),
    VARREF("VARREF");

    private final String description;

    TestTokenAttribute(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
