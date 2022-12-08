package de.jplag.cli;

import static de.jplag.cli.CommandLineArgument.BASE_CODE;
import static de.jplag.cli.CommandLineArgument.CLUSTER_AGGLOMERATIVE_INTER_CLUSTER_SIMILARITY;
import static de.jplag.cli.CommandLineArgument.CLUSTER_AGGLOMERATIVE_THRESHOLD;
import static de.jplag.cli.CommandLineArgument.CLUSTER_ALGORITHM;
import static de.jplag.cli.CommandLineArgument.CLUSTER_DISABLE;
import static de.jplag.cli.CommandLineArgument.CLUSTER_METRIC;
import static de.jplag.cli.CommandLineArgument.CLUSTER_PREPROCESSING_CDF;
import static de.jplag.cli.CommandLineArgument.CLUSTER_PREPROCESSING_NONE;
import static de.jplag.cli.CommandLineArgument.CLUSTER_PREPROCESSING_PERCENTILE;
import static de.jplag.cli.CommandLineArgument.CLUSTER_PREPROCESSING_THRESHOLD;
import static de.jplag.cli.CommandLineArgument.CLUSTER_SPECTRAL_BANDWIDTH;
import static de.jplag.cli.CommandLineArgument.CLUSTER_SPECTRAL_KMEANS_ITERATIONS;
import static de.jplag.cli.CommandLineArgument.CLUSTER_SPECTRAL_MAX_RUNS;
import static de.jplag.cli.CommandLineArgument.CLUSTER_SPECTRAL_MIN_RUNS;
import static de.jplag.cli.CommandLineArgument.CLUSTER_SPECTRAL_NOISE;
import static de.jplag.cli.CommandLineArgument.DEBUG;
import static de.jplag.cli.CommandLineArgument.EXCLUDE_FILE;
import static de.jplag.cli.CommandLineArgument.LANGUAGE;
import static de.jplag.cli.CommandLineArgument.MIN_TOKEN_MATCH;
import static de.jplag.cli.CommandLineArgument.NEW_DIRECTORY;
import static de.jplag.cli.CommandLineArgument.OLD_DIRECTORY;
import static de.jplag.cli.CommandLineArgument.RESULT_FOLDER;
import static de.jplag.cli.CommandLineArgument.ROOT_DIRECTORY;
import static de.jplag.cli.CommandLineArgument.SHOWN_COMPARISONS;
import static de.jplag.cli.CommandLineArgument.SIMILARITY_THRESHOLD;
import static de.jplag.cli.CommandLineArgument.SUBDIRECTORY;
import static de.jplag.cli.CommandLineArgument.SUFFIXES;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.cli.logger.CollectedLoggerFactory;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.clustering.Preprocessing;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.reportobject.ReportObjectFactory;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command line interface class, allows using via command line.
 * @see CLI#main(String[])
 */
public final class CLI {

    private static final Logger logger = LoggerFactory.getLogger(CLI.class);

    private static final Random RANDOM = new SecureRandom();

    private static final String CREDITS = "Created by IPD Tichy, Guido Malpohl, and others. JPlag logo designed by Sandro Koch. Currently maintained by Sebastian Hahner and Timur Saglam.";

    private static final String[] DESCRIPTIONS = {"Detecting Software Plagiarism", "Software-Archaeological Playground", "Since 1996",
            "Scientifically Published", "Maintained by SDQ", "RIP Structure and Table", "What else?", "You have been warned!", "Since Java 1.0",
            "More Abstract than Tree", "Students Nightmare", "No, changing variable names does not work", "The tech is out there!"};

    private static final String PROGRAM_NAME = "jplag";
    static final String CLUSTERING_GROUP_NAME = "Clustering";
    static final String ADVANCED_GROUP = "Advanced";

    private final ArgumentParser parser;

    /**
     * Main class for using JPlag via the CLI.
     * @param args are the CLI arguments that will be passed to JPlag.
     */
    public static void main(String[] args) {
        try {
            logger.debug("Your version of JPlag is {}", JPlag.JPLAG_VERSION);
            CLI cli = new CLI();
            Namespace arguments = cli.parseArguments(args);
            JPlagOptions options = cli.buildOptionsFromArguments(arguments);
            JPlag jplag = new JPlag(options);
            logger.debug("JPlag initialized");
            JPlagResult result = jplag.run();
            ReportObjectFactory reportObjectFactory = new ReportObjectFactory();
            reportObjectFactory.createAndSaveReport(result, arguments.getString(RESULT_FOLDER.flagWithoutDash()));
        } catch (ExitException exception) {
            logger.error(exception.getMessage()); // do not pass exception here to keep log clean
            finalizeLogger();
            System.exit(1);
        }
    }

    private static void finalizeLogger() {
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        if (!(factory instanceof CollectedLoggerFactory collectedLoggerFactory))
            return;
        collectedLoggerFactory.finalizeInstances();
    }

