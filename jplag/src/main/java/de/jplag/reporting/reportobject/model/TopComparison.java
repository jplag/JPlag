package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TopComparison(@JsonProperty("first_submission") String firstSubmission, @JsonProperty("second_submission") String secondSubmission,
        @JsonProperty("match_percentage") float matchPercentage) {

    public TopComparison(String firstSubmission, String secondSubmission, float matchPercentage) {
        this.firstSubmission = firstSubmission;
        this.secondSubmission = secondSubmission;
        this.matchPercentage = matchPercentage;
    }

    @Override
    public String firstSubmission() {
        return firstSubmission;
    }

    @Override
    public String secondSubmission() {
        return secondSubmission;
    }

    @Override
    public float matchPercentage() {
        return matchPercentage;
    }

}
