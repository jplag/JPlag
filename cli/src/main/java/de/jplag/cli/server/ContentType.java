package de.jplag.cli.server;

/**
 * The type of data
 */
public enum ContentType {
    HTML("text/html; charset=utf-8"),
    JS("application/javascript; charset=utf-8"),
    CSS("text/css; charset=utf-8"),
    PNG("image/png"),
    PLAIN("text/plain; charset=utf-8"),
    ZIP("application/zip");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Guesses the type from the given path using the suffix after the last '.'.
     * @param path The path to guess from
     * @return The guessed type
     */
    public static ContentType fromPath(String path) {
        return switch (path.substring(path.lastIndexOf('.'))) {
            case ".html" -> ContentType.HTML;
            case ".js" -> ContentType.JS;
            case ".css" -> ContentType.CSS;
            case ".png" -> ContentType.PNG;
            case ".zip" -> ContentType.ZIP;
            default -> ContentType.PLAIN;
        };
    }
}
