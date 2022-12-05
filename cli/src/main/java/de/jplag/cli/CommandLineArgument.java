package de.jplag.cli;

import static de.jplag.cli.CLI.ADVANCED_GROUP;
import static de.jplag.cli.CLI.CLUSTERING_GROUP_NAME;
import static de.jplag.options.JPlagOptions.DEFAULT_SHOWN_COMPARISONS;
import static de.jplag.options.JPlagOptions.DEFAULT_SIMILARITY_THRESHOLD;
import static net.sourceforge.argparse4j.impl.Arguments.append;
import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.jplag.Language;
import de.jplag.Messages;
import de.jplag.NumberOfArgumentValues;
import de.jplag.clustering.ClusteringAlgorithm;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.clustering.algorithm.InterClusterSimilarity;
import de.jplag.options.SimilarityMetric;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentContainer;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.FeatureControl;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command line arguments for the JPlag CLI. Each argument is defined through an enumeral.
 * @author Timur Saglam
 */
public enum CommandLineArgument {
    ROOT_DIRECTORY(new Builder("rootDir", String.class).nargs(NumberOfArgumentValues.ZERO_OR_MORE_VALUES)),
    NEW_DIRECTORY(new Builder("-new", String.class).nargs(NumberOfArgumentValues.ONE_OR_MORE_VALUES)),
    OLD_DIRECTORY(new Builder("-old", String.class).nargs(NumberOfArgumentValues.ONE_OR_MORE_VALUES)),
    LANGUAGE(
            new Builder("-l", String.class).defaultsTo(new de.jplag.java.Language().getIdentifier())
                    .choices(LanguageLoader.getAllAvailableLanguageIdentifiers())),
    BASE_CODE("-bc", String.class),

    // real logging library
    DEBUG(new Builder("-d", Boolean.class).argumentGroup(ADVANCED_GROUP)),
    SUBDIRECTORY(new Builder("-s", String.class).argumentGroup(ADVANCED_GROUP)),
    SUFFIXES(new Builder("-p", String.class).argumentGroup(ADVANCED_GROUP)),
    EXCLUDE_FILE(new Builder("-x", String.class).argumentGroup(ADVANCED_GROUP)),
    MIN_TOKEN_MATCH("-t", Integer.class),
    SIMILARITY_THRESHOLD(new Builder("-m", Double.class).defaultsTo(DEFAULT_SIMILARITY_THRESHOLD).argumentGroup(ADVANCED_GROUP)),
    SHOWN_COMPARISONS(new Builder("-n", Integer.class).defaultsTo(DEFAULT_SHOWN_COMPARISONS)),
    RESULT_FOLDER(new Builder("-r", String.class).defaultsTo("result")),
    CLUSTER_DISABLE(new Builder("--cluster-skip", Boolean.class).argumentGroup(CLUSTERING_GROUP_NAME).action(Arguments.storeTrue())),
    CLUSTER_ALGORITHM(
            new Builder("--cluster-alg", ClusteringAlgorithm.class).argumentGroup(CLUSTERING_GROUP_NAME)
                    .defaultsTo(new ClusteringOptions().algorithm())),
    CLUSTER_METRIC(
            new Builder("--cluster-metric", SimilarityMetric.class).argumentGroup(CLUSTERING_GROUP_NAME)
                    .defaultsTo(new ClusteringOptions().similarityMetric())),
    CLUSTER_SPECTRAL_BANDWIDTH(
            new Builder("--cluster-spectral-bandwidth", Double.class).metaVar("bandwidth")
                    .defaultsTo(new ClusteringOptions().spectralKernelBandwidth()).hidden()),
    CLUSTER_SPECTRAL_NOISE(
            new Builder("--cluster-spectral-noise", Double.class).metaVar("noise")
                    .defaultsTo(new ClusteringOptions().spectralGaussianProcessVariance()).hidden()),
    CLUSTER_SPECTRAL_MIN_RUNS(
            new Builder("--cluster-spectral-min-runs", Integer.class).metaVar("min").defaultsTo(new ClusteringOptions().spectralMinRuns()).hidden()),
    CLUSTER_SPECTRAL_MAX_RUNS(
            new Builder("--cluster-spectral-max-runs", Integer.class).metaVar("max").defaultsTo(new ClusteringOptions().spectralMaxRuns()).hidden()),
    CLUSTER_SPECTRAL_KMEANS_ITERATIONS(
            new Builder("--cluster-spectral-kmeans-interations", Integer.class).metaVar("iterations")
                    .defaultsTo(new ClusteringOptions().spectralMaxKMeansIterationPerRun()).hidden()),
    CLUSTER_AGGLOMERATIVE_THRESHOLD(
            new Builder("--cluster-agglomerative-threshold", Double.class).metaVar("threshold")
                    .defaultsTo(new ClusteringOptions().agglomerativeThreshold()).hidden()),
    CLUSTER_AGGLOMERATIVE_INTER_CLUSTER_SIMILARITY(
            new Builder("--cluster-agglomerative-inter-cluster-similarity", InterClusterSimilarity.class)
                    .defaultsTo(new ClusteringOptions().agglomerativeInterClusterSimilarity()).hidden()),
    CLUSTER_PREPROCESSING_NONE(new Builder("--cluster-pp-none", Boolean.class).action(Arguments.storeTrue()).hidden()),
    CLUSTER_PREPROCESSING_CDF(new Builder("--cluster-pp-cdf", Boolean.class).action(Arguments.storeTrue()).hidden()),
    CLUSTER_PREPROCESSING_PERCENTILE(new Builder("--cluster-pp-percentile", Double.class).metaVar("percentile").hidden()),
    CLUSTER_PREPROCESSING_THRESHOLD(new Builder("--cluster-pp-threshold", Double.class).metaVar("threshold").hidden());

