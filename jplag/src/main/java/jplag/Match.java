package jplag;

public class Match {

    public int startA;
    public int startB;
    public int length;

    public Match() {
        // non-parameterized constructor for uninitialized matches 
    }

    public Match(int startA, int startB, int length) {
        this.startA = startA;
        this.startB = startB;
        this.length = length;
    }

    public void set(int startA, int startB, int length) {
        this.startA = startA;
        this.startB = startB;
        this.length = length;
    }

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
