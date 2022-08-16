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
    public boolean overlaps(Match other) {
        if (startOfFirst < other.startOfFirst) {
            if ((other.startOfFirst - startOfFirst) < length) {
                return true;
            }
        } else {
            if ((startOfFirst - other.startOfFirst) < other.length) {
                return true;
            }
        }

        if (startOfSecond < other.startOfSecond) {
            return (other.startOfSecond - startOfSecond) < length;
        } else {
            return (startOfSecond - other.startOfSecond) < other.length;
        }
    }
}
