package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record SubmissionFileIndex(
        @JsonProperty("submission_file_indexes") Map<String, List<String>> fileIndexes
) {}
