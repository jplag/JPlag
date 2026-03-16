package edu.kit.informatik.logic.entitys;

/** Klasse die ein Auto im Netzwerk implementiert.
 * @author ujiqk
 * @version 1.0
 */
public class Car extends Entity {

    private static final int MINIMAL_VELOCITY = 20;
    private static final int MAXIMAL_VELOCITY = 40;
    private static final int MINIMAL_ACCELERATION = 1;
    private static final int MAXIMAL_ACCELERATION = 10;
    private final int desiredVelocity;          //minimal 20, maximal 40
    private final int acceleration;             //minimal 1, maximal 10
    private int desiredDirection;               //0-3
    private int velocity;
    private int streetID;
    private boolean hasTurned;      //bezieht sich auf den letzten Tick, standard ist false
    private boolean wasMoved;       //bezieht sich auf den letzten Tick, standard ist true

    /** Erzeugt ein neues Auto.
     * @param id Die ID des Autos. Eine Ganzzahl größer gleich null.
     * @param desiredVelocity Die Wunschgeschwindigkeit des Autos. Eine Ganzzahl. Sollte zwischen 20 und 40 liegen.
     * @param acceleration Die Beschleunigung des Autos. Eine Ganzzahl. Sollte zwischen 1 und 10 liegen.
     * @param streetId Die ID der Straße auf der das Auto starten soll. Eine Ganzzahl. Sollte größer 0 sein.
     * @throws IllegalArgumentException Falls Parameter nicht im erlaubten Bereich liegen.
     */
    public Car(int id, int desiredVelocity, int acceleration, int streetId) throws IllegalArgumentException {
        super(id);
        if (desiredVelocity < MINIMAL_VELOCITY || desiredVelocity > MAXIMAL_VELOCITY
            || acceleration < MINIMAL_ACCELERATION || acceleration > MAXIMAL_ACCELERATION) {
            throw new IllegalArgumentException("Illegal car parameters on street " + id);
        }
        if (streetId < 0) {
            throw new IllegalArgumentException("Illegal car-street id " + id);
        }
        this.streetID = streetId;
        this.desiredVelocity = desiredVelocity;
        this.acceleration = acceleration;
        desiredDirection = 0;
        velocity = 0;
        hasTurned = false;
        wasMoved = true;
    }

    /** Aktualisiert die Geschwindigkeit des Autos.
     * der minimale Wert aus: Beschleunigung + Geschwindigkeit, Wunschgeschwindigkeit oder Höchstgeschwindigkeit
     * @param maxStreetVelocity Die Höchstgeschwindigkeit der Straße auf der das Auto sich befindet.
     */
    public void updateOneTick(int maxStreetVelocity) {
        //Minimaler Wert aus: Beschleunigung + Geschwindigkeit, Wunschgeschwindigkeit oder Höchstgeschwindigkeit
        velocity = Math.min(maxStreetVelocity, Math.min(desiredVelocity, velocity + acceleration));
    }

    /** Aktualisiert die Wunschrichtung des Autos.
     * Der Wert rotiert von 1 bis 4.
     */
    public void updateTurnDirection() {
        desiredDirection++;
        if (desiredDirection > 3) {
            desiredDirection = 0;
        }
    }

    /** Setzt den Wert der angibt, ob das Auto in diesem Tick abgebogen ist.
     * @param hasTurned Der neue Wert als boolean.
     */
    public void setHasTurned(boolean hasTurned) {
        this.hasTurned = hasTurned;
    }

    /** Getter für den Wert der angibt, ob das Auto in diesem Tick schon abgebogen ist.
     * @return True: Das Auto hat diesem Tick abgebogen. False: Das Auto hat in diesem Tick nicht abgebogen.
     */
    public boolean hasTurned() {
        return hasTurned;
    }

    /** Setzt den Wert der angibt, ob das Auto in diesem Tick bewegt wurde.
     * @param wasMoved Der neue Wert als boolean.
     */
    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }

    /** Getter für den Wert der angibt, ob das Auto in diesem Tick schon bewegt wurde.
     * @return True: Das Auto wurde in diesem Tick bewegt. False: Das Auto wurde in diesem Tick nicht bewegt.
     */
    public boolean wasMoved() {
        return wasMoved;
    }

    /** Setzt die ID der Straße, auf der das Auto sich befindet.
     * @param id Die ID der Straße auf der das Auto sich befindet. Eine Ganzzahl.
     */
    public void setStreetID(int id) {
        streetID = id;
    }

    /** Getter für die ID der Straße, auf der das Auto sich befindet.
     * @return Die ID der Straße auf der das Auto sich befindet. Eine Ganzzahl.
     */
    public int getStreetID() {
        return streetID;
    }

    /** Setzt die Geschwindigkeit des Autos auf einen neuen Wert.
     * @param velocity Die neue Geschwindigkeit des Autos. Eine positive Ganzzahl.
     */
    public void setVelocity(int velocity) {
        if (velocity < 0) {
            throw new IllegalArgumentException("Illegal velocity " + velocity);
        }
        this.velocity = velocity;
    }

    /** Getter für die aktuelle Geschwindigkeit des Autos.
     * @return Die aktuelle Geschwindigkeit des Autos. Eine positive Ganzzahl.
     */
    public int getVelocity() {
        return velocity;
    }

    /** Getter für die Wunschrichtung des Autos.
     * @return Die Wunschrichtung des Autos. Eine Ganzzahl zwischen 0 und 3.
     */
    public int getDesiredDirection() {
        return desiredDirection;
    }
}
