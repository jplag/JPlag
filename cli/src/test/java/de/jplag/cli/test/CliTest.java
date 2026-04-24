package de.jplag.cli.test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.event.Level;

import de.jplag.JPlagResult;
import de.jplag.cli.CLI;
import de.jplag.cli.JPlagOptionsBuilder;
import de.jplag.cli.JPlagRunner;
import de.jplag.cli.OutputFileGenerator;
import de.jplag.cli.logger.CollectedLogger;
import de.jplag.cli.picocli.CliInputHandler;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

/**
 * Base class for Cli tests.
 * <p>
 * A test class may override the initializeParameters method, to set different default arguments for all test in the
 * class. Each test method should call runCli or runCliForOptions, to execute the cli.
 */
public abstract class CliTest {
    /**
     * Working directory path as string.
     */
    protected static final String CURRENT_DIRECTORY = ".";
    /**
     * Tolerated delta for floating point comparison.
     */
    protected static final double DELTA = 1E-5;
    private static final Field inputHandlerField;
    private static final Method getWritableFileMethod;

    static {
        try {
            inputHandlerField = CLI.class.getDeclaredField("inputHandler");
            inputHandlerField.setAccessible(true);

            getWritableFileMethod = CLI.class.getDeclaredMethod("getWritableFileName");
            getWritableFileMethod.setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final CliArgumentBuilder defaultArgumentBuilder;

    /**
     * Creates the test suite.
     */
    public CliTest() {
        this.defaultArgumentBuilder = new CliArgumentBuilder();
    }

    /**
     * Executes the cli using the default parameters. JPlag itself is not called as a part of this, only the cli.
     * @return The cli artifacts
     * @throws ExitException If JPlag throws an exception
     * @throws IOException If JPlag throws an exception
     */
    protected CliResult runCli() throws ExitException, IOException {
        return runCli(ignore -> {
        });
    }

    /**
     * Runs the cli.
     * @return The options returned by the cli
     * @throws ExitException If JPlag throws an exception
     * @throws IOException If JPlag throws an exception
     * @see #runCli()
     */
    protected JPlagOptions runCliForOptions() throws ExitException, IOException {
        return runCli().jPlagOptions();
    }

    /**
     * Runs the cli using custom options.
     * @param additionalOptionsBuilder May modify the {@link CliArgumentBuilder} object to set custom options for this run.
     * @return The options returned by the cli
     * @throws ExitException If JPlag throws an exception
     * @throws IOException If JPlag throws an exception
     * @see #runCli()
     */
    protected JPlagOptions runCliForOptions(Consumer<CliArgumentBuilder> additionalOptionsBuilder) throws ExitException, IOException {
        return runCli(additionalOptionsBuilder).jPlagOptions();
    }

    /**
     * Runs the cli.
     * @return The target path used by the cli
     * @throws ExitException If JPlag throws an exception
     * @throws IOException If JPlag throws an exception
     * @see #runCli()
     */
    protected String runCliForTargetPath() throws IOException, ExitException {
        return runCli().targetPath();
    }

    /**
     * Runs the cli using custom options.
     * @param additionalOptionsBuilder May modify the {@link CliArgumentBuilder} object to set custom options for this run.
     * @return The target path used by the cli
     * @throws ExitException If JPlag throws an exception
     * @throws IOException If JPlag throws an exception
     * @see #runCli()
     */
    protected String runCliForTargetPath(Consumer<CliArgumentBuilder> additionalOptionsBuilder) throws IOException, ExitException {
        return runCli(additionalOptionsBuilder).targetPath();
    }

    /**
     * Runs the cli.
     * @return The log level set by the cli
     * @throws ExitException If JPlag throws an exception
     * @throws IOException If JPlag throws an exception
     * @see #runCli()
     */
    protected Level runCliForLogLevel() throws IOException, ExitException {
        return runCli().logLevel();
    }

    /**
     * Runs the cli using custom options.
     * @param additionalOptionsBuilder May modify the {@link CliArgumentBuilder} object to set custom options for this run.
     * @return The log level set by the cli
     * @throws ExitException If JPlag throws an exception
     * @throws IOException If JPlag throws an exception
     * @see #runCli()
     */
    protected Level runCliForLogLevel(Consumer<CliArgumentBuilder> additionalOptionsBuilder) throws IOException, ExitException {
        return runCli(additionalOptionsBuilder).logLevel();
    }

    /**
     * Runs the cli.
     * @param additionalOptionsBuilder consumer that adds custom CLI arguments
     * @return The options returned by the cli
     * @throws ExitException If JPlag throws an exception
     * @throws IOException If JPlag throws an exception
     * @see #runCli()
     */
    protected CliResult runCli(Consumer<CliArgumentBuilder> additionalOptionsBuilder) throws ExitException, IOException {
        try (MockedStatic<JPlagRunner> runnerMock = Mockito.mockStatic(JPlagRunner.class);
                MockedStatic<OutputFileGenerator> generatorMock = Mockito.mockStatic(OutputFileGenerator.class)) {
            runnerMock.when(() -> JPlagRunner.runJPlag(ArgumentMatchers.any())).thenReturn(new JPlagResult(Collections.emptyList(), null, 1, null));
            generatorMock.when(() -> OutputFileGenerator.generateJPlagResultFile(ArgumentMatchers.any(), ArgumentMatchers.any()))
                    .then(invocationOnMock -> null);

            CliArgumentBuilder copy = this.defaultArgumentBuilder.copy();
            additionalOptionsBuilder.accept(copy);

            CLI cli = new CLI(copy.buildArguments());
            cli.executeCli();

            CliInputHandler inputHandler = (CliInputHandler) inputHandlerField.get(cli);
            JPlagOptionsBuilder optionsBuilder = new JPlagOptionsBuilder(inputHandler);

            String targetPath = (String) getWritableFileMethod.invoke(cli);

            return new CliResult(optionsBuilder.buildOptions(), targetPath, CollectedLogger.getLogLevel(), inputHandler);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Assumptions.abort("Could not access private field in CLI for test.");
            return null; // will not be executed
        }
    }

    @BeforeEach
    void setup() {
        this.initializeParameters(this.defaultArgumentBuilder);
    }

    /**
     * Adds the default parameters for this instance of {@link CliTest}.
     */
    public void addDefaultParameters() {
        this.defaultArgumentBuilder.with(CliArgument.SUBMISSION_DIRECTORIES, new String[] {CURRENT_DIRECTORY});
    }

    /**
     * Used to add options for all tests in this test class.
     * @param args The arguments builder
     */
    public void initializeParameters(CliArgumentBuilder args) {
        this.addDefaultParameters();
    }
}
