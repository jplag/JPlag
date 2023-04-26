package de.jplag.scxml.sorting;

import java.util.List;

import de.jplag.scxml.parser.model.StatechartElement;
import de.jplag.scxml.util.AbstractScxmlVisitor;

/**
 * This sorting strategy sorts the list of StatechartElements by the ordinal of the first token that was to be extracted
 * without affecting the main token stream. This implies that child elements of nested model objects do not change the
 * token order.
 */
public class SimpleSortingStrategy implements SortingStrategy {

    private final AbstractScxmlVisitor visitor;

    /**
     * Constructs a new sorter based on the simple strategy.
     * @param visitor the visitor used to peek tokens
     */
    public SimpleSortingStrategy(AbstractScxmlVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public <T extends StatechartElement> List<T> sort(List<T> statechartElements) {
        statechartElements.sort((v1, v2) -> {
            int v1FirstTokenOrdinal = visitor.peekTokens(v1).get(0);
            int v2FirstTokenOrdinal = visitor.peekTokens(v2).get(0);
            return Integer.compare(v1FirstTokenOrdinal, v2FirstTokenOrdinal);
        });
        return statechartElements;
    }
}
