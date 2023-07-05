package de.jplag.cli;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ReportViewer implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ReportViewer.class);
    private static final int SUCCESS_RESPONSE = 200;
    private static final int METHOD_NOT_ALLOWED_RESPONSE = 405;
    private static final int NOT_FOUND_RESPONSE = 404;
    private final Map<String, byte[]> cache = new HashMap<>();

    private HttpServer server;

    public int start() throws IOException {
        if (server != null) {
            throw new IllegalStateException("Server already started");
        }
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/", this);
        server.setExecutor(null);
        server.start();

        return server.getAddress().getPort();
    }

    public void stop() {
        server.stop(0);
    }

    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(METHOD_NOT_ALLOWED_RESPONSE, 0);
            exchange.getResponseBody().close();
            return;
        }

        String path = exchange.getRequestURI().getPath();
        path = path.endsWith("/") ? path + "index.html" : path;

        logger.debug("Serving {}", path);

        byte[] data = cache.get(path);
        if (data == null) {
            InputStream stream = getClass().getResourceAsStream(path);
            if (stream != null) {
                try (stream) {
                    data = stream.readAllBytes();
                }
                cache.put(path, data);
            }
        }

        if (data != null) {
            String fileEnding = path.substring(path.lastIndexOf('.'));
            String contentType = switch (fileEnding) {
                case ".html" -> "text/html; charset=utf-8";
                case ".js" -> "application/javascript; charset=utf-8";
                case ".css" -> "text/css; charset=utf-8";
                case ".png" -> "image/png";
                default -> "text/plain; charset=utf-8";
            };

            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.add("Accept-Ranges", "bytes");
            responseHeaders.add("Content-Type", contentType);
            exchange.sendResponseHeaders(SUCCESS_RESPONSE, 0);

            OutputStream outputStream = exchange.getResponseBody();
            ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
            dataStream.transferTo(outputStream);

            outputStream.flush();
            outputStream.close();
            dataStream.close();
            return;
        }

        exchange.sendResponseHeaders(NOT_FOUND_RESPONSE, 0);
        exchange.getResponseBody().close();
    }
}
