package edu.kit.informatik.ui;

import edu.kit.informatik.logik.Market;
import edu.kit.informatik.logik.Possessions;
import edu.kit.informatik.logik.QueensFarming;
import edu.kit.informatik.logik.Vegetable;
import edu.kit.informatik.logik.tiles.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Kann aktuelle Spielzustände eines "Queens Farming" Spiels auf der Kommandozeile ausgeben.
 *
 * @author ujiqk
 * @version 1.0
 */
public class GamePrinter {

    private static final String SHOW_BARN = "Barn";
    private static final String SHOW_BARN1 = " (spoils in ";
    private static final String SHOW_BARN2 = " turns)";
    private static final String SHOW_BARN3 = " turn)";
    private static final String TOMATOES = "tomatoes:%s%d";
    private static final String SALADS = "salads:%s%d";
    private static final String CARROTS = "carrots:%s%d";
    private static final String MUSHROOMS = "mushrooms:%s%d";
    private static final String SUMM = "Sum:%s%d";
    private static final String GOLD = "Gold:%s%d";
    private static final String SPACER = "|";
    private static final String SPACER_VERTICAL = "-";
    private static final String BARN_NAME = " B %s ";
    private static final String GARDE_NAME = " G %s ";
    private static final String FIELD_NAME = " Fi %s";
    private static final String LFIELD_NAME = "LFi %s";
    private static final String FOREST_NAME = " Fo %s";
    private static final String LFOREST_NAME = "LFo %s";
    private static final String NO_COUNTDOWN = "*";
    private static final String STRING_FORMAT = "%s";
    private static final String MAP_EMPTY_ROW = "     ";
    private static final String CARROT_SHORT = "  C  ";
    private static final String MUSHROOM_SHORT = "  M  ";
    private static final String SALAD_SHORT = "  S  ";
    private static final String TOMATO_SHORT = "  T  ";
    private static final String AMOUNT_CAPACITY = " %d/%d ";
    private static final String BREAK = System.lineSeparator();
    private final QueensFarming game;

    /**
     * Konstruktor, um die Zustandsausgabe zu initialisieren.
     *
     * @param game Das Spiel von dem die Daten kommen
     */
    public GamePrinter(QueensFarming game) {
        this.game = game;
    }

    /**
     * Druckt den Zustand einer Scheune, des Markts oder des Spielfelds.
     * Scheune und Spielfeld sind vom Spieler, der gerade dran ist.
     *
     * @param input Die Eingabe des Benutzers. Ist sie falsch passiert nichts.
     */
    public void show(String input) {
        if (Pattern.matches("show barn", input)) {
            printPossessions(game.getPlayerPossessions());
        } else if (Pattern.matches("show board", input)) {
            printBoard(game.getPlayerBoard());
        } else if (Pattern.matches("show market", input)) {
            printMarket(game.getMarket());
        }
    }

    private void printPossessions(Possessions possessions) {
        int maxLength = getLongestBarnString(possessions);
        System.out.print(SHOW_BARN);
        //Wenn die Scheune nicht leer ist
        if (!possessions.barnContent().isEmpty()) {
            printBarn(possessions, maxLength);
        }
        System.out.println();
        //Gold
        String print = String.format(GOLD, STRING_FORMAT, possessions.gold());
        String fill = "";
        for (int i = 0; i < maxLength + 2 - print.length(); i++) {        //2 wegen %s
            fill += " ";
        }
        System.out.println(String.format(print, fill));
    }

    private void printBarn(Possessions possessions, int maxLength) {
        if (possessions.spoilTime() == 1) {
            System.out.println(SHOW_BARN1 + possessions.spoilTime() + SHOW_BARN3);
        } else {
            System.out.println(SHOW_BARN1 + possessions.spoilTime() + SHOW_BARN2);
        }
        int sum = 0;
        //Auflistung der Gemüse
        while (!possessions.barnContent().isEmpty()) {      //Alle Elemente durchgehen
            sum = printNextBarnContent(possessions, maxLength, sum);
        }
        //Spacer
        for (int i = 0; i <= maxLength - 2; i++) {      //2 wegen %s im String
            System.out.print(SPACER_VERTICAL);
        }
        System.out.println(SPACER_VERTICAL);
        //Summe
        String print = String.format(SUMM, STRING_FORMAT, sum);
        String fill = "";
        for (int i = 0; i < maxLength + 2 - print.length(); i++) {        //2 wegen %s im String
            fill += " ";
        }
        System.out.println(String.format(print, fill));
    }

