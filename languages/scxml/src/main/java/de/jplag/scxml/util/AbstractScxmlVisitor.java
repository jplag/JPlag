package de.jplag.scxml.util;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.scxml.parser.PeekAdapter;
import de.jplag.scxml.parser.ScxmlParserAdapter;
import de.jplag.scxml.parser.model.State;
import de.jplag.scxml.parser.model.Statechart;
import de.jplag.scxml.parser.model.StatechartElement;
import de.jplag.scxml.parser.model.Transition;
import de.jplag.scxml.parser.model.executable_content.Action;
import de.jplag.scxml.parser.model.executable_content.Else;
import de.jplag.scxml.parser.model.executable_content.ElseIf;
import de.jplag.scxml.parser.model.executable_content.ExecutableContent;
import de.jplag.scxml.parser.model.executable_content.If;
import de.jplag.scxml.parser.model.executable_content.SimpleExecutableContent;
import de.jplag.scxml.sorting.RecursiveSortingStrategy;
import de.jplag.scxml.sorting.SortingStrategy;

/**
 * Visitor for all StatechartElements in a Statechart object.
 */
public abstract class AbstractScxmlVisitor {

    /**
     * The current parser adapter that is called to add new tokens.
     */
    protected ScxmlParserAdapter adapter;

    /**
     * The sorting strategy to use for visiting nested statechart elements.
     */
    protected SortingStrategy sorter;

    /**
     * The current depth in the statechart.
     */
    protected int depth;

    /**
     * Constructs a new ScxmlVisitor with the given adapter to use for collecting tokens and using the recursive sorting
     * strategy.
     * @param adapter the adapter used for collecting tokens
     */
    protected AbstractScxmlVisitor(ScxmlParserAdapter adapter) {
        this.adapter = adapter;
        this.sorter = new RecursiveSortingStrategy(this);
    }

    /**
     * Sets the current sorting strategy for this visitor.
     * @param sorter the sorter to use for sorting nested statechart elements before extracting tokens for them
     */
    public void setSorter(SortingStrategy sorter) {
        this.sorter = sorter;
    }

    /**
     * Visits a statechart element without effecting the main token stream by temporarily swapping out the current parser
     * adapter. Returns a list of collected token type ordinals.
     * @param element the statechart element to visit
     * @return a list of visited token type ordinals
     */
    public List<Integer> peekTokens(StatechartElement element) {
        ScxmlParserAdapter previousAdapter = this.adapter;
        PeekAdapter peekAdapter = new PeekAdapter();
        // Switch out the main adapter for the peek adapter
        // so that the main token stream is not affected
        this.adapter = peekAdapter;
        visit(element);
        this.adapter = previousAdapter;
        return peekAdapter.getTokenTypes();
    }

    /**
     * Returns the current depth in the statechart. The depth is incremented whenever child elements of a nested statechart
     * element are visited and decremented after all child elements have been visited.
     * @return the current depth in the statechart
     */
    public int getCurrentStatechartDepth() {
        return depth;
    }

    /**
     * Visits the given statechart element while adding extracted tokens to the current parser adapter.
     * @param element the statechart element to visit
     * @throws IllegalArgumentException when the statechart element is of a type that is not currently handled
     */
    public final void visit(StatechartElement element) throws IllegalArgumentException {
        Map<Class<? extends StatechartElement>, Consumer<StatechartElement>> visitorMap = Map.of(Statechart.class,
                e -> visitStatechart((Statechart) e), State.class, e -> visitState((State) e), If.class, e -> visitIf((If) e),
                SimpleExecutableContent.class, e -> visitSimpleExecutableContent((SimpleExecutableContent) e), ExecutableContent.class,
                e -> visitExecutableContent((ExecutableContent) e), Transition.class, e -> visitTransition((Transition) e));
        if (!visitorMap.containsKey(element.getClass())) {
            throw new IllegalArgumentException("AbstractScxmlVisitor.visit: unhandled class " + element.getClass());
        }
        visitorMap.get(element.getClass()).accept(element);
    }

    /**
     * Recursively visits a statechart.
     * @param statechart the statechart to visit
     */
    protected abstract void visitStatechart(Statechart statechart);

    /**
     * Recursively visits a state.
     * @param state the state to visit
     */
    protected abstract void visitState(State state);

    /**
     * Recursively visits a transition.
     * @param transition the transition to visit
     */
    protected abstract void visitTransition(Transition transition);

    /**
     * Recursively visits a list of actions.
     * @param actions the list of actions to visit
     */
    protected abstract void visitActions(List<Action> actions);

    /**
     * Recursively visits an if statechart element.
     * @param ifElement the if element to visit
     */
    protected abstract void visitIf(If ifElement);

    /**
     * Recursively visits an elseIf statechart element.
     * @param elseIf the elseIf element to visit
     */
    protected abstract void visitElseIf(ElseIf elseIf);

    /**
     * Recursively visits an else statechart element.
     * @param elseElement the else element to visit
     */
    protected abstract void visitElse(Else elseElement);

    /**
     * Recursively visits executable content.
     * @param content the executable content to visit
     */
    protected abstract void visitExecutableContent(ExecutableContent content);

    /**
     * Visits simple executable content.
     * @param content the simple executable content to visit
     */
    protected abstract void visitSimpleExecutableContent(SimpleExecutableContent content);
}
