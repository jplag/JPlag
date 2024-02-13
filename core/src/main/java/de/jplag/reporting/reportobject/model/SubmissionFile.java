package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubmissionFile(@JsonProperty("token_count") int tokenCount) {
}
