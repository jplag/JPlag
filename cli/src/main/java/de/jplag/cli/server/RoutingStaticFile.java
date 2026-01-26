package de.jplag.cli.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

/**
 * Responds with a given file.
 */
public class RoutingStaticFile implements Routing {
    private final byte[] data;
    private final ContentType contentType;

    /**
     * @param file The file to use
     * @param contentType The type of content in the file
     * @throws IOException If the file cannot be read
     */
    public RoutingStaticFile(File file, ContentType contentType) throws IOException {
        if (file != null) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                this.data = inputStream.readAllBytes();

                this.contentType = contentType;
            }
        } else {
            this.data = null;
            this.contentType = contentType;
        }
    }

    @Override
    public ResponseData fetchData(RoutingPath subPath, HttpExchange request, ReportViewer viewer) {
        if (this.data != null) {
            return new ResponseData(new ByteArrayInputStream(this.data), contentType, this.data.length);
        }
        return null;
    }
}
