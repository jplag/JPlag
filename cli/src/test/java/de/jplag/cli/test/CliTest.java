package de.jplag.cli.test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import de.jplag.JPlagResult;
import de.jplag.cli.*;
import de.jplag.cli.picocli.CliInputHandler;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

public abstract class CliTest {
    protected static final String CURRENT_DIRECTORY = ".";
    protected static final double DELTA = 1E-5;
    private static final Field inputHandlerField;

    static {
        try {
            inputHandlerField = CLI.class.getDeclaredField("inputHandler");
            inputHandlerField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final CliArgBuilder args;

    public CliTest() {
        this.args = new CliArgBuilder();
    }

    protected CliResult runCli() throws ExitException, IOException {
        return runCli(ignore -> {
        });
    }

    protected JPlagOptions runCliForOptions() throws ExitException, IOException {
        return runCli().jPlagOptions();
    }

    protected JPlagOptions runCliForOptions(Consumer<CliArgBuilder> additionalOptionsBuilder) throws ExitException, IOException {
        return runCli(additionalOptionsBuilder).jPlagOptions();
    }

    protected CliResult runCli(Consumer<CliArgBuilder> additionalOptionsBuilder) throws ExitException, IOException {
        try {
            try (MockedStatic<JPlagRunner> runnerMock = Mockito.mockStatic(JPlagRunner.class)) {
                runnerMock.when(() -> JPlagRunner.runJPlag(Mockito.any())).thenReturn(new JPlagResult(Collections.emptyList(), null, 1, null));
                try (MockedStatic<OutputFileGenerator> generatorMock = Mockito.mockStatic(OutputFileGenerator.class)) {
                    generatorMock.when(() -> OutputFileGenerator.generateJPlagResultZip(Mockito.any(), Mockito.any())).then(invocationOnMock -> null);

                    CliArgBuilder copy = this.args.copy();
                    additionalOptionsBuilder.accept(copy);

                    CLI cli = new CLI(copy.buildArguments());
                    cli.executeCli();

                    CliInputHandler inputHandler = (CliInputHandler) inputHandlerField.get(cli);
                    JPlagOptionsBuilder optionsBuilder = new JPlagOptionsBuilder(inputHandler);

                    return new CliResult(optionsBuilder.buildOptions());
                }
            }
        } catch (IllegalAccessException e) {
            Assumptions.abort("Could not access private field in CLI for test.");
            return null; // will not be executed
        }
    }

    @BeforeEach
    void setup() {
        this.initializeParameters(this.args);
    }

    public void addDefaultParameters() {
        this.args.with(CliArg.SUBMISSION_DIRECTORIES, new String[] {CURRENT_DIRECTORY});
    }

    public abstract void initializeParameters(CliArgBuilder args);
}
