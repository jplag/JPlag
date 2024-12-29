package de.jplag.tokentypes;

import de.jplag.TokenType;

public enum InlineIfTokenTypes implements TokenType {
    CONDITION("CONDITION");

    private String description;

    InlineIfTokenTypes(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
