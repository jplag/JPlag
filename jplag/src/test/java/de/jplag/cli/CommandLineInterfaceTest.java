package de.jplag.cli;

import static de.jplag.CommandLineArgument.ROOT_DIRECTORY;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.fail;

import java.util.Arrays;

import de.jplag.CLI;
import de.jplag.CommandLineArgument;
import de.jplag.ExitException;
import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.options.JPlagOptions;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Test base for tests regarding the {@link CLI} and any {@link CommandLineArgument}.
 * @author Timur Saglam
 */
public class CommandLineInterfaceTest {
    protected static final String CURRENT_DIRECTORY = ".";
    protected static final float DELTA = 0.0001f;

    protected CLI cli;
    protected Namespace namespace;
    protected JPlagOptions options;

    /**
     * Creates a string for all arguments and their values that have been succesfully parsed.F
     */
    private String parsedKeys(String... arguments) {
        var keys = namespace.getAttrs().keySet().stream()
                .filter(key -> key.equals(ROOT_DIRECTORY.flag()) || Arrays.stream(arguments).anyMatch(arg -> arg.contains("-" + key)));
        return keys.map(it -> it + "=" + namespace.get(it)).collect(toSet()).toString();
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

    /**
     * Runs JPlag via the command line interface. Sets {@link CommandLineInterfaceTest#cli},
     * {@link CommandLineInterfaceTest#namespace}, and {@link CommandLineInterfaceTest#options}.
     * @param arguments arguments are the command line interface arguments.
     * @return the result of the JPlag run.
     */
    protected JPlagResult runViaCLI(String... arguments) {
        buildOptionsFromCLI(arguments);
        try {
            JPlag program = new JPlag(options);
            return program.run();
        } catch (ExitException e) {
            e.printStackTrace();
            fail(e.getMessage());
            e.printStackTrace();
        }
        throw new IllegalStateException("Should never be reached!");
    }

}
