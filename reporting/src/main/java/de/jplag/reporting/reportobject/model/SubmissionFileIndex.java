package de.jplag.reporting.reportobject.model;

import java.util.Map;

/**
 * Represents a nested mapping structure that stores indexed information about submission files.
 * @param fileIndexes A map where each key is a submission ID, and each value is another map mapping file names to their
 * corresponding {@link SubmissionFile} metadata.
 */
public record SubmissionFileIndex(Map<String, Map<String, SubmissionFile>> fileIndexes) {
}
