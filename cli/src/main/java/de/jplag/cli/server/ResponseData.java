package de.jplag.cli.server;

import java.io.InputStream;

/**
 * Data for a http response.
 * @param stream The stream containing the binary data
 * @param contentType The type of data
 * @param size The total size of the data
 */
public record ResponseData(InputStream stream, ContentType contentType, int size) {
    /**
     * Constructor with unknown type and size. Type will be set to PLAIN.
     * @param data The binary data to respond with
     */
    public ResponseData(InputStream data) {
        this(data, ContentType.PLAIN, 0);
    }

    /**
     * Constructor with unknown size.
     * @param data The binary data
     * @param contentType The type of content
     */
    public ResponseData(InputStream data, ContentType contentType) {
        this(data, contentType, 0);
    }

    /**
     * Creates a new instance for a given resource url.
     * @param url The resource url
     * @return The new response data
     */
    public static ResponseData fromResourceUrl(String url) {
        if (url.endsWith("/")) {
            return null;
        }

        InputStream inputStream = ResponseData.class.getResourceAsStream(url);

        if (inputStream != null) {
            return new ResponseData(inputStream, ContentType.fromPath(url));
        }
        return null;
    }
}
