package de.jplag.reporting.reportobject.model;

import java.util.Map;

/**
 * Holds mappings related to submissions and their associated comparison file names.
 * @param submissionIds A map from submission identifiers to their internal or normalized submission IDs.
 * @param submissionIdsToComparisonFileName A nested map where each key is a submission ID, and the value is another map
 * that associates comparison file names with their normalized or processed names.
 */
public record SubmissionMappings(Map<String, String> submissionIds, Map<String, Map<String, String>> submissionIdsToComparisonFileName) {
}
