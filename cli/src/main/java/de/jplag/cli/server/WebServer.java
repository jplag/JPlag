package de.jplag.cli.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

abstract class WebServer implements HttpHandler {

    protected final RoutingTree routingTree;

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int SUCCESS_RESPONSE = 200;
    private static final int NOT_FOUND_RESPONSE = 404;
    private static final int MAX_PORT_LOOKUPS = 4;

    private final int port;

    private HttpServer server;

    /**
     * Launches a locally hosted web server.
     * @param routingTree The routing information for the files the server provides.
     * @param port The port to use for the server. You can use 0 to use any free port.
     */
    public WebServer(RoutingTree routingTree, int port) {
        this.routingTree = routingTree;
        this.port = port;
    }

    /**
     * Launches a locally hosted web server.
     * @param port The port to use for the server. You can use 0 to use any free port.
     */
    public WebServer(int port) {
        this(new RoutingTree(), port);
    }


    /**
     * Starts the server and serves the internal report viewer. If available, the result.jplag is also exposed. If the given
     * port is already in use, the next free port will be used.
     * @return The port the server runs at
     * @throws IOException If the server cannot be started
     * @throws IllegalStateException if the server is already started.
     */
    public int start() throws IOException {
        if (server != null) {
            throw new IllegalStateException("Server already started");
        }

        int currentPort = this.port;
        int remainingLookups = MAX_PORT_LOOKUPS;
        BindException lastException = new BindException("Could not create server. Probably due to no free port found.");
        while (server == null && remainingLookups-- > 0) {
            try {
                server = HttpServer.create(new InetSocketAddress(InetAddress.getByAddress(new byte[] {127, 0, 0, 1}), currentPort), 0);
            } catch (BindException e) {
                logger.info("Port {} is not available. Trying to find a different one.", currentPort);
                lastException = e;
                currentPort++;
            }
        }
        if (server == null) {
            throw lastException;
        }
        server.createContext("/", this);
        server.setExecutor(null);
        server.start();

        return server.getAddress().getPort();
    }

    /**
     * Stops the server.
     */
    public void stop() {
        server.stop(0);
    }

    /**
     * Do not call manually. Called by the running web server.
     * @param exchange The http request
     * @throws IOException If the IO handling goes wrong
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        RoutingPath path = new RoutingPath(exchange.getRequestURI().getPath());
        Pair<RoutingPath, Routing> resolved = this.routingTree.resolveRouting(path);
        HttpRequestMethod method = HttpRequestMethod.fromName(exchange.getRequestMethod());

        if (resolved == null || !ArrayUtils.contains(resolved.getRight().allowedMethods(), method)) {
            exchange.sendResponseHeaders(NOT_FOUND_RESPONSE, 0);
            exchange.close();
            return;
        }

        logger.debug("Serving {}", path);

        ResponseData responseData = resolved.getRight().fetchData(resolved.getLeft(), exchange, this);
        if (responseData == null) {
            logger.warn("No response data found for path: {}", path.asPath());
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
