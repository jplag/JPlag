package edu.kit.informatik.logik;

/** Logischer Fehler in der Spiellogik
 * @author ujiqk
 * @version 1.0 */
public class LogicException extends Exception {
    /** Standardkonstruktor
     */
    public LogicException() { }

    /** Standardkonstruktor mit Errornachricht
     * @param message Die Errornachricht als String
     */
    public LogicException(String message) {
        super(message);
    }
}
