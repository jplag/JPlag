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
     * {@link MergingParameters} and removes remaining too short matches afterwards.
     * @return JPlagResult containing the merged matches
     */
    public JPlagResult run() {
        long timeBeforeStartInMillis = System.currentTimeMillis();
        for (int i = 0; i < comparisons.size(); i++) {
            firstSubmission = comparisons.get(i).firstSubmission().copy();
            secondSubmission = comparisons.get(i).secondSubmission().copy();
            List<Match> globalMatches = new ArrayList<>(comparisons.get(i).matches());
            globalMatches.addAll(comparisons.get(i).ignoredMatches());
            globalMatches = removeTooShortMatches(mergeNeighbors(globalMatches));
            comparisons.set(i, new JPlagComparison(firstSubmission, secondSubmission, globalMatches, new ArrayList<>()));

        }
        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisons, result.getSubmissions(), result.getDuration() + durationInMillis, options);
    }

    /**
     * Computes neighbors by sorting based on order of matches in the first and the second submission and then checking
     * which are next to each other in both.
     * @param globalMatches
     * @return neighbors containing a list of pairs of neighboring matches
     */
    private List<List<Match>> computeNeighbors(List<Match> globalMatches) {
        List<List<Match>> neighbors = new ArrayList<>();
        List<Match> sortedByFirst = new ArrayList<>(globalMatches);
        Collections.sort(sortedByFirst, (m1, m2) -> m1.startOfFirst() - m2.startOfFirst());
        List<Match> sortedBySecond = new ArrayList<>(globalMatches);
        Collections.sort(sortedBySecond, (m1, m2) -> m1.startOfSecond() - m2.startOfSecond());
        for (int i = 0; i < sortedByFirst.size() - 1; i++) {
            if (sortedBySecond.indexOf(sortedByFirst.get(i)) == (sortedBySecond.indexOf(sortedByFirst.get(i + 1)) - 1)) {
                neighbors.add(Arrays.asList(sortedByFirst.get(i), sortedByFirst.get(i + 1)));
            }
        }
        return neighbors;
    }

    /**
     * This function iterates through the neighboring matches and checks which fit the merging criteria. Those who do are
     * merged and the original matches are removed. This is done, until there are either no neighbors left, or none fit the
     * criteria
     * @return globalMatches containing merged matches.
     */
    private List<Match> mergeNeighbors(List<Match> globalMatches) {
        int i = 0;
        List<List<Match>> neighbors = computeNeighbors(globalMatches);
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
                removeToken(globalMatches, neighbors.get(i).get(0).startOfFirst(), neighbors.get(i).get(0).startOfSecond(), lengthUpper,
                        seperatingFirst, seperatingSecond);
                neighbors = computeNeighbors(globalMatches);
                i = 0;
            } else {
                i++;
            }
        }
        return globalMatches;
    }

    /**
     * This function removes token from both submissions after a merge has been performed. Additionally it moves the
     * starting positions from matches, that occur after the merged neighboring matches, by the amount of removed token.
     * @param globalMatches
     * @param startFirst begin of the upper neighbor in the first submission
     * @param startSecond begin of the upper neighbor in the second submission
     * @param lengthUpper length of the upper neighbor
     * @param seperatingFirst amount of token that separate the neighboring matches in the first submission and need to be
     * removed
     * @param seperatingSecond amount token that separate the neighboring matches in the send submission and need to be
     * removed
     * @return globalMatches with the mentioned changes.
     */
    private List<Match> removeToken(List<Match> globalMatches, int startFirst, int startSecond, int lengthUpper, int seperatingFirst,
            int seperatingSecond) {
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
        return globalMatches;
    }

    /**
     * This method marks the end of the merging pipeline and removes the remaining too short matches from
     * @param globalMatches
     */
    private List<Match> removeTooShortMatches(List<Match> globalMatches) {
        List<Match> toRemove = new ArrayList<>();
        for (Match match : globalMatches) {
            if (match.length() < options.minimumTokenMatch()) {
                toRemove.add(match);
            }
        }
        globalMatches.removeAll(toRemove);
        return globalMatches;
    }
}