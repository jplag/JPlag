package de.jplag.reportingV2.reportobject.model;

import java.util.List;

public class ComparisonReport {

    private final String first_submission_id;
    private final String second_submission_id;
    private final float match_percentage;
    private final List<FilesOfSubmission> files_of_first_submission;
    private final List<FilesOfSubmission> files_of_second_submission;
    private final List<Match> matches;

    public ComparisonReport(String first_submission_id, String second_submission_id, float match_percentage,
            List<FilesOfSubmission> files_of_first_submission, List<FilesOfSubmission> files_of_second_submission, List<Match> matches) {

        this.first_submission_id = first_submission_id;
        this.second_submission_id = second_submission_id;
        this.match_percentage = match_percentage;
        this.files_of_first_submission = List.copyOf(files_of_first_submission);
        this.files_of_second_submission = List.copyOf(files_of_second_submission);
        this.matches = matches;
    }

    public String getFirst_submission_id() {
        return first_submission_id;
    }

    public String getSecond_submission_id() {
        return second_submission_id;
    }

    public float getMatch_percentage() {
        return match_percentage;
    }

    public List<FilesOfSubmission> getFiles_of_first_submission() {
        return files_of_first_submission;
    }

    public List<FilesOfSubmission> getFiles_of_second_submission() {
        return files_of_second_submission;
    }

    public List<Match> getMatches() {
        return matches;
    }
}
