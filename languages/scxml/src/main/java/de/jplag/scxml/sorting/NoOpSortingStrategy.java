package de.jplag.scxml.sorting;

import java.util.List;

import de.jplag.scxml.parser.model.StatechartElement;

/**
 * A sorting strategy that returns the provided statechart elements unchanged. Can be used in the parser adapter to
 * disable sorting entirely.
 */
public class NoOpSortingStrategy implements SortingStrategy {

    @Override
    public <T extends StatechartElement> List<T> sort(List<T> statechartElements) {
        return statechartElements;
    }
}
