package de.jplag.reportingV2.reportobject.model;

import java.util.List;

public class ComparisonReport {

	private String first_submission_id;
	private String second_submission_id;
	private float match_percentage;
	private List<FilesOfSubmission> files_of_first_submission;
	private List<FilesOfSubmission> files_of_second_submission;
	private List<Match> matches;

	public ComparisonReport(String first_submission_id,
							String second_submission_id,
							float match_percentage,
							List<FilesOfSubmission> files_of_first_submission,
							List<FilesOfSubmission> files_of_second_submission,
							List<Match> matches) {

		this.first_submission_id = first_submission_id;
		this.second_submission_id = second_submission_id;
		this.match_percentage = match_percentage;
		this.files_of_first_submission = files_of_first_submission;
		this.files_of_second_submission = files_of_second_submission;
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

	public void setFirst_submission_id(String first_submission_id) {
		this.first_submission_id = first_submission_id;
	}

	public void setSecond_submission_id(String second_submission_id) {
		this.second_submission_id = second_submission_id;
	}

	public void setMatch_percentage(float match_percentage) {
		this.match_percentage = match_percentage;
	}

	public void setFiles_of_first_submission(List<FilesOfSubmission> files_of_first_submission) {
		this.files_of_first_submission = files_of_first_submission;
	}

	public void setFiles_of_second_submission(List<FilesOfSubmission> files_of_second_submission) {
		this.files_of_second_submission = files_of_second_submission;
	}

	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}
}
