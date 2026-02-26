package de.jplag;

/**
 * Represents two code fragments in two submissions that are structurally similar. These sections are usually identical
 * token subsequences, but can vary slightly when employing post-processing mechanisms, for example subsequence match
 * merging.
 */
public record Match(TokenRange leftTokens, TokenRange rightTokens) {

    public Match(int startOfFirst, int startOfSecond, int lengthOfFirst, int lengthOfSecond) {
        this(new TokenRange(startOfFirst, startOfFirst + lengthOfFirst - 1), new TokenRange(startOfSecond, startOfFirst + lengthOfSecond - 1));
    }

    /**
     * Checks if two matches overlap.
     * @param other the match that is compared against the given.
     * @return true if they do.
     */
    public boolean overlaps(Match other) {
        return leftTokens.overlaps(other.leftTokens) || rightTokens.overlaps(other.rightTokens);
    }

    /**
     * @return the token index of the last token of the match in the first submission.
     */
    public int endOfFirst() {
        return leftTokens.getLastToken();
    }

    /**
     * @return the token index of the last token of the match in the second submission.
     */
    public int endOfSecond() {
        return rightTokens.getLastToken();
    }

    /**
     * @return the minimal length of the match, which is the minimum of both sides of the match. For many matches, both
     * sides have the same length.
     */
    public int minimumLength() {
        return Math.min(leftTokens.tokenCount(), rightTokens.tokenCount());
    }

    /**
     * @return length is the length of these similar sections (number of tokens).
     * @see Match#minimumLength()
     * @deprecated matches are no longer required to be symmetrical. Thus, both sides can have different lengths. This
     * method now returns the minimal length.
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    public int length() {
        return minimumLength();
    }

    public int startOfFirst() {
        return leftTokens.getFirstToken();
    }

    public int startOfSecond() {
        return rightTokens.getLastToken();
    }

    public int numberOfFirstTokens() {
        return leftTokens.tokenCount();
    }

    public int numberOfSecondTokens() {
        return rightTokens.tokenCount();
    }
}
