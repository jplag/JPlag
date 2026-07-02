package de.jplag.reporting.reportobject.model;

import de.jplag.JPlagResult;
import de.jplag.Submission;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds mappings related to submissions and their associated comparison file names.
 * @param submissionIds A map from a submission ID to the submissions display name.
 * @param submissionIdsToComparisonFileName A nested map where each key is a submission ID, and the value is another map
 * that maps a second submission ID to the name of the JSON file containing the comparison of the two submissions.
 */
public record SubmissionMappings(Map<String, String> submissionIds, Map<String, Map<String, String>> submissionIdsToComparisonFileName) {
    public SubmissionMappings(JPlagResult result, Map<String, Map<String, String>> comparisons) {
        Map<String, String> nameMap = new HashMap<>();

        for (Submission submission : result.getSubmissions().getSubmissions()) {
            nameMap.put(submission.getName(), submission.getName());
        }
        this(nameMap, comparisons);
    }
}
