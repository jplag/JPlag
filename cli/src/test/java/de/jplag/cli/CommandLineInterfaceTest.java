package de.jplag.cli;

import de.jplag.options.JPlagOptions;

import picocli.CommandLine;

/**
 * Test base for tests regarding the {@link CLI}. Solely tests if the arguments set via the command line interface are
 * propagated correctly into options. JPlag is not executed for the different command line arguments, thus these tests
 * do not test the functionality of the options during the comparison.
 * @author Timur Saglam
 */
public abstract class CommandLineInterfaceTest {
    protected static final String CURRENT_DIRECTORY = ".";
    protected static final double DELTA = 1E-5;

    protected CLI cli;
    protected JPlagOptions options;

    /**
     * Builds a CLI string for a CLI argument and a value.
     * @param flag is the CLI argument.
     * @param value is the value for that argument.
     * @return <code>"flag value"</code>
     */
    protected String buildArgument(String flag, String value) {
        return flag + "=" + value;
    }

    /**
     * Builds {@link JPlagOptions} via the command line interface. Sets {@link CommandLineInterfaceTest#cli}
     * @param arguments are the command line interface arguments.
     */
    protected void buildOptionsFromCLI(String... arguments) {
        cli = new CLI();
        CommandLine.ParseResult result = cli.parseOptions(arguments);
        // System.out.println("Parsed arguments: " + parsedKeys(arguments));
        options = cli.buildOptionsFromArguments(result);
    }

}
