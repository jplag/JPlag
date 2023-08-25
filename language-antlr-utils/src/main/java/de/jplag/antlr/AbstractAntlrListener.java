package de.jplag.antlr;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.semantics.VariableRegistry;

/**
 * Base class for Antlr listeners. You can use the create*Mapping functions to map antlr tokens to jplag tokens.
 * <p>
 * You should create a constructor matching one of the constructors and create your mapping after calling super.
 */
@SuppressWarnings("unused")
public class AbstractAntlrListener implements ParseTreeListener {
    private final TokenCollector collector;
    private final File currentFile;
    private final List<ContextVisitor<ParserRuleContext>> contextVisitors;
    private final List<TerminalVisitor> terminalVisitors;
    protected final VariableRegistry variableRegistry;

    /**
     * New instance
     * @param collector The token collector
     * @param currentFile The currently processed file
     * @param extractsSemantics If true, the listener will extract semantics along with every token
     */
    public AbstractAntlrListener(TokenCollector collector, File currentFile, boolean extractsSemantics) {
        this.collector = collector;
        this.currentFile = currentFile;
        this.contextVisitors = new ArrayList<>();
        this.terminalVisitors = new ArrayList<>();
        this.variableRegistry = new VariableRegistry();
    }

    /**
     * Creates a new AbstractAntlrListener, that does not collect semantics information
     * @param collector The collector, obtained by the parser
     * @param currentFile The current file, obtained by the parser
     */
    public AbstractAntlrListener(TokenCollector collector, File currentFile) {
        this(collector, currentFile, false);
    }

    /**
     * Visit the given node.
     * @param antlrType The antlr type of the node.
     * @param condition An additional condition for the visit.
     * @return A visitor for the node.
     * @param <T> The class of the node.
     */
    @SuppressWarnings("unchecked")
    public <T extends ParserRuleContext> ContextVisitor<T> visit(Class<T> antlrType, Predicate<T> condition) {
        ContextVisitor<T> visitor = new ContextVisitor<>(condition.and(rule -> rule.getClass() == antlrType), collector, variableRegistry);
        contextVisitors.add((ContextVisitor<ParserRuleContext>) visitor);
        return visitor;
    }

    /**
     * Visit the given node.
     * @param antlrType The antlr type of the node.
     * @return A visitor for the node.
     * @param <T> The class of the node.
     */
    public <T extends ParserRuleContext> ContextVisitor<T> visit(Class<T> antlrType) {
        return visit(antlrType, ignore -> true);
    }

    /**
     * Visit the given terminal.
     * @param terminalType The type of the terminal.
     * @param condition An additional condition for the visit.
     * @return A visitor for the node.
     */
    public TerminalVisitor visit(int terminalType, Predicate<org.antlr.v4.runtime.Token> condition) {
        TerminalVisitor visitor = new TerminalVisitor(condition.and(rule -> rule.getType() == terminalType), collector, variableRegistry);
        terminalVisitors.add(visitor);
        return visitor;
    }

    /**
     * Visit the given terminal.
     * @param terminalType The type of the terminal.
     * @return A visitor for the node.
     */
    public TerminalVisitor visit(int terminalType) {
        return visit(terminalType, ignore -> true);
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
        this.terminalVisitors.stream().filter(visitor -> visitor.matches(terminalNode.getSymbol()))
                .forEach(visitor -> visitor.enter(terminalNode.getSymbol()));
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
        // does nothing, because we do not handle error nodes right now.
    }

    @Override
    public void enterEveryRule(ParserRuleContext rule) {
        this.contextVisitors.stream().filter(visitor -> visitor.matches(rule)).forEach(visitor -> visitor.enter(rule));
    }

    @Override
    public void exitEveryRule(ParserRuleContext rule) {
        this.contextVisitors.stream().filter(visitor -> visitor.matches(rule)).forEach(visitor -> visitor.exit(rule));
    }

    /**
     * Searches the ancestors of an element for an element of the specific type.
     * @param context the current element to start the search from.
     * @param ancestor the class representing the type to search for.
     * @param stops the types of elements to stop the upward search at.
     * @param <T> the type of the element to search for.
     * @return an ancestor of the specified type, or null if not found.
     */
    @SafeVarargs
    protected final <T extends ParserRuleContext> T getAncestor(ParserRuleContext context, Class<T> ancestor,
            Class<? extends ParserRuleContext>... stops) {
        ParserRuleContext currentContext = context;
        Set<Class<? extends ParserRuleContext>> forbidden = Set.of(stops);
        boolean abort = false;
        while (currentContext != null && !abort) {
            if (currentContext.getClass() == ancestor) {
                return ancestor.cast(currentContext);
            }
            if (forbidden.contains(currentContext.getClass())) {
                abort = true;
            }

            currentContext = currentContext.getParent();
        }

        return null;
    }

    /**
     * {@return true if an ancestor of the specified type exists}
     * @param context the current element to start the search from.
     * @param parent the class representing the type to search for.
     * @param stops the types of elements to stop the upward search at.
     * @see #getAncestor(ParserRuleContext, Class, Class[])
     */
    @SafeVarargs
    protected final boolean hasAncestor(ParserRuleContext context, Class<? extends ParserRuleContext> parent,
            Class<? extends ParserRuleContext>... stops) {
        return getAncestor(context, parent, stops) != null;
    }

    /**
     * Searches a subtree for a descendant of a specific type. Search is done breath-first.
     * @param context the context to search the subtree from.
     * @param descendant the class representing the type to search for.
     * @param <T> the type to search for.
     * @return the first appearance of an element of the given type in the subtree, or null if no such element exists.
     */
    protected final <T extends ParserRuleContext> T getDescendant(ParserRuleContext context, Class<T> descendant) {
        // simple iterative bfs
        ArrayDeque<ParserRuleContext> queue = new ArrayDeque<>();
        queue.add(context);
        while (!queue.isEmpty()) {
            ParserRuleContext next = queue.removeFirst();
            for (ParseTree tree : next.children) {
                if (tree.getClass() == descendant) {
                    return descendant.cast(tree);
                }
                if (tree instanceof ParserRuleContext parserRuleContext) {
                    queue.addLast(parserRuleContext);
                }
            }
        }
        return null;
    }
}
