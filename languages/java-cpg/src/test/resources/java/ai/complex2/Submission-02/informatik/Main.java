package edu.kit.informatik;

import edu.kit.informatik.ui.SimUi;

/** Das Programm simuliert ein vereinfachtes Straßennetzwerk.
 * Das Netzwerk wird aus Dateien eingelesen.
 * Der Zustand kann über das Terminal ausgegeben werden.
 * @author ujiqk
 * @version 1.0
 */
public final class Main {

    private Main() {
        throw new IllegalStateException();
    }

    /** Main Methode die das Programm startet.
     * @param args Der Kommandozeilenparameter. Wird vom Programm ignoriert.
     */
    public static void main(String[] args) {

        //Simulation starten
        SimUi simulationUI = new SimUi();
    }

}
