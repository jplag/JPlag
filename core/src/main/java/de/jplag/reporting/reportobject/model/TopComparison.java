package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TopComparison(@JsonProperty("first_submission") String firstSubmission, @JsonProperty("second_submission") String secondSubmission,
        @JsonProperty("similarity") double similarity) {
}
