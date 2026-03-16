package edu.kit.informatik.logik.tiles;

import edu.kit.informatik.logik.LogicException;
import edu.kit.informatik.logik.Vegetable;

/** Klasse, die ein Feld auf dem angebaut werden kann, des "Queens Farming" Spiels implementiert.
 * @author ujiqk
 * @version 1.0 */
public class CropArea extends Tile {
    private static final int CARROT_GROW_TIME = 1;
    private static final int SALAD_GROW_TIME = 2;
    private static final int MUSHROOM_GROW_TIME = 4;
    private static final int TOMATO_GROW_TIME = 3;
    private static final int GARDEN_CAPACITY = 2;
    private static final int SMALL_CAPACITY = 4;
    private static final int LARGE_CAPACITY = 8;

    private final TileType type;    //Art des Feldes
    private final int capacity;
    private int countdown;      //Wenn nichts wächst: Countdown null

    //Die angepflanzten Gemüse
    private Vegetable plantType;
    private int amount;

    /** Konstruktor, erzeugt aus dem Kacheltyp ein Feld auf dem angebaut werden kann.
     * @param type der Kacheltyp der die Kachel haben soll. Darf nicht null oder eine Scheune sein.
     */
    public CropArea(TileType type) {
        this.type = type;
        switch (type) {     //Der Typ legt Kapazität fest
            case FIELD, FOREST -> {
                capacity = SMALL_CAPACITY;
            }
            case GARDEN -> {
                capacity = GARDEN_CAPACITY;
            }
            case LARGE_FIELD, LARGE_FOREST -> {
                capacity = LARGE_CAPACITY;
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private void grown() {
        if (plantType == null) {
            return;     //Wenn nichts angebaut ist
        }
        if (capacity > (amount * 2)) {      //Die Pflanzen sind gewachsen und auf der Kachel ist Platz
            amount *= 2;
            countdown = timeToGrow(plantType);
        }
        else {                              //die Kachel ist voll
            amount = capacity;
            countdown = 0;
        }
    }

    /** Pflanzt ein Gemüse auf der Kachel an
     * @param vegetable Das Gemüse was angepflanzt wird. Darf nicht null sein.
     * @throws LogicException falls schon etwas auf der Kachel wächst oder der Kacheltyp das Gemüse nicht zulässt
     */
    public void plant(Vegetable vegetable) throws LogicException {
        if (plantType != null) {
            throw new LogicException();     //schon belegt
        }
        //Vegetable Type checken
        if (!canPlant(vegetable)) {
            throw new LogicException();     //falscher Typ
        }
        plantType = vegetable;
        amount = 1;
        countdown = timeToGrow(vegetable);
    }

    private boolean canPlant(Vegetable vegetable) {
        //Ob das Gemüse angepflanzt werden kann.
        if (type == TileType.GARDEN) {
            return true;
        } else if (type == TileType.FOREST || type == TileType.LARGE_FOREST) {
            return vegetable == Vegetable.CARROT || vegetable == Vegetable.MUSHROOM;

        } else if (type == TileType.FIELD || type == TileType.LARGE_FIELD) {
            return vegetable == Vegetable.CARROT || vegetable == Vegetable.SALAD || vegetable == Vegetable.TOMATO;
        }
        return false;
    }

    private int timeToGrow(Vegetable vegetable) {
        //Wachstumszeiten der Gemüse
        switch (vegetable) {
            case MUSHROOM -> {
                return MUSHROOM_GROW_TIME;
            }
            case SALAD -> {
                return SALAD_GROW_TIME;
            }
            case CARROT -> {
                return CARROT_GROW_TIME;
            }
            case TOMATO -> {
                return TOMATO_GROW_TIME;
            }
            default -> throw new IllegalStateException();
        }
    }

    /** Getter für die Anzahl der Gemüse auf der Kachel
     * @return die Anzahl als Ganzzahl
     */
    public int getAmount() {
        return amount;
    }

    /** Erntet Gemüse von der Kachel. Dabei wird das Gemüse von der Kachel entfernt.
     * @param number Die Anzahl die entfernt werden soll. Eine Ganzzahl.
     * @return Das Gemüse (Vegetable) das geerntet wurde
     * @throws LogicException falls nichts angebaut ist
     */
    public Vegetable harvest(int number) throws LogicException {
        //Number muss positiv sein
        if (plantType != null && amount >= number) {
            amount -= number;
            Vegetable vegType = plantType;
            if (countdown == 0) {       //Falls die Kachel voll war
                countdown = timeToGrow(plantType);
            }
            if (amount <= 0) {          //Wenn die Kachel jetzt leer ist
                plantType = null;
                amount = 0;
                countdown = 0;
            }
            return vegType;
        }
        else {
            throw new LogicException();     //Wenn nichts angebaut ist
        }
    }

    /** Lässt eine Spielrunde vergehen
     * @return True: wenn etwas gewachsen ist, False: wenn nicht
     */
    @Override
    public boolean updateTimer() {
        if (plantType == null || countdown == 0) {
            return false;     //Wenn nichts angebaut ist, oder Kachel voll
        }
        countdown--;
        if (countdown <= 0) {
            //Pflanzen sind gewachsen
            grown();
            return true;
        }
        return false;
    }

    @Override
    public TileType getType() {
        return type;
    }

    @Override
    public int getCountdown() {
        return countdown;
    }

    /** Getter für den aktuelle angebauten Gemüsetyp
     * @return der Gemüsetyp, kann null sein, wenn nichts angebaut ist
     */
    public Vegetable getPlantType() {
        return plantType;       //kann und soll null sein.
    }

    /** Getter für die Kapazität der Kachel.
     * @return die Kapazität als Ganzzahl
     */
    public int getCapacity() {
        return capacity;
    }

}
