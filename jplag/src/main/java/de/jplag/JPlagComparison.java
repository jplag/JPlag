package de.jplag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This method represents the whole result of a comparison between two submissions.
 */
public class JPlagComparison { // FIXME TS: contains a lot of code duplication

    private static final int ROUNDING_FACTOR = 10;

    private final Submission firstSubmission;
    private final Submission secondSubmission;

    private final List<Match> matches;

    public JPlagComparison(Submission firstSubmission, Submission secondSubmission) {
        this.firstSubmission = firstSubmission;
        this.secondSubmission = secondSubmission;
        matches = new ArrayList<>();
    }

    /**
     * Add a match to the comparison (token indices and number of tokens), if it does not overlap with the existing matches.
     * @see Match#Match(int, int, int)
     */
    /* package-private */ final void addMatch(int startOfFirst, int startOfSecond, int length) {
        for (Match match : matches) {
            if (match.overlap(startOfFirst, startOfSecond, length)) {
                return;
            }
        }
        matches.add(new Match(startOfFirst, startOfSecond, length));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof JPlagComparison otherComparison)) {
            return false;
        }
        return firstSubmission.equals(otherComparison.getFirstSubmission()) && secondSubmission.equals(otherComparison.getSecondSubmission())
                && matches.equals(otherComparison.matches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstSubmission, secondSubmission, matches);
    }

    /**
     * @return the base code matches of the first submission.
     */
    public JPlagComparison getFirstBaseCodeMatches() {
        return firstSubmission.getBaseCodeComparison();
    }

    /**
     * @return the first of the two submissions.
     */
    public Submission getFirstSubmission() {
        return firstSubmission;
    }

    /**
     * @return all matches between the two submissions.
     */
    public List<Match> getMatches() {
        return matches;
    }

    /**
     * Get the total number of matched tokens for this comparison.
     */
    public final int getNumberOfMatchedTokens() {
        int numberOfMatchedTokens = 0;

        for (Match match : matches) {
            numberOfMatchedTokens += match.length();
        }

        return numberOfMatchedTokens;
    }

    /**
     * @return the base code matches of the second submissions.
     */
    public JPlagComparison getSecondBaseCodeMatches() {
        return secondSubmission.getBaseCodeComparison();
    }

    /**
     * @return the second of the two submissions.
     */
    public Submission getSecondSubmission() {
        return secondSubmission;
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
        float sa = firstSubmission.getSimilarityDivisor(subtractBaseCode);
        float sb = secondSubmission.getSimilarityDivisor(subtractBaseCode);
        return (200 * getNumberOfMatchedTokens()) / (sa + sb);
    }

    /**
     * @return Similarity in percent for the first submission (what percent of the first submission is similar to the
     * second).
     */
    public final float similarityOfFirst() {
        int divisor = firstSubmission.getSimilarityDivisor(true);
        return (divisor == 0 ? 0f : (getNumberOfMatchedTokens() * 100 / (float) divisor));
    }

    /**
     * @return Similarity in percent for the second submission (what percent of the second submission is similar to the
     * first).
     */
    public final float similarityOfSecond() {
        int divisor = secondSubmission.getSimilarityDivisor(true);
        return (divisor == 0 ? 0f : (getNumberOfMatchedTokens() * 100 / (float) divisor));
    }

    /**
     * @return Similarity in percent rounded down to the nearest tenth.
     */
    public final float roundedSimilarity() {
        return ((int) (similarity() * ROUNDING_FACTOR)) / (float) ROUNDING_FACTOR;
    }

    /**
     * @return Similarity of the first submission to the basecode in percent rounded down to the nearest tenth.
     */
    public final float basecodeSimilarityOfFirst() {
        return ((int) (firstBasecodeSimilarity() * ROUNDING_FACTOR)) / (float) ROUNDING_FACTOR;
    }

    /**
     * @return Similarity of the second submission to the basecode in percent rounded down to the nearest tenth.
     */
    public final float basecodeSimilarityOfSecond() {
        return ((int) (secondBasecodeSimilarity() * ROUNDING_FACTOR)) / (float) ROUNDING_FACTOR;
    }

    @Override
    public String toString() {
        return firstSubmission.getName() + " <-> " + secondSubmission.getName();
    }

    private float firstBasecodeSimilarity() {
        float sa = firstSubmission.getSimilarityDivisor(false);
        JPlagComparison firstBaseCodeMatches = firstSubmission.getBaseCodeComparison();
        return firstBaseCodeMatches.getNumberOfMatchedTokens() * 100 / sa;
    }

    private float secondBasecodeSimilarity() {
        float sb = secondSubmission.getSimilarityDivisor(false);
        JPlagComparison secondBaseCodeMatches = secondSubmission.getBaseCodeComparison();
        return secondBaseCodeMatches.getNumberOfMatchedTokens() * 100 / sb;
    }
}
