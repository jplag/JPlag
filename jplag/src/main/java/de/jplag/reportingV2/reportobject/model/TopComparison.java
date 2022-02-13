package de.jplag.reportingV2.reportobject.model;

public class TopComparison {

    private String first_submission;
    private String second_submission;
    private float match_percentage;

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

    public void setFirst_submission(String first_submission) {
        this.first_submission = first_submission;
    }

    public void setSecond_submission(String second_submission) {
        this.second_submission = second_submission;
    }

    public void setMatch_percentage(float match_percentage) {
        this.match_percentage = match_percentage;
    }
}
