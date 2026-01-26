package de.jplag.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the argument string for tests.
 */
public class ArgumentBuilder {
    private final List<String> arguments;

    /**
     * New instance, prefer using {@link CommandLineInterfaceTest#arguments()} or
     * {@link CommandLineInterfaceTest#defaultArguments()}.
     */
    public ArgumentBuilder() {
        this.arguments = new ArrayList<>();
    }

    /**
     * Sets the root directory option.
     * @param directoryNames The names of the root directories.
     * @return self reference.
     */
    public ArgumentBuilder rootDirectory(String... directoryNames) {
        this.arguments.add(String.join(",", directoryNames));
        return this;
    }

    /**
     * Sets the new root directory option.
     * @param directoryNames The directory names.
     * @return self reference.
     */
    public ArgumentBuilder newRootDirectories(String... directoryNames) {
        this.arguments.add("--new=" + String.join(",", directoryNames));
        return this;
    }

    /**
     * Sets the old directory option.
     * @param directoryNames The directory names.
     * @return self reference.
     */
    public ArgumentBuilder oldRootDirectories(String... directoryNames) {
        this.arguments.add("--old");
        this.arguments.add(String.join(",", directoryNames));
        return this;
    }

    /**
     * Sets the base code option.
     * @param baseCode The base code directory.
     * @return self reference.
     */
    public ArgumentBuilder baseCode(String baseCode) {
        this.arguments.add("--base-code=" + baseCode);
        return this;
    }

    /**
     * Sets the skip clustering option.
     * @return self reference.
     */
    public ArgumentBuilder skipClustering() {
        this.arguments.add("--cluster-skip");
        return this;
    }

    /**
     * Sets the clustering preprocessor percentile option.
     * @param percentile The option value.
     * @return self reference.
     */
    public ArgumentBuilder clusterPpPercentile(double percentile) {
        this.arguments.add("--cluster-pp-percentile=" + percentile);
        return this;
    }

    /**
     * Sets the clustering preprocessor to cdf.
     * @return self reference.
     */
    public ArgumentBuilder clusterPpCdf() {
        this.arguments.add("--cluster-pp-cdf");
        return this;
    }

    /**
     * Sets the clustering preprocessor to none.
     * @return self reference.
     */
    public ArgumentBuilder clusterPpNone() {
        this.arguments.add("--cluster-pp-none");
        return this;
    }

    /**
     * Sets the language as an option.
     * @param languageName The identifier of the language.
     * @return self reference.
     */
    public ArgumentBuilder language(String languageName) {
        this.arguments.add("-l");
        this.arguments.add(languageName);
        return this;
    }

    /**
     * Sets the suffixes option.
     * @param suffixes The suffixes.
     * @return self reference.
     */
    public ArgumentBuilder suffixes(String... suffixes) {
        this.arguments.add("-p");
        this.arguments.add(String.join(",", suffixes));
        return this;
    }

    /**
     * Sets the min tokens option as a string, so invalid values can be configured.
     * @param value The option value.
     * @return self reference.
     */
    public ArgumentBuilder minTokens(String value) {
        this.arguments.add("--min-tokens");
        this.arguments.add(value);
        return this;
    }

    /**
     * Sets the min tokens option.
     * @param count The min token count.
     * @return self reference.
     */
    public ArgumentBuilder minTokens(int count) {
        return minTokens(String.valueOf(count));
    }

    /**
     * Sets the similarity threshold as a string, so invalid values can be configured.
     * @param value The value.
     * @return self reference.
     */
    public ArgumentBuilder similarityThreshold(String value) {
        this.arguments.add("-m");
        this.arguments.add(value);
        return this;
    }

    /**
     * Sets the similarity threshold.
     * @param value The threshold.
     * @return self reference.
     */
    public ArgumentBuilder similarityThreshold(double value) {
        return similarityThreshold(String.valueOf(value));
    }

    /**
     * Sets the shown comparisons option as a string, so invalid values can be configured.
     * @param value The value.
     * @return self reference.
     */
    public ArgumentBuilder shownComparisons(String value) {
        this.arguments.add("-n");
        this.arguments.add(value);
        return this;
    }

    /**
     * Sets the result file.
     * @param path The path to the result file.
     * @return self reference.
     */
    public ArgumentBuilder resultFile(String path) {
        this.arguments.add("-r");
        this.arguments.add(path);
        return this;
    }

    /**
     * Adds the overwrite argument.
     * @return self reference.
     */
    public ArgumentBuilder overwrite() {
        this.arguments.add("--overwrite");
        return this;
    }

    /**
     * Sets the shown comparisons option.
     * @param value The option value.
     * @return self reference.
     */
    public ArgumentBuilder shownComparisons(int value) {
        return shownComparisons(String.valueOf(value));
    }

    /**
     * @return The list of arguments as a string array.
     */
    public String[] getArgumentsAsArray() {
        return this.arguments.toArray(new String[0]);
    }
}
