package de.jplag.cli;

import de.jplag.cli.picocli.CliInputHandler;
import de.jplag.options.JPlagOptions;

/**
 * Test base for tests regarding the {@link CLI}. Solely tests if the arguments set via the command line interface are
 * propagated correctly into options. JPlag is not executed for the different command line arguments, thus these tests
 * do not test the functionality of the options during the comparison.
 * @author Timur Saglam
 */
public abstract class CommandLineInterfaceTest {
    protected static final String CURRENT_DIRECTORY = ".";
    protected static final double DELTA = 1E-5;

    protected JPlagOptions options;

    /**
     * @return An empty {@link ArgumentBuilder}
     */
    protected ArgumentBuilder arguments() {
        return new ArgumentBuilder();
    }

    /**
     * @return A {@link ArgumentBuilder} containing the CURRENT_DIRECTORY as the root directory
     */
    protected ArgumentBuilder defaultArguments() {
        return arguments().rootDirectory(CURRENT_DIRECTORY);
    }

    /**
     * Builds {@link JPlagOptions} via the command line interface.
     * @param builder The argument builder containing the values to pass to the cli
     */
    protected void buildOptionsFromCLI(ArgumentBuilder builder) throws CliException {
        CliInputHandler inputHandler = new CliInputHandler(builder.getArgumentsAsArray());
        inputHandler.parse();
        this.options = new JPlagOptionsBuilder(inputHandler).buildOptions();
    }

}
