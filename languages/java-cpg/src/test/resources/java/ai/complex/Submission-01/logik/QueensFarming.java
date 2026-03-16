package edu.kit.informatik.logik;

import edu.kit.informatik.logik.tiles.Board;
import edu.kit.informatik.logik.tiles.Coordinates;
import edu.kit.informatik.logik.tiles.Deck;
import edu.kit.informatik.logik.tiles.Tile;
import edu.kit.informatik.logik.tiles.TileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** Klasse die ein komplettes "Queens Farming" Spiel implementiert.
 *
 * @author ujiqk
 * @version 1.0 */
public class QueensFarming {

    //Array mit Spielern
    private final Player[] players;

    //index des aktuellen Spielers
    private int playersTurn;

    //Kartenstapel
    private final Deck cardDeck;

    //Market
    private final Market market;

    /** Konstruktor: Erzeugt ein neues Spiel
     * @param playerNames Die Namen der Spieler in Spielreihenfolge als String-Array
     * @param winGold Das zum Gewinnen nötige Gold als Ganzzahl
     * @param startGold Das Startgold als Ganzzahl
     * @param seed Der Seed zum Mischeln als Ganzzahl
     */
    public QueensFarming(String[] playerNames, int winGold, int startGold, int seed) {
        //Game init
        market = new Market();
        //Kartenstapel erschaffen
        cardDeck = new Deck(playerNames.length);
        cardDeck.cardShuffle(seed);
        //Spieler erschaffen
        players = new Player[playerNames.length];
        for (int i = 0; i < playerNames.length; i++) {
            players[i] = new Player(playerNames[i], startGold, winGold );
        }
        playersTurn = 0;
    }

    /** Gibt die Spieler an, die gewonnen haben.
     * @return Ein String-array mit den Spielern die gewonnen haben, hat niemand gewonnen
     * werden die Spieler mit dem meisten Gold zurückgegeben.
     */
    public List<String> getWinningPlayers() {
        List<String> winningNames = new ArrayList<>();
        List<String> maxGoldPlayer = new ArrayList<>();
        int maxGold = 0;
        //Es können zwei Spieler gleichzeitig gewinnen
        for (Player player : players) {
            if (player.hasWon()) {
                winningNames.add(player.getName());
            }
            //Spieler mit maximalem Gold merken
            if (player.getGold() == maxGold) {
                maxGoldPlayer.add(player.getName());
            }
            if (player.getGold() > maxGold) {
                maxGold = player.getGold();
                maxGoldPlayer = new ArrayList<>();
                maxGoldPlayer.add(player.getName());
            }
        }
        //Falls niemand gewonnen hat, der mit am meisten Gold
        if (winningNames.isEmpty()) {
            return maxGoldPlayer;
        }
        return winningNames;
    }

    /** Ermittelt ob schon jemand gewonnen hat
     * @return True: wenn jemand gewonnen hat, False: niemand hat gewonnen
     */
    public boolean hasSomebodyWon() {
        for (Player player : players) {
            if (player.hasWon()) {
                return true;
            }
        }
        return false;
    }

    /** Getter für die Namen der Spieler
     * @return Die Namen als String-Array
     */
    public String[] getNames() {
        String[] playerNames = new String[players.length];
        for (int i = 0; i < players.length; i++) {
            playerNames[i] = players[i].getName();
        }
        return playerNames;
    }

    /** Getter für den Index des Spielers der gerade dran ist.
     * @return der Index als Ganzzahl
     */
    public int getPlayersTurn() {
        return playersTurn;
    }

    /** Setter für den Index des Spielers der gerade dran ist.
     * @param playersTurn der neue Index als Ganzzahl
     */
    public void setPlayersTurn(int playersTurn) {
        this.playersTurn = playersTurn;
    }

    /** Getter für den Besitz des Spielers der gerade dran ist.
     * @return Der Inhalt der Scheune als 'Possession'
     */
    public Possessions getPlayerPossessions() {
        return players[playersTurn].getPossessions();
    }

    /** Getter für das Spielfeld des Spielers der gerade dran ist.
     * @return das Spielfeld Objekt
     */
    public Board getPlayerBoard() {
        return players[playersTurn].getBoard();
    }

    /** Getter für den Markt des Spieles
     * @return der Markt
     */
    public Market getMarket() {
        return market;
    }

    /** Getter für die Spieler
     * @return Eine Kopie der Spieler-Objekte
     */
    public Player[] getPlayers() {
        return players.clone();
    }

    /** Verkauft eine Liste an Gemüse aus der Scheune des aktuellen Spielers am Markt und schreibt ihm das Gold zu.
     * @param vegetablesStrings Die zu verkaufenden Gemüse
     * @return 1: Gold, 2: Anzahl Gemüse, als Ganzzahlen
     * @throws LogicException Wenn der Spieler nicht genug in der Scheune hat
     */
    public Entry<Integer, Integer> sell(List<String> vegetablesStrings) throws LogicException {
        List<Vegetable> content = new ArrayList<>(getPlayerBoard().getBarn().getContentListCopy());
        List<Vegetable> forSale = new ArrayList<>();
        int gold = 0;
        for (String veg : vegetablesStrings) {      //Schauen ob der Spieler genug hat
            Vegetable vegetable = getVegetable(veg);
            if (content.contains(vegetable)) {
                content.remove(vegetable);
                forSale.add(vegetable);
            }
            else {
                throw new LogicException();     //Der Spieler hat nicht genug in der Scheune
            }
        }
        for (Vegetable veg : forSale) {         //Verkaufen
            players[playersTurn].addGold(market.getPrice(veg));
            players[playersTurn].getBarn().removeItem(veg);
            gold += market.getPrice(veg);
        }
        market.soldThisTurn(forSale);
        return Map.entry(gold, forSale.size());
    }

