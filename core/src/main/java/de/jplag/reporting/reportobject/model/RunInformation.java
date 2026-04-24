package de.jplag.reporting.reportobject.model;

import java.util.List;

/**
 * Holds metadata and summary statistics about a JPlag run, including version, failures, execution time, and comparison
 * count.
 * @param version The version of JPlag used during the run.
 * @param failedSubmissions A list of submissions that failed during processing.
 * @param dateOfExecution The date and time the run was executed, formatted as a string.
 * @param executionTime The total time taken for execution in milliseconds.
 * @param totalComparisons The total number of comparisons performed during the run.
 */
public record RunInformation(Version version, List<FailedSubmission> failedSubmissions, String dateOfExecution, long executionTime,
        int totalComparisons) {
}
