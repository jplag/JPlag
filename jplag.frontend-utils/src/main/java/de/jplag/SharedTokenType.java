package de.jplag;

/**
 * Shared token types that occur for any language.
 */
public enum SharedTokenType implements TokenType {
    /**
     * Marks the end of the file. Every parsed file must have this token type as its last element.
     */
    FILE_END("EOF"),

    /**
     * Indicates a separation in the token list. Match subsequences cannot extend beyond tokens with this type.
     */
    SEPARATOR("---------");

    private final String description;

    public String getDescription() {
        return description;
    }

    private SharedTokenType(String description) {
        this.description = description;
    }
}