package de.jplag.cli.test;

/**
 * Contains the cli arguments used for the tests. They intentionally duplicate the argument in
 * {@link de.jplag.cli.options.CliOptions}, so the tests will be sensitive to changes to those arguments.
 * @param name
 * @param isPositional
 * @param <T>
 */
public record CliArgument<T>(String name, boolean isPositional) {
    public static CliArgument<String[]> SUBMISSION_DIRECTORIES = new CliArgument<>("", true);
    public static CliArgument<String[]> NEW_SUBMISSION_DIRECTORIES = new CliArgument<>("new", false);
    public static CliArgument<String[]> OLD_SUBMISSION_DIRECTORIES = new CliArgument<>("old", false);

    public static CliArgument<String[]> SUFFIXES = new CliArgument<>("suffixes", false);
    public static CliArgument<Double> SIMILARITY_THRESHOLD = new CliArgument<>("m", false);
    public static CliArgument<Integer> MIN_TOKEN_MATCH = new CliArgument<>("t", false);
    public static CliArgument<Integer> SHOWN_COMPARISONS = new CliArgument<>("n", false);

    public static CliArgument<String> BASE_CODE = new CliArgument<>("base-code", false);

    public static CliArgument<Boolean> SKIP_CLUSTERING = new CliArgument<>("cluster-skip", false);
    public static CliArgument<Double> CLUSTER_PP_PERCENTILE = new CliArgument<>("cluster-pp-percentile", false);
    public static CliArgument<Boolean> CLUSTER_PP_CDF = new CliArgument<>("cluster-pp-cdf", false);
    public static CliArgument<Boolean> CLUSTER_PP_NONE = new CliArgument<>("cluster-pp-none", false);

    public static CliArgument<String> LANGUAGE = new CliArgument<>("l", false);

    public static CliArgument<String> RESULT_FILE = new CliArgument<>("r", false);
    public static CliArgument<Boolean> OVERWRITE_RESULT_FILE = new CliArgument<>("overwrite", false);

    public static CliArgument<String> LOG_LEVEL = new CliArgument<>("log-level", false);
    public static CliArgument<Boolean> DEBUG = new CliArgument<>("d", false);

    public static CliArgument<String> SUBDIRECTORY = new CliArgument<>("subdirectory", false);
    public static CliArgument<String> EXCLUDE_FILES = new CliArgument<>("x", false);

    public static CliArgument<String> MODE = new CliArgument<>("mode", false);

    public static CliArgument<Boolean> MERGING_ENABLED = new CliArgument<>("match-merging", false);
    public static CliArgument<Integer> NEIGHBOR_LENGTH = new CliArgument<>("neighbor-length", false);
    public static CliArgument<Integer> GAP_SIZE = new CliArgument<>("gap-size", false);
    public static CliArgument<Integer> REQUIRED_MERGES = new CliArgument<>("required-merges", false);
}
