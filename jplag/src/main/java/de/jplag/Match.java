package de.jplag;

/**
 * Represents two sections of two submissions that are similar.
 */
public class Match {

    private final int startOfFirst;
    private final int startOfSecond;
    private final int length;

    /**
     * Creates a match.
     * @param startOfFirst is the starting token index in the first submission.
     * @param startOfSecond is the starting token index in the second submission.
     * @param length is the length of these similar sections (number of tokens).
     */
    public Match(int startOfFirst, int startOfSecond, int length) {
        this.startOfFirst = startOfFirst;
        this.startOfSecond = startOfSecond;
        this.length = length;
    }

    /**
     * @return the starting index in the first submission.
     */
    public int getStartOfFirst() {
        return startOfFirst;
    }

    /**
     * @return the starting index in the second submission.
     */
    public int getStartOfSecond() {
        return startOfSecond;
    }

    /**
     * @param getFirst Whether to return the starting token index of the first submission, else the starting token index of
     * the second submission is returned.
     * @return Start index of the requested submission.
     */
    public int getStart(boolean getFirst) {
        return getFirst ? startOfFirst : startOfSecond;
    }

    /**
     * @return the length of the similar sections, meaning the number of tokens.
     */
    public int getLength() {
        return length;
    }

    /**
     * Checks if two matches overlap.
     * @return true if they do.
     */
    public final boolean overlap(int otherStartOfFirst, int otherStartOfSecond, int oLength) {
        if (startOfFirst < otherStartOfFirst) {
            if ((otherStartOfFirst - startOfFirst) < length) {
                return true;
            }
        } else {
            if ((startOfFirst - otherStartOfFirst) < oLength) {
                return true;
            }
        }

        if (startOfSecond < otherStartOfSecond) {
            return (otherStartOfSecond - startOfSecond) < length;
        } else {
            return (startOfSecond - otherStartOfSecond) < oLength;
        }
    }
}
