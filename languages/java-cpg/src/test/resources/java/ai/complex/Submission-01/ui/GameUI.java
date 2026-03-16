package edu.kit.informatik.ui;

import edu.kit.informatik.logik.LogicException;
import edu.kit.informatik.logik.Player;
import edu.kit.informatik.logik.QueensFarming;
import edu.kit.informatik.logik.TooExpensiveException;
import edu.kit.informatik.logik.Vegetable;
import edu.kit.informatik.logik.tiles.Coordinates;
import edu.kit.informatik.logik.tiles.TileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Pattern;

/** Klasse die ein "Queens Farming" Spiel in der Kommandozeile implementiert.
 * Mit ihr kann man ein komplettes Spiel spielen
 * @author ujiqk
 * @version 1.0 */
public class GameUI {

    private static final String PLAYER_TURN1 = "It is ";
    private static final String PLAYER_TURN2 = "'s turn!";
    private static final String PLAYER = "Player ";
    private static final String HAS_WON = " has won!";
    private static final String HAVE_WON = "%s and %s have won!";
    private static final String COMMA = ",";
    private static final String CLAMP1 = ")";
    private static final String CLAMP2 = "(";
    private static final String SPOILED = "The vegetables in your barn are spoiled.";
    private static final String VEGETABLE_GROWN = "1 vegetable has grown since your last turn.";
    private static final String VEGETABLES_GROWN = " vegetables have grown since your last turn.";
    private static final String TOMATOES = "tomatoes";
    private static final String SALADS = "salads";
    private static final String CARROTS = "carrots";
    private static final String MUSHROOMS = "mushrooms";
    private static final String COLON = ":";
    private static final String SOLD = "You have sold %d %s for %d gold.";
    private static final String VEGETABLE = "vegetable";
    private static final String VEGETABLES = "vegetables";
    private static final String BOUGHT_ITEM = "You have bought a %s for %d gold.";
    private static final String TOMATO = "tomato";
    private static final String CARROT = "carrot";
    private static final String MUSHROOM = "mushroom";
    private static final String SALAD = "salad";
    private static final String FOREST = "Forest";
    private static final String GARDEN = "Garden";
    private static final String LARGE_FOREST = "Large Forest";
    private static final String FIELD = "Field";
    private static final String LARGE_FIELD = "Large Field";
    private static final String HARVESTED = "You have harvested %d %s.";
    private static final String ERROR_ILLEGAL_COMMAND = "Error: Illegal command";
    private static final String ERROR_NOT_ENOUGH_ITEMS = "Error: Not enough items";
    private static final String ERROR_NOT_ENOUGH_GOLD = "Error: Not enough gold";
    private static final String ERROR_INVALID_COORDINATES = "Error: Coordinates invalid";

    private final QueensFarming game;
    private final Scanner inputScanner;
    private boolean quit;
    private boolean playerFinished;
    private int playerAction;
    private final GamePrinter gamePrinter;

    /**
     * Konstruktor, macht I/O für ein übergebenes "Queens Farming" Spiel.
     *
     * @param game         Das "Queens Farming" Spiel. Darf nicht null sein.
     * @param inputScanner Der java Scanner um Eingaben einzulesen. darf nicht null sein
     */
    public GameUI(QueensFarming game, Scanner inputScanner) {
        this.game = game;
        this.inputScanner = inputScanner;
        playerAction = 0;
        quit = false;
        playerFinished = false;
        gamePrinter = new GamePrinter(game);
        //quit ist Klassen-variable
        while (!quit) {
            //Eine Runde spielen
            round();
        }
    }

    private void playerTurn() {     //max 2 Aktionen oder "end turn"
        System.out.println();
        System.out.println(PLAYER_TURN1 + game.getNames()[game.getPlayersTurn()] + PLAYER_TURN2);
        //Gemüse wächst
        int grownVeg = game.growVegetables();
        //Dinge werden schlecht
        boolean spoiled = game.updateBarn();
        //Print Update der Felder
        if (grownVeg == 1) {
            System.out.println(VEGETABLE_GROWN);
        } else if (grownVeg > 1) {
            System.out.println(grownVeg + VEGETABLES_GROWN);
        }
        //Print update der Barn
        if (spoiled) {
            System.out.println(SPOILED);
        }
        //Auf Befehle warten
        while (!playerFinished) {
            readInput();
            if (playerAction >= 2 || quit) {
                playerFinished = true;
            }
        }
        playerFinished = false;
        playerAction = 0;
        //Markt anpassen, nächster Spieler
        game.updateMarket();
        game.setPlayersTurn(game.getPlayersTurn() + 1);
    }

