package jplag;

/**
 * Minimal data structure that stores "Match" objects.
 * <p>
 * Note: This class is only used by {@link GreedyStringTiling} as a data structure to store matches.
 */
public class Matches {

    public Match[] matches;

    private int numberOfMatches;
    private final int increment = 20;

    public Matches() {
        matches = new Match[10];
        for (int i = 0; i < 10; i++) {
            matches[i] = new Match();
        }
        numberOfMatches = 0;
    }

    public final int size() {
        return numberOfMatches;
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = matches.length;
        if (minCapacity > oldCapacity) {
            Match[] oldMatches = matches;
            int newCapacity = (oldCapacity + increment);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            matches = new Match[newCapacity];
            System.arraycopy(oldMatches, 0, matches, 0, oldCapacity);
            for (int i = oldCapacity; i < newCapacity; i++) {
                matches[i] = new Match();
            }
        }
    }

    public final void addMatch(int startA, int startB, int length) {
        for (int i = numberOfMatches - 1; i >= 0; i--) { // starting at the end is better(?)
            if (matches[i].overlap(startA, startB, length)) {
                return;
            }
            // no overlaps!
        }

        ensureCapacity(numberOfMatches + 1);

        matches[numberOfMatches].set(startA, startB, length);
        numberOfMatches++;
    }

    public final void clear() {
        numberOfMatches = 0;
    }
}
