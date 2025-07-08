package de.jplag.highlight_extraction.frequencyDetermination;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.highlight_extraction.FrequencySimilarity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FSTest {

    @Test
    void testFrequencySimilarity_withRealisticMatchData() {
        Submission sub1 = mock(Submission.class);
        Submission sub2 = mock(Submission.class);
        when(sub1.getSimilarityDivisor()).thenReturn(100);
        when(sub2.getSimilarityDivisor()).thenReturn(100);

        Match match = mock(Match.class);
        when(match.getLengthOfFirst()).thenReturn(30);
        when(match.getLengthOfSecond()).thenReturn(30);
        when(match.getFrequencyWeight()).thenReturn(0.2);

        Match match2 = mock(Match.class);
        when(match2.getLengthOfFirst()).thenReturn(10);
        when(match2.getLengthOfSecond()).thenReturn(10);
        when(match2.getFrequencyWeight()).thenReturn(0.9);

        List<Match> matches = List.of(match, match2);
        JPlagComparison comparison = new JPlagComparison(sub1, sub2, matches, List.of());
        FrequencySimilarity fs = new FrequencySimilarity(List.of(comparison));

        double result = fs.frequencySimilarity(comparison, 0.5);
        double expected = (43.416666666666664 + 43.416666666666664) / 200.0;
        double roundedExpected = Math.round(expected * 100.0) / 100.0;
        double roundedResult = Math.round(result * 100.0) / 100.0;
        assertEquals(roundedExpected, roundedResult);
    }
    @Test
    void testFrequencySimilarity_withWeightZero_returnsStandardSimilarity() {
        Submission sub1 = mock(Submission.class);
        Submission sub2 = mock(Submission.class);
        when(sub1.getSimilarityDivisor()).thenReturn(100);
        when(sub2.getSimilarityDivisor()).thenReturn(100);

        Match match = mock(Match.class);
        when(match.getLengthOfFirst()).thenReturn(30);
        when(match.getLengthOfSecond()).thenReturn(30);
        when(match.getFrequencyWeight()).thenReturn(0.2);

        Match match2 = mock(Match.class);
        when(match2.getLengthOfFirst()).thenReturn(10);
        when(match2.getLengthOfSecond()).thenReturn(10);
        when(match2.getFrequencyWeight()).thenReturn(0.9);

        List<Match> matches = List.of(match, match2);
        JPlagComparison comparison = spy(new JPlagComparison(sub1, sub2, matches, List.of()));

        double expectedSimilarity = 0.12345;
        doReturn(expectedSimilarity).when(comparison).similarity();

        FrequencySimilarity fs = new FrequencySimilarity(List.of(comparison));

        double result = fs.frequencySimilarity(comparison, 0.0);

        assertEquals(expectedSimilarity, result, 1e-9);
    }

    @Test
    void testFrequencySimilarity_linearWeighting() {
        Submission sub1 = mock(Submission.class);
        Submission sub2 = mock(Submission.class);
        when(sub1.getSimilarityDivisor()).thenReturn(100);
        when(sub2.getSimilarityDivisor()).thenReturn(100);

        Match match = mock(Match.class);
        when(match.getLengthOfFirst()).thenReturn(30);
        when(match.getLengthOfSecond()).thenReturn(30);
        when(match.getFrequencyWeight()).thenReturn(0.2);

        Match match2 = mock(Match.class);
        when(match2.getLengthOfFirst()).thenReturn(10);
        when(match2.getLengthOfSecond()).thenReturn(10);
        when(match2.getFrequencyWeight()).thenReturn(0.9);

        List<Match> matches = List.of(match, match2);

        JPlagComparison comparison = new JPlagComparison(sub1, sub2, matches, List.of());

        FrequencySimilarity fs = new FrequencySimilarity(List.of(comparison));

        double sim0 = fs.frequencySimilarity(comparison, 0.0);
        double sim1 = fs.frequencySimilarity(comparison, 1.0);
        double sim05 = fs.frequencySimilarity(comparison, 0.5);
        double expectedSim05 = (sim0 + sim1) / 2.0;
        assertEquals(expectedSim05, sim05, 0.01);
    }


}
