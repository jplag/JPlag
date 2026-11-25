package de.jplag.optionsWithHelper;

import de.jplag.options.JPlagOptions;
import de.jplag.options.SubmissionFileOptions;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public final class SubmissionFileOptionsWith {

    private SubmissionFileOptionsWith() {
        // utility class
    }

    public static JPlagOptions submissionDirectories(JPlagOptions options, Set<Path> submissionDirectories) {
        SubmissionFileOptions s = options.submissionFileOptions();
        return options.withSubmissionFileOptions(new SubmissionFileOptions(
                submissionDirectories,
                s.oldSubmissionDirectories(),
                s.baseCodeSubmissionDirectory(),
                s.subdirectoryName(),
                s.exclusionFileName()));
    }

    public static JPlagOptions oldSubmissionDirectories(JPlagOptions options, Set<Path> oldSubmissionDirectories) {
        SubmissionFileOptions s = options.submissionFileOptions();
        return options.withSubmissionFileOptions(new SubmissionFileOptions(
                s.submissionDirectories(),
                oldSubmissionDirectories,
                s.baseCodeSubmissionDirectory(),
                s.subdirectoryName(),
                s.exclusionFileName()));
    }

    public static JPlagOptions baseCodeSubmissionDirectory(JPlagOptions options, Path baseCodeSubmissionDirectory) {
        SubmissionFileOptions s = options.submissionFileOptions();
        return options.withSubmissionFileOptions(new SubmissionFileOptions(
                s.submissionDirectories(),
                s.oldSubmissionDirectories(),
                baseCodeSubmissionDirectory,
                s.subdirectoryName(),
                s.exclusionFileName()));
    }

    public static JPlagOptions subdirectoryName(JPlagOptions options, String subdirectoryName) {
        SubmissionFileOptions s = options.submissionFileOptions();
        return options.withSubmissionFileOptions(new SubmissionFileOptions(
                s.submissionDirectories(),
                s.oldSubmissionDirectories(),
                s.baseCodeSubmissionDirectory(),
                subdirectoryName,
                s.exclusionFileName()));
    }
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