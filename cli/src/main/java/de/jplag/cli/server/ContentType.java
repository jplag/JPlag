package de.jplag.cli.server;

/**
 * Data types used by JPlag in the context of http. Contains the according mime type.
 */
public enum ContentType {
    HTML("text/html; charset=utf-8", ".html"),
    JS("application/javascript; charset=utf-8", ".js"),
    CSS("text/css; charset=utf-8", ".css"),
    PNG("image/png", ".png"),
    PLAIN("text/plain; charset=utf-8", null),
    ZIP("application/zip", ".zip"),
    RESULT_FILE("application/zip", ".jplag");

    private final String value;

    private final String nameSuffix;

    ContentType(String value, String nameSuffix) {
        this.value = value;
        this.nameSuffix = nameSuffix;
    }

    public String getValue() {
        return value;
    }

    /**
     * Guesses the type from the given path using the extension after the last dot.
     * @param path The path to guess from
     * @return The guessed type
     */
    public static ContentType fromPath(String path) {
        String extension = path.substring(path.lastIndexOf('.'));
        for (ContentType value : ContentType.values()) {
            if (extension.equals(value.nameSuffix)) {
                return value;
            }
        }
        return ContentType.PLAIN;
    }
}
