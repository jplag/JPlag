package de.jplag.tokentypes;

import de.jplag.TokenType;

public enum ObjectOrientationTokens implements TokenType {
    NEW("NEW");

    private String description;

    ObjectOrientationTokens(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