    /**
     * The identifier of the default {@link Language}.
     * @see Language#getIdentifier()
     */
    public static final String DEFAULT_LANGUAGE_IDENTIFIER = new de.jplag.java.Language().getIdentifier();

    private final String flag;
    private final NumberOfArgumentValues numberOfValues;
    private final String description;
    private final Optional<Object> defaultValue;
    private final Optional<Collection<String>> choices;
    private final Optional<String> argumentGroup;
    private final Optional<String> mutuallyExclusiveGroup;
    private final Optional<ArgumentAction> action;
    private final Optional<String> metaVar;
    private final Class<?> type;
    private final boolean hidden;

    CommandLineArgument(String flag, Class<?> type) {
        this(new Builder(flag, type));
    }

    CommandLineArgument(Builder builder) {
        this.flag = builder.flag;
        this.type = builder.type;
        this.defaultValue = builder.defaultValue;
        this.choices = builder.choices;
        this.argumentGroup = builder.argumentGroup;
        this.mutuallyExclusiveGroup = builder.mutuallyExclusiveGroup;
        this.action = builder.action;
        this.metaVar = builder.metaVar;
        this.numberOfValues = builder.nargs.orElse(NumberOfArgumentValues.SINGLE_VALUE);
        this.hidden = builder.hidden;
        this.description = retrieveDescriptionFromMessages();

    }

    /**
     * @return the flag name of the command line argument.
     */
    public String flag() {
        return flag;
    }

    /**
     * @return the flag name of the command line argument without leading dashes and inner dashes replaced with underscores.
     */
    public String flagWithoutDash() {
        return flag.replaceAll("^-+", "").replace("-", "_");
    }

    /**
     * Returns the value of this argument for arguments with a single value. Convenience method for
     * {@link Namespace#get(String)} and {@link CommandLineArgument#flagWithoutDash()}.
     * @param <T> is the argument type.
     * @param namespace stores a value for the argument.
     * @return the argument value.
     */
    public <T> T getFrom(Namespace namespace) {
        return namespace.get(flagWithoutDash());
    }

