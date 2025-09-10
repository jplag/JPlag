package de.jplag.reporting.reportobject.model;

import java.util.Map;

/**
 * Holds mappings related to submissions and their associated comparison file names.
 * @param submissionIds A map from a submission ID to the submissions display name.
 * @param submissionIdsToComparisonFileName A nested map where each key is a submission ID, and the value is another map
 * that maps a second submission ID to the name of the JSON file containing the comparison of the two submissions.
 */
public record SubmissionMappings(Map<String, String> submissionIds, Map<String, Map<String, String>> submissionIdsToComparisonFileName) {
}
