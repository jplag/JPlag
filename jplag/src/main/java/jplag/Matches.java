package jplag;


/* Minimal class that stores "Match" objects.
 */
public class Matches {
  public Match[] matches;

  private int anzahl;
  private final int increment = 20;
  
  public Matches() {
    matches = new Match[10];
    for (int i=0; i<10; i++) matches[i] = new Match();
    anzahl = 0;
  }

  public final int size() {
    return anzahl;
  }

  public final void ensureCapacity(int minCapacity) {
    int oldCapacity = matches.length;
    if (minCapacity > oldCapacity) {
      Match[] oldMatches = matches;
      int newCapacity = (oldCapacity + increment);
      if (newCapacity < minCapacity) {
	newCapacity = minCapacity;
      }
      matches = new Match[newCapacity];
      System.arraycopy(oldMatches, 0, matches, 0, oldCapacity);
      for (int i=oldCapacity; i<newCapacity; i++)
	matches[i] = new Match();
    }
  }
  /*
  public final void addMatch(Match match) {
    for (int i=0; i<anzahl; i++)
      if (match.overlap(matches[i])) return;  // do not allow overlaps
    ensureCapacity(anzahl + 1);
    matches[anzahl++] = match;
  }
  */
  public final void addMatch(int startA, int startB, int length) {
    for (int i=anzahl-1; i>=0; i--) { // starting at the end is better(?)
      if (matches[i].overlap(startA,startB,length)) return;
                                      // no overlaps!
    }
    ensureCapacity(anzahl + 1);
    //if (matches[anzahl] != null)  // object recycling...
    matches[anzahl].set(startA,startB,length);
    //else
    //matches[anzahl ] = new Match(startA,startB,length);
    anzahl++;
  }

  public final void clear() {
    anzahl=0;
  }
}
