package edu.kit.informatik.ui;

/** Beschreibt die Zustände des Spielstarts
 * @author ujiqk
 * @version 1.0
 */
public enum InputState {
    /** Einlesen der Spieleranzahl
     */
    PLAYER_COUNT,
    /** Einlesen der Namen der Spieler
     */
    PLAYER_NAMES,
    /** einlesen des Startgoldes
     */
    START_GOLD,
    /** Einlesen des für den Gewinn notwendigen Goldes
     */
    WIN_GOLD,
    /** Einlesen des Seeds für das Mischeln
     */
    SHUFFLE,
    /** Alles erfolgreich eingelesen
     */
    READY

}