    /**
     * Returns whether the value of this argument is set to a value not equal to {@code null}.
     * @param namespace stores a value for the argument
     * @return the indicator
     */
    public boolean isSet(Namespace namespace) {
        return namespace.get(flagWithoutDash()) != null;
    }

    /**
     * Returns the value of this argument for arguments that allow more than a single value. Convenience method for
     * {@link Namespace#getList(String)} and {@link CommandLineArgument#flagWithoutDash()}.
     * <p>
     * Depending on the action of the option, result types may change.
     * </p>
     * @param <T> is the argument type.
     * @param namespace stores a value for the argument.
     * @return the argument value.
     */
    public <T> List<T> getListFrom(Namespace namespace) {
        return namespace.getList(flagWithoutDash());
    }

    /**
     * Parses the command line argument with a specific parser.
     * @param parser is that parser.
     */
    public void parseWith(ArgumentParser parser, CliGroupHelper groupHelper) {
        ArgumentContainer argContainer = mutuallyExclusiveGroup.map(groupHelper::getMutuallyExclusiveGroup)
                .or(() -> argumentGroup.map(groupHelper::getArgumentGroup)).orElse(parser);

        Argument argument = argContainer.addArgument(flag).help(description);
        choices.ifPresent(argument::choices);
        defaultValue.ifPresent(argument::setDefault);
        action.ifPresent(argument::action);
        metaVar.ifPresent(argument::metavar);
        argument.type(type);
        if (type == Boolean.class) {
            argument.action(storeTrue());
        }
        if (hidden) {
            argument.help(FeatureControl.SUPPRESS);
        }
        if (!numberOfValues.toString().isEmpty()) {
            // For multi-value arguments keep all invocations.
            // This causes the argument value to change its type to 'List<List<String>>'.
            // Also, when the retrieved value after parsing the CLI is 'null', the argument is not used.
            argument.nargs(numberOfValues.toString());
            argument.action(append());
        }
    }

    /**
     * Dynamically loads the description from the message file. For an option named <code>NEW_OPTION</code> the messages key
     * should be <code>CommandLineArgument.NewOption</code>.
     */
    private String retrieveDescriptionFromMessages() {
        StringBuilder builder = new StringBuilder();
        for (String substring : toString().toLowerCase().split("_")) {
            builder.append(substring.substring(0, 1).toUpperCase());
            builder.append(substring.substring(1));
        }
        return Messages.getString(getClass().getSimpleName() + "." + builder.toString());
    }

    private static class Builder {
        private final String flag;
        private final Class<?> type;
        private Optional<Object> defaultValue = Optional.empty();
        private Optional<Collection<String>> choices = Optional.empty();
        private Optional<String> argumentGroup = Optional.empty();
        private Optional<String> mutuallyExclusiveGroup = Optional.empty();
        private Optional<ArgumentAction> action = Optional.empty();
        private Optional<String> metaVar = Optional.empty();
        private Optional<NumberOfArgumentValues> nargs = Optional.empty();
        private boolean hidden;

        public Builder(String flag, Class<?> type) {
            this.flag = flag;
            this.type = type;
        }

        public Builder defaultsTo(Object defaultValue) {
            this.defaultValue = Optional.of(defaultValue);
            return this;
        }

        public Builder choices(String... choices) {
            this.choices = Optional.of(Arrays.asList(choices));
            return this;
        }

        public Builder choices(Collection<String> choices) {
            this.choices = Optional.of(choices);
            return this;
        }

        public Builder argumentGroup(String argumentGroup) {
            this.argumentGroup = Optional.of(argumentGroup);
            return this;
        }

        public Builder action(ArgumentAction action) {
            this.action = Optional.of(action);
            return this;
        }

        public Builder metaVar(String metaVar) {
            this.metaVar = Optional.of(metaVar);
            return this;
        }

        public Builder nargs(NumberOfArgumentValues nargs) {
            this.nargs = Optional.of(nargs);
            return this;
        }

        public Builder hidden() {
            this.hidden = true;
            return this;
        }
    }
}
