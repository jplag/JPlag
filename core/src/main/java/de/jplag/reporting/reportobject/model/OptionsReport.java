package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OptionsReport(@JsonProperty("language") String language, @JsonProperty("min_token_match") int minTokenMatch,
        @JsonProperty("submission_directories") List<String> submissionDirectories, @JsonProperty("old_directories") List<String> oldDirectories,
        @JsonProperty("base_directory") String baseDirectory, @JsonProperty("subdirectory_name") String subdirectoryName,
        @JsonProperty("file_suffixes") List<String> fileSuffixes, @JsonProperty("exclusion_file_name") String exclusionFileName,
        @JsonProperty("similarity_metric") String similarityMetric, @JsonProperty("similarity_threshold") double similarityThreshold,
        @JsonProperty("max_comparisons") int maximumNumberOfComparisons, @JsonProperty("cluster") ClusterOptionsReport clusterOptions,
        @JsonProperty("merging") MergingOptionsReport mergingOptions) {
}
