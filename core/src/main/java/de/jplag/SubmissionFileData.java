package de.jplag;

import java.io.File;

/**
 * Contains the information about a single file in a submission. For single file submissions the submission file is the
 * same as the root.
 * @param submissionFile The file, that is part of a submission
 * @param root The root of the submission
 * @param isNew Indicates weather this follows the new or the old syntax
 */
public record SubmissionFileData(File submissionFile, File root, boolean isNew) {
}
