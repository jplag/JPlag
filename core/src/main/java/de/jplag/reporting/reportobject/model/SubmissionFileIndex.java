package de.jplag.reporting.reportobject.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubmissionFileIndex(@JsonProperty("submission_file_indexes") Map<String, Map<String, SubmissionFile>> fileIndexes) {
}
