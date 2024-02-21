package de.jplag.cli.server;

/**
 * Wraps the http request methods used by JPlag. Request methods determine the capabilities of a http request.
 */
public enum HttpRequestMethod {
    GET("GET"),
    POST("POST");

    private final String name;

    /**
     * @param name The name of the request method
     */
    HttpRequestMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static HttpRequestMethod fromName(String name) {
        for (HttpRequestMethod value : HttpRequestMethod.values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }

        return null;
    }
}
