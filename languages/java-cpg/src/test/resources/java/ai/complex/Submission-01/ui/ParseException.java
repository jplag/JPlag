package edu.kit.informatik.ui;

/** Fehler beim Parsen einer Benutzereingabe
 * @author ujiqk
 * @version 1.0 */
public class ParseException extends Exception {
    /** Standardkonstruktor
     */
    public ParseException() { }

    /** Standardkonstruktor mit Errornachricht
     * @param message Die Errornachricht als String
     */
    public ParseException(String message) {
        super(message);
    }
}
