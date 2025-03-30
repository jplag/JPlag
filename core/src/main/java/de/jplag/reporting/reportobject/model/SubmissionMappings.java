package de.jplag.reporting.reportobject.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubmissionMappings(@JsonProperty("submission_id_to_display_name") Map<String, String> submissionIds,
        @JsonProperty("submission_ids_to_comparison_file_name") Map<String, Map<String, String>> submissionIdsToComparisonFileName) {
}
