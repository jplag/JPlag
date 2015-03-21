package jplag;


public class Match {
  public int startA;
  public int startB;
  public int length;

  public Match(int startA, int startB, int length) {
    this.startA = startA;
    this.startB = startB;
    this.length = length;
  }
  public Match() {}

  public void set(int startA, int startB, int length) {
    this.startA = startA;
    this.startB = startB;
    this.length = length;
  }

  public final boolean contains(int index, int sub) {
    int start = (sub==0 ? startA : startB);
    return (start <= index && index < (start+length));
  }

  public final boolean overlap(Match other) {
    if (startA < other.startA) {
      if ((other.startA - startA) < length) return true;
    } else {
      if ((startA - other.startA) < other.length) return true;
    }
    if (startB < other.startB) {
      if ((other.startB - startB) < length) return true;
    } else {
      if ((startB - other.startB) < other.length) return true;
    }
    return false;
  }

  public final boolean overlap(int oStartA, int oStartB, int oLength) {
    if (startA < oStartA) {
      if ((oStartA - startA) < length) return true;
    } else {
      if ((startA - oStartA) < oLength) return true;
    }
    if (startB < oStartB) {
      if ((oStartB - startB) < length) return true;
    } else {
      if ((startB - oStartB) < oLength) return true;
    }
    return false;
  }
}

