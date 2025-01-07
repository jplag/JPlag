package de.jplag.tokentypes;

import de.jplag.TokenAttribute;

public enum ObjectOrientationWithUnionsTokenAttributes implements TokenAttribute {
    UNION_DEF("UNION_DEF"),
    UNION_END("UNION_END"),;

    private String description;

    ObjectOrientationWithUnionsTokenAttributes(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
