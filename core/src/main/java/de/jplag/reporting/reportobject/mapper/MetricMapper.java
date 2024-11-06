package de.jplag.reporting.reportobject.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.options.SimilarityMetric;
import de.jplag.reporting.reportobject.model.TopComparison;

/**
 * Extracts and maps metrics from the JPlagResult to the corresponding JSON DTO
 */
public class MetricMapper {
    private final Function<Submission, String> submissionToIdFunction;

    public MetricMapper(Function<Submission, String> submissionToIdFunction) {
        this.submissionToIdFunction = submissionToIdFunction;
    }

    /**
     * Generates a map of all distributions
     * @param result Result containing distributions
     * @return Map with key as name of metric and value as distribution
     */
    public static Map<String, List<Integer>> getDistributions(JPlagResult result) {
        Map<String, List<Integer>> distributions = new HashMap<>();
        for (SimilarityMetric metric : SimilarityMetric.values()) {
            distributions.put(metric.name(), result.calculateDistributionFor(metric));
        }
        return distributions;
    }

    /**
     * Generates a List of the top comparisons
     * @param result Result containing comparisons
     * @return List of top comparisons with similarities in all metrics
     */
    public List<TopComparison> getTopComparisons(JPlagResult result) {
        return result.getComparisons(result.getOptions().maximumNumberOfComparisons()).stream()
                .map(comparison -> new TopComparison(submissionToIdFunction.apply(comparison.firstSubmission()),
                        submissionToIdFunction.apply(comparison.secondSubmission()), getComparisonMetricMap(comparison)))
                .toList();
    }

    private Map<String, Double> getComparisonMetricMap(JPlagComparison comparison) {
        return Map.of(SimilarityMetric.AVG.name(), comparison.similarity(), SimilarityMetric.MAX.name(), comparison.maximalSimilarity());
    }
}
