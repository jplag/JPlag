package de.jplag.reporting.reportobject.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TopComparison(@JsonProperty("first_submission") String firstSubmission, @JsonProperty("second_submission") String secondSubmission,
        @JsonProperty("similarities") Map<String, Double> similarities) {
}
