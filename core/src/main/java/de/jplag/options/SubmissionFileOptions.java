package de.jplag.options;

import de.jplag.util.FileUtils;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @param submissionDirectories Directories with new submissions. These must be checked for plagiarism.
 * @param oldSubmissionDirectories Directories with old submissions to check against.
 * @param baseCodeSubmissionDirectory Directory containing the base code.
 * @param subdirectoryName Example: If the subdirectoryName is 'src', only the code inside submissionDir/src of each
 * submission will be used for comparison.
 * @param fileSuffixes List of file extensions or custom suffixes that should be included.
 * @param exclusionFileName Name of the file that contains the names of files to exclude from comparison.
 */
@RecordBuilder()
public record SubmissionFileOptions(Set<Path> submissionDirectories, Set<Path> oldSubmissionDirectories,
                                    Path baseCodeSubmissionDirectory, String subdirectoryName, List<String> fileSuffixes, String exclusionFileName) implements SubmissionFileOptionsBuilder.With {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionFileOptions.class);

    public SubmissionFileOptions(Set<Path> submissionDirectories, Set<Path> oldSubmissionDirectories,
                                 Path baseCodeSubmissionDirectory, String subdirectoryName, List<String> fileSuffixes, String exclusionFileName) {
        this.submissionDirectories = submissionDirectories == null ? null : Collections.unmodifiableSet(submissionDirectories);
        this.oldSubmissionDirectories = oldSubmissionDirectories == null ? null : Collections.unmodifiableSet(oldSubmissionDirectories);
        this.baseCodeSubmissionDirectory = baseCodeSubmissionDirectory;
        this.subdirectoryName = subdirectoryName;
        this.fileSuffixes = fileSuffixes == null || fileSuffixes.isEmpty() ? null : Collections.unmodifiableList(fileSuffixes);
        this.exclusionFileName = exclusionFileName;
    }

    public SubmissionFileOptions(Set<Path> submissionDirectories, Set<Path> oldSubmissionDirectories,
                                 Path baseCodeSubmissionDirectory) {
        this(submissionDirectories, oldSubmissionDirectories, null, null, null, null);
    }

    /**
     * Checks if a base code submission directory is set.
     * @return true if base code directory is present; false otherwise
     */
    public boolean hasBaseCode() {
        return baseCodeSubmissionDirectory != null;
    }

    /**
     * Returns a set of file names to exclude from the comparison, read from the exclusion file if provided.
     * @return Set of excluded file names; empty set if no exclusion file is set or readable
     */
    public Set<String> excludedFiles() {
        return Optional.ofNullable(exclusionFileName).map(this::readExclusionFile).orElse(Collections.emptySet());
    }

    private Set<String> readExclusionFile(final String exclusionFileName) {
        try (BufferedReader reader = FileUtils.openFileReader(Path.of(exclusionFileName))) {
            final var excludedFileNames = reader.lines().collect(Collectors.toSet());
            if (logger.isDebugEnabled()) {
                logger.debug("Excluded files:{}{}", System.lineSeparator(), String.join(System.lineSeparator(), excludedFileNames));
            }
            return excludedFileNames;
        } catch (IOException e) {
            logger.error("Could not read exclusion file: " + e.getMessage(), e);
            return Collections.emptySet();
        }
    }
}
