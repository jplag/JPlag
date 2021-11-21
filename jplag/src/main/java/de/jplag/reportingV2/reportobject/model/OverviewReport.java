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
	private float max_similarity_threshold;
	private float avg_similarity_threshold;
	private int match_sensitivity;
	private String date_of_execution;
	private long execution_time;
	private List<Integer> distribution_max;
	private List<Integer> distribution_avg;
	private List<String> comparison_names;
	private List<TopComparison> top_max_comparisons;
	private List<TopComparison> top_avg_comparisons;

	public OverviewReport() {
		submission_folder_path = "";
		base_code_folder_path ="";
		language ="";
		file_extensions = List.of();
		submission_ids = List.of();
		failed_submission_names = List.of();
		excluded_files = List.of();
		max_similarity_threshold = 0;
		avg_similarity_threshold = 0;
		match_sensitivity = 0;
		date_of_execution = "";
		execution_time = 0;
		distribution_max = List.of();
		distribution_avg = List.of();
		top_max_comparisons = List.of();
		top_avg_comparisons = List.of();
	}

	public OverviewReport(String submission_folder_path,
						  String base_code_folder_path,
						  String language, List<String> file_extensions,
						  List<String> submission_ids,
						  List<String> failed_submission_names,
						  List<String> excluded_files,
						  int max_similarity_threshold,
						  int avg_similarity_threshold,
						  int match_sensitivity,
						  String date_of_execution,
						  int execution_time,
						  List<Integer> distribution_max,
						  List<Integer> distribution_avg,
						  List<String> comparison_names,
						  List<TopComparison> top_max_comparisons,
						  List<TopComparison> top_avg_comparisons) {

		this.submission_folder_path = submission_folder_path;
		this.base_code_folder_path = base_code_folder_path;
		this.language = language;
		this.file_extensions = file_extensions;
		this.submission_ids = submission_ids;
		this.failed_submission_names = failed_submission_names;
		this.excluded_files = excluded_files;
		this.max_similarity_threshold = max_similarity_threshold;
		this.avg_similarity_threshold = avg_similarity_threshold;
		this.match_sensitivity = match_sensitivity;
		this.date_of_execution = date_of_execution;
		this.execution_time = execution_time;
		this.distribution_max = distribution_max;
		this.distribution_avg = distribution_avg;
		this.comparison_names = comparison_names;
		this.top_max_comparisons = top_max_comparisons;
		this.top_avg_comparisons = top_avg_comparisons;
	}

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

	public float getMax_similarity_threshold() {
		return max_similarity_threshold;
	}

	public float getAvg_similarity_threshold() {
		return avg_similarity_threshold;
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

	public List<Integer> getDistribution_max() {
		return distribution_max;
	}

	public List<Integer> getDistribution_avg() {
		return distribution_avg;
	}

	public List<String> getComparison_names() {
		return comparison_names;
	}

	public List<TopComparison> getTop_max_comparisons() {
		return top_max_comparisons;
	}

	public List<TopComparison> getTop_avg_comparisons() {
		return top_avg_comparisons;
	}

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

	public void setMax_similarity_threshold(float max_similarity_threshold) {
		this.max_similarity_threshold = max_similarity_threshold;
	}

	public void setAvg_similarity_threshold(float avg_similarity_threshold) {
		this.avg_similarity_threshold = avg_similarity_threshold;
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

	public void setDistribution_max(List<Integer> distribution_max) {
		this.distribution_max = distribution_max;
	}

	public void setDistribution_avg(List<Integer> distribution_avg) {
		this.distribution_avg = distribution_avg;
	}

	public void setComparison_names(List<String> comparison_names) {
		this.comparison_names = comparison_names;
	}

	public void setTop_max_comparisons(List<TopComparison> top_max_comparisons) {
		this.top_max_comparisons = top_max_comparisons;
	}

	public void setTop_avg_comparisons(List<TopComparison> top_avg_comparisons) {
		this.top_avg_comparisons = top_avg_comparisons;
	}
}
