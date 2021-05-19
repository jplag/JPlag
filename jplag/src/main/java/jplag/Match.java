package jplag;

public class Match {

    public int startA;
    public int startB;
    public int length;

    public Match() {
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

    public final boolean contains(int index, int sub) {
        int start = (sub == 0 ? startA : startB);
        return (start <= index && index < (start + length));
    }

    public final boolean overlap(Match other) {
        return overlap(other.startA, other.startB, other.length);
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