    private void round() {
        //Spieler haben ihre Züge
        for (int i = 0; i < game.getNames().length; i++) {
            playerTurn();
            if (quit) {
                printWin(game.getWinningPlayers());
                return;
            }
        }
        //Siegesbedingungen prüfen
        if (game.hasSomebodyWon()) {
            printWin(game.getWinningPlayers());
        }
        game.setPlayersTurn(0);
    }

    private void printWin(List<String> winningPlayers) {
        Player[] players = game.getPlayers();
        //Gold der Spieler ausgeben
        for (int i = 0; i < players.length; i++) {
            System.out.println(PLAYER + (i + 1) + " " + CLAMP2 + players[i].getName()
                + CLAMP1 + COLON + " " + players[i].getGold());
        }
        //Gewinner ausgeben
        if (winningPlayers.size() == 1) {
            System.out.println(winningPlayers.get(0) + HAS_WON);
        } else if (winningPlayers.size() >= 2) {
            String output = "";
            for (int i = 0; i < winningPlayers.size() - 2; i++) {
                output = output + winningPlayers.get(i) + COMMA + " ";
            }
            output += winningPlayers.get(winningPlayers.size() - 2);
            System.out.println(String.format(HAVE_WON, output, winningPlayers.get(winningPlayers.size() - 1)));
        }
        quit = true;
    }

