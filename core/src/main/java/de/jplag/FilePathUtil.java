package de.jplag;

import de.jplag.util.RelativePath;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * Utility class for handling file paths related to submissions, including generating relative paths, enforcing relative
 * paths, and formatting paths for ZIP archives.
 */
public final class FilePathUtil {
    private static final String ZIP_PATH_SEPARATOR = "/"; // Paths in zip files are always separated by a slash

    private FilePathUtil() {
        // private constructor to prevent instantiation
    }

    /**
     * Returns the files path relative to the root folder of the submission ID.
     *
     * @param file                   File that should be relativized
     * @param submission             Submission file belongs to
     * @param submissionToIdFunction Function to map names to ids
     * @return Relative path
     */
    public static RelativePath getRelativeSubmissionPath(Path file, Submission submission, Function<Submission, String> submissionToIdFunction) {
        if (file.equals(submission.getRoot())) {
            return RelativePath.of(submissionToIdFunction.apply(submission), submissionToIdFunction.apply(submission));
        }
        return RelativePath.of(submissionToIdFunction.apply(submission), submission.getRoot().relativize(file).toString());
    }

    /**
     * Forces a path to be relative. If the path is absolute, the returned path will be relative to the root. If a relative
     * path does not exist, it returns the absolute path.
     *
     * @param path The path to relativize
     * @return The relative path
     */
    public static Path forceRelativePath(Path path) {
        if (path.isAbsolute()) {
            try {
                return Path.of("./").toAbsolutePath().relativize(path);
            } catch (IllegalArgumentException _) {
                return path.toAbsolutePath();
            }
        }
        return path;
    }

    /**
     * Formats the path for usage with zip files. Returns the path segments separated by {@link #ZIP_PATH_SEPARATOR}.
     *
     * @param path The path to format
     * @return The zip file path
     */
    public static String pathAsZipPath(Path path) {
        Path relativePath = forceRelativePath(path);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < relativePath.getNameCount(); i++) {
            if (i != 0) {
                builder.append(ZIP_PATH_SEPARATOR);
            }
            builder.append(relativePath.getName(i));
        }
        return builder.toString();
    }

    /**
     * Formats the path for usage with zip files. Returns the path segments separated by {@link #ZIP_PATH_SEPARATOR}.
     *
     * @param path The path to format
     * @return The zip file path
     */
    public static String pathAsZipPath(RelativePath path) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < path.getNameCount(); i++) {
            if (i != 0) {
                builder.append(ZIP_PATH_SEPARATOR);
            }
            builder.append(path.getName(i));
        }
        return builder.toString();
    }
}
