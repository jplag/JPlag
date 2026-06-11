package de.jplag.java.babylon.tokenizer.impl;

import de.jplag.TokenType;

/**
 * TokenType from a CodeModel without a corresponding enum entry.
 *
 * @param description the user-readable description of this token type
 */
public record UnknownTokenType(String description) implements TokenType {
    @Override
    public String getDescription() {
        return description;
    }
}
