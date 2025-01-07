package de.jplag.text;

import de.jplag.TokenAttribute;

public record TextTokenAttribute(String description) implements TokenAttribute {
    public TextTokenAttribute(String description) {
        this.description = description.toLowerCase();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
