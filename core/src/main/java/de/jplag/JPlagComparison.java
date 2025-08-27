package de.jplag;

import java.util.Collections;
import java.util.List;

/**
 * This record represents results of a structural comparison between two submissions.
 * @param firstSubmission is the first of the two submissions.
 * @param secondSubmission is the second of the two submissions.
 * @param matches is the unmodifiable list of all subsequence matches between the two submissions.
 * @param ignoredMatches is the unmodifiable list of ignored matches whose length is below the minimum token match
 * threshold.
 */
public record JPlagComparison(Submission firstSubmission, Submission secondSubmission, List<Match> matches, List<Match> ignoredMatches) {

    /**
     * Constructs a new comparison between two submissions. The match lists are wrapped as unmodifiable to preserve
     * immutability.
     * @param firstSubmission is the first of the two submissions.
     * @param secondSubmission is the second of the two submissions.
     * @param matches is the list of all matches between the two submissions.
     */
    public JPlagComparison(Submission firstSubmission, Submission secondSubmission, List<Match> matches, List<Match> ignoredMatches) {
        this.firstSubmission = firstSubmission;
        this.secondSubmission = secondSubmission;
        this.matches = Collections.unmodifiableList(matches);
        this.ignoredMatches = Collections.unmodifiableList(ignoredMatches);
    }

    private static double frequencyWeightedScore = -1;
    private static boolean frequency = false;

    public static void setFrequency(boolean frequency) {
        JPlagComparison.frequency = frequency;
    }

    public void setFrequencyWeightedScore(double score) {
        frequencyWeightedScore = score;
    }

    /**
     * Get the total number of matched tokens for this comparison, which is the sum of the minimum lengths of all
     * subsequence matches. This excludes ignored matches.
     */
    public int getNumberOfMatchedTokens() {
        return matches.stream().mapToInt(Match::minimumLength).sum();
    }

    /**
     * Returns the maximum similarity score, which is either the similarity of the first submission to the second or vice
     * versa. The similarity is adjusted based on whether both submissions contain base code matches.
     * @return Maximum similarity in interval [0, 1]. 0 means zero percent structural similarity, 1 means 100 percent
     * structural similarity.
     */
    public final double maximalSimilarity() {
        return Math.max(similarityOfFirst(), similarityOfSecond());
    }

    /**
     * Returns the minimum similarity score, which is either the similarity of the first submission to the second or vice
     * versa.
     * @return Minimum similarity in interval [0, 1]. 0 means zero percent structural similarity, 1 means 100 percent
     * structural similarity.
     * @deprecated Metric was not used in JPlag
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    public final double minimalSimilarity() {
        return Math.min(similarityOfFirst(), similarityOfSecond());
    }

    /**
     * Computes the average (or symmetric) similarity between the two submissions. The similarity is adjusted based on
     * whether both submissions contain base code matches.
     * @return Average similarity in interval [0, 1]. 0 means zero percent structural similarity, 1 means 100 percent
     * structural similarity.
     */
    public final double similarity() {
        if (frequency && frequencyWeightedScore >= 0) {
            return frequencyWeightedScore;
        }
        int divisor = firstSubmission.getSimilarityDivisor() + secondSubmission.getSimilarityDivisor();
        if (divisor == 0) {
            return 0;
        }
        int matchedTokensOfFirst = matches.stream().mapToInt(Match::getLengthOfFirst).sum();
        int matchedTokensOfSecond = matches.stream().mapToInt(Match::getLengthOfSecond).sum();
        return (matchedTokensOfFirst + matchedTokensOfSecond) / (double) divisor;
    }

    /**
     * @return Similarity of the first submission to the second in interval [0, 1]. The similarity is adjusted based on
     * whether both submissions contain base code matches. 0 means zero percent structural similarity, 1 means 100 percent
     * structural similarity.
     */
    public final double similarityOfFirst() {
        int divisor = firstSubmission.getSimilarityDivisor();
        int matchedTokens = matches.stream().mapToInt(Match::getLengthOfFirst).sum();
        return divisor == 0 ? 0.0 : matchedTokens / (double) divisor;
    }

    /**
     * @return Similarity of the second submission to the first in interval [0, 1]. The similarity is adjusted based on
     * whether both submissions contain base code matches. 0 means zero percent structural similarity, 1 means 100 percent
     * structural similarity.
     */
    public final double similarityOfSecond() {
        int divisor = secondSubmission.getSimilarityDivisor();
        int matchedTokens = matches.stream().mapToInt(Match::getLengthOfSecond).sum();
        return divisor == 0 ? 0.0 : matchedTokens / (double) divisor;
    }

    @Override
    public String toString() {
        return firstSubmission.getName() + " <-> " + secondSubmission.getName();
    }

}
