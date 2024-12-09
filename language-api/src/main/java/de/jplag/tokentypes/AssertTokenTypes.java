package de.jplag.tokentypes;

import de.jplag.TokenType;

public enum AssertTokenTypes implements TokenType {
    ASSERT("ASSERT"),;

    private String description;

    AssertTokenTypes(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
