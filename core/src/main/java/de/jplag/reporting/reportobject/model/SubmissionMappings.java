package de.jplag.reporting.reportobject.model;

import java.util.Map;

public record SubmissionMappings(Map<String, String> submissionIds, Map<String, Map<String, String>> submissionIdsToComparisonFileName) {
}
