package de.jplag.cli;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.cli.server.ReportViewer;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

/**
 * Wraps the CLI-based execution of the JPlag components.
 */
public final class JPlagRunner {
    private static final Logger logger = LoggerFactory.getLogger(JPlagRunner.class);

    private JPlagRunner() {
    }

    /**
     * Executes JPlag.
     * @param options The options to pass to JPlag
     * @return The result returned by JPlag
     * @throws ExitException If JPlag throws an error
     */
    public static JPlagResult runJPlag(JPlagOptions options) throws ExitException {
        return JPlag.run(options);
    }

    /**
     * Runs the internal server. Blocks until the server has stopped.
     * @param resultFile is the result file to pass to the server. May be null.
     * @param port is the port to open the server on.
     * @param bindAddress is the address to bind the server to.
     * @throws IOException if the internal server throws an exception
     */
    public static void runInternalServer(File resultFile, int port, InetAddress bindAddress) throws IOException {
        if (!ReportViewer.hasCompiledViewer()) {
            logger.warn("The report viewer is not available. Check whether you compiled JPlag with the report viewer.");
            return;
        }

        if (!bindAddress.isLoopbackAddress()) {
            logger.warn("Binding to non-loopback address ({}). The report viewer may be accessible from any network.", bindAddress.getHostAddress());
        }

        ReportViewer reportViewer = new ReportViewer(resultFile, port, bindAddress);
        int actualPort = reportViewer.start();

        String displayHost = getDisplayHost(bindAddress);
        String urlHost = getUrlHost(bindAddress);

        logger.info("ReportViewer started on http://{}:{}", displayHost, actualPort);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI.create("http://" + urlHost + ":" + actualPort + "/"));
        } else {
            logger.info("Could not open browser. You can open the Report Viewer here: http://{}:{}/", displayHost, actualPort);
        }

        System.out.println("Press Enter key to exit...");
        System.in.read();
        reportViewer.stop();
    }

    private static String getDisplayHost(InetAddress bindAddress) {
        if (bindAddress.isAnyLocalAddress()) {
            return "localhost";
        }
        return bindAddress.getHostAddress();
    }

    private static String getUrlHost(InetAddress bindAddress) {
        String hostAddress = bindAddress.getHostAddress();
        if (bindAddress instanceof Inet6Address) {
            return "[" + hostAddress + "]";
        }
        return hostAddress;
    }
}
