package de.jplag.cli.test;

public record CliArg<T>(String name, boolean isPositional) {
    public static CliArg<String[]> SUBMISSION_DIRECTORIES = new CliArg<>("", true);
    public static CliArg<String[]> NEW_SUBMISSION_DIRECTORIES = new CliArg<>("new", false);
    public static CliArg<String[]> OLD_SUBMISSION_DIRECTORIES = new CliArg<>("old", false);

    public static CliArg<String[]> SUFFIXES = new CliArg<>("suffixes", false);
    public static CliArg<Double> SIMILARITY_THRESHOLD = new CliArg<>("m", false);
    public static CliArg<Integer> MIN_TOKEN_MATCH = new CliArg<>("t", false);
    public static CliArg<Integer> SHOWN_COMPARISONS = new CliArg<>("n", false);

    public static CliArg<String> BASE_CODE = new CliArg<>("base-code", false);

    public static CliArg<Boolean> SKIP_CLUSTERING = new CliArg<>("cluster-skip", false);
    public static CliArg<Double> CLUSTER_PP_PERCENTILE = new CliArg<>("cluster-pp-percentile", false);
    public static CliArg<Boolean> CLUSTER_PP_CDF = new CliArg<>("cluster-pp-cdf", false);
    public static CliArg<Boolean> CLUSTER_PP_NONE = new CliArg<>("cluster-pp-none", false);

    public static CliArg<String> LANGUAGE = new CliArg<>("l", false);
}
