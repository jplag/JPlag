package de.jplag.scxml.util;

import de.jplag.scxml.parser.PeekAdapter;
import de.jplag.scxml.parser.ScxmlParserAdapter;
import de.jplag.scxml.parser.model.State;
import de.jplag.scxml.parser.model.Statechart;
import de.jplag.scxml.parser.model.StatechartElement;
import de.jplag.scxml.parser.model.Transition;
import de.jplag.scxml.parser.model.executable_content.*;
import de.jplag.scxml.sorting.RecursiveSortingStrategy;
import de.jplag.scxml.sorting.SortingStrategy;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Visitor for all StatechartElements in a Statechart object.
 */
public abstract class AbstractScxmlVisitor {

    protected ScxmlParserAdapter adapter;
    protected SortingStrategy sorter;
    protected int depth;

    public AbstractScxmlVisitor(ScxmlParserAdapter adapter) {
        this.adapter = adapter;
        this.sorter = new RecursiveSortingStrategy(this);
    }

    /**
     * Sets the current sorting strategy for this visitor.
     *
     * @param sorter the sorter to use for sorting nested
     *               statechart elements before extracting tokens for them
     */
    public void setSorter(SortingStrategy sorter) {
        this.sorter = sorter;
    }

    /**
     * Visits a statechart element without effecting the main
     * token stream by temporarily swapping out the current parser
     * adapter. Returns a list of collected token type ordinals.
     *
     * @param element the statechart element to visit
     */
    public List<Integer> peekTokens(StatechartElement element) {
        ScxmlParserAdapter prevAdapter = this.adapter;
        PeekAdapter peekAdapter = new PeekAdapter();
        // Switch out the main adapter for the peek adapter
        // so that the main token stream is not affected
        this.adapter = peekAdapter;
        visit(element);
        this.adapter = prevAdapter;
        return peekAdapter.getTokenTypes();
    }

    /**
     * Returns the current depth in the statechart. The depth is incremented
     * whenever child elements of a nested statechart element are visited
     * and decremented after all child elements have been visited.
     *
     * @return the current depth in the statechart
     */
    public int getCurrentStatechartDepth() {
        return depth;
    }

    /**
     * Visits the given statechart element while adding extracted tokens
     * to the current parser adapter.
     *
     * @throws IllegalArgumentException when the statechart element is of a type that is not currently handled
     */
    public final void visit(StatechartElement element) throws IllegalArgumentException {
        Map<Class<? extends StatechartElement>, Consumer<StatechartElement>> visitorMap = Map.of(
                Statechart.class, e -> visitStatechart((Statechart) e),
                State.class, e -> visitState((State) e),
                If.class, e -> visitIf((If) e),
                SimpleExecutableContent.class, e -> visitSimpleExecutableContent((SimpleExecutableContent) e),
                ExecutableContent.class, e -> visitExecutableContent((ExecutableContent) e),
                Transition.class, e -> visitTransition((Transition) e)
        );
        if (!visitorMap.containsKey(element.getClass())) {
            throw new IllegalArgumentException("AbstractScxmlVisitor.visit: unhandled class " + element.getClass());
        }
        visitorMap.get(element.getClass()).accept(element);
    }

    protected abstract void visitStatechart(Statechart statechart);

    protected abstract void visitState(State state);

    protected abstract void visitActions(List<Action> actions);

    protected abstract void visitIf(If if_);

    protected abstract void visitElseIf(ElseIf elseIf);

    protected abstract void visitElse(Else else_);

    protected abstract void visitExecutableContent(ExecutableContent content);

    protected abstract void visitSimpleExecutableContent(SimpleExecutableContent content);

    protected abstract void visitTransition(Transition transition);

}
