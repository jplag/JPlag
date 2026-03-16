package edu.kit.informatik.logik;

import java.util.ArrayList;
import java.util.List;

/** Klasse die einen Markt des "Queens Farming" Spiels implementiert.
 * Er legt die dynamischen Preise für Gemüse fest.
 *
 * @author ujiqk
 * @version 1.0 */

public class Market {

    private static final int[][] MUSH_CAR_PRICE = {{12, 15, 16, 17, 20}, {3, 2, 2, 2, 1}};
    private static final int[][] TOM_SAL_PRICE = {{3, 5, 6, 7, 9}, {6, 5, 4, 3, 2}};
    private int mushCarIndicator;           //Die Indikatoren, wo im Array der Preis gerade steht
    private int tomSalIndikator;
    private List<Vegetable> updateWaitList;             //List um sich verkaufte Gemüse in einem Zug zu merken

    /** Konstruktor: Initialisiert einen Standardmarkt.
     */
    public Market() {
        mushCarIndicator = 2;
        tomSalIndikator = 2;
        updateWaitList = new ArrayList<>();
    }

    /** Speichert Gemüse zur späteren Marktaktualisierung ab.
     * @param vegetables Liste an Gemüsen. Darf nicht null sein.
     */
    public void soldThisTurn(List<Vegetable> vegetables) {
        updateWaitList.addAll(vegetables);
    }

    /**
     * Aktualisiert den Markt mithilfe der vorher abgespeicherten Gemüse.
     * Verändert dabei die Preise der Gemüse
     */
    public void update() {
        if (updateWaitList == null) {
            return;
        }
        //Alle Einträge durchgehen und doppelte entfernen
        int index = 0;
        while (index < updateWaitList.size() - 1) {
            int index2 = 0;
            Vegetable entry = updateWaitList.get(index);
            while ( index2 < updateWaitList.size()) {
                if (entry == pair(updateWaitList.get(index2))) {
                    updateWaitList.remove(index);       //beide Elemente entfernen
                    updateWaitList.remove(Math.abs(index2 - 1));
                    index = Math.max(0, (index - 1));       //Index anpassen, da Elemente entfernt
                    break;                  //nach einem gefundenem aufhören
                }
                index2++;
            }
            index++;
        }
        //pro zwei übrige die Indikatoren verschieben
        while (!updateWaitList.isEmpty()) {
            Vegetable entry = updateWaitList.get(0);
            updateWaitList.remove(0);
            //Falls ein weiterer Eintrag existiert → entfernen, Preis updaten
            for (int i = 0; i < updateWaitList.size(); i++) {
                if (updateWaitList.get(i) == entry) {
                    adjustPrice(entry);
                    updateWaitList.remove(i);
                    break;
                }
            }
        }
        updateWaitList = new ArrayList<>();
    }

    /** Gibt den aktuellen preis eines Gemüses zurück.
     * @param vegetable das Gemüse
     * @return Der Preis des Gemüses als Ganzzahl
     * @throws IllegalArgumentException wenn das Gemüse null ist
     */
    public int getPrice(Vegetable vegetable) {
        //Preise sind in der Array-struktur festgeschrieben
        if (vegetable == Vegetable.MUSHROOM) {
            return MUSH_CAR_PRICE[0][mushCarIndicator];
        }
        else if (vegetable == Vegetable.CARROT) {
            return MUSH_CAR_PRICE[1][mushCarIndicator];
        }
        else if (vegetable == Vegetable.TOMATO) {
            return TOM_SAL_PRICE[0][tomSalIndikator];
        }
        else if (vegetable == Vegetable.SALAD) {
            return TOM_SAL_PRICE[1][tomSalIndikator];
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    private Vegetable pair(Vegetable vegetable) {
        switch (vegetable) {
            case SALAD -> {
                return Vegetable.TOMATO;
            }
            case TOMATO -> {
                return Vegetable.SALAD;
            }
            case MUSHROOM -> {
                return Vegetable.CARROT;
            }
            case CARROT -> {
                return Vegetable.MUSHROOM;
            }
            default -> throw new IllegalStateException();
        }
    }

    private void adjustPrice(Vegetable vegetable) {
        switch (vegetable) {
            case CARROT -> {
                if (mushCarIndicator < MUSH_CAR_PRICE[0].length - 1) {
                    mushCarIndicator++;
                }
            }
            case MUSHROOM -> {
                if (mushCarIndicator > 0) {
                    mushCarIndicator--;
                }
            }
            case TOMATO -> {
                if (tomSalIndikator > 0) {
                    tomSalIndikator--;
                }
            }
            case SALAD -> {
                if (tomSalIndikator < TOM_SAL_PRICE[0].length) {
                    tomSalIndikator++;
                }
            }
            default -> throw new IllegalStateException();
        }
    }

}
