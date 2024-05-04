package de.jplag.haskell;

import de.jplag.TokenType;

public enum HaskellTokenType implements TokenType {
    ;

    private final String description;

    @Override
    public String getDescription() {
        return this.description;
    }

    HaskellTokenType(String description) {
        this.description = description;
    }
}
