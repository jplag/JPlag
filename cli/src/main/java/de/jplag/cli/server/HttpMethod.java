package de.jplag.cli.server;

/**
 * Wraps the http methods used by JPlag.
 */
public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static HttpMethod fromName(String name) {
        for (HttpMethod value : HttpMethod.values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }

        return null;
    }
}
