package edu.kit.informatik.logic.entitys;

/** Implementiert eine Kreuzung mit Ampelschaltung.
 * Nur von der einen Straße die gerade Grün hat, können Autos abbiegen.
 * @author ujiqk
 * @version 1.0
 */
public class CrossingWithLight extends Crossing {

    private static final int MIN_GREEN_PHASE_DURATION = 3;
    private static final int MAX_GREEN_PHASE_DURATION = 10;
    private final int greenPhaseDuration;
    private int greenPhase;
    private int greenIndicator;

    /** Konstruktor für eine Kreuzung mit Ampelschaltung.
     * @param id Die ID der Kreuzung. Eine Ganzzahl größer gleich null.
     * @param greenPhaseDuration Die Dauer der Grünphase. Eine Ganzzahl. Sollte zwischen 3 und 10 liegen.
     * @throws IllegalArgumentException Falls Parameter nicht im erlaubten Bereich liegen.
     */
    public CrossingWithLight(int id, int greenPhaseDuration) throws IllegalArgumentException {
        super(id);
        if (greenPhaseDuration < MIN_GREEN_PHASE_DURATION || greenPhaseDuration > MAX_GREEN_PHASE_DURATION) {
            throw new IllegalArgumentException("Illegal crossing duration on crossing " + id);
        }
        this.greenPhaseDuration = greenPhaseDuration;
        greenIndicator = 0;
        greenPhase = greenPhaseDuration;
    }

    /** Methode um ein Auto über die Kreuzung abbiegen zu lassen.
     * @param car Das Auto das abbiegen will.
     * @param distance Die Distanz, die das Auto nach der Abbiegung fahren soll.
     * @return True, wenn das Auto abbiegen konnte; False, wenn nicht.
     */
    @Override
    public boolean turnCar(Car car, int distance) {
        //Schauen, wo das Auto herkommt
        int direction = getIncomingStreetsID().indexOf(car.getStreetID());
        //Abbiegen
        if (direction != greenIndicator) {          //Ampel ist Rot
            return false;
        }
        else {                                      //Ampel ist Grün
            return super.turnCar(car, distance);           //--> normal Abbiegen
        }
    }

    /** Aktualisiert die Ampelschaltung um eine Zeiteinheit.
     */
    public void updateLights() {
        greenPhase--;
        if (greenPhase == 0) {
            //Ampel umschalten
            greenPhase = greenPhaseDuration;
            greenIndicator++;
            if (greenIndicator >= getIncomingStreetsID().size()) {
                greenIndicator = 0;
            }
        }
    }

}
