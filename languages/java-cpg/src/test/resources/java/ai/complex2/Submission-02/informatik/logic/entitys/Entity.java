package edu.kit.informatik.logic.entitys;

/** Überklasse für alle Elemente des Straßennetzwerkes.
 * Sie enthält die ID jedes Elements.
 * @author ujiqk
 * @version 1.0
 */
public class Entity {

    private final int id;     //Der Identifikator ist eine Ganzzahl im Bereich [0, Integer.MAX_VALUE]

    /** Erzeugt ein Element mit einer ID
     * @param id Die ID. Muss größer oder gleich 0 sein.
     * @throws IllegalArgumentException Wenn die ID kleiner als 0 ist.
     */
    protected Entity(int id) throws IllegalArgumentException {
        if (id < 0) {
            throw new IllegalArgumentException("Illegal ID " + id);
        }
        this.id = id;
    }

    /** Getter für die ID
     * @return Die ID des Elements. Eine Ganzzahl im Bereich [0, Integer.MAX_VALUE].
     */
    public int getId() {
        return id;
    }

}
