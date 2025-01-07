package de.jplag.tokentypes;

import de.jplag.TokenAttribute;

public enum ObjectOrientationWithInterfacesTokenAttributes implements TokenAttribute {
    INTERFACE_DEF("INTERFACE_DEF"),
    INTERFACE_END("INTERFACE_END"),;

    private String description;

    ObjectOrientationWithInterfacesTokenAttributes(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
