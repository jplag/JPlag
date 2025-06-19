package de.jplag;

import java.util.Objects;

/**
 * Represents two code fragments in two submissions that are structurally similar. These sections are usually identical
 * token subsequences, but can vary slightly when employing post-processing mechanisms, for example subsequence match
 * merging.
 */
public class Match {
    private final int startOfFirst;
    private final int startOfSecond;
    private final int lengthOfFirst;
    private final int lengthOfSecond;
    private double frequencyWeight;

    /**
     * Represents two code fragments in two submissions that are structurally similar. These sections are usually identical
     * token subsequences, but can vary slightly when employing post-processing mechanisms, for example subsequence match
     * merging.
     * @param startOfFirst is the index of the first token of the match in the first submission.
     * @param startOfSecond is the index of the first token of the match in the second submission.
     * @param lengthOfFirst is the length of these similar sections (number of tokens) in the first submission.
     * @param lengthOfSecond is the length of these similar sections (number of tokens) in the second submission.
     */
    public Match(int startOfFirst, int startOfSecond, int lengthOfFirst, int lengthOfSecond) {
        this.startOfFirst = startOfFirst;
        this.startOfSecond = startOfSecond;
        this.lengthOfFirst = lengthOfFirst;
        this.lengthOfSecond = lengthOfSecond;
    }

    public int getStartOfFirst() {
        return startOfFirst;
    }

    public int getStartOfSecond() {
        return startOfSecond;
    }

    public int getLengthOfFirst() {
        return lengthOfFirst;
    }

    public int getLengthOfSecond() {
        return lengthOfSecond;
    }

    public double getFrequencyWeight() {
        return frequencyWeight;
    }

    public void setFrequencyWeight(double freuencyWeight) {
        this.frequencyWeight = freuencyWeight;
    }

    /**
     * Checks if two matches overlap.
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
     * @deprecated matches are no longer required to be symmetrical. Thus, both sides can have different lengths. This
     * method now returns the minimal length.
     * @see Match#minimumLength()
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    public int length() {
        return minimumLength();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Match match = (Match) o;
        return startOfFirst == match.startOfFirst && startOfSecond == match.startOfSecond && lengthOfFirst == match.lengthOfFirst
                && lengthOfSecond == match.lengthOfSecond;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startOfFirst, startOfSecond, lengthOfFirst, lengthOfSecond);
    }
}
