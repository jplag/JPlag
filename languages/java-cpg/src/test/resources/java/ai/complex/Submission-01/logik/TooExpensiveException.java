package edu.kit.informatik.logik;

/** Fehler in der Spiellogik, etwas ist zu teuer um gekauft zu werden.
 * @author ujiqk
 * @version 1.0 */
public class TooExpensiveException extends LogicException {
    /** Standardkonstruktor
     */
    public TooExpensiveException() { }

    /** Standardkonstruktor mit Errornachricht
     * @param message Die Errornachricht als String
     */
    public TooExpensiveException(String message) {
        super(message);
    }
}
