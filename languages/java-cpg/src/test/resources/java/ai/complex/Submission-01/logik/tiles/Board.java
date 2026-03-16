package edu.kit.informatik.logik.tiles;

import edu.kit.informatik.logik.LogicException;
import edu.kit.informatik.logik.Vegetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Klasse die ein Spielfeld des "Queens Farming" Spiels implementiert.
 * @author ujiqk
 * @version 1.0 */
public class Board {
    private static final int TILE_PRICE_FACTOR = 10;

    private final Map<Coordinates, Tile> boardMap;      //Koordinatensystem mit Einträgen

    /** Standardkonstruktor der ein Standardboard erzeugt.
     * Standard: Scheune bei (0,0), Gärten bei (1,0) und (-1,0) und Feld bei (0,1).
     */
    public Board() {
        //init Board
        boardMap = new HashMap<>();
        boardMap.put(new Coordinates(0, 0), new Barn());
        boardMap.put(new Coordinates(1, 0), new CropArea(TileType.GARDEN));
        boardMap.put(new Coordinates(-1, 0), new CropArea(TileType.GARDEN));
        boardMap.put(new Coordinates(0, 1), new CropArea(TileType.FIELD));
    }

    /** Schaut, ob die gegebenen Koordinaten frei sind
     * @param coordinates die Koordinaten an denen geschaut werden soll.
     * @return True: wenn das Feld noch frei ist, False: wenn schon ein Feld da ist
     */
    public boolean isFree(Coordinates coordinates) {
        return !boardMap.containsKey(coordinates);
    }

    private boolean hasNeighbour(Coordinates coordinates) {
        int xCoordinate = coordinates.getXCoordinate();
        int yCoordinate = coordinates.getYCoordinate();
        //Nachbarn sind links/rechts oder unten     (NICHT oben)
        return !isFree(new Coordinates(xCoordinate + 1, yCoordinate))
            || !isFree(new Coordinates(xCoordinate - 1, yCoordinate))
            //|| !isFree(new Coordinates(xCoordinate, yCoordinate + 1))
            || !isFree(new Coordinates(xCoordinate, yCoordinate - 1));
    }

    /** Ermittelt den Preis eines noch nicht besetzten Feldes
     * @param coordinates Die Koordinaten. Darf nicht null sein.
     * @return Der Preis als Ganzzahl
     * @throws LogicException wenn kein Preis für das Feld existiert (schon belegt,
     *          kein Nachbar links, rechts oder drunter).
     */
    public int getPrice(Coordinates coordinates) throws LogicException {
        //Checkt auf erlaubte Koordinaten: rechts, links, darüber muss ein Feld sein
        //geht nicht für schon gekaufte/irgendwelche Felder
        if (!isFree(coordinates) || !hasNeighbour(coordinates)) {
            throw new LogicException();
        }
        //Kosten = Manhattan Distanz zum Ursprung
        return TILE_PRICE_FACTOR
            * ((Math.abs(coordinates.getXCoordinate())
            + Math.abs(coordinates.getYCoordinate())) - 1);
    }

    /** Fügt eine Kachel zum Spielfeld hinzu.
     * @param tile Die Kachel zum Hinzufügen.
     * @param coordinates Die Koordinaten. Darf nicht null sein.
     * @throws LogicException Wenn die Koordinaten schon besetzt sind oder die Kachel null ist.
     */
    public void addTile(Tile tile, Coordinates coordinates) throws LogicException {
        //Checkt nicht auf erlaubte Koordinaten
        if (!isFree(coordinates) || tile == null) {
            throw new LogicException();
        }
        boardMap.put(coordinates, tile);
    }

    /** Getter für die Scheune vom Spielfeld
     * @return Die Scheune
     */
    public Barn getBarn() {
        Tile barn = boardMap.get(new Coordinates(0, 0));
        return (Barn) barn;     //An Stelle (0,0) ist immer die Scheune
    }

    /** Gibt die maximale Höhe des Koordinatensystems aus (die Höhe startet bei null).
     * @return Die höhe als Ganzzahl
     */
    public int getMaxHeight() {
        int maxY = 0;
        for (Coordinates key : boardMap.keySet()) {
            if (key.getYCoordinate() > maxY ) {
                maxY = key.getYCoordinate();
            }
        }
        return maxY;
    }

    /** Gibt die maximale Breite des Koordinatensystems in positive Richtung (die Breite startet bei null).
     * @return Die Breite als Ganzzahl
     */
    public int getMaxPositiveX() {
        int maxX = 0;
        for (Coordinates key : boardMap.keySet()) {
            if (key.getXCoordinate() > maxX ) {
                maxX = key.getXCoordinate();
            }
        }
        return maxX;
    }

    /** Gibt die maximale Breite des Koordinatensystems in negative Richtung (die Breite startet bei null).
     * @return Die Breite als Ganzzahl
     */
    public int getMaxNegativeX() {      //Gibt negative Zahl zurück
        int maxMinusX = 0;
        for (Coordinates key : boardMap.keySet()) {
            if (key.getXCoordinate() < maxMinusX ) {
                maxMinusX = key.getXCoordinate();
            }
        }
        return maxMinusX;
    }

