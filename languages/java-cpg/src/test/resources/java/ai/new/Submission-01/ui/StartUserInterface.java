package edu.kit.informatik.ui;

import java.util.Scanner;

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
//        while (!quit) {
        String input = inputScanner.nextLine();
//            //'quit' befehl befolgen      (beendet auch, falls der Name eines Spielers "quit" ist)
//            if (Pattern.matches("quit", input)) {
//                break;
//                //Hiernach darf nichts mehr kommen
//            }
//            quit = gameInitUi(input);
//        }
//        if (state == InputState.READY) {
//            GameUI gameUI = new GameUI(new QueensFarming(playerNames, winGold, startGold, seed), inputScanner);
//        }
        inputScanner.close();
        //Ende des Programms
    }

}
