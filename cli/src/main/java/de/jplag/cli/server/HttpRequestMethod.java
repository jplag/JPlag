package de.jplag.cli.server;

/**
 * Wraps the http request methods used by JPlag. Request methods determine the capabilities of a http request.
 */
public enum HttpRequestMethod {
    GET("GET"),
    POST("POST");

    private final String name;

    /**
     * Constructs a request method with the given name.
     * @param name The name of the request method.
     */
    HttpRequestMethod(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this HTTP request method.
     * @return the method name (e.g. "GET", "POST").
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the corresponding HttpRequestMethod for the given name.
     * @param name the method name to match.
     * @return the matching HttpRequestMethod, or null if not found.
     */
    public static HttpRequestMethod fromName(String name) {
        for (HttpRequestMethod value : HttpRequestMethod.values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }

        return null;
    }
}
