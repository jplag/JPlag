package edu.kit.informatik.logik.tiles;

/** Implementiert zweidimensionale Koordinaten.
 * @author ujiqk
 * @version 1.0 */
public class Coordinates {
    private final int xCoordinate;
    private final int yCoordinate;

    /** Konstruktor für Koordinaten
     * @param xCoordinate Die x-Koordinate als Ganzzahl
     * @param yCoordinate Die y-Koordinate als Ganzzahl
     */
    public Coordinates(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    /** Getter für die x-Koordinate
     * @return Die Koordinate als Ganzzahl
     */
    public int getXCoordinate() {
        return xCoordinate;
    }

    /** Getter für die y-Koordinate
     * @return Die Koordinate als Ganzzahl
     */
    public int getYCoordinate() {
        return yCoordinate;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == this.getClass()
            && ((Coordinates) obj).getXCoordinate() == this.getXCoordinate()
            && ((Coordinates) obj).getYCoordinate() == this.getYCoordinate();
        //Vergleicht die beiden Koordinaten miteinander
    }

    @Override
    public int hashCode() {
        int result = xCoordinate;
        result += yCoordinate;
        return result;
    }
}
