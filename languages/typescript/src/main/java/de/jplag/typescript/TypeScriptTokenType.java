package de.jplag.typescript;

public enum TypeScriptTokenType {

    ;

    private final String description;

    public String getDescription() {
        return this.description;
    }

    TypeScriptTokenType(String description) {
        this.description = description;
    }
}
