package de.jplag.cli.test;

/**
 * Contains the cli arguments used for the tests. They intentionally duplicate the argument in
 * {@link de.jplag.cli.options.CliOptions}, so the tests will be sensitive to changes to those arguments.
 * @param name the argument name
 * @param isPositional whether the argument is positional
 * @param <T> the argument value type
 */
public record CliArgument<T>(String name, boolean isPositional) {
    /** Submission directories (positional argument). */
    public static CliArgument<String[]> SUBMISSION_DIRECTORIES = new CliArgument<>("", true);

    /** New submission directories. */
    public static CliArgument<String[]> NEW_SUBMISSION_DIRECTORIES = new CliArgument<>("new", false);

    /** Old submission directories. */
    public static CliArgument<String[]> OLD_SUBMISSION_DIRECTORIES = new CliArgument<>("old", false);

    /** File suffixes to include. */
    public static CliArgument<String[]> SUFFIXES = new CliArgument<>("suffixes", false);

    /** Similarity threshold (m). */
    public static CliArgument<Double> SIMILARITY_THRESHOLD = new CliArgument<>("m", false);

    /** Minimum token match count (t). */
    public static CliArgument<Integer> MIN_TOKEN_MATCH = new CliArgument<>("t", false);

    /** Number of shown comparisons (n). */
    public static CliArgument<Integer> SHOWN_COMPARISONS = new CliArgument<>("n", false);

    /** Base code directory. */
    public static CliArgument<String> BASE_CODE = new CliArgument<>("base-code", false);

    /** Skip clustering flag. */
    public static CliArgument<Boolean> SKIP_CLUSTERING = new CliArgument<>("cluster-skip", false);

    /** Cluster post-processing percentile. */
    public static CliArgument<Double> CLUSTER_PP_PERCENTILE = new CliArgument<>("cluster-pp-percentile", false);

    /** Use CDF for cluster post-processing. */
    public static CliArgument<Boolean> CLUSTER_PP_CDF = new CliArgument<>("cluster-pp-cdf", false);

    /** Disable cluster post-processing. */
    public static CliArgument<Boolean> CLUSTER_PP_NONE = new CliArgument<>("cluster-pp-none", false);

    /** Programming language identifier (l). */
    public static CliArgument<String> LANGUAGE = new CliArgument<>("l", false);

    /** Result file path (r). */
    public static CliArgument<String> RESULT_FILE = new CliArgument<>("r", false);

    /** Overwrite result file flag. */
    public static CliArgument<Boolean> OVERWRITE_RESULT_FILE = new CliArgument<>("overwrite", false);

    /** Log level setting. */
    public static CliArgument<String> LOG_LEVEL = new CliArgument<>("log-level", false);

    /** Debug mode flag (d). */
    public static CliArgument<Boolean> DEBUG = new CliArgument<>("d", false);

    /** Subdirectory to include. */
    public static CliArgument<String> SUBDIRECTORY = new CliArgument<>("subdirectory", false);

    /** Files to exclude (x). */
    public static CliArgument<String> EXCLUDE_FILES = new CliArgument<>("x", false);

    /** Mode setting. */
    public static CliArgument<String> MODE = new CliArgument<>("mode", false);

    /** Match merging enabled flag. */
    public static CliArgument<Boolean> MERGING_ENABLED = new CliArgument<>("match-merging", false);

    /** Neighbor length for merging. */
    public static CliArgument<Integer> NEIGHBOR_LENGTH = new CliArgument<>("neighbor-length", false);

    /** Gap size for merging. */
    public static CliArgument<Integer> GAP_SIZE = new CliArgument<>("gap-size", false);

    /** Required number of merges. */
    public static CliArgument<Integer> REQUIRED_MERGES = new CliArgument<>("required-merges", false);

}
