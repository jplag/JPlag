package de.jplag.scxml;

import de.jplag.scxml.parser.ScxmlParserAdapter;
import de.jplag.scxml.sorting.SortingStrategy;
import de.jplag.scxml.util.AbstractScxmlVisitor;

/**
 * SCXML parser adapter that allows dynamic configuration of the visitor and sorting strategy. Provides methods to set
 * or update the {@link AbstractScxmlVisitor} and its {@link SortingStrategy}.
 */

public class ConfigurableScxmlParserAdapter extends ScxmlParserAdapter {

    /**
     * Configures the parser adapter with a visitor and a sorting strategy.
     * @param visitor the SCXML visitor to use
     * @param sorter the strategy for sorting elements during parsing
     */

    public void configure(AbstractScxmlVisitor visitor, SortingStrategy sorter) {
        visitor.setSorter(sorter);
        this.visitor = visitor;
    }

    /**
     * Updates the sorting strategy of the current SCXML visitor.
     * @param sortingStrategy the new sorting strategy to apply
     */

    public void setSorter(SortingStrategy sortingStrategy) {
        this.visitor.setSorter(sortingStrategy);
    }
}
