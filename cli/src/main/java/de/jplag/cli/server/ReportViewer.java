package de.jplag.cli.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Manages the internal report viewer. Serves the static files for the report viewer and the results.jplag.
 */
public class ReportViewer implements HttpHandler {
    private static final String REPORT_VIEWER_RESOURCE_PREFIX = "report-viewer";
    private static final String INDEX_PATH = "index.html";
    private static final String RESULT_PATH = "results.jplag";
    private static final String[] OLD_VERSION_DIRECTORIES = new String[] {"v5"};

    private static final Logger logger = LoggerFactory.getLogger(ReportViewer.class);
    private static final int SUCCESS_RESPONSE = 200;
    private static final int NOT_FOUND_RESPONSE = 404;
    private static final int MAX_PORT_LOOKUPS = 4;

    private final RoutingTree routingTree;
    private final int port;

    private HttpServer server;

    /**
     * Launches a locally hosted report viewer.
     * @param zipFile The zip file to use for the report viewer
     * @param port The port to use for the server. You can use 0 to use any free port.
     * @throws IOException If the zip file cannot be read
     */
    public ReportViewer(File zipFile, int port) throws IOException {
        this.routingTree = new RoutingTree();

        this.routingTree.insertRouting("", new RoutingResources(REPORT_VIEWER_RESOURCE_PREFIX).or(new RoutingAlias(INDEX_PATH)));
        this.routingTree.insertRouting(RESULT_PATH, new RoutingStaticFile(zipFile, ContentType.RESULT_FILE));
        for (String version : OLD_VERSION_DIRECTORIES) {
            this.routingTree.insertRouting(version, new RoutingResources(version).or(new RoutingAlias(version + "/" + INDEX_PATH)));
        }

        this.port = port;
    }

    /**
     * Starts the server and serves the internal report viewer. If available, the result.jplag is also exposed. If the given
     * port is already in use, the next free port will be used.
     * @return The port the server runs at
     * @throws IOException If the server cannot be started
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
     * Stops the server
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
        if (exchange.getRequestMethod().equals("GET") && exchange.getRequestURI().getPath().equals("/folder")) {
            openFileChooser(exchange);
            return;
        }
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

    public static boolean hasCompiledViewer() {
        return ResponseData.fromResourceUrl("/" + REPORT_VIEWER_RESOURCE_PREFIX + "/index.html") != null;
    }

    public static void openFileChooser(HttpExchange exchange) throws IOException {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            logger.warn("Can not open file chooser in headless environment");
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {

                }
                // we need to create hidden JFrame to ensure the file chooser actually gets shown on all operating systems
                JFrame frame = new JFrame();
                frame.setAlwaysOnTop(true);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setUndecorated(true);
                frame.setSize(0, 0);
                frame.setLocationRelativeTo(null); // center on screen
                frame.setVisible(true);

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int choice = fileChooser.showOpenDialog(frame);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    try {
                        exchange.sendResponseHeaders(200, 0);
                        String path = fileChooser.getSelectedFile().getAbsolutePath();
                        exchange.getResponseHeaders().set("Content-Type", "text/plain");
                        exchange.getResponseHeaders().set("Content-Length", String.valueOf(path.length()));
                        exchange.getResponseBody().write(path.getBytes());
                        exchange.getResponseBody().flush();
                        exchange.getResponseBody().close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        exchange.sendResponseHeaders(400, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                frame.dispose();
            });
        } catch (InterruptedException | InvocationTargetException e) {
            logger.error("Could not open file chooser");
            exchange.sendResponseHeaders(400, 0);
        }
        exchange.close();
    }
}
