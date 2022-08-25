package de.jplag;

public enum SharedTokenType implements TokenType {
    /**
     * Marks the end of the file, has a special purpose in the comparison algorithm.
     */
    FILE_END("EOF"),

    /**
     * Used to optionally separate methods from each other with an always marked token.
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