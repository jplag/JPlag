package de.jplag.reporting.reportobject.model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OverviewReport {

    @JsonProperty("submission_folder_path")
    private List<String> submissionFolderPath;

    @JsonProperty("base_code_folder_path")
    private String baseCodeFolderPath;

    @JsonProperty("language")
    private String language;

    @JsonProperty("file_extensions")
    private List<String> fileExtensions;

    @JsonProperty("submission_ids")
    private List<String> submissionIds;

    @JsonProperty("failed_submission_names")
    private List<String> failedSubmissionNames;

    @JsonProperty("excluded_files")
    private Set<String> excludedFiles;

    @JsonProperty("match_sensitivity")
    private int matchSensitivity;

    @JsonProperty("date_of_execution")
    private String dateOfExecution;

    @JsonProperty("execution_time")
    private long executionTime;

    @JsonProperty("comparison_names")
    private List<String> comparisonNames;

    @JsonProperty("metrics")
    private List<Metric> metrics;

    @JsonProperty("clusters")
    private List<Cluster> clusters;

    public OverviewReport() {
        submissionFolderPath = List.of();
        baseCodeFolderPath = "";
        language = "";
        fileExtensions = List.of();
        submissionIds = List.of();
        failedSubmissionNames = List.of();
        excludedFiles = Set.of();
        matchSensitivity = 0;
        dateOfExecution = "";
        executionTime = 0;
        clusters = List.of();
    }

    /*******************************
     * GETTERS
     *******************************/

    public List<String> getSubmissionFolderPath() {
        return submissionFolderPath;
    }

    public String getBaseCodeFolderPath() {
        return baseCodeFolderPath;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    public List<String> getSubmissionIds() {
        return submissionIds;
    }

    public List<String> getFailedSubmissionNames() {
        return failedSubmissionNames;
    }

    public Set<String> getExcludedFiles() {
        return excludedFiles;
    }

    public int getMatchSensitivity() {
        return matchSensitivity;
    }

    public String getDateOfExecution() {
        return dateOfExecution;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public List<String> getComparisonNames() {
        return comparisonNames;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    /*******************************
     * SETTERS
     *******************************/

    public void setSubmissionFolderPath(List<String> submissionFolderPath) {
        this.submissionFolderPath = submissionFolderPath;
    }

    public void setBaseCodeFolderPath(String baseCodeFolderPath) {
        this.baseCodeFolderPath = baseCodeFolderPath;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setFileExtensions(List<String> fileExtensions) {
        this.fileExtensions = List.copyOf(fileExtensions);
    }

    public void setSubmissionIds(List<String> submissionIds) {
        this.submissionIds = List.copyOf(submissionIds);
    }

    public void setFailedSubmissionNames(List<String> failedSubmissionNames) {
        this.failedSubmissionNames = List.copyOf(failedSubmissionNames);
    }

    public void setExcludedFiles(Set<String> excludedFiles) {
        this.excludedFiles = Set.copyOf(excludedFiles);
    }

    public void setMatchSensitivity(int matchSensitivity) {
        this.matchSensitivity = matchSensitivity;
    }

    public void setDateOfExecution(String dateOfExecution) {
        this.dateOfExecution = dateOfExecution;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public void setComparisonNames(List<String> comparisonNames) {
        this.comparisonNames = List.copyOf(comparisonNames);
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = List.copyOf(metrics);
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = List.copyOf(clusters);
    }
}
