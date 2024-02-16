package de.jplag.reporting;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;

import de.jplag.Submission;

public final class FilePathUtil {

    private FilePathUtil() {
        // private constructor to prevent instantiation
    }

    /**
     * Returns the files path relative to the root folder of the submission ID
     * @param file File that should be relativized
     * @param submission Submission file belongs to
     * @param submissionToIdFunction Function to map names to ids
     * @return Relative path
     */
    public static String getRelativeSubmissionPath(File file, Submission submission, Function<Submission, String> submissionToIdFunction) {
        if (file.toPath().equals(submission.getRoot().toPath())) {
            return Path.of(submissionToIdFunction.apply(submission), submissionToIdFunction.apply(submission)).toString();
        }
        return Path.of(submissionToIdFunction.apply(submission), submission.getRoot().toPath().relativize(file.toPath()).toString()).toString();
    }

    /**
     * Joins logical paths using a slash. This method ensures, that no duplicate slashes are created in between.
     * @param left The left path segment
     * @param right The right path segment
     * @return The joined paths
     */
    public static String joinPathSegments(String left, String right) {
        String rightStripped = right;
        while (rightStripped.startsWith("/")) {
            rightStripped = rightStripped.substring(1);
        }

        String leftStripped = left;
        while (leftStripped.endsWith("/")) {
            leftStripped = leftStripped.substring(0, leftStripped.length() - 1);
        }

        return leftStripped + "/" + rightStripped;
    }
}
