package de.jplag.tokentypes;

import de.jplag.TokenAttribute;

/**
 * Contains token types for code structures like packages or namespaces
 */
public enum CodeStructureTokenTypes implements TokenAttribute {
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