    /**
     * Creates the command line interface and initializes the argument parser.
     */
    public CLI() {
        parser = ArgumentParsers.newFor(PROGRAM_NAME).build().defaultHelp(true).description(generateDescription());
        CliGroupHelper groupHelper = new CliGroupHelper(parser);
        for (CommandLineArgument argument : CommandLineArgument.values()) {
            argument.parseWith(parser, groupHelper);
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

        // Collect the root directories.
        List<String> submissionDirectoryPaths = new ArrayList<>();
        List<String> oldSubmissionDirectoryPaths = new ArrayList<>();
        addAllMultiValueArgument(ROOT_DIRECTORY.getListFrom(namespace), submissionDirectoryPaths);
        addAllMultiValueArgument(NEW_DIRECTORY.getListFrom(namespace), submissionDirectoryPaths);
        addAllMultiValueArgument(OLD_DIRECTORY.getListFrom(namespace), oldSubmissionDirectoryPaths);
        var submissionDirectories = submissionDirectoryPaths.stream().map(File::new).collect(Collectors.toSet());
        var oldSubmissionDirectories = oldSubmissionDirectoryPaths.stream().map(File::new).collect(Collectors.toSet());

        var language = LanguageLoader.getLanguage(LANGUAGE.getFrom(namespace)).orElseThrow();
        ClusteringOptions clusteringOptions = getClusteringOptions(namespace);

        JPlagOptions options = new JPlagOptions(language, MIN_TOKEN_MATCH.getFrom(namespace), submissionDirectories, oldSubmissionDirectories, null,
                SUBDIRECTORY.getFrom(namespace), Arrays.stream(fileSuffixes).toList(), EXCLUDE_FILE.getFrom(namespace),
                JPlagOptions.DEFAULT_SIMILARITY_METRIC, SIMILARITY_THRESHOLD.getFrom(namespace), SHOWN_COMPARISONS.getFrom(namespace),
                clusteringOptions, DEBUG.getFrom(namespace));

        String baseCodePath = BASE_CODE.getFrom(namespace);
        File baseCodeDirectory = baseCodePath == null ? null : new File(baseCodePath);
        if (baseCodeDirectory == null || baseCodeDirectory.exists()) {
            return options.withBaseCodeSubmissionDirectory(baseCodeDirectory);
        } else {
            logger.warn("Using legacy partial base code API. Please migrate to new full path base code API.");
            return options.withBaseCodeSubmissionName(baseCodePath);
        }
    }

    private static ClusteringOptions getClusteringOptions(Namespace namespace) {
        ClusteringOptions clusteringOptions = new ClusteringOptions();
        if (CLUSTER_DISABLE.isSet(namespace)) {
            boolean disabled = CLUSTER_DISABLE.getFrom(namespace);
            clusteringOptions = clusteringOptions.withEnabled(!disabled);
        }
        if (CLUSTER_ALGORITHM.isSet(namespace)) {
            clusteringOptions = clusteringOptions.withAlgorithm(CLUSTER_ALGORITHM.getFrom(namespace));
        }
        if (CLUSTER_METRIC.isSet(namespace)) {
            clusteringOptions = clusteringOptions.withSimilarityMetric(CLUSTER_METRIC.getFrom(namespace));
        }
        if (CLUSTER_SPECTRAL_BANDWIDTH.isSet(namespace)) {
            clusteringOptions = clusteringOptions.withSpectralKernelBandwidth(CLUSTER_SPECTRAL_BANDWIDTH.getFrom(namespace));
        }
        if (CLUSTER_SPECTRAL_NOISE.isSet(namespace)) {
            clusteringOptions = clusteringOptions.withSpectralGaussianProcessVariance(CLUSTER_SPECTRAL_NOISE.getFrom(namespace));
        }
        if (CLUSTER_SPECTRAL_MIN_RUNS.isSet(namespace)) {
            clusteringOptions = clusteringOptions.withSpectralMinRuns(CLUSTER_SPECTRAL_MIN_RUNS.getFrom(namespace));
        }
        if (CLUSTER_SPECTRAL_MAX_RUNS.isSet(namespace)) {
            clusteringOptions = clusteringOptions.withSpectralMaxRuns(CLUSTER_SPECTRAL_MAX_RUNS.getFrom(namespace));
        }
        if (CLUSTER_SPECTRAL_KMEANS_ITERATIONS.isSet(namespace)) {
            clusteringOptions = clusteringOptions.withSpectralMaxKMeansIterationPerRun(CLUSTER_SPECTRAL_KMEANS_ITERATIONS.getFrom(namespace));
        }
        if (CLUSTER_AGGLOMERATIVE_THRESHOLD.isSet(namespace)) {
            clusteringOptions = clusteringOptions.withAgglomerativeThreshold(CLUSTER_AGGLOMERATIVE_THRESHOLD.getFrom(namespace));
        }
        if (CLUSTER_AGGLOMERATIVE_INTER_CLUSTER_SIMILARITY.isSet(namespace)) {
            clusteringOptions = clusteringOptions
                    .withAgglomerativeInterClusterSimilarity(CLUSTER_AGGLOMERATIVE_INTER_CLUSTER_SIMILARITY.getFrom(namespace));
        }
        if (CLUSTER_PREPROCESSING_NONE.isSet(namespace) && Boolean.TRUE.equals(CLUSTER_PREPROCESSING_NONE.getFrom(namespace))) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.NONE);
        }
        if (CLUSTER_PREPROCESSING_CDF.isSet(namespace) && Boolean.TRUE.equals(CLUSTER_PREPROCESSING_CDF.getFrom(namespace))) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION);
        }
        if (CLUSTER_PREPROCESSING_PERCENTILE.isSet(namespace)) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.PERCENTILE)
                    .withPreprocessorPercentile(CLUSTER_PREPROCESSING_PERCENTILE.getFrom(namespace));
        }
        if (CLUSTER_PREPROCESSING_THRESHOLD.isSet(namespace)) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.THRESHOLD)
                    .withPreprocessorPercentile(CLUSTER_PREPROCESSING_THRESHOLD.getFrom(namespace));
        }
        return clusteringOptions;
    }

    private String generateDescription() {
        var randomDescription = DESCRIPTIONS[RANDOM.nextInt(DESCRIPTIONS.length)];
        return String.format("JPlag - %s%n%n%s", randomDescription, CREDITS);
    }

    private void addAllMultiValueArgument(List<List<String>> argumentValues, List<String> destinationRootDirectories) {
        if (argumentValues == null) {
            return;
        }
        argumentValues.forEach(destinationRootDirectories::addAll);
    }
}
