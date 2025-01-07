package de.jplag.tokentypes;

import de.jplag.TokenAttribute;

public enum AssertTokenTypes implements TokenAttribute {
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
