package de.jplag;

/**
 * Represents two code fragments in two submissions that are structurally similar. These sections are usually identical
 * token subsequences, but can vary slightly when employing post-processing mechanisms, for example subsequence match
 * merging.
 * @param startOfFirst is the index of the first token of the match in the first submission.
 * @param startOfSecond is the index of the first token of the match in the second submission.
 * @param lengthOfFirst is the length of these similar sections (number of tokens) in the first submission.
 * @param lengthOfSecond is the length of these similar sections (number of tokens) in the second submission.
 */
public record Match(int startOfFirst, int startOfSecond, int lengthOfFirst, int lengthOfSecond) {

    /**
     * Checks if two matches overlap.
     * @param other the match that is compared against the given.
     * @return true if they do.
     */
    public boolean overlaps(Match other) {
        if (startOfFirst < other.startOfFirst) {
            if (other.startOfFirst - startOfFirst < lengthOfFirst) {
                return true;
            }
        } else if (startOfFirst - other.startOfFirst < other.lengthOfFirst) {
            return true;
        }

        if (startOfSecond < other.startOfSecond) {
            return other.startOfSecond - startOfSecond < lengthOfSecond;
        }
        return startOfSecond - other.startOfSecond < other.lengthOfSecond;
    }

    /**
     * @return the token index of the last token of the match in the first submission.
     */
    public int endOfFirst() {
        return startOfFirst + lengthOfFirst - 1;
    }

    /**
     * @return the token index of the last token of the match in the second submission.
     */
    public int endOfSecond() {
        return startOfSecond + lengthOfSecond - 1;
    }

    /**
     * @return the minimal length of the match, which is the minimum of both sides of the match. For many matches, both
     * sides have the same length.
     */
    public int minimumLength() {
        return Math.min(lengthOfFirst, lengthOfSecond);
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
}
