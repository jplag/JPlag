package de.jplag.cli.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ReportViewer implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ReportViewer.class);
    private static final int SUCCESS_RESPONSE = 200;
    private static final int NOT_FOUND_RESPONSE = 404;

    private final RoutingTree routingTree;

    private HttpServer server;

    public ReportViewer(File zipFile) throws IOException {
        this.routingTree = new RoutingTree();

        this.routingTree.insertRouting("", new RoutingResources("report-viewer").or(new RoutingRedirect("index.html")));
        this.routingTree.insertRouting("result.zip", new RoutingStaticFile(zipFile, ContentType.ZIP));
    }

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

    public RoutingTree getRoutingTree() {
        return routingTree;
    }
}
