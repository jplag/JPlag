package de.jplag.merging;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;
import de.jplag.options.JPlagOptions;

/**
 * This class implements a match merging algorithm which serves as a defense mechanism against obfuscation attacks.
 * Based on configurable parameters MinimumNeighborLength and MaximumGapSize, it alters prior results from pairwise
 * submission comparisons and merges all neighboring matches that fit the specified thresholds. Submissions are referred
 * to as left and right and neighboring matches as upper and lower. When neighboring matches get merged they become one
 * and the tokens separating them get removed from the submission clone. MinimumNeighborLength describes how short a
 * match can be and MaximumGapSize describes how many tokens can be between two neighboring matches. Both are set in
 * {@link JPlagOptions} as {@link MergingOptions} and default to (2,6).
 */
public class MatchMerging {
    private final JPlagOptions options;

    /**
     * Instantiates the match merging algorithm for a comparison result and a set of specific options.
     * @param options encapsulates the adjustable options
     */
    public MatchMerging(JPlagOptions options) {
        this.options = options;
    }

    /**
     * Runs the internal match merging pipeline. It computes neighboring matches, merges them based on
     * {@link MergingOptions} and removes remaining too short matches afterwards.
     * @param result is the initially computed result object
     * @return JPlagResult containing the merged matches
     */
    public JPlagResult mergeMatchesOf(JPlagResult result) {
        long timeBeforeStartInMillis = System.currentTimeMillis();
        List<JPlagComparison> comparisons = new ArrayList<>(result.getAllComparisons());

        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.MATCH_MERGING, comparisons.size());
        List<JPlagComparison> comparisonsMerged = comparisons.parallelStream().map(it -> mergeMatchesOf(it, progressBar)).toList();
        progressBar.dispose();

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisonsMerged, result.getSubmissions(), result.getDuration() + durationInMillis, options);
    }

    private JPlagComparison mergeMatchesOf(JPlagComparison comparison, ProgressBar progressBar) {
        Submission leftSubmission = comparison.firstSubmission().copy();
        Submission rightSubmission = comparison.secondSubmission().copy();
        List<Match> globalMatches = new ArrayList<>(comparison.matches());
        globalMatches.addAll(comparison.ignoredMatches());

        int matchesBeforeMerging = globalMatches.size();
        mergeNeighbors(globalMatches, leftSubmission, rightSubmission);
        int matchesAfterMerging = globalMatches.size();
        globalMatches = globalMatches.stream().filter(it -> it.minimumLength() >= options.minimumTokenMatch()).toList();
        progressBar.step();

        if (matchesBeforeMerging - matchesAfterMerging >= options.mergingOptions().minimumRequiredMerges()) {
            return new JPlagComparison(leftSubmission, rightSubmission, globalMatches, new ArrayList<>());
        }
        return comparison;
    }

    /**
     * Computes neighbors by sorting based on order of matches in the left and right submissions and then checking which are
     * next to each other in both.
     * @param globalMatches is list of all matches.
     * @return neighbors containing a list of pairs of neighboring matches
     */
    private static List<Neighbor> computeNeighbors(List<Match> globalMatches) {
        List<Neighbor> neighbors = new ArrayList<>();
        List<Match> sortedByLeft = new ArrayList<>(globalMatches);
        List<Match> sortedByRight = new ArrayList<>(globalMatches);

        sortedByLeft.sort(Comparator.comparingInt(Match::startOfFirst));
        sortedByRight.sort(Comparator.comparingInt(Match::startOfSecond));

        for (int i = 0; i < sortedByLeft.size() - 1; i++) {

            if (sortedByRight.indexOf(sortedByLeft.get(i)) == sortedByRight.indexOf(sortedByLeft.get(i + 1)) - 1) {
                neighbors.add(new Neighbor(sortedByLeft.get(i), sortedByLeft.get(i + 1)));
            }
        }

        return neighbors;
    }

    /**
     * Updates the list of neighbors intelligently after merging two matches by updating surrounding neighbors.
     * @param neighbors the old list of neighbors.
     * @param previouslyMergedNeighbor the neighboring matches that were merged.
     * @param newMatch the resulting merged match.
     * @return the list of new neighbors (updated shallow copy of input).
     */
    private static List<Neighbor> updateNeighbors(List<Neighbor> neighbors, Neighbor previouslyMergedNeighbor, Match newMatch) {
        List<Neighbor> newNeighbors = new ArrayList<>();
        for (Neighbor neighbor : neighbors) {
            if (previouslyMergedNeighbor == neighbor) {
                continue; // Do not add merged neighbor.
            }
            if (neighbor.lowerMatch() == previouslyMergedNeighbor.upperMatch()) {
                newNeighbors.add(new Neighbor(neighbor.upperMatch(), newMatch)); // Update neighbor above new match.
            } else if (neighbor.upperMatch() == previouslyMergedNeighbor.lowerMatch()) {
                newNeighbors.add(new Neighbor(newMatch, neighbor.lowerMatch())); // Update neighbor below new match.
            } else {
                newNeighbors.add(neighbor); // Do not update neighbor.
            }
        }
        return newNeighbors;
    }

    /**
     * This function iterates through the neighboring matches and checks which fit the merging criteria. Those who do are
     * merged and the original matches are removed. This is done, until there are either no neighbors left, or none fit the
     * criteria.
     * @return globalMatches containing merged matches.
     */
    private void mergeNeighbors(List<Match> globalMatches, Submission leftSubmission, Submission rightSubmission) {
        int i = 0;
        List<Neighbor> neighbors = computeNeighbors(globalMatches);

        while (i < neighbors.size()) {
            Match upperMatch = neighbors.get(i).upperMatch();
            Match lowerMatch = neighbors.get(i).lowerMatch();

            int tokensBetweenLeft = lowerMatch.startOfFirst() - upperMatch.endOfFirst() - 1;
            int tokensBetweenRight = lowerMatch.startOfSecond() - upperMatch.endOfSecond() - 1;
            double averageTokensBetweenMatches = (tokensBetweenLeft + tokensBetweenRight) / 2.0;
            // Checking length is not necessary as GST already checked length while computing matches
            if (averageTokensBetweenMatches <= options.mergingOptions().maximumGapSize()
                    && !mergeOverlapsFiles(leftSubmission, rightSubmission, upperMatch, tokensBetweenLeft, tokensBetweenRight)) {
                globalMatches.remove(upperMatch);
                globalMatches.remove(lowerMatch);
                int leftLength = upperMatch.lengthOfFirst() + tokensBetweenLeft + lowerMatch.lengthOfFirst();
                int leftRight = upperMatch.lengthOfSecond() + tokensBetweenRight + lowerMatch.lengthOfSecond();
                Match mergedMatch = new Match(upperMatch.startOfFirst(), upperMatch.startOfSecond(), leftLength, leftRight);

                globalMatches.add(mergedMatch);
                neighbors = updateNeighbors(neighbors, neighbors.get(i), mergedMatch);
                i = 0; // reset loop
            } else {
                i++;
            }
        }
    }

    /**
     * This function checks if a merge would go over file boundaries.
     * @param leftSubmission is the left submission
     * @param rightSubmission is the right submission
     * @param upperMatch is the upper neighboring match
     * @param tokensBetweenLeft amount of token that separate the neighboring matches in the left submission and need to be
     * removed
     * @param tokensBetweenRight amount token that separate the neighboring matches in the send submission and need to be
     * removed
     * @return true if the merge goes over file boundaries.
     */
    private static boolean mergeOverlapsFiles(Submission leftSubmission, Submission rightSubmission, Match upperMatch, int tokensBetweenLeft,
            int tokensBetweenRight) {
        if (leftSubmission.getFiles().size() == 1 && rightSubmission.getFiles().size() == 1) {
            return false;
        }

        List<Token> tokenLeft = new ArrayList<>(leftSubmission.getTokenList());
        List<Token> tokenRight = new ArrayList<>(rightSubmission.getTokenList());

        tokenLeft = tokenLeft.subList(upperMatch.endOfFirst() + 1, upperMatch.endOfFirst() + tokensBetweenLeft + 1);
        tokenRight = tokenRight.subList(upperMatch.endOfSecond() + 1, upperMatch.endOfSecond() + tokensBetweenRight + 1);

        return containsFileEndToken(tokenLeft) || containsFileEndToken(tokenRight);
    }

    /**
     * This function checks whether a list of token contains FILE_END.
     * @param token is the list of token.
     * @return true if FILE_END is in token.
     */
    private static boolean containsFileEndToken(List<Token> token) {
        return token.stream().map(Token::getType).anyMatch(TokenType::isExcludedFromMatching);
    }
}