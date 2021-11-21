package de.jplag.reportingV2.reportobject.model;

import java.util.List;

public class OverviewReport {

	private String submission_folder_path;
	private String base_code_folder_path;
	private String language;
	private List<String> file_extensions;
	private List<String> submission_ids;
	private List<String> failed_submission_names;
	private List<String> excluded_files;
	private int match_sensitivity;
	private String date_of_execution;
	private long execution_time;
	private List<String> comparison_names;
	private List<Metric> metrics;

	public OverviewReport() {
		submission_folder_path = "";
		base_code_folder_path ="";
		language ="";
		file_extensions = List.of();
		submission_ids = List.of();
		failed_submission_names = List.of();
		excluded_files = List.of();
		match_sensitivity = 0;
		date_of_execution = "";
		execution_time = 0;
	}

	/*******************************
	 *
	 *  GETTERS
	 *
	 *******************************/

	public String getSubmission_folder_path() {
		return submission_folder_path;
	}

	public String getBase_code_folder_path() {
		return base_code_folder_path;
	}

	public String getLanguage() {
		return language;
	}

	public List<String> getFile_extensions() {
		return file_extensions;
	}

	public List<String> getSubmission_ids() {
		return submission_ids;
	}

	public List<String> getFailed_submission_names() {
		return failed_submission_names;
	}

	public List<String> getExcluded_files() {
		return excluded_files;
	}

	public int getMatch_sensitivity() {
		return match_sensitivity;
	}

	public String getDate_of_execution() {
		return date_of_execution;
	}

	public long getExecution_time() {
		return execution_time;
	}

	public List<String> getComparison_names() {
		return comparison_names;
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	/*******************************
	 *
	 *  SETTERS
	 *
	 *******************************/

	public void setSubmission_folder_path(String submission_folder_path) {
		this.submission_folder_path = submission_folder_path;
	}

	public void setBase_code_folder_path(String base_code_folder_path) {
		this.base_code_folder_path = base_code_folder_path;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setFile_extensions(List<String> file_extensions) {
		this.file_extensions = file_extensions;
	}

	public void setSubmission_ids(List<String> submission_ids) {
		this.submission_ids = submission_ids;
	}

	public void setFailed_submission_names(List<String> failed_submission_names) {
		this.failed_submission_names = failed_submission_names;
	}

	public void setExcluded_files(List<String> excluded_files) {
		this.excluded_files = excluded_files;
	}

	public void setMatch_sensitivity(int match_sensitivity) {
		this.match_sensitivity = match_sensitivity;
	}

	public void setDate_of_execution(String date_of_execution) {
		this.date_of_execution = date_of_execution;
	}

	public void setExecution_time(long execution_time) {
		this.execution_time = execution_time;
	}


	public void setComparison_names(List<String> comparison_names) {
		this.comparison_names = comparison_names;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}
}
