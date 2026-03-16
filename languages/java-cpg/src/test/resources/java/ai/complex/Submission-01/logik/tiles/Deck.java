package edu.kit.informatik.logik.tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Klasse, die einen Kartenstapel des "Queens Farming" Spiels implementiert.
 * Der Kartenstapel hält für alle Spieler Landkachel, die sie, wenn sie Land kaufen zufällig bekommen.
 * @author ujiqk
 * @version 1.0 */
public class Deck {

    private final List<Tile> cardDeck;           //Der Kartenstapel
    private Tile lastTile;

    /** Standardkonstruktor der aus der Spieleranzahl ein Standardkartenstapel erzeugt.
     * (Anzahl Garten: 2xn, Anzahl Feld: 3xn, Anzahl großes Feld: 2xn, Anzahl Wald: 2xn, Anzahl großer Wald: n,
     * wobei n=Anzahl Spieler)
     * @param playerCount die Anzahl Spieler als Ganzzahl.
     */
    public Deck(int playerCount) {
        //Den Kartenstapel das erste Mal befüllen
        cardDeck = new ArrayList<>();
        for (int i = 0; i < (2 * playerCount); i++) {
            cardDeck.add(new CropArea(TileType.GARDEN));
        }
        for (int i = 0; i < (3 * playerCount); i++) {
            cardDeck.add(new CropArea(TileType.FIELD));
        }
        for (int i = 0; i < (2 * playerCount); i++) {
            cardDeck.add(new CropArea(TileType.LARGE_FIELD));
        }
        for (int i = 0; i < (2 * playerCount); i++) {
            cardDeck.add(new CropArea(TileType.FOREST));
        }
        for (int i = 0; i < (playerCount); i++) {
            cardDeck.add(new CropArea(TileType.LARGE_FOREST));
        }
    }

    /** Mischt den Kartenstapel
     * @param seed der Seed für den java.util.Random Zufälligkeitsgenerator
     */
    public void cardShuffle(int seed) {
        Random randomGenerator = new Random(seed);
        Collections.shuffle(cardDeck, randomGenerator);
    }

    /** Gibt die nächste Kachel aus dem Kartenstapel aus.
     * @return eine Kachel, null, falls der Stapel leer ist
     */
    public Tile getTile() {
        if (cardDeck.isEmpty()) {
            return null;
        }
        Tile item = cardDeck.get(0);
        lastTile = item;        //Speichern zum vielleicht später wiederherstellen
        cardDeck.remove(0);
        return item;
    }

    /** Getter für die größe des Kartenstapels
     * @return die größe als positive Ganzzahl
     */
    public int getSize() {
        return cardDeck.size();
    }

    /** Stellt die zuletzt ausgegebene Karte wieder ganz oben auf den Kartenstapel
     */
    public void restoreLastTile() {
        if (lastTile != null) {
            cardDeck.add(0, lastTile);
            lastTile = null;
        }
    }

}
