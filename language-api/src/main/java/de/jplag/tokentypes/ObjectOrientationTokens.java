package de.jplag.tokentypes;

import de.jplag.TokenType;

public enum ObjectOrientationTokens implements TokenType {
    CONSTRUCTOR("CONST"),
    CONSTRUCTOR_END("CONT_END"),
    METHOD("METHOD"),
    METHOD_END("METHOD_END"),
    NEW("NEW"),;

    private String description;

    ObjectOrientationTokens(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
