package de.jplag.reporting.reportobject.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Messages;
import de.jplag.Submission;
import de.jplag.options.SimilarityMetric;
import de.jplag.reporting.reportobject.model.Metric;
import de.jplag.reporting.reportobject.model.TopComparison;

/**
 * Extracts and maps metrics from the JPlagResult to the corresponding JSON DTO
 */
public class MetricMapper {
    private final Function<Submission, String> submissionToIdFunction;

    public MetricMapper(Function<Submission, String> submissionToIdFunction) {
        this.submissionToIdFunction = submissionToIdFunction;
    }

    public Metric getAverageMetric(JPlagResult result) {
        return new Metric(SimilarityMetric.AVG.name(), convertDistribution(result.getSimilarityDistribution()),
                getTopComparisons(getComparisons(result)), Messages.getString("SimilarityMetric.Avg.Description"));
    }

    public Metric getMaxMetric(JPlagResult result) {
        return new Metric(SimilarityMetric.MAX.name(), convertDistribution(result.getMaxSimilarityDistribution()),
                getMaxSimilarityTopComparisons(getComparisons(result)), Messages.getString("SimilarityMetric.Max.Description"));
    }

    private List<JPlagComparison> getComparisons(JPlagResult result) {
        int maxNumberOfComparisons = result.getOptions().maximumNumberOfComparisons();
        return result.getComparisons(maxNumberOfComparisons);
    }

    private List<Integer> convertDistribution(int[] array) {
        List<Integer> list = new ArrayList<>(Arrays.stream(array).boxed().toList());
        Collections.reverse(list);
        return list;
    }

    private List<TopComparison> getTopComparisons(List<JPlagComparison> comparisons, Function<JPlagComparison, Double> similarityExtractor) {
        return comparisons.stream().sorted(Comparator.comparing(similarityExtractor).reversed())
                .map(comparison -> new TopComparison(submissionToIdFunction.apply(comparison.firstSubmission()),
                        submissionToIdFunction.apply(comparison.secondSubmission()), similarityExtractor.apply(comparison)))
                .toList();
    }

    private List<TopComparison> getTopComparisons(List<JPlagComparison> comparisons) {
        return getTopComparisons(comparisons, JPlagComparison::similarity);
    }

    private List<TopComparison> getMaxSimilarityTopComparisons(List<JPlagComparison> comparisons) {
        return getTopComparisons(comparisons, JPlagComparison::maximalSimilarity);
    }

}
