package edu.kit.informatik.logik.tiles;

import edu.kit.informatik.logik.Vegetable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Klasse die eine Scheune des "Queens Farming" Spiels implementiert.
 * Die Scheune speichert nicht angebautes Gemüse für einen Spieler
 * @author ujiqk
 * @version 1.0 */
public class Barn extends Tile {

    private static final int STALE_TIME = 6;
    private final List<Vegetable> content;
    private int countdown;

    /** Konstruktor, erzeugt eine Scheune in Startkonfiguration: (Eine Scheune mit je einem Gemüse jeder Art)
     */
    public Barn() {
        content = new ArrayList<>();
        countdown = STALE_TIME + 1;     //+1 Für die erste Runde
        content.add(Vegetable.CARROT);
        content.add(Vegetable.MUSHROOM);
        content.add(Vegetable.SALAD);
        content.add(Vegetable.TOMATO);
    }

    /** Standardkonstruktor. Erzeugt eine neue Scheune mit dem übergebenem Inhalt
     * @param content der Anfangsinhalt der Scheune. Darf nicht null sein
     */
    public Barn(List<Vegetable> content) {
        this.content = new ArrayList<>(content);
        if (!content.isEmpty()) {
            countdown = STALE_TIME;
        }
    }

    /** Lässt das Gemüse eine Runde älter werden
     * @return True: wenn der Inhalt verfault, False: wenn nichts passiert
     */
    @Override
    public boolean updateTimer() {
        //True, wenn gemüse schlecht wird
        if (!content.isEmpty()) {
            countdown--;
            if (countdown <= 0) {
                content.clear();
                countdown = 0;
                return true;
            }
        }
        return false;
    }

    @Override
    public TileType getType() {
        return TileType.BARN;
    }

    /** Gibt den Inhalt der Scheune als HashMap zurück.
     * @return der Inhalt: Die Gemüse mit ihrer Anzahl (auch 0) aufgelistet.
     */
    public Map<Vegetable, Integer> getContent() {
        int carrots = 0;
        int tomatoes = 0;
        int mushrooms = 0;
        int salads = 0;
        for (Vegetable vegetable : content) {       //Unsortierte Liste durchgehen
            if (vegetable == Vegetable.CARROT) {
                carrots++;
            } else if (vegetable == Vegetable.TOMATO) {
                tomatoes++;
            } else if (vegetable == Vegetable.MUSHROOM) {
                mushrooms++;
            } else if (vegetable == Vegetable.SALAD) {
                salads++;
            }
        }
        Map<Vegetable, Integer> contentMap = new EnumMap<>(Vegetable.class);
        contentMap.put(Vegetable.CARROT, carrots);
        contentMap.put(Vegetable.TOMATO, tomatoes);
        contentMap.put(Vegetable.MUSHROOM, mushrooms);
        contentMap.put(Vegetable.SALAD, salads);
        return contentMap;
    }

    /** Gibt eine Kopie des Inhalts der Scheune aus.
     * @return der Inhalt als unsortierte Liste
     */
    public List<Vegetable> getContentListCopy() {
        return List.copyOf(content);
    }

    @Override
    public int getCountdown() {
        return countdown;
    }

    /** Entfernt das angegebene Gemüse einmal aus der Scheune
     * @param vegetable Das zu entfernende Gemüse. Darf nicht null sein.
     * @throws IllegalArgumentException wenn das Gemüse nicht vorhanden ist.
     */
    public void removeItem(Vegetable vegetable) throws IllegalArgumentException {
        if (content.contains(vegetable)) {
            content.remove(vegetable);
        }
        else {
            throw new IllegalArgumentException();
        }
        if (content.isEmpty()) {
            countdown = 0;
        }
    }

    /** Fügt ein Gemüse zur Scheune hinzu.
     * @param vegetable Das Gemüse. Darf nicht null sein.
     */
    public void addItem(Vegetable vegetable) {
        if (content.isEmpty()) {
            countdown = STALE_TIME;
        }
        content.add(vegetable);
    }

}
