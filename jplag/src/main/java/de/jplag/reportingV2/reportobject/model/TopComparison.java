package de.jplag.reportingV2.reportobject.model;

public class TopComparison {

    private final String first_submission;
    private final String second_submission;
    private final float match_percentage;

    public TopComparison(String first_submission, String second_submission, float match_percentage) {
        this.first_submission = first_submission;
        this.second_submission = second_submission;
        this.match_percentage = match_percentage;
    }

    public String getFirst_submission() {
        return first_submission;
    }

    public String getSecond_submission() {
        return second_submission;
    }

    public float getMatch_percentage() {
        return match_percentage;
    }

}
