package de.jplag.cli.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Manages the internal report viewer. Serves the static files for the report viewer and the results.zip.
 */
public class ReportViewer implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ReportViewer.class);
    private static final int SUCCESS_RESPONSE = 200;
    private static final int NOT_FOUND_RESPONSE = 404;

    private final RoutingTree routingTree;

    private HttpServer server;

    /**
     * @param zipFile The zip file to use for the report viewer
     * @throws IOException If the zip file cannot be read
     */
    public ReportViewer(File zipFile) throws IOException {
        this.routingTree = new RoutingTree();

        this.routingTree.insertRouting("", new RoutingResources("report-viewer").or(new RoutingAlias("index.html")));
        this.routingTree.insertRouting("results.zip", new RoutingStaticFile(zipFile, ContentType.ZIP));
    }

    /**
     * Starts the server
     * @return The port the server runs at
     * @throws IOException If the server cannot be started
     */
    public int start() throws IOException {
        if (server != null) {
            throw new IllegalStateException("Server already started");
        }
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 1996), 0);
        server.createContext("/", this);
        server.setExecutor(null);
        server.start();

        return server.getAddress().getPort();
    }

    /**
     * Stops the server
     */
    public void stop() {
        server.stop(0);
    }

    /**
     * Do not call manually. Called by the running web server.
     * @param exchange The http reqest
     * @throws IOException If the IO handling goes wrong
     */
    public void handle(HttpExchange exchange) throws IOException {
        RoutingPath path = new RoutingPath(exchange.getRequestURI().getPath());
        Pair<RoutingPath, Routing> resolved = this.routingTree.resolveRouting(path);
        HttpMethod method = HttpMethod.fromName(exchange.getRequestMethod());

        if (resolved == null || !ArrayUtils.contains(resolved.getRight().allowedMethods(), method)) {
            exchange.sendResponseHeaders(NOT_FOUND_RESPONSE, 0);
            exchange.close();
            return;
        }

        logger.debug("Serving {}", path);

        ResponseData responseData = resolved.getRight().fetchData(resolved.getLeft(), exchange, this);
        if (responseData == null) {
            logger.warn("No response data found for path: " + path.asPath());
            exchange.sendResponseHeaders(NOT_FOUND_RESPONSE, 0);
            exchange.close();
            return;
        }

        InputStream inputStream = responseData.stream();

        if (responseData.contentType() != null) {
            exchange.getResponseHeaders().set("Content-Type", responseData.contentType().getValue());
        }
        exchange.sendResponseHeaders(SUCCESS_RESPONSE, responseData.size());

        inputStream.transferTo(exchange.getResponseBody());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        inputStream.close();
    }

    RoutingTree getRoutingTree() {
        return routingTree;
    }
}
