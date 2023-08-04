package de.jplag.merging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.Token;
import de.jplag.options.JPlagOptions;

/**
 * This class implements a match merging algorithm which serves as defense mechanism against obfuscation attacks. Based
 * on configurable parameters MergeBuffer and SeperatingThreshold, it alters prior results and merges all neighboring
 * matches that fit the specified thresholds. When neighboring matches get merged they become one and the tokens
 * separating them get removed from the submission clone. MergeBuffer describes how shorter a match can be than the
 * Minimum Token Match. SeperatingThreshold describes how many tokens can be between two neighboring matches. Both are
 * set in {@link JPlagOptions} as {@link MergingParameters} and default to 0 (which deactivates merging).
 */
public class MatchMerging {
    private Submission firstSubmission;
    private Submission secondSubmission;
    private List<Match> globalMatches;
    private List<List<Match>> neighbors;
    private JPlagResult result;
    private List<JPlagComparison> comparisons;
    private JPlagOptions options;

    /**
     * Instantiates the match merging algorithm for a comparison result and a set of specific options.
     * @param result is the initially computed result object
     * @param options encapsulates the adjustable options
     */
    public MatchMerging(JPlagResult result, JPlagOptions options) {
        this.result = result;
        this.comparisons = new ArrayList<>(result.getAllComparisons());
        this.options = options;
    }

    /**
     * Runs the internal match merging pipeline. It computes neighboring matches, merges them based on
     * {@link MergingParameters} and removes the merge buffer afterwards.
     * @return JPlagResult containing the merged matches
     */
    public JPlagResult run() {
        long timeBeforeStartInMillis = System.currentTimeMillis();
        for (int i = 0; i < comparisons.size(); i++) {
            firstSubmission = comparisons.get(i).firstSubmission().copy();
            secondSubmission = comparisons.get(i).secondSubmission().copy();
            globalMatches = new ArrayList<>(comparisons.get(i).matches());
            globalMatches.addAll(comparisons.get(i).ignoredMatches());
            computeNeighbors();
            mergeNeighbors();
            removeBuffer();
            comparisons.set(i, new JPlagComparison(firstSubmission, secondSubmission, globalMatches, new ArrayList<>()));

        }
        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisons, result.getSubmissions(), result.getDuration() + durationInMillis, options);
    }

    private void computeNeighbors() {
        neighbors = new ArrayList<>();
        List<Match> sortedByFirst = new ArrayList<>(globalMatches);
        Collections.sort(sortedByFirst, (m1, m2) -> m1.startOfFirst() - m2.startOfFirst());
        List<Match> sortedBySecond = new ArrayList<>(globalMatches);
        Collections.sort(sortedBySecond, (m1, m2) -> m1.startOfSecond() - m2.startOfSecond());
        for (int i = 0; i < sortedByFirst.size() - 1; i++) {
            if (sortedBySecond.indexOf(sortedByFirst.get(i)) == (sortedBySecond.indexOf(sortedByFirst.get(i + 1)) - 1)) {
                neighbors.add(Arrays.asList(sortedByFirst.get(i), sortedByFirst.get(i + 1)));
            }
        }
    }

    private void mergeNeighbors() {
        int i = 0;
        while (i < neighbors.size()) {
            int lengthUpper = neighbors.get(i).get(0).length();
            int lengthLower = neighbors.get(i).get(1).length();
            int seperatingFirst = neighbors.get(i).get(1).startOfFirst() - neighbors.get(i).get(0).endOfFirst() - 1;
            int seperatingSecond = neighbors.get(i).get(1).startOfSecond() - neighbors.get(i).get(0).endOfSecond() - 1;
            double seperating = (seperatingFirst + seperatingSecond) / 2.0;
            // Checking length is not necessary as GST already checked length while computing matches
            if (seperating <= options.mergingParameters().seperatingThreshold()) {
                globalMatches.removeAll(neighbors.get(i));
                globalMatches
                        .add(new Match(neighbors.get(i).get(0).startOfFirst(), neighbors.get(i).get(0).startOfSecond(), lengthUpper + lengthLower));
                removeToken(neighbors.get(i).get(0).startOfFirst(), neighbors.get(i).get(0).startOfSecond(), lengthUpper, seperatingFirst,
                        seperatingSecond);
                computeNeighbors();
                i = 0;
            } else {
                i++;
            }
        }
    }

    private void removeToken(int startFirst, int startSecond, int lengthUpper, int seperatingFirst, int seperatingSecond) {
        List<Token> tokenFirst = new ArrayList<>(firstSubmission.getTokenList());
        List<Token> tokenSecond = new ArrayList<>(secondSubmission.getTokenList());
        tokenFirst.subList(startFirst + lengthUpper, startFirst + lengthUpper + seperatingFirst).clear();
        tokenSecond.subList(startSecond + lengthUpper, startSecond + lengthUpper + seperatingSecond).clear();
        firstSubmission.setTokenList(tokenFirst);
        secondSubmission.setTokenList(tokenSecond);

        for (int i = 0; i < globalMatches.size(); i++) {
            if (globalMatches.get(i).startOfFirst() > startFirst) {
                Match alteredMatch = new Match(globalMatches.get(i).startOfFirst() - seperatingFirst, globalMatches.get(i).startOfSecond(),
                        globalMatches.get(i).length());
                globalMatches.set(i, alteredMatch);
            }
            if (globalMatches.get(i).startOfSecond() > startSecond) {
                Match alteredMatch = new Match(globalMatches.get(i).startOfFirst(), globalMatches.get(i).startOfSecond() - seperatingSecond,
                        globalMatches.get(i).length());
                globalMatches.set(i, alteredMatch);
            }
        }
    }

    private void removeBuffer() {
        List<Match> toRemove = new ArrayList<>();
        for (Match match : globalMatches) {
            if (match.length() < options.minimumTokenMatch()) {
                toRemove.add(match);
            }
        }
        globalMatches.removeAll(toRemove);
    }
}