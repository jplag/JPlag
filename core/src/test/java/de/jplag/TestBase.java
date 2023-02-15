package de.jplag;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.jplag.exceptions.ExitException;
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
        String blackList = Path.of(BASE_PATH, testSampleName, exclusionFileName).toString();
        return runJPlag(testSampleName, options -> options.withExclusionFileName(blackList));
    }

    /**
     * Runs JPlag with default options for a given test sample and returns the result.
     */
    protected JPlagResult runJPlagWithDefaultOptions(String testSampleName) throws ExitException {
        return runJPlag(testSampleName, options -> options);
    }

    /**
     * Runs JPlag with customized options and returns the result.
     */
    protected JPlagResult runJPlag(String testSampleName, Function<JPlagOptions, JPlagOptions> customization) throws ExitException {
        return runJPlag(List.of(getBasePath(testSampleName)), List.of(), customization);
    }

    /**
     * Runs JPlag with multiple root folders and customized options and returns the result.
     */
    protected JPlagResult runJPlag(List<String> newPaths, Function<JPlagOptions, JPlagOptions> customization) throws ExitException {
        return runJPlag(newPaths, List.of(), customization);
    }

    /**
     * Runs JPlag with multiple root folders (old and new) and customized options and returns the result.
     */
    protected JPlagResult runJPlag(List<String> newPaths, List<String> oldPaths, Function<JPlagOptions, JPlagOptions> customization)
            throws ExitException {
        var newFiles = newPaths.stream().map(path -> new File(path)).collect(Collectors.toSet());
        var oldFiles = oldPaths.stream().map(path -> new File(path)).collect(Collectors.toSet());
        JPlagOptions options = new JPlagOptions(new de.jplag.java.Language(), newFiles, oldFiles);
        options = customization.apply(options);
        JPlag jplag = new JPlag(options);
        return jplag.run();
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
}
