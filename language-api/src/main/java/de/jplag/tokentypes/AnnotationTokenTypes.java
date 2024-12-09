package de.jplag.tokentypes;

import de.jplag.TokenType;

public enum AnnotationTokenTypes implements TokenType {
    ANNOTATION("ANNO");

    private String description;

    AnnotationTokenTypes(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
