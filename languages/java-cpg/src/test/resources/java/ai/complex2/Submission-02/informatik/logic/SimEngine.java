package edu.kit.informatik.logic;

import edu.kit.informatik.logic.entitys.Car;
import edu.kit.informatik.logic.entitys.Crossing;
import edu.kit.informatik.logic.entitys.CrossingWithLight;
import edu.kit.informatik.logic.entitys.Entity;
import edu.kit.informatik.logic.entitys.Street;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Die Klasse SimEngine ist die zentrale Klasse der Programmlogik.
 * Sie verwaltet die Straßen, Autos und Kreuzungen.
 * Mit ihr kann man im Netzwerk Zeit vergehen lassen und Autos bewegen.
 * @author ujiqk
 * @version 1.0
 */
public class SimEngine {

    private final Map<Integer, Street> streetMap;     //Straßen mit IDs
    private final Map<Integer, Car> carMap;
    private final Map<Integer, Crossing> crossingMap;


    /** Erzeugt eine neue SimEngine mit den als Strings gegebenen Straßen, Autos und Kreuzungen.
     * @param cars Die Autos als Strings.
     * @param streets Die Straßen als Strings.
     * @param crossings Die Kreuzungen als Strings.
     * @throws LogicException Wenn das gegebene Straßennetz nicht korrekt ist.
     */
    public SimEngine(List<Entity> cars, List<Entity> streets, List<Entity> crossings)  throws LogicException {
        carMap = new HashMap<>();
        streetMap = new HashMap<>();
        crossingMap = new HashMap<>();
        //Crossings, Cars, Streets überprüfen und Straßennetz erzeugen
        //1: IDs überprüfen
        if (!checkIds(cars) || !checkIds(crossings)) {
            throw new LogicException("wrong Car or Crossing ID format");
        }
        //2: Autos und Straßen Initialisieren
        for (Entity entity : cars) {
            carMap.put(entity.getId(), (Car) entity);
        }
        for (Entity entity : streets) {
            streetMap.put(entity.getId(), (Street) entity);
        }
        //3: Straßen-IDs der Autos kontrollieren, zu viele Autos auf einer Straße kontrollieren
        for (Entity entity : cars) {
            Car car = (Car) entity;
            if (streetMap.get(car.getStreetID()) == null) {
                //wenn die Straßen nicht existiert
                throw new LogicException("Street " + car.getStreetID() + " does not exist");
            }
            streetMap.get(car.getStreetID()).addCarForemost((Car) entity);      //throws Logic Exception wenn voll
        }
        //4: Kreuzungen kontrollieren/Initialisieren:   - keine Kreuzungen ohne Straßen
        //                                              - Kreuzungen max. vier Eingehende und ausgehende Straßen
        for (Entity entity : crossings) {
            initCrossing((Crossing) entity);
        }
        //5: Straßen können invalide Kreuzungen haben
        for (Street street : streetMap.values()) {
            if (!checkStreets(street)) {
                throw new LogicException("Street " + street.getId() + " has invalid crossings.");
            }
        }
        initEntity();
    }

    private boolean checkIds(List<Entity> entities) {
        //True: alles ok; False: mehrfache IDs
        List<Integer> usedIds = new ArrayList<>();
        for (Entity entity : entities) {
            int id = entity.getId();
            if (usedIds.contains(id)) {
                return false;
            }
            else {
                usedIds.add(id);
            }
        }
        return true;
    }

    private void initCrossing(Crossing crossing) throws LogicException {
        crossingMap.put(crossing.getId(), crossing);
        for (Street street : streetMap.values()) {
            if (street.getEndNodeID() == crossing.getId()) {
                crossing.addIncoming(street.getId());    //throws Logic Exception wenn die Kreuzung voll
            }
            if (street.getStartNodeID() == crossing.getId()) {
                crossing.addOutgoing(street.getId());    //throws Logic Exception wenn die Kreuzung voll
            }
        }
        if (!crossing.validCrossing()) {
            throw new LogicException("Crossing " + crossing.getId() + " not valid");      //zu wenig Straßen
        }
    }

    private boolean checkStreets(Street street) {
        //keine gleichen oder nicht existenten Kreuzungen
        return street.getEndNodeID() != street.getStartNodeID()
            && crossingMap.containsKey(street.getStartNodeID())
            && crossingMap.containsKey(street.getEndNodeID());
    }

    private void initEntity() {
        //Den Straßen ihre Kreuzungen geben
        for (Street street : streetMap.values()) {
            street.setEndNode(crossingMap.get(street.getEndNodeID()));
        }
        //Den Kreuzungen ihre ausgehenden Straßen geben
        for (Crossing crossing : crossingMap.values()) {
            List<Street> outgoingStreets = new ArrayList<>();
            List<Integer> outgoingStreetIDs = crossing.getOutgoingStreetsID();
            for (Integer id : outgoingStreetIDs) {
                outgoingStreets.add(streetMap.get(id));
            }
            crossing.setOutgoingStreets(outgoingStreets);
        }
    }

    /** Lässt eine Einheit Zeit vergehen.
     * Aktualisiert alle Autos im Straßennetzwerk.
     */
    public void doOneTick() {
        //1. Aktualisierung aller Straßen aufsteigend dem Identifikator nach
        //1.1 Geschwindigkeit der Autos anpassen
        updateCars();
        //1.2 Autos bewegen:    - nur mit eingehaltenem Abstand
        //                      - Überholen möglich
        //                          - nur ein Auto
        //                          - Abstände einhalten
        //                          - danach nicht abbiegen, davor nicht abbiegen
        //                      - wenn am Ender der Straße und kann noch weiter fahren: Abbiegen
        //                          - Maximalgeschwindigkeit der neuen Straße ignorieren,
        //                              nicht zweimal abbiegen, nicht nochmal aktualisieren
        //                          - Ampeln: nur wenn die Straße grün hat
        //                      - Geschwindigkeit wird beim Nichtbewegen null
        updateStreets();
        //2. Aktualisierung aller Kreuzungen aufsteigend dem Identifikator nach
        updateTrafficLights();                  //alle Ampeln umstellen
    }

    private void updateTrafficLights() {
        for (Crossing crossing : crossingMap.values()) {
            if (crossing.getClass() == CrossingWithLight.class) {
                ((CrossingWithLight) crossing).updateLights();
            }
        }
    }

    private void updateCars() {
        //Aktualisiert die Geschwindigkeit der Autos
        for (Car car : carMap.values()) {
            int streetID = car.getStreetID();
            car.updateOneTick(streetMap.get(streetID).getMaxVelocity());
        }
    }

    private void updateStreets() {
        for (Street street : streetMap.values()) {
            street.updateOneTick();
        }
        //Setzt die Autos wieder auf standard
        for (Car car : carMap.values()) {
            if (!car.wasMoved()) {
                car.setVelocity(0);
            }
            car.setWasMoved(true);  //die Standardwerte
            car.setHasTurned(false);
        }
    }

    /** Getter für ein beliebiges Auto.
     * @param id Die Identifikationsnummer des Autos.
     * @return Das Auto mit der entsprechenden ID. null, wenn es nicht existiert.
     */
    public Car getCar(int id) {
        return carMap.get(id);
    }

    /** Gibt die Position eines Autos auf seiner Straße zurück.
     * @param id Die Identifikationsnummer des Autos.
     * @return Die Position des Autos auf seiner Straße. Als Ganzzahl.
     * @throws LogicException Wenn das Auto nicht existiert.
     */
    public int getCarLocation(int id) throws LogicException {
        return streetMap.get(carMap.get(id).getStreetID()).getCarLocation(id);
    }

}
