package de.jplag.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.cli.logger.CliProgressBarProvider;
import de.jplag.cli.logger.CollectedLogger;
import de.jplag.cli.logger.CollectedLoggerFactory;
import de.jplag.cli.picocli.CliInputHandler;
import de.jplag.exceptions.ExitException;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.options.JPlagOptions;
import de.jplag.util.FileUtils;

/**
 * Command line interface class, allows using via command line.
 * @see CLI#main(String[])
 */
public final class CLI {
    private static final Logger logger = LoggerFactory.getLogger(CLI.class);

    private static final String DEFAULT_FILE_EXTENSION = ".jplag";
    private static final int NAME_COLLISION_ATTEMPTS = 4;

    private static final String OUTPUT_FILE_EXISTS = "The output file already exists. You can use --overwrite to overwrite the file.";
    private static final String OUTPUT_FILE_NOT_WRITABLE = "The output file (%s) cannot be written to.";

    private static final String ZIP_FILE_EXTENSION = ".zip";

    private final CliInputHandler inputHandler;

    /**
     * Creates a cli.
     * @param args The command line arguments
     */
    public CLI(String[] args) {
        this.inputHandler = new CliInputHandler(args);
    }

    /**
     * Executes the cli.
     * @throws ExitException If anything on the side of JPlag goes wrong
     * @throws IOException If any files did not work
     */
    public void executeCli() throws ExitException, IOException {
        logger.debug("Your version of JPlag is {}", JPlag.JPLAG_VERSION);

        boolean shouldAbortRunNow = this.inputHandler.parse();

        // check version regardless of parsing result
        if (!this.inputHandler.getCliOptions().advanced.skipVersionCheck) {
            JPlagVersionChecker.printVersionNotification();
        }

        if (shouldAbortRunNow) {
            // help text has been printed, do nothing else
            return;
        }

        CollectedLogger.setLogLevel(this.inputHandler.getCliOptions().advanced.logLevel);
        ProgressBarLogger.setProgressBarProvider(new CliProgressBarProvider());
        if (this.inputHandler.getCliOptions().advanced.submissionCharsetOverride != null) {
            FileUtils.setOverrideSubmissionCharset(this.inputHandler.getCliOptions().advanced.submissionCharsetOverride);
        }

        switch (this.inputHandler.getCliOptions().mode) {
            case RUN -> runJPlag();
            case VIEW -> runViewer(this.inputHandler.getFileForViewMode());
            case RUN_AND_VIEW -> runAndView();
            case AUTO -> selectModeAutomatically();
        }

    }

    /**
     * Executes the cli and handles the exceptions that might occur.
     * @return true, if an exception has been caught.
     */
    public boolean executeCliAndHandleErrors() {
        boolean hadErrors = false;

        try {
            this.executeCli();
        } catch (IOException | ExitException exception) {
            if (exception.getCause() != null) {
                logger.error("{} - {}", exception.getMessage(), exception.getCause().getMessage());
            } else {
                logger.error(exception.getMessage());
            }
            hadErrors = true;
        } finally {
            finalizeLogger();
        }

        return hadErrors;
    }

    /**
     * Runs JPlag and returns the file the result has been written to.
     * @return The file containing the result
     * @throws ExitException If JPlag threw an exception
     * @throws FileNotFoundException If the file could not be written
     */
    public File runJPlag() throws ExitException, FileNotFoundException {
        File target = new File(getWritableFileName());

        JPlagOptionsBuilder optionsBuilder = new JPlagOptionsBuilder(this.inputHandler);
        JPlagOptions options = optionsBuilder.buildOptions();
        JPlagResult result = JPlagRunner.runJPlag(options);

        OutputFileGenerator.generateJPlagResultFile(result, target);
        OutputFileGenerator.generateCsvOutput(result, new File(getResultFileBaseName()), this.inputHandler.getCliOptions());

        return target;
    }

