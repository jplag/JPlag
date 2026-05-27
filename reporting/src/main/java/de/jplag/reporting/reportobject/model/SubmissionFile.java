package de.jplag.reporting.reportobject.model;

/**
 * Represents metadata about a submission file used in a plagiarism comparison.
 * @param tokenCount The number of tokens contained in the file. of tokenized elements.
 */
public record SubmissionFile(int tokenCount) {
}
