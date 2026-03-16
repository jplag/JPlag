package edu.kit.informatik.logic.entitys;

import edu.kit.informatik.logic.LogicException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Implementiert einen Kreisel im Straßennetz.
 * Ein Kreisel ist eine Kreuzung ohne Ampel, an der alle gleichzeitig abbiegen können.
 * @author ujiqk
 * @version 1.0
 */
public class Crossing extends Entity {

    private static final int MAX_CONNECTED_STREETS = 4;

    private final List<Integer> outgoingStreetsID;     //Die Straßen
    private List<Street> outgoingStreets;
    private final List<Integer> incomingStreetsID;

    /** Konstruktor der eine neue Kreuzung ohne verbundene Straßen erstellt.
     * @param id Die ID der Kreuzung. Eine Ganzzahl größer gleich null.
     * @throws IllegalArgumentException Falls Parameter nicht im erlaubten Bereich liegen.
     */
    public Crossing(int id) throws IllegalArgumentException {
        super(id);
        outgoingStreetsID = new ArrayList<>();
        incomingStreetsID = new ArrayList<>();
    }

    /** Methode um ein Auto über die Kreuzung abbiegen zu lassen.
     * @param car Das Auto das abbiegen soll.
     * @param distance Die Distanz, die das Auto nach der Abbiegung fahren soll.
     * @return True, wenn das Auto abbiegen konnte; False, wenn nicht.
     */
    public boolean turnCar(Car car, int distance) {
        if (outgoingStreets == null) {              //outgoingStreets muss gesetzt sein
            throw new IllegalArgumentException();
        }
        int direction = car.getDesiredDirection();  //falls die Richtung nicht existiert
        if (outgoingStreets.size() <= direction) {
            direction = 0;
        }
        Street street = outgoingStreets.get(direction);
        //Versuchen abzubiegen                                      //-1 da drivablePosition die alte position ignoriert
        int drivableDistance = street.drivablePosition(-1, distance);
        if (drivableDistance < 0) {
            return false;               //Abbiegen nicht möglich
        }
        else {                  //Abbiegen
            street.addCar(car, Math.min(street.drivablePosition(0, distance), street.getLength()));
            car.setHasTurned(true);
            car.updateTurnDirection();
            car.setStreetID(street.getId());
            if (drivableDistance > 0) {
                car.setWasMoved(true);
            }
            return true;
        }
    }

    /** Setzt die ausgehenden Straßen der Kreuzung.
     * @param outgoingStreets Alle ausgehenden Straßen der Kreuzung als geordnete Liste.
     *                        Muss mit den IDs der ausgehenden Straßen übereinstimmen.
     */
    public void setOutgoingStreets(List<Street> outgoingStreets) {
        for (int i = 0; i < outgoingStreets.size(); i++) {
            if (outgoingStreets.get(i).getId() != outgoingStreetsID.get(i)) {
                throw new IllegalArgumentException();
            }
        }
        this.outgoingStreets = outgoingStreets;
    }

    /** Getter für die eingehenden StraßenIDs.
     * @return Eine Liste mit den IDs aller eingehenden Straßen.
     */
    public List<Integer> getIncomingStreetsID() {
        return Collections.unmodifiableList(incomingStreetsID);
    }

    /** Getter für die ausgehenden StraßenIDs.
     * @return Eine Liste mit den IDs aller ausgehenden Straßen.
     */
    public List<Integer> getOutgoingStreetsID() {
        return Collections.unmodifiableList(outgoingStreetsID);
    }

    /** Fügt der Kreuzung eine eingehende Straße hinzu.
     * @param street Die ID der eingehenden Straße.
     * @throws LogicException Wenn die Kreuzung zu viele eingehende Straßen hat.
     */
    public void addIncoming(int street) throws LogicException {
        if (incomingStreetsID.size() < MAX_CONNECTED_STREETS) {
            incomingStreetsID.add(street);
        }
        else {
            throw new LogicException("Too much Streets for crossing: " + getId());
        }
    }

    /** Fügt der Kreuzung eine ausgehende Straße hinzu.
     * @param street Die ID der ausgehenden Straße.
     * @throws LogicException Wenn die Kreuzung zu viele ausgehende Straßen hat.
     */
    public void addOutgoing(int street) throws LogicException {
        if (outgoingStreetsID.size() < MAX_CONNECTED_STREETS) {
            outgoingStreetsID.add(street);
        }
        else {
            throw new LogicException("Too much Streets for crossing: " + getId());
        }
    }

    /** Prüft, ob die Kreuzung gültig ist.
     * Sie ist gültig, wenn sie mindestens eine eingehende und eine ausgehende Straße hat.
     * Außerdem darf sie nicht mehr als 4 eingehende und 4 ausgehende Straßen haben.
     * @return True, wenn die Kreuzung gültig ist; False, wenn nicht.
     */
    public boolean validCrossing() {
        return !outgoingStreetsID.isEmpty() && !incomingStreetsID.isEmpty()
            && outgoingStreetsID.size() <= MAX_CONNECTED_STREETS && incomingStreetsID.size() <= MAX_CONNECTED_STREETS;
    }

}