    private void readInput() {
        String input = inputScanner.nextLine();
        //quit befehl befolgen
        if (Pattern.matches("quit", input)) {
            quit = true;
            //Hiernach darf nichts mehr kommen
        } else if (Pattern.matches("end turn", input)) {
            playerFinished = true;
        } else if (Pattern.matches("show (barn|market|board)", input)) {
            gamePrinter.show(input);
        }
//        else if (Pattern.matches("sell( mushroom| carrot| tomato | salad)?( mushroom| carrot| tomato| salad)*", input)
//            || Pattern.matches("sell all", input)) {
//            sell(input);
        } else if (Pattern.matches("buy ((vegetable (mushroom|carrot|tomato|salad))|(land -?\\d* \\d*))", input)) {
            buy(input);
        } else if (Pattern.matches("harvest -?\\d* \\d* ([1-9]\\d*)", input)) {
            harvest(input);
        } else if (Pattern.matches("plant -?\\d* \\d* (mushroom|carrot|tomato|salad)", input)) {
            plant(input);
        } else {
            System.err.println(ERROR_ILLEGAL_COMMAND);
        }
    }

    private void sell(String input) {
        Entry<Integer, Integer> soldInfo;                   //1: Gold, 2: Anzahl Gemüse
        List<String> vegetables = new ArrayList<>();
        if (Pattern.matches("sell all", input)) {           //Alles Verkaufen
            soldInfo = game.sellAll();
        }
        else {          //String durchgehen
            Collections.addAll(vegetables, input.split(" "));   //Korrektheit des Strings wird vorher überprüft
            vegetables.remove(0);       //"sell" entfernen
            try {       //Verkaufen
                soldInfo = game.sell(vegetables);
            } catch (LogicException e) {
                System.err.println(ERROR_NOT_ENOUGH_ITEMS);
                return;
            }
        }
        //Output
        if (soldInfo.getValue() == 1) {
            System.out.println(String.format(SOLD, soldInfo.getValue(), VEGETABLE, soldInfo.getKey()));
        }
        else {
            System.out.println(String.format(SOLD, soldInfo.getValue(), VEGETABLES, soldInfo.getKey()));
        }
        playerAction++;
    }

    private void buy(String input) {
        String [] splitInput = input.split(" ");
        if (Pattern.matches("buy (vegetable (mushroom|carrot|tomato|salad))", input)) {
            int gold;
            //Gemüse kaufen
            try {
                gold = game.buyVegetable(Vegetable.valueOf(splitInput[2].toUpperCase()));
            } catch (TooExpensiveException e) {
                System.err.println(ERROR_NOT_ENOUGH_GOLD);
                return;
            }
            //Ausgabe
            printBuyVeg(Vegetable.valueOf(splitInput[2].toUpperCase()), gold);
            playerAction++;
        }
        else if (Pattern.matches("buy land -?\\d* \\d*", input)) {
            Entry<TileType, Integer> output;
            //Land kaufen
            try {
                int xCoord = Integer.parseInt(splitInput[2]);
                int yCoord = Integer.parseInt(splitInput[3]);
                output = game.buyLand(new Coordinates(xCoord, yCoord));
            } catch (TooExpensiveException e) {
                System.err.println(ERROR_NOT_ENOUGH_GOLD);
                return;
            } catch (LogicException e) {
                System.err.println(ERROR_INVALID_COORDINATES);
                return;
            }
            printBuyLAND(output.getKey(), output.getValue());
            playerAction++;
        }
    }

    private void printBuyVeg(Vegetable vegetable, int cost) {
        switch (vegetable) {
            case CARROT -> System.out.println(String.format(BOUGHT_ITEM, CARROT, cost));
            case SALAD -> System.out.println(String.format(BOUGHT_ITEM, SALAD, cost));
            case TOMATO -> System.out.println(String.format(BOUGHT_ITEM, TOMATO, cost));
            case MUSHROOM -> System.out.println(String.format(BOUGHT_ITEM, MUSHROOM, cost));
            default -> throw new IllegalStateException();
        }
    }

    private void printBuyLAND(TileType type, int cost) {
        switch (type) {
            case FOREST -> System.out.println(String.format(BOUGHT_ITEM, FOREST, cost));
            case LARGE_FOREST -> System.out.println(String.format(BOUGHT_ITEM, LARGE_FOREST, cost));
            case FIELD -> System.out.println(String.format(BOUGHT_ITEM, FIELD, cost));
            case LARGE_FIELD -> System.out.println(String.format(BOUGHT_ITEM, LARGE_FIELD, cost));
            case GARDEN -> System.out.println(String.format(BOUGHT_ITEM, GARDEN, cost));
            default -> throw new IllegalStateException();
        }
    }

    private void harvest(String input) {
        String [] splitInput = input.split(" ");
        int xCoord = Integer.parseInt(splitInput[1]);
        int yCoord = Integer.parseInt(splitInput[2]);
        int amount = Integer.parseInt(splitInput[3]);
        Vegetable vegetable;
        try {
            vegetable = game.harvest(new Coordinates(xCoord, yCoord), amount);
        }
        catch (LogicException e) {
            System.err.println(ERROR_INVALID_COORDINATES);
            //Oder Item nicht auf der Kachel/Falsche Anzahl
            return;
        }
        //Output
        if (amount == 1 || amount == 0) {
            switch (vegetable) {
                case MUSHROOM -> System.out.println(String.format(HARVESTED, amount, MUSHROOM));
                case TOMATO -> System.out.println(String.format(HARVESTED, amount, TOMATO));
                case SALAD -> System.out.println(String.format(HARVESTED, amount, SALAD));
                case CARROT -> System.out.println(String.format(HARVESTED, amount, CARROT));
                default -> throw new IllegalStateException();
            }
        }
        else {
            switch (vegetable) {        //Mehrzahlnamen sind im vorderen Teil des Strings für die Barn
                case MUSHROOM -> System.out.println(String.format(HARVESTED, amount, MUSHROOMS));
                case TOMATO -> System.out.println(String.format(HARVESTED, amount, TOMATOES));
                case SALAD -> System.out.println(String.format(HARVESTED, amount, SALADS));
                case CARROT -> System.out.println(String.format(HARVESTED, amount, CARROTS));
                default -> throw new IllegalStateException();
            }
        }
        playerAction++;
    }

    private void plant(String input) {
        String [] splitInput = input.split(" ");
        int xCoord = Integer.parseInt(splitInput[1]);
        int yCoord = Integer.parseInt(splitInput[2]);
        try {
            game.plantVegetable(Vegetable.valueOf(splitInput[3].toUpperCase()), new Coordinates(xCoord, yCoord));
        }
        catch (LogicException e) {
            System.err.println(ERROR_INVALID_COORDINATES);
            //Oder Item nicht in der Barn
            return;
        }
        playerAction++;
    }

}
