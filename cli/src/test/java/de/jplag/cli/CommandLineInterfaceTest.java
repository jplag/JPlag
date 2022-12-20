package de.jplag.cli;

import static de.jplag.cli.CommandLineArgument.ROOT_DIRECTORY;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;

import de.jplag.options.JPlagOptions;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Test base for tests regarding the {@link CLI} and any {@link CommandLineArgument}. Solely tests if the arguments set
 * via the command line interface are propagated correctly into options. JPlag is not executed for the different command
 * line arguments, thus these tests do not test the functionality of the options during the comparison.
 * @author Timur Saglam
 */
public abstract class CommandLineInterfaceTest {
    protected static final String CURRENT_DIRECTORY = ".";
    protected static final double DELTA = 1E-5;

    protected CLI cli;
    protected Namespace namespace;
    protected JPlagOptions options;

    /**
     * Creates a string for all arguments and their values that have been succesfully parsed.
     */
    private String parsedKeys(String... arguments) {
        var keys = namespace.getAttrs().keySet().stream()
                .filter(key -> key.equals(ROOT_DIRECTORY.flag()) || Arrays.stream(arguments).anyMatch(arg -> arg.contains("-" + key)));
        return keys.map(it -> it.toString() + "=" + namespace.get(it)).collect(toSet()).toString();
    }

    /**
     * Builds a CLI string for a CLI argument and a value.
     * @param argument is the CLI argument.
     * @param value is the value for that argument.
     * @return <code>"flag value"</code>
     */
    protected String buildArgument(CommandLineArgument argument, String value) {
        return argument.flag() + "=" + value;
    }

    /**
     * Builds {@link JPlagOptions} via the command line interface. Sets {@link CommandLineInterfaceTest#cli},
     * {@link CommandLineInterfaceTest#namespace}, and {@link CommandLineInterfaceTest#options}.
     * @param arguments are the command line interface arguments.
     */
    protected void buildOptionsFromCLI(String... arguments) {
        cli = new CLI();
        namespace = cli.parseArguments(arguments);
        System.out.println("Parsed arguments: " + parsedKeys(arguments));
        options = cli.buildOptionsFromArguments(namespace);
    }

}
