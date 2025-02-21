package de.jplag.tokentypes;

import de.jplag.TokenType;

/**
 * Contains token types for code structures like packages or namespaces
 */
public enum CodeStructureTokenTypes implements TokenType {
    CONTEXT_DEFINITION("CONTEXT"),
    IMPORT("IMPORT"),;

    private String description;

    CodeStructureTokenTypes(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
