package de.jplag;

/**
 * Represents two sections of two submissions that are similar.
 */
public record Match(int startOfFirst, int startOfSecond, int length) {

    /**
     * Creates a match.
     * @param startOfFirst is the starting token index in the first submission.
     * @param startOfSecond is the starting token index in the second submission.
     * @param length is the length of these similar sections (number of tokens).
     */
    public Match {
    }

    /**
     * @return the starting index in the first submission.
     */
    @Override
    public int startOfFirst() {
        return startOfFirst;
    }

    /**
     * @return the starting index in the second submission.
     */
    @Override
    public int startOfSecond() {
        return startOfSecond;
    }

    /**
     * @return the length of the similar sections, meaning the number of tokens.
     */
    @Override
    public int length() {
        return length;
    }

    /**
     * Checks if two matches overlap.
     * @return true if they do.
     */
    public boolean overlap(int otherStartOfFirst, int otherStartOfSecond, int oLength) {
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
