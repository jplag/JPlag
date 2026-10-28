package de.jplag.cli.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
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
 * Manages the internal report viewer. Serves the static files for the report viewer and the results.jplag.
 */
public class ReportViewer extends WebServer {
    private static final String REPORT_VIEWER_RESOURCE_PREFIX = "report-viewer";
    private static final String INDEX_PATH = "index.html";
    private static final String RESULT_PATH = "results.jplag";
    private static final String[] OLD_VERSION_DIRECTORIES = new String[] {"v5", "v6_1"};

    /**
     * Launches a locally hosted report viewer.
     * @param resultFile The result file to use for the report viewer
     * @param port The port to use for the server. You can use 0 to use any free port.
     * @throws IOException If the result file cannot be read
     */
    public ReportViewer(File resultFile, int port) throws IOException {
        super(port);

        this.routingTree.insertRouting("", new RoutingResources(REPORT_VIEWER_RESOURCE_PREFIX).or(new RoutingAlias(INDEX_PATH)));
        this.routingTree.insertRouting(RESULT_PATH, new RoutingStaticFile(resultFile, ContentType.RESULT_FILE));
        for (String version : OLD_VERSION_DIRECTORIES) {
            this.routingTree.insertRouting(version, new RoutingResources(version).or(new RoutingAlias(version + "/" + INDEX_PATH)));
        }
    }

    /**
     * Checks if the compiled report viewer resource is available.
     * @return true if the compiled viewer resource exists, false otherwise
     */
    public static boolean hasCompiledViewer() {
        return ResponseData.fromResourceUrl("/" + REPORT_VIEWER_RESOURCE_PREFIX + "/index.html") != null;
    }
}
