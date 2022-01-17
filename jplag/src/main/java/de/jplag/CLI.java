package de.jplag;

import static de.jplag.CommandLineArgument.BASE_CODE;
import static de.jplag.CommandLineArgument.COMPARISON_MODE;
import static de.jplag.CommandLineArgument.DEBUG;
import static de.jplag.CommandLineArgument.EXCLUDE_FILE;
import static de.jplag.CommandLineArgument.LANGUAGE;
import static de.jplag.CommandLineArgument.MIN_TOKEN_MATCH;
import static de.jplag.CommandLineArgument.RESULT_FOLDER;
import static de.jplag.CommandLineArgument.ROOT_DIRECTORY;
import static de.jplag.CommandLineArgument.SIMILARITY_THRESHOLD;
import static de.jplag.CommandLineArgument.SHOWN_COMPARISONS;
import static de.jplag.CommandLineArgument.SUBDIRECTORY;
import static de.jplag.CommandLineArgument.SUFFIXES;
import static de.jplag.CommandLineArgument.VERBOSITY;

import java.io.File;
import java.util.Random;

import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;
import de.jplag.options.Verbosity;
import de.jplag.reporting.Report;
import de.jplag.strategy.ComparisonMode;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command line interface class, allows using via command line.
 * @see CLI#main(String[])
 */
public class CLI {

    private static final String CREDITS = "Created by IPD Tichy, Guido Malpohl, and others. JPlag logo designed by Sandro Koch. Currently maintained by Sebastian Hahner and Timur Saglam.";

    private static final String[] DESCRIPTIONS = {"Detecting Software Plagiarism", "Software-Archaeological Playground", "Since 1996",
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
            File reportDir = new File(arguments.getString(RESULT_FOLDER.flagWithoutDash()));
            Report report = new Report(reportDir, program.getLanguage(), options);
            report.writeResult(result);
        } catch (ExitException exception) {
            System.out.println("Error: " + exception.getMessage());
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
        options.setMinimumTokenMatch(MIN_TOKEN_MATCH.getFrom(namespace));
        options.setSimilarityThreshold(SIMILARITY_THRESHOLD.getFrom(namespace));
        options.setMaximumNumberOfComparisons(SHOWN_COMPARISONS.getFrom(namespace));
        ComparisonMode.fromName(COMPARISON_MODE.getFrom(namespace)).ifPresentOrElse(it -> options.setComparisonMode(it),
                () -> System.out.println("Unknown comparison mode, using default mode!"));
        return options;
    }

    private String generateDescription() {
        var randomDescription = DESCRIPTIONS[new Random().nextInt(DESCRIPTIONS.length)];
        return String.format("JPlag - %s" + System.lineSeparator() + CREDITS, randomDescription);
    }
}