    private int printNextBarnContent(Possessions possessions, int maxLength, int total) {
        int sum = total;
        int min = Collections.min(possessions.barnContent().values());
        //HashMap ist nicht sortiert → mit TreeSet alphabetisch sortieren,
        //sortiert in alphabetischer Reihenfolge
        Set<Map.Entry<Vegetable, Integer>> entries = possessions.barnContent().entrySet();
//        Set<Map.Entry<Vegetable, Integer>> sortedEntries = new TreeSet<>(
//                Comparator.comparing((Map.Entry<Vegetable, Integer>::getKey)));
        Set<Map.Entry<Vegetable, Integer>> sortedEntries = new TreeSet<>();
        sortedEntries.addAll(entries);
        //Durch die HasMap durchgehen und kleinstes finden
        for (Map.Entry<Vegetable, Integer> entry : sortedEntries) {
            if (entry.getValue() == min) {
                //Nächster Wert printen
                String print;
                String fill = "";
                switch (entry.getKey()) {
                    case MUSHROOM -> {
                        print = String.format(MUSHROOMS, STRING_FORMAT, min);
                    }
                    case TOMATO -> {
                        print = String.format(TOMATOES, STRING_FORMAT, min);
                    }
                    case SALAD -> {
                        print = String.format(SALADS, STRING_FORMAT, min);
                    }
                    case CARROT -> {
                        print = String.format(CARROTS, STRING_FORMAT, min);
                    }
                    default -> throw new IllegalStateException();
                }
                //Auffüllen
                for (int i = 0; i < maxLength + 2 - print.length(); i++) {        //2 wegen %s im String
                    fill += " ";
                }
                System.out.println(String.format(print, fill));
                sum += min;
                possessions.barnContent().remove(entry.getKey());
            }
        }
        return sum;
    }

    private int getLongestBarnString(Possessions possessions) {
        //größte Zahl bekommen
        int sum = 0;
        for (Integer entry : possessions.barnContent().values()) {
            sum += entry;
        }
        List<Integer> numbers = new ArrayList<>(possessions.barnContent().values());
        numbers.add(possessions.gold());
        numbers.add(sum);
        int biggestNumber = Collections.max(numbers);
        //Längsten String bekommen
        List<String> values = new ArrayList<>();
        if (possessions.barnContent().get(Vegetable.MUSHROOM) != null) {
            values.add(String.format(MUSHROOMS, " ", biggestNumber));
        }
        if (possessions.barnContent().get(Vegetable.TOMATO) != null) {
            values.add(String.format(TOMATOES, " ", biggestNumber));
        }
        if (possessions.barnContent().get(Vegetable.SALAD) != null) {
            values.add(String.format(SALADS, " ", biggestNumber));
        }
        if (possessions.barnContent().get(Vegetable.CARROT) != null) {
            values.add(String.format(CARROTS, " ", biggestNumber));
        }
        values.add(String.format(GOLD, " ", biggestNumber));
        //return values.stream().map(String::length).max(Integer::compareTo).get();
        return values.size();
    }

    private void printBoard(Board board) {
        String output = "";
        //Wir brauchen hier konkrete x werte
        for (int y = 0; y <= board.getMaxHeight(); y++) {
            String row1 = "";
            String row2 = "";
            String row3 = "";
            for (int x = board.getMaxNegativeX(); x <= board.getMaxPositiveX(); x++) {
                //Zeilenweise durch das Spielfeld gehen
                String[] rows = boardRowToString(new String[]{row1, row2, row3}, x, y, board);
                row1 = rows[0];
                row2 = rows[1];
                row3 = rows[2];
            }
            //Falls am Ende ein Feld ist → End-spacer, wenn nicht: auffüllen
            if (!board.isFree(new Coordinates(board.getMaxPositiveX(), y))) {
                row1 += SPACER;
                row2 += SPACER;
                row3 += SPACER;
            } else {
                row1 += " ";
                row2 += " ";
                row3 += " ";
            }
            if (y == 0) {       //Keine leere Zeile
                output = row1 + BREAK + row2 + BREAK + row3 + output;
            } else {
                output = row1 + BREAK + row2 + BREAK + row3 + BREAK + output;
            }
        }
        System.out.println(output);
    }

