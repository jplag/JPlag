package de.jplag.scxml.sorting;

import java.util.List;

import de.jplag.scxml.parser.model.StatechartElement;

/**
 * Represents a sorting strategy for sorting statechart elements.
 */
public interface SortingStrategy {

    /**
     * Sorts a list of statechart elements.
     * @param statechartElements the list of statechart elements to sort
     */
    <T extends StatechartElement> List<T> sort(List<T> statechartElements);
}
