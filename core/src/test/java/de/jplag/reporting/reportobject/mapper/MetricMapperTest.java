package de.jplag.reporting.reportobject.mapper;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.reportobject.model.TopComparison;

public class MetricMapperTest {
    private static final List<Integer> EXPECTED_DISTRIBUTION = List.of(29, 23, 19, 17, 13, 11, 7, 5, 3, 2);
    private final MetricMapper metricMapper = new MetricMapper(Submission::getName);

    @Test
    public void test_getAverageMetric() {
        // given
        JPlagResult jPlagResult = createJPlagResult(MockMetric.AVG, distribution(EXPECTED_DISTRIBUTION),
                comparison(submission("1"), submission("2"), .7), comparison(submission("3"), submission("4"), .3));
        // when
        var result = metricMapper.getAverageMetric(jPlagResult);

        // then
        Assertions.assertEquals("AVG", result.name());
        Assertions.assertIterableEquals(EXPECTED_DISTRIBUTION, result.distribution());
        Assertions.assertEquals(List.of(new TopComparison("1", "2", .7), new TopComparison("3", "4", .3)), result.topComparisons());
        Assertions.assertEquals(
                "Average of both program coverages. This is the default similarity which"
                        + " works in most cases: Matches with a high average similarity indicate that the programs work " + "in a very similar way.",
                result.description());
    }

    @Test
    public void test_getMaxMetric() {
        // given
        JPlagResult jPlagResult = createJPlagResult(MockMetric.MAX, distribution(EXPECTED_DISTRIBUTION),
                comparison(submission("00"), submission("01"), .7), comparison(submission("10"), submission("11"), .3));
        // when
        var result = metricMapper.getMaxMetric(jPlagResult);

        // then
        Assertions.assertEquals("MAX", result.name());
        Assertions.assertIterableEquals(EXPECTED_DISTRIBUTION, result.distribution());
        Assertions.assertEquals(List.of(new TopComparison("00", "01", .7), new TopComparison("10", "11", .3)), result.topComparisons());
        Assertions.assertEquals(
                "Maximum of both program coverages. This ranking is especially useful if the programs are very "
                        + "different in size. This can happen when dead code was inserted to disguise the origin of the plagiarized program.",
                result.description());
    }

    private int[] distribution(List<Integer> expectedDistribution) {
        var reversedDistribution = new ArrayList<>(expectedDistribution);
        Collections.reverse(reversedDistribution);
        return reversedDistribution.stream().mapToInt(Integer::intValue).toArray();
    }

    private CreateSubmission submission(String name) {
        return new CreateSubmission(name);
    }

    private Comparison comparison(CreateSubmission submission1, CreateSubmission submission2, double similarity) {
        return new Comparison(submission1, submission2, similarity);
    }

    private JPlagResult createJPlagResult(MockMetric metricToMock, int[] distribution, Comparison... createComparisonsDto) {
        JPlagResult jPlagResult = mock(JPlagResult.class);

        if (metricToMock.equals(MockMetric.AVG)) {
            doReturn(distribution).when(jPlagResult).getSimilarityDistribution();
        } else if (metricToMock.equals(MockMetric.MAX)) {
            doReturn(distribution).when(jPlagResult).getMaxSimilarityDistribution();

        }

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
            if (metricToMock.equals(MockMetric.AVG)) {
                doReturn(comparisonDto.similarity).when(mockedComparison).similarity();
            } else if (metricToMock.equals(MockMetric.MAX)) {
                doReturn(comparisonDto.similarity).when(mockedComparison).maximalSimilarity();
            }
            comparisonList.add(mockedComparison);
        }

        doReturn(comparisonList).when(jPlagResult).getComparisons(anyInt());
        return jPlagResult;
    }

    private enum MockMetric {
        MAX,
        AVG
    }

    private record Comparison(CreateSubmission submission1, CreateSubmission submission2, double similarity) {
    }

    private record CreateSubmission(String name) {
    }

}
