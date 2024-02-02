package de.jplag.scxml.sorting;

import java.util.List;

import de.jplag.scxml.parser.model.StatechartElement;

/**
 * Represents a sorting strategy for sorting statechart elements.
 */
public interface SortingStrategy {

    /**
     * Sorts a list of statechart elements.
     * @param <T> the type of statechart elements in the list, which must extend {@link StatechartElement}
     * @param statechartElements the list of statechart elements to sort
     * @return a sorted list of statechart elements, based on the implemented sorting strategy
     */
    <T extends StatechartElement> List<T> sort(List<T> statechartElements);
}
