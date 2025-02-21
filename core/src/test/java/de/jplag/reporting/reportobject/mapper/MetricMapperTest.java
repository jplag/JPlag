package de.jplag.reporting.reportobject.mapper;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.options.JPlagOptions;
import de.jplag.options.SimilarityMetric;
import de.jplag.reporting.reportobject.model.TopComparison;

public class MetricMapperTest {
    private static final List<Integer> EXPECTED_AVG_DISTRIBUTION = List.of(1, 0, 0, 2, 3, 15, 5, 2, 16, 5, 2, 18, 3, 21, 2, 1, 5, 0, 14, 32, 25, 4, 2,
            12, 3, 2, 5, 5, 0, 5, 1, 5, 2, 5, 4, 5, 3, 5, 18, 21, 30, 4, 3, 10, 2, 3, 17, 28, 4, 10, 2, 4, 3, 0, 2, 20, 4, 0, 19, 5, 25, 9, 4, 18, 1,
            1, 1, 0, 31, 15, 35, 38, 40, 43, 45, 49, 50, 50, 50, 53, 60, 71, 73, 74, 80, 83, 87, 93, 95, 99, 102, 105, 106, 110, 113, 113, 117, 117,
            122, 124);
    private static final List<Integer> EXPECTED_MAX_DISTRIBUTION = List.of(130, 129, 124, 116, 114, 110, 110, 108, 103, 101, 99, 97, 96, 92, 82, 81,
            70, 67, 64, 63, 59, 56, 52, 50, 50, 50, 49, 47, 43, 5, 6, 11, 4, 2, 3, 20, 37, 5, 0, 2, 33, 30, 19, 4, 5, 24, 40, 6, 3, 9, 2, 3, 18, 3, 5,
            1, 4, 1, 0, 0, 5, 5, 14, 5, 42, 4, 18, 0, 0, 10, 4, 3, 17, 33, 4, 4, 3, 4, 39, 0, 20, 2, 4, 9, 0, 5, 0, 8, 23, 4, 2, 39, 3, 4, 1, 0, 3,
            33, 2, 1);
    private final MetricMapper metricMapper = new MetricMapper(Submission::getName);

    @Test
    public void test_getDistributions() {
        // given
        JPlagResult jPlagResult = createJPlagResult(distribution(EXPECTED_AVG_DISTRIBUTION), distribution(EXPECTED_MAX_DISTRIBUTION),
                comparison(submission("1"), submission("2"), .7, .8), comparison(submission("3"), submission("4"), .3, .9));

        // when
        Map<String, List<Integer>> result = MetricMapper.getDistributions(jPlagResult);

        // then
        Assertions.assertEquals(EXPECTED_AVG_DISTRIBUTION, result.get("AVG"));
        Assertions.assertEquals(EXPECTED_MAX_DISTRIBUTION, result.get("MAX"));
    }

    @Test
    public void test_getTopComparisons() {
        // given
        JPlagResult jPlagResult = createJPlagResult(distribution(EXPECTED_AVG_DISTRIBUTION), distribution(EXPECTED_MAX_DISTRIBUTION),
                comparison(submission("1", 22), submission("2", 30), .7, .8, .5, .5, new int[] {9, 3, 1}),
                comparison(submission("3", 202), submission("4", 134), .3, .9, .01, .25, new int[] {1, 15, 23, 3}));

        // when
        List<TopComparison> result = metricMapper.getTopComparisons(jPlagResult);

        // then
        Assertions.assertEquals(List.of(
                new TopComparison("1", "2",
                        Map.of("AVG", .7, "MAX", .8, "MIN", .5, "LONGEST_MATCH", 9.0, "INTERSECTION", 13.0, "OVERALL", 52.0)),
                new TopComparison("3", "4",
                        Map.of("AVG", .3, "MAX", .9, "MIN", .01, "LONGEST_MATCH", 23.0, "INTERSECTION", 42.0, "OVERALL", 336.0))),
                result);
    }

