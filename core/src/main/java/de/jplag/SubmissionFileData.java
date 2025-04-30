package de.jplag;

import java.io.File;

/**
 * Captures information about an individual submission.
 * @param submissionFile The file that is part of a submission
 * @param root The root of the submission
 * @param isNew Indicates whether this follows the new or the old syntax
 */
public record SubmissionFileData(File submissionFile, File root, boolean isNew) {
}
