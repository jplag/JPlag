package de.jplag.reporting.reportobject.model;

import java.util.Map;

/**
 * Represents entry in the list of top comparisons between two submissions, including their identifiers and a map of
 * similarity metrics associated with the comparison.
 * @param firstSubmission the identifier of the first submission
 * @param secondSubmission the identifier of the second submission
 * @param similarities a map containing similarity metric names and their corresponding values
 */
public record TopComparison(String firstSubmission, String secondSubmission, Map<String, Double> similarities) {
}
