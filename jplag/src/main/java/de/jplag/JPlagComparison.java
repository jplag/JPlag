package de.jplag;

import java.util.List;

/**
 * This record represents the whole result of a comparison between two submissions.
 * @param firstSubmission is the first of the two submissions.
 * @param secondSubmission is the second of the two submissions.
 * @param matches is the list of all matches between the two submissions.
 */
public record JPlagComparison(Submission firstSubmission, Submission secondSubmission, List<Match> matches) {

    /**
     * Get the total number of matched tokens for this comparison.
     */
    public final int getNumberOfMatchedTokens() {
        return matches.stream().mapToInt(Match::length).sum();
    }

    /**
     * @return Maximum similarity in percent of both submissions.
     */
    public final float maximalSimilarity() {
        return Math.max(similarityOfFirst(), similarityOfSecond());
    }

    /**
     * @return Minimum similarity in percent of both submissions.
     */
    public final float minimalSimilarity() {
        return Math.min(similarityOfFirst(), similarityOfSecond());
    }

    /**
     * @return Similarity in percent (what percentage of tokens across both submissions are matched).
     */
    public final float similarity() {
        boolean subtractBaseCode = firstSubmission.hasBaseCodeMatches() && secondSubmission.hasBaseCodeMatches();
        int divisorA = firstSubmission.getSimilarityDivisor(subtractBaseCode);
        int divisorB = secondSubmission.getSimilarityDivisor(subtractBaseCode);
        return 2 * similarity(divisorA + divisorB);
    }

    /**
     * @return Similarity in percent for the first submission (what percent of the first submission is similar to the
     * second).
     */
    public final float similarityOfFirst() {
        int divisor = firstSubmission.getSimilarityDivisor(true);
        return similarity(divisor);
    }

    /**
     * @return Similarity in percent for the second submission (what percent of the second submission is similar to the
     * first).
     */
    public final float similarityOfSecond() {
        int divisor = secondSubmission.getSimilarityDivisor(true);
        return similarity(divisor);
    }

    @Override
    public String toString() {
        return firstSubmission.getName() + " <-> " + secondSubmission.getName();
    }

    private float similarity(int divisor) {
        return (divisor == 0 ? 0f : (getNumberOfMatchedTokens() * 100 / (float) divisor));
    }
}
