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
public record JPlagComparison(Submission firstSubmission, Submission secondSubmission, List<Match> matches, List<Match> ignoredMatches,
        List<Match> commentMatches) {
    /**
     * Constructs a new comparison between two submissions. The match lists are wrapped as unmodifiable to preserve
     * immutability.
     * @param firstSubmission is the first of the two submissions.
     * @param secondSubmission is the second of the two submissions.
     * @param matches is the list of all matches between the two submissions.
     */
    public JPlagComparison(Submission firstSubmission, Submission secondSubmission, List<Match> matches, List<Match> ignoredMatches,
            List<Match> commentMatches) {
        this.firstSubmission = firstSubmission;
        this.secondSubmission = secondSubmission;
        this.matches = Collections.unmodifiableList(matches);
        this.ignoredMatches = Collections.unmodifiableList(ignoredMatches);
        this.commentMatches = Collections.unmodifiableList(commentMatches);
    }

    public JPlagComparison(Submission firstSubmission, Submission secondSubmission, List<Match> matches, List<Match> ignoredMatches) {
        this(firstSubmission, secondSubmission, matches, ignoredMatches, Collections.emptyList());
    }

    /**
     * Get the total number of matched tokens for this comparison, which is the sum of the lengths of all subsequence
     * matches. This excludes ignored matches.
     */
    public int getNumberOfMatchedTokens() {
        return matches.stream().mapToInt(Match::length).sum();
    }

    public int getNumberOfMatchedCommentTokens() {
        return commentMatches.stream().mapToInt(Match::length).sum();
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
     */
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
        int divisorA = this.getSimilarityDivisor(firstSubmission);
        int divisorB = this.getSimilarityDivisor(secondSubmission);
        return 2 * similarity(divisorA + divisorB);
    }

    /**
     * @return Similarity of the first submission to the second in interval [0, 1]. The similarity is adjusted based on
     * whether both submissions contain base code matches. 0 means zero percent structural similarity, 1 means 100 percent
     * structural similarity.
     */
    public final double similarityOfFirst() {
        int divisor = this.getSimilarityDivisor(firstSubmission);
        return similarity(divisor);
    }

    /**
     * @return Similarity of the second submission to the first in interval [0, 1]. The similarity is adjusted based on
     * whether both submissions contain base code matches. 0 means zero percent structural similarity, 1 means 100 percent
     * structural similarity.
     */
    public final double similarityOfSecond() {
        int divisor = this.getSimilarityDivisor(secondSubmission);
        return similarity(divisor);
    }

    @Override
    public String toString() {
        return firstSubmission.getName() + " <-> " + secondSubmission.getName();
    }

    private double similarity(int divisor) {
        return divisor == 0 ? 0.0 : (getNumberOfMatchedTokens() + getNumberOfMatchedCommentTokens()) / (double) divisor;
    }

    private int getSimilarityDivisor(Submission submission) {
        return submission.getSimilarityDivisor() + this.getNumberOfMatchedCommentTokens();
    }
}
