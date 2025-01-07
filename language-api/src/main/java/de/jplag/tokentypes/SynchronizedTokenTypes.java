package de.jplag.tokentypes;

import de.jplag.TokenAttribute;

public enum SynchronizedTokenTypes implements TokenAttribute {
    SYNCHRONIZED_START("SYNC_START"),
    SYNCHRONIZED_END("SYNC_END");

    private String description;

    SynchronizedTokenTypes(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
