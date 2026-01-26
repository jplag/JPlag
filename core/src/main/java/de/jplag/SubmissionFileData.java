package de.jplag;

import java.io.File;

/**
 * Captures file information for a specific submission.
 * @param submissionFile the entry file of the submission, which can be either a directory or a program file.
 * @param rootDirectory the root directory in which the submission file resides.
 * @param isNew indicates whether this submission is marked as new or old. Old submissions are only compared to new
 * ones, not to each other.
 */
public record SubmissionFileData(File submissionFile, File rootDirectory, boolean isNew) {
}
