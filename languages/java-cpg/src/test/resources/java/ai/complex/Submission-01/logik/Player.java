package edu.kit.informatik.logik;

import edu.kit.informatik.logik.tiles.Barn;
import edu.kit.informatik.logik.tiles.Board;
import edu.kit.informatik.logik.tiles.Coordinates;
import edu.kit.informatik.logik.tiles.Tile;

import java.util.Collections;
import java.util.Map;

/** Klasse die einen Spieler/eine Spielerin des "Queens Farming" Spiels implementiert.
 * Zusätzlich wird der Besitz des Spielers und sein Spielfeld implementiert
 *
 * @author ujiqk
 * @version 1.0 */
public class Player {

    private final String name;
    private int gold;
    private final int winGold;

    private final Board board;    //Das Spielfeld

    /** Konstruktor der einen Standardspieler mit einem Standardspielfeld erstellt.
     * @param name Der Name als String
     * @param gold Der Goldanfangsbesitz. Eine Ganzzahl
     * @param winGold Das Gold das zum Gewinnen erreicht werden muss. Eine Ganzzahl
     */
    public Player(String name, int gold, int winGold) {
        this.name = name;
        this.gold = gold;
        this.winGold = winGold;
        //Spielfeld init
        board = new Board();
    }

    /** Kauft dem Spieler eine neue Landkachel an den angegebenen Koordinaten.
     *
     * @param tile Die Kachel (Tile) die gekauft werden soll. Sollte nicht null sein.
     * @param coordinates Die Koordinaten an denen gekauft werden soll. Sollte nicht null sein.
     * @throws LogicException Wenn die Koordinaten nicht frei oder einen linken/rechten/unten Nachbar haben oder
     * der Preis zu teuer ist.
     */
    public void buyLand(Tile tile, Coordinates coordinates) throws LogicException {
        int price = board.getPrice(coordinates);        //LogicException, wenn die Koordinaten falsch sind
        if (price <= gold) {
            board.addTile(tile, coordinates);           //LogicException, wenn die Koordinaten falsch sind
            //Falls es funktioniert, Geld abziehen
            gold -= price;
        }
        else {
            throw new LogicException();
        }
    }

    /** Getter für das Gold des Spielers
     * @return das Gold als Ganzzahl
     */
    public int getGold() {
        return gold;
    }

    /** Setter für das Gold des Spielers
     * @param gold Eine Ganzzahl. Setzt das aktuelle Gold auf diesen Wert
     */
    public void setGold(int gold) {
        this.gold = gold;
    }

    /** Fügt Gold zum aktuellen Gold des Spielers dazu.
     * @param income Das zu hinzufügende Gold als Ganzzahl
     */
    public void addGold(int income) {
        gold += income;
    }

    /** Prüft ob der Spieler mehr oder gleichviel Gold, wie zum Gewinnen notwendig ist, hat.
     * @return True: der Spieler hat gewonnen; False: Er hat noch nicht gewonnen
     */
    public boolean hasWon() {
        return gold >= winGold;
    }

    /** Getter für den Namen des Spielers
     * @return Der Name als String
     */
    public String getName() {
        return name;
    }

    /** Gibt alle wichtigen Besitztümer eines Spielers als 'Possessions' zurück.
     * (Inkludiert nicht die Kacheln)
     * @return Ein Possession Objekt mit den Informationen
     */
    public Possessions getPossessions() {
        Map<Vegetable, Integer> barnContent = board.getBarn().getContent();
        //Alle Gemüse mit Anzahl 0 entfernen        HashMap kann leer sein
        barnContent.values().removeAll(Collections.singleton(0));
        return new Possessions(
            barnContent,
            gold,
            board.getBarn().getCountdown());
    }

    /** Getter für das Spielfeld
     * @return Das Spielfeld
     */
    public Board getBoard() {
        return board;
    }

    /** Getter für nur die Scheune aus dem Spielfeld
     * @return Die Scheune des Spielers
     */
    public Barn getBarn() {
        return board.getBarn();
    }

    /** Pflanzt ein Gemüse an den angegebenen Koordinaten an.
     * Das Gemüse wird dabei aus der Scheune genommen
     * @param coordinates Die Koordinaten an denen gepflanzt wird. Darf nicht null sein.
     * @param vegetable das Gemüse was angebaut werden soll. Darf nicht null sein.
     * @throws LogicException Falls der Spieler das Gemüse nicht besitzt oder
     *                          auf den Koordinaten nicht angebaut werden kann.
     */
    public void plant(Coordinates coordinates, Vegetable vegetable) throws LogicException {
        board.plant(coordinates, vegetable);
    }

    /** Erntet Gemüse von einem Feld auf dem Spielfeld
     * @param coordinates Die Koordinaten an denen geerntet wird. Darf nicht null sein.
     * @param number Die Anzahl die geerntet werden soll.
     * @return Gibt das Gemüse (als Vegetable) zurück das geerntet wurde.
     * @throws LogicException Wenn das Feld nicht existiert oder
     *          nicht die angegebene Anzahl auf dem Feld zum Ernten ist.
     */
    public Vegetable harvest(Coordinates coordinates, int number) throws LogicException {
        return board.harvest(coordinates, number);
    }

    /** Lässt alle angebauten Gemüse auf dem Spielfeld des Spielers wachsen
     * @return Eine Ganzzahl die angibt wie viele Gemüse gewachsen sind.
     */
    public int growVegetables() {
        return board.growCropAreas();
    }

    /** Lässt die Zeit in der Scheune des Spielers vergehen
     * @return True: wenn die Scheune vergammelt ist, False: wenn nicht
     */
    public boolean updateBarn() {
        //True, wenn Gemüse schlecht wird
        return board.getBarn().updateTimer();
    }

}
