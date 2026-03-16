package edu.kit.informatik;

import edu.kit.informatik.ui.StartUserInterface;

/**
 * Main Klasse eines "Queens Farming" Spieles das über die Kommandozeile gespielt werden kann.
 *
 * @author ujiqk
 * @version 1.0
 */
public final class Main {

    private static final String ERROR_ARGUMENTS_NOT_SUPPORTED = "Error: Commandline arguments not supported";

    private Main() {
        throw new IllegalStateException();
    }

    /**
     * Startet das Spiel
     *
     * @param args Kommandozeilenparameter muss leer sein.
     */
    public static void main(String[] args) {

        if (args.length != 0) {
            System.err.println(ERROR_ARGUMENTS_NOT_SUPPORTED);
            return;
        }

        //Spiel starten
        StartUserInterface ui1 = new StartUserInterface();
    }
    
}
