package de.jplag.cli.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public record ResponseData(InputStream stream, ContentType contentType, int size) {
    public ResponseData(InputStream data) {
        this(data, ContentType.PLAIN, 0);
    }

    public ResponseData(InputStream data, ContentType contentType) {
        this(data, contentType, 0);
    }

    public ResponseData(File file) throws FileNotFoundException {
        this(new FileInputStream(file), ContentType.fromPath(file.getName()), 0);
    }

    public static ResponseData fromResourceUrl(String url) {
        if (url.endsWith("/")) {
            return null;
        }

        InputStream inputStream = ResponseData.class.getResourceAsStream(url);

        if (inputStream != null) {
            return new ResponseData(inputStream, ContentType.fromPath(url));
        } else {
            return null;
        }
    }
}
