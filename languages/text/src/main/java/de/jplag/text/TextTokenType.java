package de.jplag.text;

import de.jplag.TokenType;

/**
 * Token type for text tokens.
 * @param description is the description, will be stored in lower case.
 */
public record TextTokenType(String description) implements TokenType {

    /**
     * Creates a token.
     * @param description is the description, will be stored in lower case.
     */
    public TextTokenType(String description) {
        this.description = description.toLowerCase();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
