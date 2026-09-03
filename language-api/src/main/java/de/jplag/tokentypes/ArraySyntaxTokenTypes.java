package de.jplag.tokentypes;

import de.jplag.TokenType;

public enum ArraySyntaxTokenTypes implements TokenType {
    NEW_ARRAY("ARRAY_NEW"),
    ARRAY_INITIALIZER_START("ARRAY_INIT_START"),
    ARRAY_INITIALIZER_END("ARRAY_INIT_END"),;

    private String description;

    ArraySyntaxTokenTypes(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
