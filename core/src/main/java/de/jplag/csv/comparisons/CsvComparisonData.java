package de.jplag.csv.comparisons;

import de.jplag.csv.CsvValue;

/**
 * Comparison data for writing to a csv.
 * @param firstSubmissionName The name of the first submission
 * @param secondSubmissionName The name of the second submission
 * @param averageSimilarity The average similarity
 * @param maxSimilarity The maximum similarity
 */
public record CsvComparisonData(@CsvValue(1) String firstSubmissionName, @CsvValue(2) String secondSubmissionName,
        @CsvValue(3) double averageSimilarity, @CsvValue(4) double maxSimilarity) {
}
