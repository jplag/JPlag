package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopComparison {

    @JsonProperty("first_submission")
    private final String firstSubmission;

    @JsonProperty("second_submission")
    private final String secondSubmission;

    @JsonProperty("match_percentage")
    private final float matchPercentage;

    public TopComparison(String firstSubmission, String secondSubmission, float matchPercentage) {
        this.firstSubmission = firstSubmission;
        this.secondSubmission = secondSubmission;
        this.matchPercentage = matchPercentage;
    }

    public String getFirstSubmission() {
        return firstSubmission;
    }

    public String getSecondSubmission() {
        return secondSubmission;
    }

    public float getMatchPercentage() {
        return matchPercentage;
    }

}
