package de.jplag.antlr;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Base class for Antlr listeners. This is a quasi-static class that is only created once per language. Use by calling
 * the visit methods in the overwritten constructor.
 */
public abstract class AbstractAntlrListener {
    private final List<ContextVisitor<ParserRuleContext>> contextVisitors;
    private final List<TerminalVisitor> terminalVisitors;

    /**
     * New instance.
     */
    protected AbstractAntlrListener() {
        contextVisitors = new ArrayList<>();
        terminalVisitors = new ArrayList<>();
    }

    /**
     * Visit the given node.
     * @param <T> The class of the node.
     * @param antlrType The antlr type of the node.
     * @param condition An additional condition for the visit.
     * @return A visitor for the node.
     */
    @SuppressWarnings("unchecked")
    public <T extends ParserRuleContext> ContextVisitor<T> visit(Class<T> antlrType, Predicate<T> condition) {
        Predicate<T> typeCheck = rule -> rule.getClass() == antlrType;
        ContextVisitor<T> visitor = new ContextVisitor<>(typeCheck.and(condition));
        contextVisitors.add((ContextVisitor<ParserRuleContext>) visitor);
        return visitor;
    }

    /**
     * Visit the given node.
     * @param <T> The class of the node.
     * @param antlrType The antlr type of the node.
     * @return A visitor for the node.
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
    public TerminalVisitor visit(int terminalType, Predicate<Token> condition) {
        Predicate<Token> typeCheck = rule -> rule.getType() == terminalType;
        TerminalVisitor visitor = new TerminalVisitor(typeCheck.and(condition));
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

    /**
     * Called by {@link InternalListener#visitTerminal(TerminalNode)} as part of antlr framework.
     * @param data is the data passed to the listeners.
     */
    void visitTerminal(HandlerData<Token> data) {
        this.terminalVisitors.stream().filter(visitor -> visitor.matches(data.entity())).forEach(visitor -> visitor.enter(data));
    }

    /**
     * Called by {@link InternalListener#enterEveryRule(ParserRuleContext)} as part of antlr framework.
     * @param data is the data passed to the listeners.
     */
    void enterEveryRule(HandlerData<ParserRuleContext> data) {
        this.contextVisitors.stream().filter(visitor -> visitor.matches(data.entity())).forEach(visitor -> visitor.enter(data));
    }

    /**
     * Called by {@link InternalListener#exitEveryRule(ParserRuleContext)} as part of antlr framework.
     * @param data is the data passed to the listeners.
     */
    void exitEveryRule(HandlerData<ParserRuleContext> data) {
        this.contextVisitors.stream().filter(visitor -> visitor.matches(data.entity())).forEach(visitor -> visitor.exit(data));
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
    protected static <T extends ParserRuleContext> T getAncestor(ParserRuleContext context, Class<T> ancestor,
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
     * @param context the current element to start the search from.
     * @param parent the class representing the type to search for.
     * @param stops the types of elements to stop the upward search at.
     * @return true if an ancestor of the specified type exists.
     * @see #getAncestor(ParserRuleContext, Class, Class[])
     */
    @SafeVarargs
    protected static boolean hasAncestor(ParserRuleContext context, Class<? extends ParserRuleContext> parent,
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
    protected static <T extends ParserRuleContext> T getDescendant(ParserRuleContext context, Class<T> descendant) {
        // simple iterative bfs
        ArrayDeque<ParserRuleContext> queue = new ArrayDeque<>();
        queue.add(context);
        while (!queue.isEmpty()) {
            ParserRuleContext next = queue.removeFirst();
            if (next.children != null) {
                for (ParseTree tree : next.children) {
                    if (tree.getClass() == descendant) {
                        return descendant.cast(tree);
                    }
                    if (tree instanceof ParserRuleContext parserRuleContext) {
                        queue.addLast(parserRuleContext);
                    }
                }
            }
        }
        return null;
    }
}
