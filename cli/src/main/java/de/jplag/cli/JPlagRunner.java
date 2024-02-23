package de.jplag.cli;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.cli.server.ReportViewer;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Wraps the execution of the JPlag elements, so dummy implementations can be used for unit tests.
 */
public interface JPlagRunner {
    Logger logger = LoggerFactory.getLogger(JPlagRunner.class);

    /**
     * The default JPlag runner. Simply passes the calls to the appropriate JPlag elements
     */
    JPlagRunner DEFAULT_JPLAG_RUNNER = new JPlagRunner() {
        @Override
        public JPlagResult runJPlag(JPlagOptions options) throws ExitException {
            return JPlag.run(options);
        }

        @Override
        public void runInternalServer(File zipFile, int port) throws IOException {
            ReportViewer reportViewer = new ReportViewer(zipFile, port);
            int actualPort = reportViewer.start();
            logger.info("ReportViewer started on port http://localhost:{}", actualPort);
            Desktop.getDesktop().browse(URI.create("http://localhost:" + actualPort + "/"));

            System.out.println("Press Enter key to exit...");
            System.in.read();
            reportViewer.stop();
        }
    };

    /**
     * Executes JPlag
     * @param options The options to pass to JPlag
     * @return The result returned by JPlag
     * @throws ExitException If JPlag throws an error
     */
    JPlagResult runJPlag(JPlagOptions options) throws ExitException;

    /**
     * Runs the internal server. Blocks until the server has stopped.
     * @param zipFile The zip file to pass to the server. May be null.
     * @param port The port to open the server on
     * @throws IOException If the internal server throws an exception
     */
    void runInternalServer(File zipFile, int port) throws IOException;
}
