package edu.kit.informatik.logic;

/** Exception die geworfen wird, wenn ein Fehler in der Logik auftritt.
 * @author ujiqk
 * @version 1.0
 */
public class LogicException extends Exception {

    /** Standardkonstruktor mit Errornachricht
     * @param message Die Errornachricht als String
     */
    public LogicException(String message) {
        super(message);
    }
}