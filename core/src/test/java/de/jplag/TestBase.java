package de.jplag;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assumptions;
import org.opentest4j.TestAbortedException;

import de.jplag.clustering.ClusteringOptions;
import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;

/**
 * Shared base class for all core test cases. Provides functionality regarding executing JPlag and checking the results.
 */
public abstract class TestBase {

    /**
     * The base path where the test samples reside.
     */
    protected static final String BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "samples").toString();

    /**
     * Delta for similarity comparison.
     */
    protected static final double DELTA = 0.001;

    /**
     * @param subdirectories list of directories that form a path relative to the base path.
     * @return the base path where the test samples reside concatenated with any number of subdirectories.
     */
    protected String getBasePath(String... subdirectories) {
        StringJoiner path = new StringJoiner(File.separator);
        path.add(BASE_PATH);
        for (String directory : subdirectories) {
            path.add(directory);
        }
        return path.toString();
    }

    protected JPlagResult runJPlagWithExclusionFile(String testSampleName, String exclusionFileName) throws ExitException {
        return JPlag.run(getOptionsWithExclusionFile(testSampleName, exclusionFileName));

    }

    /**
     * Runs JPlag with default options for a given test sample and returns the result.
     * @param testSampleName is the name of the test sample directory in the resources.
     * @return the result of the JPlag run.
     * @throws ExitException if JPlag fails.
     */
    protected JPlagResult runJPlagWithDefaultOptions(String testSampleName) throws ExitException {
        return JPlag.run(getDefaultOptions(testSampleName));
    }

    /**
     * Runs JPlag with customized options and returns the result.
     * @param testSampleName is the name of the test sample directory in the resources.
     * @param customization is a function that configures and returns the JPlagOptions for the run.
     * @return the result of the JPlag run.
     * @throws ExitException if JPlag fails.
     */
    protected JPlagResult runJPlag(String testSampleName, Function<JPlagOptions, JPlagOptions> customization) throws ExitException {
        return JPlag.run(getOptions(testSampleName, customization));
    }

    /**
     * Runs JPlag with multiple root folders and customized options and returns the result.
     * @param newPaths are the root folders.
     * @param customization is a function that configures and returns the JPlagOptions for the run.
     * @return the result of the JPlag run.
     * @throws ExitException if JPlag fails.
     */
    protected JPlagResult runJPlag(List<String> newPaths, Function<JPlagOptions, JPlagOptions> customization) throws ExitException {
        return JPlag.run(getOptions(newPaths, customization));
    }

    /**
     * Runs JPlag with multiple root folders (old and new) and customized options and returns the result.
     * @param newPaths are the new root folders.
     * @param oldPaths are the old root folders (not checked internally).
     * @param customization is a function that configures and returns the JPlagOptions for the run.
     * @return the result of the JPlag run.
     * @throws ExitException if JPlag fails.
     */
    protected JPlagResult runJPlag(List<String> newPaths, List<String> oldPaths, Function<JPlagOptions, JPlagOptions> customization)
            throws ExitException {
        return JPlag.run(getOptions(newPaths, oldPaths, customization));
    }

    protected JPlagOptions getOptionsWithExclusionFile(String testSampleName, String exclusionFileName) {
        String blackList = Path.of(BASE_PATH, testSampleName, exclusionFileName).toString();
        return getOptions(testSampleName, options -> options.withExclusionFileName(blackList));
    }

    /**
     * Get default options.
     * @param testSampleName is the name of the test sample directory in the resources.
     * @return the options.
     */
    protected JPlagOptions getDefaultOptions(String testSampleName) {
        return getOptions(List.of(getBasePath(testSampleName)), List.of(), options -> options);
    }

    /**
     * Get custom options.
     * @param testSampleName is the name of the test sample directory in the resources.
     * @param customization is a function that configures and returns the JPlagOptions for the run.
     * @return the options.
     */
    protected JPlagOptions getOptions(String testSampleName, Function<JPlagOptions, JPlagOptions> customization) {
        return getOptions(List.of(getBasePath(testSampleName)), List.of(), customization);
    }

    /**
     * Get customized options for JPlag run with multiple root folders.
     * @param newPaths are the new folders.
     * @param customization is a function that configures and returns the JPlagOptions for the run.
     * @return the options.
     */
    protected JPlagOptions getOptions(List<String> newPaths, Function<JPlagOptions, JPlagOptions> customization) {
        return getOptions(newPaths, List.of(), customization);
    }

    /**
     * Get customized options for JPlag run with multiple root folders (old and new).
     * @param newPaths are the new root folders.
     * @param oldPaths are the old root folders (not checked internally).
     * @param customization is a function that configures and returns the JPlagOptions for the run.
     * @return the options.
     */
    protected JPlagOptions getOptions(List<String> newPaths, List<String> oldPaths, Function<JPlagOptions, JPlagOptions> customization) {
        var newFiles = newPaths.stream().map(File::new).collect(Collectors.toSet());
        var oldFiles = oldPaths.stream().map(File::new).collect(Collectors.toSet());
        JPlagOptions options = new JPlagOptions(new JavaLanguage(), newFiles, oldFiles)
                .withClusteringOptions(new ClusteringOptions().withEnabled(false));
        return customization.apply(options);
    }

    /**
     * Retrieves the similarity of a specific comparison from a result object.
     * @param result is the result object.
     * @param nameA is the name of the first submission of the comparison.
     * @param nameB is the name of the second submission of the comparison.
     * @return the comparison optionally, if it could be retrieved.
     */
    protected static double getSelectedPercent(JPlagResult result, String nameA, String nameB) {
        return getSelectedComparison(result, nameA, nameB).map(JPlagComparison::similarity).orElse(-1.0);
    }

    /**
     * Retrieves a specific comparison from a result object.
     * @param result is the result object.
     * @param nameA is the name of the first submission of the comparison.
     * @param nameB is the name of the second submission of the comparison.
     * @return the comparison optionally, if it could be retrieved.
     */
    protected static Optional<JPlagComparison> getSelectedComparison(JPlagResult result, String nameA, String nameB) {
        return result.getAllComparisons().stream()
                .filter(comparison -> comparison.firstSubmission().getName().equals(nameA) && comparison.secondSubmission().getName().equals(nameB)
                        || comparison.firstSubmission().getName().equals(nameB) && comparison.secondSubmission().getName().equals(nameA))
                .findFirst();
    }

    /**
     * Deletes a directory with all its file, all its subdirectories and their files.
     * @param directory is the directory to delete.
     */
    protected static void deleteDirectory(File directory) {
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    /**
     * Validate a given assumption for object equality based on {@link Assumptions#assumeTrue(boolean)}.
     * @param expected the expected value.
     * @param actual the actual value.
     * @throws TestAbortedException if the assumption is not {@code true}.
     */
    protected static void assumeEquals(Object expected, Object actual) {
        assumeTrue(expected.equals(actual), "Expected: " + expected + ", Actual: " + actual);
    }
}