    /** Verkauft alles Gemüse des aktuellen Spielers aus seiner Scheune.
     * @return 1: Gold, 2: Anzahl Gemüse, als Ganzzahlen
     */
    public Entry<Integer, Integer> sellAll() {
        List<Vegetable> vegetables = new ArrayList<>();
        int gold = 0;
        //alles verkaufen
        for (Vegetable veg : players[playersTurn].getPossessions().barnContent().keySet()) {
            for (int i = 0; i < players[playersTurn].getPossessions().barnContent().get(veg); i++) {
                vegetables.add(veg);
                gold += market.getPrice(veg);
            }
        }
        players[playersTurn].getBoard().emptyBarn();     //barn leeren
        players[playersTurn].addGold(gold);
        market.soldThisTurn(new ArrayList<>(vegetables));
        return Map.entry(gold, vegetables.size());
    }

    private Vegetable getVegetable(String input) throws IllegalArgumentException {
        return Vegetable.valueOf(input.toUpperCase());      //Gemüse als String --> Gemüse
    }

    /** Kauft ein Gemüse für den aktuellen Spieler und legt es in seine Scheune.
     * @param vegetable das zu kaufende Gemüse
     * @return der Preis zu dem verkauft wurde als Ganzzahl
     * @throws TooExpensiveException Wenn das gemüse zu teuer ist.
     */
    public int buyVegetable(Vegetable vegetable) throws TooExpensiveException {
        int price = market.getPrice(vegetable);
        int playerGold = players[playersTurn].getGold();
        if (playerGold >= price) {
            players[playersTurn].setGold(playerGold - price);   //Geld abziehen
            players[playersTurn].getBarn().addItem(vegetable);  //Gemüse geben
        }
        else {
            throw new TooExpensiveException();     //zu teuer
        }
        return price;
    }

    /** Kauft eine neue Kachel für den aktuellen Spieler. Die Kachel wird vom Kartenstapel gezogen
     * @param coordinates Die Koordinaten an denen gekauft werden soll. Darf nicht null sein.
     * @return 1: die gekaufte Kachel als Kacheltyp, 2: der bezahlte Preis als Ganzzahl
     * @throws LogicException Wenn die Koordinaten nicht valide sind
     * @throws TooExpensiveException Wenn der Spieler nicht genug Gold hat
     */
    public Entry<TileType, Integer> buyLand(Coordinates coordinates) throws LogicException, TooExpensiveException {
        int price = players[playersTurn].getBoard().getPrice(coordinates);  //Wirft Exception bei schlechten Koordinaten
        int playerGold = players[playersTurn].getGold();
        if (playerGold >= price) {
            //Geld wird im Player abgezogen
            //Tile kaufen
            Tile tile;
            if (cardDeck.getSize() > 0) {
                tile = cardDeck.getTile();
            }
            else {
                throw new LogicException();     //Alle Karten weg
            }
            try {
                players[playersTurn].buyLand(tile, coordinates);
            } catch (IllegalArgumentException e) {
                cardDeck.restoreLastTile();     //weil getTile tile entfernt
                throw new LogicException();
            }
            return Map.entry(tile.getType(), price);         //Paar an zwei Werten
        }
        else {
            throw new TooExpensiveException();
        }
    }

    /** Pflanzt für den aktuellen Spieler ein Gemüse an.
     * @param vegetable das Gemüse, darf nicht null sein.
     * @param coordinates Die Koordinaten an die gepflanzt wird, darf nicht null sein.
     * @throws LogicException wenn das Anpflanzen schiefgeht (Schon etwas auf der Kachel, Kachel existiert nicht)
     */
    public void plantVegetable(Vegetable vegetable, Coordinates coordinates) throws LogicException {
        players[playersTurn].plant(coordinates, vegetable);
    }

    /** Erntet für den aktuellen Spieler.
     * @param coordinates Die Koordinaten an denen geerntet wird, darf nicht null sein.
     * @param number Die Anzahl die geerntet werden soll.
     * @return Das Gemüse das geerntet wurde.
     * @throws LogicException wenn das Ernten schiefgeht (Kachel existiert nicht, nicht genug Gemüse auf der Kachel)
     */
    public Vegetable harvest(Coordinates coordinates, int number) throws LogicException {
        return players[playersTurn].harvest(coordinates, number);
    }

    /** Lässt eine Runde für die anbaubaren Kacheln des aktuellen Spielers vergehen.
     * @return die Anzahl der gewachsenen Gemüse als Ganzzahl
     */
    public int growVegetables() {
        //(nur fur den aktuellen Spieler)
        return players[playersTurn].growVegetables();
    }

    /** Lässt eine Runde für die Scheune des aktuellen Spielers vergehen.
     * @return True: wenn die Scheune verschimmelt ist, False: wenn nichts passiert ist
     */
    public boolean updateBarn() {
        //(nur fur den aktuellen Spieler)
        return players[playersTurn].updateBarn();
    }

    /** Aktualisiert den Markt mit den vorher gespeicherten Gemüse.
     */
    public void updateMarket() {
        market.update();
    }

}
