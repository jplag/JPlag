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
            "Scientifically Published", "Maintained by SDQ", "RIP Structure and Table", "What else?", "You have been warned", "Since Java 1.0",
            "More Abstract than Tree", "Students Nightmare", "No, changing variable names does not work"};

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
            File reportDir = new File(arguments.getString("r"));
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
        String fileSuffixString = namespace.getString(SUFFIXES.flag());
        String[] fileSuffixes = new String[] {};
        if (fileSuffixString != null) {
            fileSuffixes = fileSuffixString.replaceAll("\\s+", "").split(",");
        }
        LanguageOption language = LanguageOption.fromDisplayName(namespace.getString(LANGUAGE.flag()));
        JPlagOptions options = new JPlagOptions(namespace.getString(ROOT_DIRECTORY.flag()), language);
        options.setBaseCodeSubmissionName(namespace.getString(BASE_CODE.flag()));
        options.setVerbosity(Verbosity.fromOption(namespace.getString(VERBOSITY.flag())));
        options.setDebugParser(namespace.getBoolean(DEBUG.flag()));
        options.setSubdirectoryName(namespace.getString(SUBDIRECTORY.flag()));
        options.setFileSuffixes(fileSuffixes);
        options.setExclusionFileName(namespace.getString(EXCLUDE_FILE.flag()));
        ComparisonMode.fromName(namespace.getString(COMPARISON_MODE.flag())).ifPresentOrElse(it -> options.setComparisonMode(it),
                () -> System.out.println("Unknown comparison mode, using default mode!"));

        String minTokenMatch = namespace.getString(MIN_TOKEN_MATCH.flag());
        if (minTokenMatch != null) {
            try {
                options.setMinTokenMatch(Integer.parseInt(minTokenMatch));
            } catch (NumberFormatException e) {
                System.out.println("Illegal comparison sensitivity. Ignoring input and taking language default value.");
            }
        }

        String similarityThreshold = namespace.getString(SIMILARITY_THRESHOLD.flag());
        if (similarityThreshold != null) {
            try {
                options.setSimilarityThreshold(Float.parseFloat(similarityThreshold));
            } catch (NumberFormatException e) {
                System.out.println("Illegal similarity threshold. Taking 0 as default value.");
                options.setSimilarityThreshold(0); // TODO SH: Remove code duplication
            }
        }

        String maxNumberOfMatches = namespace.getString(STORED_MATCHES.flag());
        if (maxNumberOfMatches != null) {
            try {
                options.setMaxNumberOfMatches(Integer.parseInt(maxNumberOfMatches));
            } catch (NumberFormatException e) {
                System.out.println("Illegal maximum number of matches. Taking 30 as default value.");
                options.setMaxNumberOfMatches(30); // TODO SH: Remove code duplication
            }
        }

        return options;
    }

    private String generateDescription() {
        var randomDescription = DESCRIPTIONS[new Random().nextInt(DESCRIPTIONS.length)];
        return String.format("JPlag - %s", randomDescription);
    }
}
