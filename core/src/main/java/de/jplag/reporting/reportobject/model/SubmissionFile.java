package de.jplag.reporting.reportobject.model;

/**
 * Represents metadata about a submission file used in a plagiarism comparison.
 * @param tokenCount The number of tokens contained in the file. This indicates the file's size or complexity in terms
 * of tokenized elements.
 */

public record SubmissionFile(int tokenCount) {
}
