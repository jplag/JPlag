package de.jplag.cli;

import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_FOOTER;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_OPTION_LIST;

import java.io.File;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.cli.logger.CollectedLoggerFactory;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.clustering.Preprocessing;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.reporting.reportobject.ReportObjectFactory;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.ParseResult;

/**
 * Command line interface class, allows using via command line.
 *
 * @see CLI#main(String[])
 */
public final class CLI {

    private static final Logger logger = LoggerFactory.getLogger(CLI.class);

    private static final Random RANDOM = new SecureRandom();

    private static final String CREDITS = "Created by IPD Tichy, Guido Malpohl, and others. JPlag logo designed by Sandro Koch. Currently maintained by Sebastian Hahner and Timur Saglam.";

    private static final String[] DESCRIPTIONS = {"Detecting Software Plagiarism", "Software-Archaeological Playground", "Since 1996", "Scientifically Published", "Maintained by SDQ", "RIP Structure and Table", "What else?", "You have been warned!", "Since Java 1.0", "More Abstract than Tree", "Students Nightmare", "No, changing variable names does not work", "The tech is out there!", "Developed by plagiarism experts."};

    private final CommandLine commandLine;
    private final CliOptions options;

    private static final String IMPOSSIBLE_EXCEPTION = "This should not have happened." + " Please create an issue on github (https://github.com/jplag/JPlag/issues) with the entire output.";

    /**
     * Main class for using JPlag via the CLI.
     *
     * @param args are the CLI arguments that will be passed to JPlag.
     */
    public static void main(String[] args) {
        try {
            logger.debug("Your version of JPlag is {}", JPlag.JPLAG_VERSION);

            CLI cli = new CLI();

            ParseResult parseResult = cli.parseOptions(args);

            if (!parseResult.isUsageHelpRequested() && !(parseResult.subcommand() != null && parseResult.subcommand().isUsageHelpRequested())) {
                JPlagOptions options = cli.buildOptionsFromArguments(parseResult);
                JPlagResult result = JPlag.run(options);
                ReportObjectFactory reportObjectFactory = new ReportObjectFactory();
                reportObjectFactory.createAndSaveReport(result, cli.getResultFolder());
            }
        } catch (ExitException exception) {
            logger.error(exception.getMessage()); // do not pass exception here to keep log clean
            finalizeLogger();
            System.exit(1);
        }
    }

    /**
     * Creates a new instance
     */
    public CLI() {
        this.options = new CliOptions();
        this.commandLine = new CommandLine(options);

        this.commandLine.getHelpSectionMap().put(SECTION_KEY_OPTION_LIST, help -> help.optionList().lines().map(it -> {
            if (it.startsWith("  -")) {
                return "    " + it;
            } else {
                return it;
            }
        }).collect(Collectors.joining(System.lineSeparator())));

        buildSubcommands().forEach(commandLine::addSubcommand);

        this.commandLine.getHelpSectionMap().put(SECTION_KEY_FOOTER, help -> generateDescription());
        this.commandLine.setAllowSubcommandsAsOptionParameters(true);
    }

    private List<CommandSpec> buildSubcommands() {
        return LanguageLoader.getAllAvailableLanguages().values().stream().map(language -> {
            CommandSpec command = CommandSpec.create().name(language.getIdentifier());

            for (LanguageOption<?> option : language.getOptions().getOptionsAsList()) {
                command.addOption(OptionSpec.builder(option.getNameAsUnixParameter()).type(option.getType().getJavaType())
                        .description(option.getDescription()).build());
            }
            command.mixinStandardHelpOptions(true);
            command.addPositional(
                    CommandLine.Model.PositionalParamSpec.builder().type(List.class).auxiliaryTypes(File.class).hidden(true).required(false).build());

            return command;
        }).toList();
    }

    /**
     * Parses the options from the given command line arguments. Also prints help pages when requested.
     *
     * @param args The command line arguments
     * @return the parse result generated by picocli
     */
    public ParseResult parseOptions(String... args) throws CliException {
        try {
            ParseResult result = commandLine.parseArgs(args);
            if (result.isUsageHelpRequested() || (result.subcommand() != null && result.subcommand().isUsageHelpRequested())) {
                commandLine.getExecutionStrategy().execute(result);
            }
            return result;
        } catch (CommandLine.PicocliException e) {
            throw new CliException("Error during parsing", e);
        }
    }

