package edu.kit.informatik.logik.tiles;

/** Abstrakte Klasse die Kachel-arten des "Queens Farming" Spiels implementiert.
 * @author ujiqk
 * @version 1.0 */
public abstract class Tile {

    /** Leere Konstruktor der nichts macht
     */
    protected Tile() {

    }

    /** Lässt eine Runde Zeit vergehen
     * @return True: wenn etwas passiert, False: wenn nichts passiert
     */
    public abstract boolean updateTimer();

    /** Getter für den Kacheltyp einer Kachel
     * @return der Kacheltyp
     */
    public abstract TileType getType();

    /** Getter für den Countdown einer Kachel
     * @return der countdown als Ganzzahl
     */
    public abstract int getCountdown();

}
