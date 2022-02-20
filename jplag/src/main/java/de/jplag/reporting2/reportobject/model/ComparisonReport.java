package de.jplag.reporting2.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ComparisonReport {

    @JsonProperty("first_submission_id")
    private final String firstSubmissionId;

    @JsonProperty("second_submission_id")
    private final String secondSubmissionId;

    @JsonProperty("match_percentage")
    private final float matchPercentage;

    @JsonProperty("files_of_first_submission")
    private final List<FilesOfSubmission> filesOfFirstSubmission;

    @JsonProperty("files_of_second_submission")
    private final List<FilesOfSubmission> filesOfSecondSubmission;

    @JsonProperty("matches")
    private final List<Match> matches;

    public ComparisonReport(String firstSubmissionId, String secondSubmissionId, float matchPercentage,
            List<FilesOfSubmission> filesOfFirstSubmission, List<FilesOfSubmission> filesOfSecondSubmission, List<Match> matches) {

        this.firstSubmissionId = firstSubmissionId;
        this.secondSubmissionId = secondSubmissionId;
        this.matchPercentage = matchPercentage;
        this.filesOfFirstSubmission = List.copyOf(filesOfFirstSubmission);
        this.filesOfSecondSubmission = List.copyOf(filesOfSecondSubmission);
        this.matches = matches;
    }

    public String getFirstSubmissionId() {
        return firstSubmissionId;
    }

    public String getSecondSubmissionId() {
        return secondSubmissionId;
    }

    public float getMatchPercentage() {
        return matchPercentage;
    }

    public List<FilesOfSubmission> getFilesOfFirstSubmission() {
        return filesOfFirstSubmission;
    }

    public List<FilesOfSubmission> getFilesOfSecondSubmission() {
        return filesOfSecondSubmission;
    }

    public List<Match> getMatches() {
        return matches;
    }
}
