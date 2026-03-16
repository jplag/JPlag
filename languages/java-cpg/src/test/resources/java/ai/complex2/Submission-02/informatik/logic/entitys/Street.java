package edu.kit.informatik.logic.entitys;

import edu.kit.informatik.logic.LogicException;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Implementiert eine Straße im Straßennetzwerk.
 * Auf ihr können Autos fahren.
 * @author ujiqk
 * @version 1.0
 */
public class Street extends Entity {

    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 10000;
    private static final int MIN_VELOCITY = 5;
    private static final int MAX_VELOCITY = 40;
    private static final int CAR_SPACING = 10;
    private final int length;       //minimal 10, maximal 10000
    private final int maxVelocity;  //minimal 5, maximal 40
    private final int startNodeID;
    private final int endNodeID;
    private Crossing endNode;

    //Startknoten: 0 --- length :Endknoten
    private final HashMap<Integer, Car> cars;   //<die Position auf der Straße, das Auto>

    /** Erzeugt eine neue Straße mit den gegebenen Parametern.
     * @param id Die ID der Straße. Eine Ganzzahl größer gleich null.
     * @param length Die Länge der Straße. Eine Ganzzahl.
     * @param maxVelocity Die Höchstgeschwindigkeit auf der Straße. Eine Ganzzahl.
     * @param startNodeID Die ID der Kreuzung, an der die Straße beginnt. Eine Ganzzahl.
     * @param endNodeID Die ID der Kreuzung, an der die Straße endet. Eine Ganzzahl.
     * @throws IllegalArgumentException Wenn Parameter nicht den Vorgaben entsprechen.
     */
    public Street(int id, int length, int maxVelocity, int startNodeID, int endNodeID) throws IllegalArgumentException {
        super(id);
        if (length < MIN_LENGTH || length > MAX_LENGTH
            || maxVelocity < MIN_VELOCITY || maxVelocity > MAX_VELOCITY) {
            throw new IllegalArgumentException("Illegal street parameters on street " + id);
        }
        this.length = length;
        this.maxVelocity = maxVelocity;
        this.endNodeID = endNodeID;
        this.startNodeID = startNodeID;
        cars = new HashMap<>(length);
    }

    /** Updatet die Straße um eine Zeiteinheit. //<die Position auf der Straße, das Auto>
     * Verschiebt dabei die Autos auf der Straße und lässt sie gegeben falls abbiegen oder überholen.
     * Kann erst aufgerufen werden, wenn eine Endkreuzung als Element gesetzt wurde.
     */
    public void updateOneTick() {
        if (endNode == null) {
            throw new IllegalArgumentException();
        }
        //alle Autos durchgehen:
        //Alle Positionen rückwärts sortiert
        Set<Integer> carPositions = new TreeSet<>(Comparator.reverseOrder());
        carPositions.addAll(cars.keySet());
        for (Integer position : carPositions) {
            //mit dem letzten unbewegten Auto anfangen
            Car lastCar = cars.get(position);
            if (!lastCar.hasTurned()) {
                updateCarOnStreet(lastCar, position);
            }
        }
    }

    private void updateCarOnStreet(Car lastCar, int position) {
        int speed = lastCar.getVelocity();
        int newPosition = position + speed;         //Die Position wo das Auto hin will
        //3 Möglichkeiten: 1: Kreuzung erreicht, 2: neue Position frei, 3: Auto im Weg
        int drivablePosition = drivablePosition(position, newPosition);
        if (drivablePosition == position) {
            lastCar.setWasMoved(false);
            return;
        }
        //1 Kreuzung
        if (drivablePosition > length) {
            if (position == length) {
                lastCar.setWasMoved(false);
            }
            //Der verbleibende Weg kann nach dem Abbiegen weitergefahren werden.
            boolean canTurn = endNode.turnCar(lastCar, newPosition - length);
            if (!canTurn) {                 //Abbiegen war nicht möglich
                cars.put(length, lastCar);  //Das Auto bleibt am Ende stehen
                if (position != length) {
                    cars.remove(position);  //Auto war vorher nicht am Ende
                }
                return;
            }
        }
        //2/3 fahren
        else {
            //Das Auto fährt so weit es kann
            cars.put(drivablePosition, lastCar);
        }
        cars.remove(position);
    }

