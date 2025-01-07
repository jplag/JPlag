package de.jplag.tokentypes;

import de.jplag.TokenAttribute;

public enum AnnotationTokenTypes implements TokenAttribute {
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
