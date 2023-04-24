package de.jplag.reporting.reportobject.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubmissionFileIndex(@JsonProperty("submission_file_indexes") Map<String, List<String>> fileIndexes) {
}
