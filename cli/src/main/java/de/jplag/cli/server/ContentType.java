package de.jplag.cli.server;

public enum ContentType {
    ;
    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