    /** Gibt nur anbaubare Kacheln an den Koordinaten zurück.
     * Gibt null zurück, wenn die Koordinaten nicht besetzt sind oder an der Stelle die Scheune ist.
     * @param coordinates Die Koordinaten. Darf nicht null sein.
     * @return Die anbaubare Kachel an der Stelle. Null, wenn sie nicht existiert.
     */
    private CropArea getCropArea(Coordinates coordinates) {      //Return null erlaubt!, gibt die Scheune nicht aus
        if (isFree(coordinates) || (coordinates.getXCoordinate() == 0 && coordinates.getYCoordinate() == 0)) {
            return null;
        }
        else {
            return (CropArea) boardMap.get(coordinates);
        }
    }

    /** Gibt die Kachel an den Koordinaten zurück.
     * Gibt null zurück, wenn die Koordinaten nicht besetzt sind.
     * @param coordinates Die Koordinaten. Darf nicht null sein.
     * @return Die Kachel an der Stelle. Null, wenn sie nicht existiert.
     */
    public Tile getTile(Coordinates coordinates) {      //Return null erlaubt!
        if (isFree(coordinates)) {
            return null;
        }
        else {
            return boardMap.get(coordinates);
        }
    }

    /** Ermittelt ob Links frei ist
     * @param coordinates Die Koordinaten. Darf nicht null sein.
     * @return True: wenn links von den Koordinaten frei ist, False: wenn belegt
     */
    public boolean isLeftFree(Coordinates coordinates)  {
        return isFree(new Coordinates(coordinates.getXCoordinate() - 1, coordinates.getYCoordinate()));
    }

    /** Setzt die Scheune auf einen komplett leeren Zustand zurück.
     */
    public void emptyBarn() {
        boardMap.remove(new Coordinates(0, 0));
        //Scheune mit leerer anfangskonfiguration
        boardMap.put(new Coordinates(0, 0), new Barn(new ArrayList<>()));
    }

    /** Lässt alle angebauten Gemüse auf dem Spielfeld wachsen.
     * @return Die anzahl der gewachsenen Gemüse. Eine Ganzzahl.
     */
    public int growCropAreas() {
        int amount = 0;
        for (Tile tile: boardMap.values()) {
            if (tile.getClass() == CropArea.class) {    //Barn nicht behandeln
                int amountOnTileBefore = ((CropArea) tile).getAmount();
                boolean grown = tile.updateTimer();
                int amountOnTileAfter = ((CropArea) tile).getAmount();
                if (grown) {
                    amount += (amountOnTileAfter - amountOnTileBefore);
                }
            }
        }
        return amount;
    }

    /** Erntet Gemüse von einem Feld auf dem Spielfeld
     * @param coordinates Die Koordinaten an denen geerntet wird. Darf nicht null sein.
     * @param number Die Anzahl die geerntet werden soll.
     * @return Gibt das Gemüse (als Vegetable) zurück das geerntet wurde.
     * @throws LogicException Wenn das Feld nicht existiert oder
     *          nicht die angegebene Anzahl auf dem Feld zum Ernten ist.
     */
    public Vegetable harvest(Coordinates coordinates, int number) throws LogicException {
        if (isFree(coordinates)
            || (coordinates.getXCoordinate() == 0 && coordinates.getYCoordinate() == 0)) {
            throw new LogicException();     //Land noch nicht gekauft oder Land ist Scheune
        }
        if (getCropArea(coordinates).getAmount() < number) {
            throw new LogicException();     //nicht genug Gemüse zum Ernten
        }
        Vegetable veg = getCropArea(coordinates).harvest(number);       //ernten
        for (int i = 0; i < number; i++) {                              //Gemüse in die Scheune legen
            getBarn().addItem(veg);
        }
        return veg;
    }

    /** Pflanzt ein Gemüse an den angegebenen Koordinaten an.
     * Das Gemüse wird dabei aus der Scheune genommen.
     * @param coordinates Die Koordinaten an denen gepflanzt wird. Darf nicht null sein.
     * @param vegetable das Gemüse was angebaut werden soll. Darf nicht null sein.
     * @throws LogicException Falls der Spieler das Gemüse nicht besitzt oder
     *                          auf den Koordinaten nicht angebaut werden kann.
     */
    public void plant(Coordinates coordinates, Vegetable vegetable) throws LogicException {
        if (isFree(coordinates)
            || (coordinates.getXCoordinate() == 0 && coordinates.getYCoordinate() == 0)) {
            throw new LogicException();     //Land noch nicht gekauft oder Scheune an der Stelle
        }
        Map<Vegetable, Integer> barnContent = getBarn().getContent();
        barnContent.values().removeAll(Collections.singleton(0));
        if (!barnContent.containsKey(vegetable)) {
            throw new LogicException();     //Gemüse nicht in der Scheune
        }
        //Anpflanzen und Gemüse abziehen
        getCropArea(coordinates).plant(vegetable);    //LogicException wenn: schon belegt, falsche Kachel Art
        getBarn().removeItem(vegetable);
    }

}
