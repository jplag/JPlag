package jplag;

/**
 * Represents two sections of two submissions A and B that are similar.
 */
public class Match {

    private final int startA;
    private final int startB;
    private final int length;

    /**
     * Creates a match.
     * @param startA is the starting index in the submission A.
     * @param startB is the starting index in the submission B.
     * @param length is the length of these similar sections.
     */
    public Match(int startA, int startB, int length) {
        this.startA = startA;
        this.startB = startB;
        this.length = length;
    }

    /**
     * @return the starting index in the submission A.
     */
    public int getStartA() {
        return startA;
    }

    /**
     * @return the starting index in the submission B.
     */
    public int getStartB() {
        return startB;
    }

    /**
     * @return the length of the similar sections.
     */
    public int getLength() {
        return length;
    }

    /**
     * Checks if two matches overlap.
     * @return true if they do.
     */
    public final boolean overlap(int oStartA, int oStartB, int oLength) {
        if (startA < oStartA) {
            if ((oStartA - startA) < length) {
                return true;
            }
        } else {
            if ((startA - oStartA) < oLength) {
                return true;
            }
        }

        if (startB < oStartB) {
            return (oStartB - startB) < length;
        } else {
            return (startB - oStartB) < oLength;
        }
    }
}
