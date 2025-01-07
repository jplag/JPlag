package de.jplag.tokentypes;

import de.jplag.TokenAttribute;

public enum InlineIfTokenTypes implements TokenAttribute {
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
