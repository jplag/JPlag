package edu.kit.informatik.ui;

import edu.kit.informatik.logik.QueensFarming;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Klasse die den Start eines "Queens Farming" Spiels auf der Kommandozeile implementiert.
 * Alle zum Start wichtigen Parameter werden eingelesen und danach das Spiel gestartet.
 *
 * @author ujiqk
 * @version 1.0
 */
public class StartUserInterface {
    private static final String PLAYER_QUESTION = "How many players?";
    private static final String NAME_QUESTION = "Enter the name of player ";
    private static final String START_GOLD_QUESTION = "With how much gold should each player start?";
    private static final String WIN_GOLD_QUESTION = "With how much gold should a player win?";
    private static final String SEED_QUESTION = "Please enter the seed used to shuffle the tiles:";
    private static final String COLON = ":";
    private static final String PIXEL_ART = """
                                       _.-^-._    .--.   \s
                                    .-'   _   '-. |__|   \s
                                   /     |_|     \\|  |   \s
                                  /               \\  |   \s
                                 /|     _____     |\\ |   \s
                                  |    |==|==|    |  |   \s
              |---|---|---|---|---|    |--|--|    |  |   \s
              |---|---|---|---|---|    |==|==|    |  |   \s
            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            ^^^^^^^^^^^^^^^ QUEENS FARMING ^^^^^^^^^^^^^^^
            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^""";
    private static final String ERROR_ILLEGAL_NUMBER_OF_PLAYERS = "Error: Illegal number of players";
    private static final String ERROR_ILLEGAL_PLAYER_NAME = "Error: Illegal player name";
    private static final String ERROR_ILLEGAL_GOLD_AMOUNT = "Error: Illegal amount of gold";
    private static final String ERROR_SEED = "Error: Illegal shuffle seed";

    //Wichtige Sachen um das Spiel zu erzeugen
    private InputState state;
    private int playerCount;
    private int startGold;
    private int winGold;
    private int seed;
    private String[] playerNames;

    /**
     * Konstruktor, liest Eingabe ein und übergibt sie gesammelt an das Spiel User Interface
     * eingelesene Eingaben: Spieleranzahl, Namen, Startgold, Gold um zu gewinnen, Seed zum Mischeln
     */
    public StartUserInterface() {
        System.out.println(PIXEL_ART);

        //Status des Einlesens
        state = InputState.PLAYER_COUNT;
        playerCount = 0;
        playerNames = null;

        System.out.println(PLAYER_QUESTION);

        Scanner inputScanner = new Scanner(System.in);
        //Schleife um Eingaben einlesen
        boolean quit = false;
        while (!quit) {
            String input = inputScanner.nextLine();
            //'quit' befehl befolgen      (beendet auch, falls der Name eines Spielers "quit" ist)
            if (Pattern.matches("quit", input)) {
                break;
                //Hiernach darf nichts mehr kommen
            }
            quit = gameInitUi(input);
        }
        if (state == InputState.READY) {
            GameUI gameUI = new GameUI(new QueensFarming(playerNames, winGold, startGold, seed), inputScanner);
        }
        inputScanner.close();
        //Ende des Programms
    }

    /**
     * Liest eine Benutzereingabe ein und parst sie je nach aktuellem Eingabestatus.
     * Ist die Eingabe erfolgreich wird in den nächsten Eingabestatus gesprungen.
     *
     * @param input Die Benutzereingabe, ungefiltert
     * @return True: wenn die Eingabe komplett fertig ist
     */
    private boolean gameInitUi(String input) {
        if (state == InputState.PLAYER_COUNT) {      //Einlesen der Spielerzahl
            state = readPlayerCount(input);
        }
        //Einlesen der Namen
        else if (state == InputState.PLAYER_NAMES) {
            state = readPlayerNames(input);
        }
        //Einlesen des Startgoldes
        else if (state == InputState.START_GOLD) {
            state = readStartGold(input);
        }
        //Einlesen des Gewinngoldes
        else if (state == InputState.WIN_GOLD) {
            state = readWinGold(input);
        }
        //Einlesen des Seeds
        else if (state == InputState.SHUFFLE) {
            try {
                seed = Integer.parseInt(input);
                //Speichern und in den nächsten state wechseln
                state = InputState.READY;
                return true;            //das Einlesen ist fertig
            } catch (NumberFormatException e) {
                System.err.println(ERROR_SEED);
            }
        }
        return false;
    }

    private InputState readWinGold(String input) {
        try {
            winGold = parseNumberGreaterOne(input);
        } catch (NumberFormatException | ParseException e) {
            System.err.println(ERROR_ILLEGAL_GOLD_AMOUNT);
            return state;
        }
        System.out.println(SEED_QUESTION);      //zum nächsten Wechseln
        return InputState.SHUFFLE;
    }

    private InputState readStartGold(String input) {
        try {
            startGold = parsePositiveNumber(input);
        } catch (NumberFormatException | ParseException e) {
            System.err.println(ERROR_ILLEGAL_GOLD_AMOUNT);
            return state;
        }
        System.out.println(WIN_GOLD_QUESTION);
        return InputState.WIN_GOLD;
    }

    private InputState readPlayerNames(String input) {
        try {
            playerNames[playerNames.length - playerCount] = parseNames(input);
            playerCount--;              //Player count als Zählvariable
        } catch (ParseException e) {
            System.err.println(ERROR_ILLEGAL_PLAYER_NAME);
            return state;               //Nicht nochmal fragen
        }
        if (playerCount <= 0) {
            System.out.println(START_GOLD_QUESTION);
            return InputState.START_GOLD;       //zum nächsten Wechseln
        } else {
            System.out.println(NAME_QUESTION + (playerNames.length - playerCount + 1) + COLON);
        }
        return state;
    }

    private InputState readPlayerCount(String input) {
        try {
            playerCount = parseNumberGreaterOne(input);
        } catch (NumberFormatException | ParseException e) {
            System.err.println(ERROR_ILLEGAL_NUMBER_OF_PLAYERS);
            return state;
        }
        playerNames = new String[playerCount];
        System.out.println(NAME_QUESTION + 1 + COLON);      //Erste Frage nach Spieleranzahl
        return InputState.PLAYER_NAMES;
    }

    private String parseNames(String input) throws ParseException {
        if (Pattern.matches("[a-zA-Z]+", input)) {
            return input;
        } else {
            throw new ParseException();
        }
    }

    private int parseNumberGreaterOne(String input) throws NumberFormatException, ParseException {
        int number = Integer.parseInt(input);
        if (number < 1) {
            throw new ParseException();      //Ist das schön?
        }
        return number;
    }

    private int parsePositiveNumber(String input) throws NumberFormatException, ParseException {
        int number = Integer.parseInt(input);
        if (number < 0) {
            throw new ParseException();      //Ist das schön?
        }
        return number;
    }

}
