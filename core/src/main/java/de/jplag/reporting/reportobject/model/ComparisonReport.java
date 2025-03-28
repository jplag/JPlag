package de.jplag.reporting.reportobject.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ReportViewer DTO for the comparison of two submissions.
 * @param firstSubmissionId id of the first submission
 * @param secondSubmissionId id of the second submission
 * @param similarities map of metric names and corresponding similarities. between 0.0 and 1.0.
 * @param matches the list of matches found in the comparison of the two submissions
 * @param firstSimilarity is the similarity of the first submission to the second one.
 * @param secondSimilarity is the similarity of the second submission to the first one.
 */
public record ComparisonReport(@JsonProperty("id1") String firstSubmissionId, @JsonProperty("id2") String secondSubmissionId,
        @JsonProperty("similarities") Map<String, Double> similarities, @JsonProperty("matches") List<Match> matches,
        @JsonProperty("first_similarity") double firstSimilarity, @JsonProperty("second_similarity") double secondSimilarity) {

}
