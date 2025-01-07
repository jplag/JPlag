package de.jplag.tokentypes;

import de.jplag.TokenAttribute;

public enum ObjectOrientationTokens implements TokenAttribute {
    CONSTRUCTOR("CONST"),
    CONSTRUCTOR_END("CONT_END"),
    METHOD("METHOD"),
    METHOD_END("METHOD_END"),
    NEW("NEW"),
    CLASS_DEF("CLASS_DEF"),
    CLASS_END("CLASS_END"),
    ENUM_DEF("ENUM_DEF"),
    ENUM_END("ENUM_END"),;

    private String description;

    ObjectOrientationTokens(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
