package de.jplag.reporting.reportobject.mapper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Messages;
import de.jplag.options.SimilarityMetric;
import de.jplag.reporting.reportobject.model.Metric;
import de.jplag.reporting.reportobject.model.TopComparison;

/**
 * Extracts and maps metrics from the JPlagResult to the corresponding JSON DTO
 */
public class MetricMapper {
    public Metric getAverageMetric(JPlagResult result) {
        return new Metric(SimilarityMetric.AVG.name(), intArrayToList(result.getSimilarityDistribution()), getTopComparisons(getComparisons(result)),
                Messages.getString("SimilarityMetric.Avg.Description"));
    }

    public Metric getMaxMetric(JPlagResult result) {
        return new Metric(SimilarityMetric.MAX.name(), intArrayToList(result.getMaxSimilarityDistribution()),
                getMaxSimilarityTopComparisons(getComparisons(result)), Messages.getString("SimilarityMetric.Max.Description"));
    }

    private List<JPlagComparison> getComparisons(JPlagResult result) {
        int maxNumberOfComparisons = result.getOptions().getMaximumNumberOfComparisons();
        return result.getComparisons(maxNumberOfComparisons);
    }

    private static List<Integer> intArrayToList(int[] array) {
        return Arrays.stream(array).boxed().collect(Collectors.toList());
    }

    private static List<TopComparison> getTopComparisons(List<JPlagComparison> comparisons, Function<JPlagComparison, Float> similarityExtractor) {
        return comparisons.stream().sorted(Comparator.comparing(similarityExtractor).reversed())
                .map(comparison -> new TopComparison(comparison.getFirstSubmission().getName(), comparison.getSecondSubmission().getName(),
                        similarityExtractor.apply(comparison)))
                .toList();
    }

    private static List<TopComparison> getTopComparisons(List<JPlagComparison> comparisons) {
        return getTopComparisons(comparisons, JPlagComparison::similarity);
    }

    private static List<TopComparison> getMaxSimilarityTopComparisons(List<JPlagComparison> comparisons) {
        return getTopComparisons(comparisons, JPlagComparison::maximalSimilarity);
    }

}
