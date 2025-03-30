package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RunInformation(@JsonProperty("jplag_version") Version version,

        @JsonProperty("failed_submission_names") List<String> failedSubmissionNames,

        @JsonProperty("date_of_execution") String dateOfExecution,

        @JsonProperty("execution_time") long executionTime,

        @JsonProperty("total_comparisons") int totalComparisons) {
}
