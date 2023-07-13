package de.jplag.reporting.reportobject.mapper;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.reportobject.model.TopComparison;

public class MetricMapperTest {
    private static final List<Integer> EXPECTED_AVG_DISTRIBUTION = List.of(29, 23, 19, 17, 13, 11, 7, 5, 3, 2);
    private static final List<Integer> EXPECTED_MAX_DISTRIBUTION = List.of(50, 48, 20, 13, 10, 1, 3, 1, 0, 0);
    private final MetricMapper metricMapper = new MetricMapper(Submission::getName);

    @Test
    public void test_getDistributions() {
        // given
        JPlagResult jPlagResult = createJPlagResult(distribution(EXPECTED_AVG_DISTRIBUTION), distribution(EXPECTED_MAX_DISTRIBUTION),
                comparison(submission("1"), submission("2"), .7, .8), comparison(submission("3"), submission("4"), .3, .9));

        // when
        Map<String, List<Integer>> result = MetricMapper.getDistributions(jPlagResult);

        // then
        Assertions.assertEquals(Map.of("AVG", EXPECTED_AVG_DISTRIBUTION, "MAX", EXPECTED_MAX_DISTRIBUTION), result);
    }

    @Test
    public void test_getTopComparisons() {
        // given
        JPlagResult jPlagResult = createJPlagResult(distribution(EXPECTED_AVG_DISTRIBUTION), distribution(EXPECTED_MAX_DISTRIBUTION),
                comparison(submission("1"), submission("2"), .7, .8), comparison(submission("3"), submission("4"), .3, .9));

        // when
        List<TopComparison> result = metricMapper.getTopComparisons(jPlagResult);

        // then
        Assertions.assertEquals(
                List.of(new TopComparison("1", "2", Map.of("AVG", .7, "MAX", .8)), new TopComparison("3", "4", Map.of("AVG", .3, "MAX", .9))),
                result);
    }

    private int[] distribution(List<Integer> expectedDistribution) {
        var reversedDistribution = new ArrayList<>(expectedDistribution);
        Collections.reverse(reversedDistribution);
        return reversedDistribution.stream().mapToInt(Integer::intValue).toArray();
    }

    private CreateSubmission submission(String name) {
        return new CreateSubmission(name);
    }

    private Comparison comparison(CreateSubmission submission1, CreateSubmission submission2, double similarity, double maxSimilarity) {
        return new Comparison(submission1, submission2, similarity, maxSimilarity);
    }

    private JPlagResult createJPlagResult(int[] avgDistribution, int[] maxDistribution, Comparison... createComparisonsDto) {
        JPlagResult jPlagResult = mock(JPlagResult.class);
        doReturn(avgDistribution).when(jPlagResult).getSimilarityDistribution();
        doReturn(maxDistribution).when(jPlagResult).getMaxSimilarityDistribution();

        JPlagOptions options = mock(JPlagOptions.class);
        doReturn(createComparisonsDto.length).when(options).maximumNumberOfComparisons();
        doReturn(options).when(jPlagResult).getOptions();

        List<JPlagComparison> comparisonList = new ArrayList<>();
        for (Comparison comparisonDto : createComparisonsDto) {
            Submission submission1 = mock(Submission.class);
            doReturn(comparisonDto.submission1.name).when(submission1).getName();
            Submission submission2 = mock(Submission.class);
            doReturn(comparisonDto.submission2.name).when(submission2).getName();

            JPlagComparison mockedComparison = mock(JPlagComparison.class);
            doReturn(submission1).when(mockedComparison).firstSubmission();
            doReturn(submission2).when(mockedComparison).secondSubmission();
            doReturn(comparisonDto.similarity).when(mockedComparison).similarity();
            doReturn(comparisonDto.maxSimilarity).when(mockedComparison).maximalSimilarity();
            comparisonList.add(mockedComparison);
        }

        doReturn(comparisonList).when(jPlagResult).getComparisons(anyInt());
        return jPlagResult;
    }

    private record Comparison(CreateSubmission submission1, CreateSubmission submission2, double similarity, double maxSimilarity) {
    }

    private record CreateSubmission(String name) {
    }

}