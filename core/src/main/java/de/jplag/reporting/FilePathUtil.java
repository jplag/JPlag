package de.jplag.reporting;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;

import de.jplag.Submission;

public final class FilePathUtil {
    private static final String ZIP_PATH_SEPARATOR = "/"; // Paths in zip files are always separated by a slash

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
    public static Path getRelativeSubmissionPath(File file, Submission submission, Function<Submission, String> submissionToIdFunction) {
        if (file.toPath().equals(submission.getRoot().toPath())) {
            return Path.of(submissionToIdFunction.apply(submission), submissionToIdFunction.apply(submission));
        }
        return Path.of(submissionToIdFunction.apply(submission), submission.getRoot().toPath().relativize(file.toPath()).toString());
    }

    public static Path forceRelativePath(Path path) {
        if (path.isAbsolute()) {
            return Path.of("/").relativize(path);
        }
        return path;
    }

    public static String pathAsZipPath(Path path) {
        Path real = forceRelativePath(path);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < real.getNameCount(); i++) {
            if (i != 0) {
                builder.append(ZIP_PATH_SEPARATOR);
            }
            builder.append(real.getName(i));
        }
        return builder.toString();
    }
}
