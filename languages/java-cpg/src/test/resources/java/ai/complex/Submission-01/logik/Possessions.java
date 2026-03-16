package edu.kit.informatik.logik;

import java.util.Map;

/** Record der alle wichtigen Werte eines Spielers speichern kann.
 *
 * @param barnContent Die in der Scheune gespeicherten Gemüse
 * @param gold Das aktuelle Gold
 * @param spoilTime Die Zeit in Runden bis die Scheune verschimmelt
 * @author ujiqk
 * @version 1.0 */
public record Possessions(Map<Vegetable, Integer> barnContent, int gold, int spoilTime) { }
