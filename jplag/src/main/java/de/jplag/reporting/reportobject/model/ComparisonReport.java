package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ComparisonReport(@JsonProperty("first_submission_id") String firstSubmissionId,
        @JsonProperty("second_submission_id") String secondSubmissionId, @JsonProperty("match_percentage") float matchPercentage,
        @JsonProperty("files_of_first_submission") List<FilesOfSubmission> filesOfFirstSubmission,
        @JsonProperty("files_of_second_submission") List<FilesOfSubmission> filesOfSecondSubmission, @JsonProperty("matches") List<Match> matches) {

    public ComparisonReport(String firstSubmissionId, String secondSubmissionId, float matchPercentage,
            List<FilesOfSubmission> filesOfFirstSubmission, List<FilesOfSubmission> filesOfSecondSubmission, List<Match> matches) {
        this.firstSubmissionId = firstSubmissionId;
        this.secondSubmissionId = secondSubmissionId;
        this.matchPercentage = matchPercentage;
        this.filesOfFirstSubmission = List.copyOf(filesOfFirstSubmission);
        this.filesOfSecondSubmission = List.copyOf(filesOfSecondSubmission);
        this.matches = matches;

    }
}
