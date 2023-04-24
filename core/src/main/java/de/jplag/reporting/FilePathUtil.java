package de.jplag.reporting;

import de.jplag.Submission;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;

public final class FilePathUtil {

    public static String getRelativeSubmissionPath(File file, Submission submission, Function<Submission, String> submissionToIdFunction) {
        if (file.toPath().equals(submission.getRoot().toPath())) {
            return Path.of(submissionToIdFunction.apply(submission), submissionToIdFunction.apply(submission)).toString();
        }
        return Path.of(submissionToIdFunction.apply(submission), submission.getRoot().toPath().relativize(file.toPath()).toString()).toString();
    }

}
