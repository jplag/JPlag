package de.jplag.optionsWithHelper;

import de.jplag.options.JPlagOptions;
import de.jplag.options.SubmissionFileOptions;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Utility class providing "with" helpers that return a copy of {@link JPlagOptions}
 * with a single field in {@link SubmissionFileOptions} replaced.
 */
public final class SubmissionFileOptionsWith {

    private SubmissionFileOptionsWith() {
        // utility class
    }

    /**
     * Returns a copy of the options with the given submission directories.
     * @param options the original options
     * @param submissionDirectories the new submission directories
     * @return the updated options
     */
    public static JPlagOptions submissionDirectories(JPlagOptions options, Set<Path> submissionDirectories) {
        SubmissionFileOptions s = options.submissionFileOptions();
        return options.withSubmissionFileOptions(new SubmissionFileOptions(
                submissionDirectories,
                s.oldSubmissionDirectories(),
                s.baseCodeSubmissionDirectory(),
                s.subdirectoryName(),
                s.exclusionFileName()));
    }

    /**
     * Returns a copy of the options with the given old submission directories.
     * @param options the original options
     * @param oldSubmissionDirectories the new old submission directories
     * @return the updated options
     */
    public static JPlagOptions oldSubmissionDirectories(JPlagOptions options, Set<Path> oldSubmissionDirectories) {
        SubmissionFileOptions s = options.submissionFileOptions();
        return options.withSubmissionFileOptions(new SubmissionFileOptions(
                s.submissionDirectories(),
                oldSubmissionDirectories,
                s.baseCodeSubmissionDirectory(),
                s.subdirectoryName(),
                s.exclusionFileName()));
    }

    /**
     * Returns a copy of the options with the given base code submission directory.
     * @param options the original options
     * @param baseCodeSubmissionDirectory the new base code directory
     * @return the updated options
     */
    public static JPlagOptions baseCodeSubmissionDirectory(JPlagOptions options, Path baseCodeSubmissionDirectory) {
        SubmissionFileOptions s = options.submissionFileOptions();
        return options.withSubmissionFileOptions(new SubmissionFileOptions(
                s.submissionDirectories(),
                s.oldSubmissionDirectories(),
                baseCodeSubmissionDirectory,
                s.subdirectoryName(),
                s.exclusionFileName()));
    }

    /**
     * Returns a copy of the options with the given subdirectory name.
     * @param options the original options
     * @param subdirectoryName the new subdirectory name
     * @return the updated options
     */
    public static JPlagOptions subdirectoryName(JPlagOptions options, String subdirectoryName) {
        SubmissionFileOptions s = options.submissionFileOptions();
        return options.withSubmissionFileOptions(new SubmissionFileOptions(
                s.submissionDirectories(),
                s.oldSubmissionDirectories(),
                s.baseCodeSubmissionDirectory(),
                subdirectoryName,
                s.exclusionFileName()));
    }

    /**
     * Returns a copy of the options with the given exclusion file name.
     * @param options the original options
     * @param exclusionFileName the new exclusion file name
     * @return the updated options
     */
    public static JPlagOptions exclusionFileName(JPlagOptions options, String exclusionFileName) {
        SubmissionFileOptions s = options.submissionFileOptions();
        return options.withSubmissionFileOptions(new SubmissionFileOptions(
                s.submissionDirectories(),
                s.oldSubmissionDirectories(),
                s.baseCodeSubmissionDirectory(),
                s.subdirectoryName(),
                exclusionFileName));
    }
}