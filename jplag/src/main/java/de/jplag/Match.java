package de.jplag;

/**
 * Represents two sections of two submissions that are similar.
 * @param startOfFirst is the starting token index in the first submission.
 * @param startOfSecond is the starting token index in the second submission.
 * @param length is the length of these similar sections (number of tokens).
 */
public record Match(int startOfFirst, int startOfSecond, int length) {
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
