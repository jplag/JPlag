package de.jplag.reporting.reportobject.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OverviewReport(

        @JsonProperty("jplag_version") Version version,

        @JsonProperty("submission_folder_path") List<String> submissionFolderPath,

        @JsonProperty("base_code_folder_path") String baseCodeFolderPath,

        @JsonProperty("language") String language,

        @JsonProperty("file_extensions") List<String> fileExtensions,

        @JsonProperty("submission_id_to_display_name") Map<String, String> submissionIds,
        @JsonProperty("submission_ids_to_comparison_file_name") Map<String, Map<String, String>> submissionIdsToComparisonFileName,

        @JsonProperty("failed_submission_names") List<String> failedSubmissionNames,

        @JsonProperty("excluded_files") Set<String> excludedFiles,

        @JsonProperty("match_sensitivity") int matchSensitivity,

        @JsonProperty("date_of_execution") String dateOfExecution,

        @JsonProperty("execution_time") long executionTime,

        @JsonProperty("metrics") List<Metric> metrics,

        @JsonProperty("clusters") List<Cluster> clusters,

        @JsonProperty("total_comparisons") int totalComparisons) {
}
