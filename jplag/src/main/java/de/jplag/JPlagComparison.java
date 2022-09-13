package de.jplag;

import java.util.Collections;
import java.util.List;

/**
 * This record represents the whole result of a comparison between two submissions.
 * @param firstSubmission is the first of the two submissions.
 * @param secondSubmission is the second of the two submissions.
 * @param matches is the unmodifiable list of all matches between the two submissions.
 */
public record JPlagComparison(Submission firstSubmission, Submission secondSubmission, List<Match> matches) {
    /**
     * Initializes a new comparison.
     * @param firstSubmission is the first of the two submissions.
     * @param secondSubmission is the second of the two submissions.
     * @param matches is the list of all matches between the two submissions.
     */
    public JPlagComparison(Submission firstSubmission, Submission secondSubmission, List<Match> matches) {
        this.firstSubmission = firstSubmission;
        this.secondSubmission = secondSubmission;
        this.matches = Collections.unmodifiableList(matches);
    }

    /**
     * Get the total number of matched tokens for this comparison.
     */
    public final int getNumberOfMatchedTokens() {
        return matches.stream().mapToInt(Match::length).sum();
    }

    /**
     * @return Maximum similarity in percent of both submissions.
     */
    public final double maximalSimilarity() {
        return Math.max(similarityOfFirst(), similarityOfSecond());
    }

    /**
     * @return Minimum similarity in percent of both submissions.
     */
    public final double minimalSimilarity() {
        return Math.min(similarityOfFirst(), similarityOfSecond());
    }

    /**
     * @return Similarity in percent (what percentage of tokens across both submissions are matched).
     */
    public final double similarity() {
        boolean subtractBaseCode = firstSubmission.hasBaseCodeMatches() && secondSubmission.hasBaseCodeMatches();
        int divisorA = firstSubmission.getSimilarityDivisor(subtractBaseCode);
        int divisorB = secondSubmission.getSimilarityDivisor(subtractBaseCode);
        return 2 * similarity(divisorA + divisorB);
    }

    /**
     * @return Similarity in percent for the first submission (what percent of the first submission is similar to the
     * second).
     */
    public final double similarityOfFirst() {
        int divisor = firstSubmission.getSimilarityDivisor(true);
        return similarity(divisor);
    }

    /**
     * @return Similarity in percent for the second submission (what percent of the second submission is similar to the
     * first).
     */
    public final double similarityOfSecond() {
        int divisor = secondSubmission.getSimilarityDivisor(true);
        return similarity(divisor);
    }

    @Override
    public String toString() {
        return firstSubmission.getName() + " <-> " + secondSubmission.getName();
    }

    private double similarity(int divisor) {
        return (divisor == 0 ? 0.0 : (getNumberOfMatchedTokens() * 100 / (double) divisor));
    }
}
