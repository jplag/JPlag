package edu.kit.informatik.logic.entitys;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/** Implementiert eine Straße mit Überholspur für das Straßennetz.
 * Auf ihr können Autos überholen, wenn alle Mindestabstände eingehalten werden.
 * @author ujiqk
 * @version 1.0
 */
public class StreetWithFastLane extends Street {

    /** Erzeugt eine neue Straße mit Überholspur mit den gegebenen Parametern.
     * @param id Die ID der Straße. Eine Ganzzahl größer gleich null.
     * @param length Die Länge der Straße. Eine Ganzzahl.
     * @param maxVelocity Die Höchstgeschwindigkeit auf der Straße. Eine Ganzzahl.
     * @param startNodeID Die ID der Kreuzung, an der die Straße beginnt. Eine Ganzzahl.
     * @param endNodeID Die ID der Kreuzung, an der die Straße endet. Eine Ganzzahl.
     * @throws IllegalArgumentException Wenn Parameter nicht den Vorgaben entsprechen.
     */
    public StreetWithFastLane(int id, int length, int maxVelocity, int startNodeID, int endNodeID)
        throws IllegalArgumentException {
        super(id, length, maxVelocity, startNodeID, endNodeID);
    }

    /** Updatet die Straße um eine Zeiteinheit.
     * Verschiebt dabei die Autos auf der Straße und lässt sie gegeben falls abbiegen oder überholen.
     * Kann erst aufgerufen werden, wenn eine Endkreuzung als Element gesetzt wurde.
     */
    @Override
    public void updateOneTick() {
        if (getEndNode() == null) {
            throw new IllegalArgumentException();
        }
        //alle Autos von hinten (Ende der Straße) durchgehen:
        Set<Integer> carPositions = new TreeSet<>(Comparator.reverseOrder());
        carPositions.addAll(getCars().keySet());
        for (Integer position : carPositions) {
            //mit dem letzten unbewegten Auto anfangen
            Car lastCar = getCars().get(position);
            if (!lastCar.hasTurned()) {
                updateCarOnStreet(lastCar, position);
            }
        }
    }

    private void updateCarOnStreet(Car lastCar, int position) {
        int speed = lastCar.getVelocity();
        int newPosition = position + speed;         //Die Position wo das Auto hin will
        //3 Möglichkeiten: 1: Kreuzung erreicht, 2: fahren, 3: Überholen
        int drivablePosition = drivablePosition(position, newPosition);
        //1 Kreuzung
        if (drivablePosition > getLength()) {
            if (position == getLength()) {
                lastCar.setWasMoved(false);
            }
            //Der verbleibende Weg kann nach dem Abbiegen weitergefahren werden.
            boolean canTurn = getEndNode().turnCar(lastCar, newPosition - getLength());
            if (!canTurn) {                 //Abbiegen war nicht möglich
                getCars().put(getLength(), lastCar);  //Das Auto bleibt am Ende stehen
                if (position != getLength()) {
                    getCars().remove(position);     //Auto war vorher nicht am Ende
                }
                return;
            }
        }
        //2 Strecke fahren
        else {
            //Das Auto fährt so weit es kann oder überholt
            if (drivablePosition == newPosition) {
                getCars().put(drivablePosition, lastCar);
            }
            //3 Überholen oder vorher stehenbleiben
            else {
                overtakeIfPossible(position, newPosition, lastCar);
            }
        }
        if (lastCar.wasMoved() || lastCar.hasTurned()) {
            getCars().remove(position);
        }
    }

    private void overtakeIfPossible(int position, int newPosition, Car car) {
        boolean seenCar = false;
        int nextCarPosition = 0;            //Existiert auf jeden Fall
        int nextNextCarPosition = Math.min(newPosition, getLength()) + getCarSpacing();     //muss nicht existieren
        //1: Positionen der nächsten Autos bekommen
        for (int i = position + 1; i <= newPosition + getCarSpacing(); i++) {
            if (getCars().get(i) != null) {     //Stelle ist besetzt
                if (!seenCar) {
                    nextCarPosition = i;
                    seenCar = true;
                }
                else {
                    nextNextCarPosition = i;
                    break;
                }
            }
        }
        //2: Schauen, bis wo wir fahren können
        if (nextNextCarPosition - nextCarPosition >= getCarSpacing() * 2) {         //Wir können überholen
            getCars().put(nextNextCarPosition - getCarSpacing(), car);
            car.setWasMoved(true);
        }
        else {
            if (nextCarPosition - getCarSpacing() == position) {                    //Stau
                car.setWasMoved(false);
            }
            else {
                getCars().put(nextCarPosition - getCarSpacing(), car);              //bis zum nächsten Fahren
            }
        }
    }

}
