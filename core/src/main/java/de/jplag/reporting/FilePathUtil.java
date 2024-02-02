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

}
