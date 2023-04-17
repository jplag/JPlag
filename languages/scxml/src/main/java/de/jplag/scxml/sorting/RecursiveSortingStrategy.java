package de.jplag.scxml.sorting;

import java.util.List;

import de.jplag.scxml.parser.PeekAdapter;
import de.jplag.scxml.parser.model.StatechartElement;
import de.jplag.scxml.util.AbstractScxmlVisitor;

/**
 * This sorting strategy lexicographically sorts the list of statechart elements by the token streams they were to
 * produce without affecting the main token stream. This implies that child elements of nested model objects have an
 * effect on the final token order. The tokens are sorted by the ordinals of their types using
 * {@link PeekAdapter#compareTokenTypeLists(List, List)}.
 */
public class RecursiveSortingStrategy implements SortingStrategy {

    private final AbstractScxmlVisitor visitor;

    /**
     * Constructs a new sorter based on the recursive strategy.
     * @param visitor the visitor used to peek tokens
     */
    public RecursiveSortingStrategy(AbstractScxmlVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public <T extends StatechartElement> List<T> sort(List<T> statechartElements) {
        statechartElements.sort((v1, v2) -> {
            List<Integer> v1TokenOrdinals = visitor.peekTokens(v1);
            List<Integer> v2TokenOrdinals = visitor.peekTokens(v2);
            return PeekAdapter.compareTokenTypeLists(v1TokenOrdinals, v2TokenOrdinals);
        });
        return statechartElements;
    }
}