    private String[] boardRowToString(String[] row, int xCoordinate, int yCoordinate, Board board) {
        String row1 = row[0];
        String row2 = row[1];
        String row3 = row[2];
        //Zeilenweise die Felder durchgehen von minus nach plus
        Tile tile = board.getTile(new Coordinates(xCoordinate, yCoordinate));   //nächste Tile bekommen (ohne Barn)
        if (tile == null) {
            //Spacer oder Leerzeichen
            if (!board.isLeftFree(new Coordinates(xCoordinate, yCoordinate))) {
                row1 += SPACER;
                row2 += SPACER;
                row3 += SPACER;
            } else {
                row1 += " ";
                row2 += " ";
                row3 += " ";
            }
            row1 += MAP_EMPTY_ROW;
            row2 += MAP_EMPTY_ROW;
            row3 += MAP_EMPTY_ROW;
        } else {      //tile != null
            String[] rows = tileToString(new String[]{row1, row2, row3}, tile);
            row1 = rows[0];
            row2 = rows[1];
            row3 = rows[2];
        }
        return new String[]{row1, row2, row3};
    }

    private String[] tileToString(String[] row, Tile tile) {
        String row1 = row[0] + SPACER;
        String row2 = row[1] + SPACER;
        String row3 = row[2] + SPACER;
        String countdown;
        if (tile.getCountdown() == 0) {         //Countdown bekommen
            countdown = NO_COUNTDOWN;
        } else {
            countdown = String.valueOf(tile.getCountdown());
        }
        switch (tile.getType()) {           //Obere Zeile: Namen/Countdown
            case GARDEN -> {
                row1 += String.format(GARDE_NAME, countdown);
            }
            case FIELD -> {
                row1 += String.format(FIELD_NAME, countdown);
            }
            case LARGE_FIELD -> {
                row1 += String.format(LFIELD_NAME, countdown);
            }
            case FOREST -> {
                row1 += String.format(FOREST_NAME, countdown);
            }
            case LARGE_FOREST -> {
                row1 += String.format(LFOREST_NAME, countdown);
            }
            case BARN -> {
                row2 += String.format(BARN_NAME, countdown);
            }
            default -> throw new IllegalStateException();
        }
        if (tile.getType() == TileType.BARN) {      //Barn extra
            row1 += MAP_EMPTY_ROW;
            row3 += MAP_EMPTY_ROW;
        } else {                                    //Alles andere: Pflanzentyp
            CropArea cropTile = (CropArea) tile;
            //Mittlere Zeile Gemüsetyp
            if (cropTile.getPlantType() == null) {
                row2 += MAP_EMPTY_ROW;
            } else {
                switch (cropTile.getPlantType()) {
                    case CARROT -> {
                        row2 += CARROT_SHORT;
                    }
                    case SALAD -> {
                        row2 += SALAD_SHORT;
                    }
                    case TOMATO -> {
                        row2 += TOMATO_SHORT;
                    }
                    case MUSHROOM -> {
                        row2 += MUSHROOM_SHORT;
                    }
                    default -> {
                        row2 += MAP_EMPTY_ROW;
                    }       //Kein Gemüse
                }
            }
            //Letzte Zeile: belegt/Kapazität
            row3 += String.format(AMOUNT_CAPACITY, cropTile.getAmount(), cropTile.getCapacity());
        }
        return new String[]{row1, row2, row3};
    }

    private void printMarket(Market market) {
        System.out.println(String.format(MUSHROOMS, " ", market.getPrice(Vegetable.MUSHROOM)));
        System.out.println(String.format(CARROTS, "    ", market.getPrice(Vegetable.CARROT)));
        System.out.println(String.format(TOMATOES, "   ", market.getPrice(Vegetable.TOMATO)));
        System.out.println(String.format(SALADS, "     ", market.getPrice(Vegetable.SALAD)));
    }

}