    private int[] distribution(List<Integer> expectedDistribution) {
        var distribution = new ArrayList<>(expectedDistribution);
        return distribution.stream().mapToInt(Integer::intValue).toArray();
    }

    private CreateSubmission submission(String name, int tokenCount) {
        return new CreateSubmission(name, tokenCount);
    }

    private CreateSubmission submission(String name) {
        return submission(name, 0);
    }

    private Comparison comparison(CreateSubmission submission1, CreateSubmission submission2, double similarity, double maxSimilarity,
            double minSimilarity, double symSimilarity, int[] matchLengths) {
        return new Comparison(submission1, submission2, similarity, maxSimilarity, minSimilarity, symSimilarity, matchLengths);
    }

    private Comparison comparison(CreateSubmission submission1, CreateSubmission submission2, double similarity, double maxSimilarity) {
        return comparison(submission1, submission2, similarity, maxSimilarity, 0, 0, new int[0]);
    }

    private JPlagResult createJPlagResult(int[] avgDistribution, int[] maxDistribution, Comparison... createComparisonsDto) {
        JPlagResult jPlagResult = mock(JPlagResult.class);
        doReturn(Arrays.stream(avgDistribution).boxed().toList()).when(jPlagResult).calculateDistributionFor(SimilarityMetric.AVG);
        doReturn(Arrays.stream(maxDistribution).boxed().toList()).when(jPlagResult).calculateDistributionFor(SimilarityMetric.MAX);

        JPlagOptions options = mock(JPlagOptions.class);
        doReturn(createComparisonsDto.length).when(options).maximumNumberOfComparisons();
        doReturn(options).when(jPlagResult).getOptions();

        List<JPlagComparison> comparisonList = new ArrayList<>();
        for (Comparison comparisonDto : createComparisonsDto) {
            Submission submission1 = mock(Submission.class);
            doReturn(comparisonDto.submission1.name).when(submission1).getName();
            doReturn(comparisonDto.submission1.tokenCount).when(submission1).getNumberOfTokens();
            Submission submission2 = mock(Submission.class);
            doReturn(comparisonDto.submission2.name).when(submission2).getName();
            doReturn(comparisonDto.submission2.tokenCount).when(submission2).getNumberOfTokens();

            JPlagComparison mockedComparison = mock(JPlagComparison.class);
            doReturn(submission1).when(mockedComparison).firstSubmission();
            doReturn(submission2).when(mockedComparison).secondSubmission();
            doReturn(comparisonDto.similarity).when(mockedComparison).similarity();
            doReturn(comparisonDto.maxSimilarity).when(mockedComparison).maximalSimilarity();
            doReturn(comparisonDto.minSimilarity).when(mockedComparison).minimalSimilarity();
            doReturn(comparisonDto.symSimilarity).when(mockedComparison).symmetricSimilarity();
            List<Match> matches = createMockMatchList(comparisonDto.matchLengths);
            doReturn(matches).when(mockedComparison).matches();
            doReturn(matches.stream().mapToInt(Match::length).sum()).when(mockedComparison).getNumberOfMatchedTokens();
            comparisonList.add(mockedComparison);
        }

        doReturn(comparisonList).when(jPlagResult).getComparisons(anyInt());
        return jPlagResult;
    }

    private List<Match> createMockMatchList(int[] matchLengths) {
        List<Match> matches = new ArrayList<>();
        for (int l : matchLengths) {
            Match m = mock(Match.class);
            doReturn(l).when(m).length();
            matches.add(m);
        }
        return matches;
    }

    private record Comparison(CreateSubmission submission1, CreateSubmission submission2, double similarity, double maxSimilarity,
            double minSimilarity, double symSimilarity, int[] matchLengths) {
    }

    private record CreateSubmission(String name, int tokenCount) {
    }

}