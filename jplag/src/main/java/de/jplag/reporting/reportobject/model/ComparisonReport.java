package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ReportViewer DTO for the comparison of two submissions.
 * @param firstSubmissionId id of the first submission
 * @param secondSubmissionId id of the second submission
 * @param matchPercentage similarity in percent. between 0f and 100f.
 * @param matches the list of matches found in the comparison of the two submissions
 */
public record ComparisonReport(@JsonProperty("id1") String firstSubmissionId, @JsonProperty("id2") String secondSubmissionId,
        @JsonProperty("similarity") float matchPercentage, @JsonProperty("matches") List<Match> matches) {

}
