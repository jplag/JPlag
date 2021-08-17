package jplag;

import static jplag.CommandLineArgument.BASE_CODE;
import static jplag.CommandLineArgument.COMPARISON_MODE;
import static jplag.CommandLineArgument.DEBUG;
import static jplag.CommandLineArgument.EXCLUDE_FILE;
import static jplag.CommandLineArgument.LANGUAGE;
import static jplag.CommandLineArgument.MIN_TOKEN_MATCH;
import static jplag.CommandLineArgument.ROOT_DIRECTORY;
import static jplag.CommandLineArgument.SIMILARITY_THRESHOLD;
import static jplag.CommandLineArgument.STORED_MATCHES;
import static jplag.CommandLineArgument.SUBDIRECTORY;
import static jplag.CommandLineArgument.SUFFIXES;
import static jplag.CommandLineArgument.VERBOSITY;
import static jplag.CommandLineArgument.RESULT_FOLDER;

import java.io.File;
import java.util.Random;

import jplag.options.JPlagOptions;
import jplag.options.LanguageOption;
import jplag.options.Verbosity;
import jplag.reporting.Report;
import jplag.strategy.ComparisonMode;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command line interface class, allows using via command line.
 * @see CLI#main(String[])
 */
public class CLI {

    private static final String[] DESCRIPTIONS = {"Detecting Software Plagiarism", "Software-Archaeological Playground", "Since 1994",
            "Scientifically Published", "Maintained by SDQ", "RIP Structure and Table", "What else?", "You have been warned!", "Since Java 1.0",
            "More Abstract than Tree", "Students Nightmare", "No, changing variable names does not work", "The tech is out there!"};

    private static final String PROGRAM_NAME = "jplag";

    private final ArgumentParser parser;

    /**
     * Main class for using JPlag via the CLI.
     * @param args are the CLI arguments that will be passed to JPlag.
     */
    public static void main(String[] args) {
        try {
            CLI cli = new CLI();
            Namespace arguments = cli.parseArguments(args);
            JPlagOptions options = cli.buildOptionsFromArguments(arguments);
            JPlag program = new JPlag(options);
            System.out.println("JPlag initialized");
            JPlagResult result = program.run();
            File reportDir = new File(arguments.getString(RESULT_FOLDER.flag()));
            Report report = new Report(reportDir);
            report.writeResult(result);
        } catch (ExitException exception) {
            System.out.println("Error: " + exception.getReport());
            System.exit(1);
        }
    }

    /**
     * Creates the command line interface and initializes the argument parser.
     */
    public CLI() {
        parser = ArgumentParsers.newFor(PROGRAM_NAME).build().defaultHelp(true).description(generateDescription());
        for (CommandLineArgument argument : CommandLineArgument.values()) {
            argument.parseWith(parser);
        }
    }

    /**
     * Parses an array of argument strings.
     * @param arguments is the array to parse.
     * @return the parsed arguments in a {@link Namespace} format.
     */
    public Namespace parseArguments(String[] arguments) {
        try {
            return parser.parseArgs(arguments);
        } catch (ArgumentParserException exception) {
            parser.handleError(exception);
            System.exit(1);
        }
        return null;
    }

    /**
     * Builds a options instance from parsed arguments.
     * @param namespace encapsulates the parsed arguments in a {@link Namespace} format.
     * @return the newly built options.F
     */
    public JPlagOptions buildOptionsFromArguments(Namespace namespace) {
        String fileSuffixString = SUFFIXES.getFrom(namespace);
        String[] fileSuffixes = new String[] {};
        if (fileSuffixString != null) {
            fileSuffixes = fileSuffixString.replaceAll("\\s+", "").split(",");
        }
        LanguageOption language = LanguageOption.fromDisplayName(LANGUAGE.getFrom(namespace));
        JPlagOptions options = new JPlagOptions(ROOT_DIRECTORY.getFrom(namespace), language);
        options.setBaseCodeSubmissionName(BASE_CODE.getFrom(namespace));
        options.setVerbosity(Verbosity.fromOption(VERBOSITY.getFrom(namespace)));
        options.setDebugParser(DEBUG.getFrom(namespace));
        options.setSubdirectoryName(SUBDIRECTORY.getFrom(namespace));
        options.setFileSuffixes(fileSuffixes);
        options.setExclusionFileName(EXCLUDE_FILE.getFrom(namespace));
        options.setMinTokenMatch(MIN_TOKEN_MATCH.getFrom(namespace));
        options.setSimilarityThreshold(SIMILARITY_THRESHOLD.getFrom(namespace));
        options.setMaxNumberOfMatches(STORED_MATCHES.getFrom(namespace));
        ComparisonMode.fromName(COMPARISON_MODE.getFrom(namespace)).ifPresentOrElse(it -> options.setComparisonMode(it),
                () -> System.out.println("Unknown comparison mode, using default mode!"));
        return options;
    }

    private String generateDescription() {
        var randomDescription = DESCRIPTIONS[new Random().nextInt(DESCRIPTIONS.length)];
        return String.format("JPlag - %s", randomDescription);
    }
}