    /** Gibt die Position zurück, an der das Auto beim Fahren aufgrund von Abstandsregeln stehenbleiben muss.
     * @param oldPosition Die Position des Autos vor dem Fahren. Eine Ganzzahl zwischen 0 und length.
     * @param newPosition Die Position, an die das Auto optimalerweise fahren will. Eine Ganzzahl zwischen 0 und length.
     * @return Die Position, an der das Auto stehenbleiben muss. Eine Ganzzahl zwischen 0 und length.
     */
    public int drivablePosition(int oldPosition, int newPosition) {
        //Geht durch alle Koordinaten von der alten Position bis CAR_SPACING mehr als die neue durch.
        int nextCar = -1;
        for (int i = oldPosition + 1; i < newPosition + CAR_SPACING; i++) {
            if (cars.get(i) != null) {
                nextCar = i;
                break;
            }
        }
        if (nextCar == -1) {
            return newPosition;             //Alles frei
        }
        else {
            return nextCar - CAR_SPACING;   //Auto im Weg
        }
    }

    /** Fügt ein Auto an die hinterste freie Stelle der Straße hinzu.
     * Hält dabei Abstand zu den anderen Autos ein.
     * @param car Das Auto, das hinzugefügt werden soll.
     * @throws LogicException Falls die Straße bereits voll ist.
     */
    public void addCarForemost(Car car) throws LogicException {
        //cars: <die Position auf der Straße, die ID des Autos>
        if (cars.size() < length / CAR_SPACING + 1) {
            //Straße noch nicht voll
            int position;
            if (cars.isEmpty()) {
                position = length;
            }
            else {
                position = Collections.min(cars.keySet()) - CAR_SPACING;
            }
            cars.put(position, car);    //fügt ein Auto mit Abstand hinten an
        }
        else {      //Straße voll
            throw new LogicException("Street " + getId() + " is already full");
        }
    }

    /** Fügt ein Auto an der gegebenen Position hinzu.
     * @param car Das Auto, das hinzugefügt werden soll.
     * @param position Die Position, an der das Auto hinzugefügt werden soll. Eine Ganzzahl zwischen 0 und length.
     */
    public void addCar(Car car, int position) {
        cars.put(position, car);
    }

    /** Getter für die Autos auf der Straße.
     * @return Die Autos auf der Straße.
     *          Eine sortierte Map mit den Positionen der Autos als Schlüssel und den Autos als Werte.
     */
    public Map<Integer, Car> getCars() {
        return cars;
    }

    /** Getter für die ID der Kreuzung, an der die Straße beginnt.
     * @return Die ID der Kreuzung, an dem die Straße beginnt.
     */
    public int getStartNodeID() {
        return startNodeID;
    }

    /** Setzt die Kreuzung als Objekt, an der die Straße endet.
     * @param crossing Die Kreuzung, an der die Straße endet. Muss die Kreuzung mit der Konstruktoren-ID sein.S
     */
    public void setEndNode(Crossing crossing) {
        if (endNodeID != crossing.getId()) {
            throw new IllegalArgumentException("Crossing ID does not match");
        }
        endNode = crossing;
    }

    /** Getter für die Kreuzung, an der die Straße endet.
     * @return Die Kreuzung, an der die Straße endet.S
     */
    public Crossing getEndNode() {
        return endNode;
    }

    /** Getter für die ID der Kreuzung, an der die Straße endet.
     * @return Die ID der Kreuzung, an dem die Straße endet. Eine Ganzzahl größer gleich 0.
     */
    public int getEndNodeID() {
        return endNodeID;
    }

    /** Getter für die maximale Geschwindigkeit der Straße.
     * @return Die maximale Geschwindigkeit der Straße. Eine Ganzzahl.
     */
    public int getMaxVelocity() {
        return maxVelocity;
    }

    /** Getter für die Länge der Straße.
     * @return Die Länge der Straße. Eine Ganzzahl.
     */
    public int getLength() {
        return length;
    }

    /** Getter für den mindestabstand zwischen Autos.
     * @return Der mindestabstand zwischen Autos. Eine Ganzzahl.
     */
    public int getCarSpacing() {
        return CAR_SPACING;
    }

    /** Gibt die Position des Autos mit der gegebenen ID zurück.
     * @param id Die ID des Autos. Eine Ganzzahl größer gleich 0.
     * @return Die Position des Autos auf der Straße. Eine Ganzzahl zwischen 0 und length.
     * @throws LogicException Falls das Auto nicht auf der Straße ist.
     */
    public int getCarLocation(int id) throws LogicException {
        //cars: <die Position auf der Straße, die ID des Autos>
        for (Map.Entry<Integer, Car> entry : cars.entrySet()) {
            if (entry.getValue().getId() == id) {
                return entry.getKey();
            }
        }
        throw new LogicException("Car with id " + id + " not found");
    }

}
