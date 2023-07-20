package de.jplag.merging;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.TestBase;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.strategy.ComparisonStrategy;
import de.jplag.strategy.ParallelComparisonStrategy;

class MergingTest extends TestBase {
    private JPlagOptions options;
    private JPlagResult result;
    private List<Match> matches;
    private List<JPlagComparison> comparisonsBefore;
    private List<JPlagComparison> comparisonsAfter;

    MergingTest() throws ExitException {
        options = getDefaultOptions("merging").withMergingParameters(new MergingParameters(8, 2));

        GreedyStringTiling coreAlgorithm = new GreedyStringTiling(options);
        ComparisonStrategy comparisonStrategy = new ParallelComparisonStrategy(options, coreAlgorithm);

        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();

        new Altering(submissionSet, options).run();

        result = comparisonStrategy.compareSubmissions(submissionSet);
        comparisonsBefore = result.getAllComparisons();

        result = new MatchMerging(result, options).run();
        comparisonsAfter = result.getAllComparisons();
    }

    @Test
    @DisplayName("Test Lenght of Matches after Match Merging")
    void testBufferRemoval() {
        checkMatchLength(JPlagComparison::matches, options.minimumTokenMatch(), comparisonsAfter);
    }

    @Test
    @DisplayName("Test Lenght of Matches after Greedy String Tiling")
    void testGSTMatches() {
        checkMatchLength(JPlagComparison::matches, options.minimumTokenMatch(), comparisonsBefore);
    }

    @Test
    @DisplayName("Test Lenght of Ignored Matches after Greedy String Tiling")
    void testGSTIgnoredMatches() {
        int matchLengthThreshold = options.minimumTokenMatch() - options.mergingParameters().mergeBuffer();
        checkMatchLength(JPlagComparison::ignoredMatches, matchLengthThreshold, comparisonsBefore);
    }

    private void checkMatchLength(Function<JPlagComparison, List<Match>> matchFunction, int threshold, List<JPlagComparison> comparisons) {
        for (int i = 0; i < comparisons.size(); i++) {
            matches = matchFunction.apply(comparisons.get(i));
            for (int j = 0; j < matches.size(); j++) {
                assertTrue(matches.get(j).length() >= threshold);
            }
        }
    }

    @Test
    @DisplayName("Test if Similarity Increased after Match Merging")
    void testSimilarityIncreased() {
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            assertTrue(comparisonsAfter.get(i).similarity() >= comparisonsBefore.get(i).similarity());
        }
    }

    @Test
    @DisplayName("Test if Amount of Matches reduced after Match Merging")
    void testFewerMatches() {
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            assertTrue(comparisonsAfter.get(i).matches().size() + comparisonsAfter.get(i).ignoredMatches().size() <= comparisonsBefore.get(i)
                    .matches().size() + comparisonsBefore.get(i).ignoredMatches().size());
        }
    }

    @Test
    @DisplayName("Test if Amount of Token reduced after Match Merging")
    void testFewerToken() {
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            assertTrue(comparisonsAfter.get(i).firstSubmission().getTokenList().size() <= comparisonsBefore.get(i).firstSubmission().getTokenList()
                    .size()
                    && comparisonsAfter.get(i).secondSubmission().getTokenList().size() <= comparisonsBefore.get(i).secondSubmission().getTokenList()
                            .size());
        }
    }

    @Test
    @DisplayName("Test if Merged Matches have counterparts in the original Matches")
    void testCorrectMerges() {
        boolean correctMerges = true;
        for (int i = 0; i < comparisonsAfter.size(); i++) {
            matches = comparisonsAfter.get(i).matches();
            List<Match> sortedByFirst = new ArrayList<>(comparisonsBefore.get(i).matches());
            sortedByFirst.addAll(comparisonsBefore.get(i).ignoredMatches());
            Collections.sort(sortedByFirst, (m1, m2) -> m1.startOfFirst() - m2.startOfFirst());
            for (int j = 0; j < matches.size(); j++) {
                int begin = -1;
                for (int k = 0; k < sortedByFirst.size(); k++) {
                    if (sortedByFirst.get(k).startOfFirst() == matches.get(j).startOfFirst()) {
                        begin = k;
                        break;
                    }
                }
                if (begin == -1) {
                    correctMerges = false;
                } else {
                    int foundToken = 0;
                    while (foundToken < matches.get(j).length()) {
                        foundToken += sortedByFirst.get(begin).length();
                        begin++;
                        if (foundToken > matches.get(j).length()) {
                            correctMerges = false;
                        }
                    }
                }
            }
        }
        assertTrue(correctMerges);
    }
}