    private static void finalizeLogger() {
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        if (!(factory instanceof CollectedLoggerFactory collectedLoggerFactory)) {
            return;
        }
        collectedLoggerFactory.finalizeInstances();
    }

    /**
     * Builds an options instance from parsed options.
     *
     * @return the newly built options
     */
    public JPlagOptions buildOptionsFromArguments(ParseResult parseResult) throws CliException {
        Set<File> submissionDirectories = new HashSet<>(List.of(this.options.rootDirectory));
        Set<File> oldSubmissionDirectories = Set.of(this.options.oldDirectories);
        List<String> suffixes = List.of(this.options.advanced.suffixes);
        submissionDirectories.addAll(List.of(this.options.newDirectories));

        if (parseResult.subcommand() != null && parseResult.subcommand().hasMatchedPositional(0)) {
            submissionDirectories.addAll(parseResult.subcommand().matchedPositional(0).getValue());
        }

        ClusteringOptions clusteringOptions = getClusteringOptions(this.options);

        JPlagOptions jPlagOptions = new JPlagOptions(loadLanguage(parseResult), this.options.minTokenMatch, submissionDirectories, oldSubmissionDirectories, null, this.options.advanced.subdirectory, suffixes, this.options.advanced.exclusionFileName, JPlagOptions.DEFAULT_SIMILARITY_METRIC, this.options.advanced.similarityThreshold, this.options.shownComparisons, clusteringOptions, this.options.advanced.debug);

        String baseCodePath = this.options.baseCode;
        File baseCodeDirectory = baseCodePath == null ? null : new File(baseCodePath);
        if (baseCodeDirectory == null || baseCodeDirectory.exists()) {
            return jPlagOptions.withBaseCodeSubmissionDirectory(baseCodeDirectory);
        } else {
            logger.warn("Using legacy partial base code API. Please migrate to new full path base code API.");
            return jPlagOptions.withBaseCodeSubmissionName(baseCodePath);
        }
    }

    private Language loadLanguage(ParseResult result) throws CliException {
        if (result.subcommand() != null) {
            ParseResult subcommandResult = result.subcommand();
            Language language = LanguageLoader.getLanguage(subcommandResult.commandSpec().name())
                    .orElseThrow(() -> new CliException(IMPOSSIBLE_EXCEPTION));
            LanguageOptions languageOptions = language.getOptions();
            languageOptions.getOptionsAsList().forEach(option -> {
                if (subcommandResult.hasMatchedOption(option.getNameAsUnixParameter())) {
                    option.setValue(subcommandResult.matchedOptionValue(option.getNameAsUnixParameter(), null));
                }
            });
            return language;
        } else {
            return this.options.language;
        }
    }

    private static ClusteringOptions getClusteringOptions(CliOptions options) {
        ClusteringOptions clusteringOptions = new ClusteringOptions().withEnabled(!options.clustering.disable).withAlgorithm(options.clustering.enabled.algorithm).withSimilarityMetric(options.clustering.enabled.metric).withSpectralKernelBandwidth(options.clusterSpectralBandwidth).withSpectralGaussianProcessVariance(options.clusterSpectralNoise).withSpectralMinRuns(options.clusterSpectralMinRuns).withSpectralMaxRuns(options.clusterSpectralMaxRuns).withSpectralMaxKMeansIterationPerRun(options.clusterSpectralKMeansIterations).withAgglomerativeThreshold(options.clusterAgglomerativeThreshold).withAgglomerativeInterClusterSimilarity(options.clusterAgglomerativeInterClusterSimilarity);

        if (options.clusterPreprocessingNone) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.NONE);
        }

        if (options.clusterPreprocessingCdf) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION);
        }

        if (options.clusterPreprocessingPercentile != 0) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.PERCENTILE).withPreprocessorPercentile(options.clusterPreprocessingPercentile);
        }

        if (options.clusterPreprocessingThreshold != 0) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.THRESHOLD).withPreprocessorThreshold(options.clusterPreprocessingThreshold);
        }

        return clusteringOptions;
    }

    private String generateDescription() {
        var randomDescription = DESCRIPTIONS[RANDOM.nextInt(DESCRIPTIONS.length)];
        return String.format("JPlag - %s%n%n%s", randomDescription, CREDITS);
    }

    public String getResultFolder() {
        return this.options.resultFolder;
    }
}
