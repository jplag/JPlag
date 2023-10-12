package de.jplag.cli.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class RoutingStaticFile implements Routing {
    private final byte[] data;
    private final ContentType contentType;

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
        } else {
            return null;
        }
    }
}
