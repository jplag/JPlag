package de.jplag.merging;

import java.util.ArrayList;
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
 * on configurable parameters MergeBuffer and SeperatingThreshold, it alters prior results from pairwise submission
 * comparisons and merges all neighboring matches that fit the specified thresholds. Submissions are referred to as left
 * and right and neighboring matches as upper and lower. When neighboring matches get merged they become one and the
 * tokens separating them get removed from the submission clone. MergeBuffer describes how shorter a match can be than
 * the Minimum Token Match. SeperatingThreshold describes how many tokens can be between two neighboring matches. Both
 * are set in {@link JPlagOptions} as {@link MergingParameters} and default to 0 (which deactivates merging).
 */
public class MatchMerging {
    private JPlagOptions options;

    /**
     * Instantiates the match merging algorithm for a comparison result and a set of specific options.
     * @param options encapsulates the adjustable options
     */
    public MatchMerging(JPlagOptions options) {
        this.options = options;
    }

    /**
     * Runs the internal match merging pipeline. It computes neighboring matches, merges them based on
     * {@link MergingParameters} and removes remaining too short matches afterwards.
     * @param result is the initially computed result object
     * @return JPlagResult containing the merged matches
     */
    public JPlagResult mergeMatchesOf(JPlagResult result) {
        long timeBeforeStartInMillis = System.currentTimeMillis();

        List<JPlagComparison> comparisons = new ArrayList<>(result.getAllComparisons());
        List<JPlagComparison> comparisonsMerged = new ArrayList<>();

        for (JPlagComparison comparison : comparisons) {
            Submission leftSubmission = comparison.firstSubmission().copy();
            Submission rightSubmission = comparison.secondSubmission().copy();
            List<Match> globalMatches = new ArrayList<>(comparison.matches());
            globalMatches.addAll(comparison.ignoredMatches());
            globalMatches = removeTooShortMatches(mergeNeighbors(globalMatches, leftSubmission, rightSubmission));
            comparisonsMerged.add(new JPlagComparison(leftSubmission, rightSubmission, globalMatches, new ArrayList<>()));
        }

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisonsMerged, result.getSubmissions(), result.getDuration() + durationInMillis, options);
    }

    /**
     * Computes neighbors by sorting based on order of matches in the left and right submissions and then checking which are
     * next to each other in both.
     * @param globalMatches
     * @return neighbors containing a list of pairs of neighboring matches
     */
    private List<Neighbor> computeNeighbors(List<Match> globalMatches) {
        List<Neighbor> neighbors = new ArrayList<>();
        List<Match> sortedByLeft = new ArrayList<>(globalMatches);
        Collections.sort(sortedByLeft, (match1, match2) -> match1.startOfFirst() - match2.startOfFirst());
        List<Match> sortedByRight = new ArrayList<>(globalMatches);
        Collections.sort(sortedByRight, (match1, match2) -> match1.startOfSecond() - match2.startOfSecond());
        for (int i = 0; i < sortedByLeft.size() - 1; i++) {
            if (sortedByRight.indexOf(sortedByLeft.get(i)) == (sortedByRight.indexOf(sortedByLeft.get(i + 1)) - 1)) {
                neighbors.add(new Neighbor(sortedByLeft.get(i), sortedByLeft.get(i + 1)));
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
    private List<Match> mergeNeighbors(List<Match> globalMatches, Submission leftSubmission, Submission rightSubmission) {
        int i = 0;
        List<Neighbor> neighbors = computeNeighbors(globalMatches);

        while (i < neighbors.size()) {
            Match upperNeighbor = neighbors.get(i).upperMatch();
            Match lowerNeighbor = neighbors.get(i).lowerMatch();

            int lengthUpper = upperNeighbor.length();
            int lengthLower = lowerNeighbor.length();
            int tokenBetweenLeft = lowerNeighbor.startOfFirst() - upperNeighbor.endOfFirst() - 1;
            int tokensBetweenRight = lowerNeighbor.startOfSecond() - upperNeighbor.endOfSecond() - 1;
            double averageTokensBetweenMatches = (tokenBetweenLeft + tokensBetweenRight) / 2.0;
            // Checking length is not necessary as GST already checked length while computing matches
            if (averageTokensBetweenMatches <= options.mergingParameters().seperatingThreshold()) {
                globalMatches.remove(upperNeighbor);
                globalMatches.remove(lowerNeighbor);
                globalMatches.add(new Match(upperNeighbor.startOfFirst(), upperNeighbor.startOfSecond(), lengthUpper + lengthLower));
                globalMatches = removeToken(globalMatches, leftSubmission, rightSubmission, upperNeighbor, tokenBetweenLeft, tokensBetweenRight);
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
     * @param leftSubmission is the left submission
     * @param rightSubmission is the right submission
     * @param upperNeighbor is the upper neighboring match
     * @param tokensBetweenLeft amount of token that separate the neighboring matches in the left submission and need to be
     * removed
     * @param tokensBetweenRight amount token that separate the neighboring matches in the send submission and need to be
     * removed
     * @return shiftedMatches with the mentioned changes.
     */
    private List<Match> removeToken(List<Match> globalMatches, Submission leftSubmission, Submission rightSubmission, Match upperNeighbor,
            int tokensBetweenLeft, int tokensBetweenRight) {
        int startLeft = upperNeighbor.startOfFirst();
        int startRight = upperNeighbor.startOfSecond();
        int lengthUpper = upperNeighbor.length();

        List<Token> tokenLeft = new ArrayList<>(leftSubmission.getTokenList());
        List<Token> tokenRight = new ArrayList<>(rightSubmission.getTokenList());
        tokenLeft.subList(startLeft + lengthUpper, startLeft + lengthUpper + tokensBetweenLeft).clear();
        tokenRight.subList(startRight + lengthUpper, startRight + lengthUpper + tokensBetweenRight).clear();
        leftSubmission.setTokenList(tokenLeft);
        rightSubmission.setTokenList(tokenRight);

        List<Match> shiftedMatches = new ArrayList<>();
        for (Match match : globalMatches) {
            int leftShift = match.startOfFirst() > startLeft ? tokensBetweenLeft : 0;
            int rightShift = match.startOfSecond() > startRight ? tokensBetweenRight : 0;
            Match alteredMatch = new Match(match.startOfFirst() - leftShift, match.startOfSecond() - rightShift, match.length());
            shiftedMatches.add(alteredMatch);
        }

        return shiftedMatches;
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