    /**
     * Runs JPlag and shows the result in the report viewer.
     * @throws IOException If something went wrong with the internal server.
     * @throws ExitException If JPlag threw an exception.
     */
    public void runAndView() throws IOException, ExitException {
        runViewer(runJPlag());
    }

    /**
     * Runs the report viewer using the given file as the default result.jplag.
     * @param resultFile is the result file to pass to the viewer. Can be null, if no result should be opened by default
     * @throws IOException If something went wrong with the internal server
     */
    public void runViewer(File resultFile) throws IOException {
        finalizeLogger(); // Prints the errors. The later finalizeLogger will print any errors logged after this point.
        JPlagRunner.runInternalServer(resultFile, this.inputHandler.getCliOptions().advanced.port);
    }

    private void selectModeAutomatically() throws IOException, ExitException {
        List<File> inputs = this.getAllInputs();

        if (inputs.isEmpty()) {
            this.runViewer(null);
            return;
        }

        // if the selected mode is auto and there is exactly one result file specified it is opened in the report viewer
        if (inputs.size() == 1
                && (inputs.getFirst().getName().endsWith(ZIP_FILE_EXTENSION) || inputs.getFirst().getName().endsWith(DEFAULT_FILE_EXTENSION))) {
            this.runViewer(inputs.getFirst());
            return;
        }

        this.runAndView();
    }

    private List<File> getAllInputs() {
        List<File> inputs = new ArrayList<>();
        inputs.addAll(List.of(this.inputHandler.getCliOptions().rootDirectory));
        inputs.addAll(List.of(this.inputHandler.getCliOptions().newDirectories));
        inputs.addAll(List.of(this.inputHandler.getCliOptions().oldDirectories));
        return inputs;
    }

    private void finalizeLogger() {
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        if (!(factory instanceof CollectedLoggerFactory collectedLoggerFactory)) {
            return;
        }
        collectedLoggerFactory.finalizeInstances();
    }

    private String getResultFilePath() {
        String optionValue = this.inputHandler.getCliOptions().resultFile;
        if (optionValue.endsWith(DEFAULT_FILE_EXTENSION)) {
            return optionValue;
        }
        if (optionValue.endsWith(ZIP_FILE_EXTENSION)) {
            int endIndex = optionValue.length() - ZIP_FILE_EXTENSION.length();
            return optionValue.substring(0, endIndex) + DEFAULT_FILE_EXTENSION;
        }
        return optionValue + DEFAULT_FILE_EXTENSION;
    }

    private String getResultFileBaseName() {
        String defaultOutputFile = getResultFilePath();
        return defaultOutputFile.substring(0, defaultOutputFile.length() - DEFAULT_FILE_EXTENSION.length());
    }

    private String getOffsetFileName(int offset) {
        if (offset <= 0) {
            return getResultFilePath();
        }
        return getResultFileBaseName() + "(" + offset + ")" + DEFAULT_FILE_EXTENSION;
    }

    private String getWritableFileName() throws CliException {
        int retryAttempt = 0;
        while (!this.inputHandler.getCliOptions().advanced.overwrite && new File(getOffsetFileName(retryAttempt)).exists()
                && retryAttempt < NAME_COLLISION_ATTEMPTS) {
            retryAttempt++;
        }

        String targetFileName = this.getOffsetFileName(retryAttempt);
        File targetFile = new File(targetFileName);
        if (!this.inputHandler.getCliOptions().advanced.overwrite && targetFile.exists()) {
            throw new CliException(OUTPUT_FILE_EXISTS);
        }

        if (!FileUtils.checkWritable(targetFile)) {
            throw new CliException(String.format(OUTPUT_FILE_NOT_WRITABLE, targetFileName));
        }

        return targetFileName;
    }

    /**
     * Entry point for the JPlag CLI application. Initializes the CLI and handles execution and errors.
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        // This needs to be executed before any other code, as it changes the default behavior of the JVM for network
        // connections.
        System.setProperty("java.net.preferIPv4Stack", "true");

        CLI cli = new CLI(args);
        if (cli.executeCliAndHandleErrors()) {
            System.exit(1);
        }
    }
}
