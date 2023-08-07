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

import de.jplag.TokenType;
import de.jplag.semantics.VariableRegistry;

/**
 * Base class for Antlr listeners. You can use the create*Mapping functions to map antlr tokens to jplag tokens.
 * <p>
 * You should create a constructor matching one of the constructors and create your mapping after calling super.
 */
@SuppressWarnings("unused")
public class AbstractAntlrListener implements ParseTreeListener {
    private final List<ContextTokenBuilder<ParserRuleContext>> startMappings;
    private final List<ContextTokenBuilder<ParserRuleContext>> endMappings;

    private final List<TerminalTokenBuilder> terminalMapping;

    private final TokenCollector collector;
    private final File currentFile;

    private VariableRegistry variableRegistry;

    /**
     * New instance
     * @param collector The token collector
     * @param currentFile The currently processed file
     * @param extractsSemantics If true, the listener will extract semantics along with every token
     */
    public AbstractAntlrListener(TokenCollector collector, File currentFile, boolean extractsSemantics) {
        this.collector = collector;
        this.currentFile = currentFile;

        this.startMappings = new ArrayList<>();
        this.endMappings = new ArrayList<>();

        this.terminalMapping = new ArrayList<>();

        if (extractsSemantics) {
            this.variableRegistry = new VariableRegistry();
        }
    }

    /**
     * Creates a new AbstractAntlrListener, that does not collect semantics information
     * @param collector The collector, obtained by the parser
     * @param currentFile The current file, obtained by the parser
     */
    public AbstractAntlrListener(TokenCollector collector, File currentFile) {
        this(collector, currentFile, false);
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
        this.terminalMapping.stream().filter(mapping -> mapping.matches(terminalNode.getSymbol()))
                .forEach(mapping -> mapping.createToken(terminalNode.getSymbol(), variableRegistry));
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
        // does nothing, because we do not handle error nodes right now.
    }

    @Override
    public void enterEveryRule(ParserRuleContext rule) {
        this.startMappings.stream().filter(mapping -> mapping.matches(rule)).forEach(mapping -> mapping.createToken(rule, variableRegistry));
    }

    @Override
    public void exitEveryRule(ParserRuleContext rule) {
        this.endMappings.stream().filter(mapping -> mapping.matches(rule)).forEach(mapping -> mapping.createToken(rule, variableRegistry));
    }

    /**
     * Creates a mapping using the start token from antlr as the location
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> mapEnter(Class<T> antlrType, TokenType jplagType) {
        return this.mapEnter(antlrType, jplagType, it -> true);
    }

    /**
     * Creates a mapping using the start token from antlr as the location
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param condition The condition under which the mapping applies
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    @SuppressWarnings("unchecked")
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> mapEnter(Class<T> antlrType, TokenType jplagType, Predicate<T> condition) {
        ContextTokenBuilder<T> builder = initTypeBuilder(antlrType, jplagType, condition, ContextTokenBuilderType.START);
        this.startMappings.add((ContextTokenBuilder<ParserRuleContext>) builder);
        return builder;
    }

    /**
     * Creates a mapping using the stop token from antlr as the location
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> mapExit(Class<T> antlrType, TokenType jplagType) {
        return this.mapExit(antlrType, jplagType, it -> true);
    }

    /**
     * Creates a mapping using the stop token from antlr as the location
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param condition The condition under which the mapping applies
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    @SuppressWarnings("unchecked")
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> mapExit(Class<T> antlrType, TokenType jplagType, Predicate<T> condition) {
        ContextTokenBuilder<T> builder = initTypeBuilder(antlrType, jplagType, condition, ContextTokenBuilderType.STOP);
        this.endMappings.add((ContextTokenBuilder<ParserRuleContext>) builder);
        return builder;
    }

    /**
     * Creates a mapping using the beginning of the start token as the start location and the distance from the start to the
     * stop token as the length
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> mapRange(Class<T> antlrType, TokenType jplagType) {
        return this.mapRange(antlrType, jplagType, it -> true);
    }

    /**
     * Creates a mapping using the beginning of the start token as the start location and the distance from the start to the
     * stop token as the length
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param condition The condition under which the mapping applies
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    @SuppressWarnings("unchecked")
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> mapRange(Class<T> antlrType, TokenType jplagType, Predicate<T> condition) {
        ContextTokenBuilder<T> builder = initTypeBuilder(antlrType, jplagType, condition, ContextTokenBuilderType.RANGE);
        this.startMappings.add((ContextTokenBuilder<ParserRuleContext>) builder);
        return builder;
    }

    /**
     * Creates a start mapping from antlrType to startType and a stop mapping from antlrType to stopType.
     * @param antlrType The antlr token type
     * @param startType The token type for the start mapping
     * @param stopType The token type for the stop mapping
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    protected <T extends ParserRuleContext> RangeBuilder<T> mapEnterExit(Class<T> antlrType, TokenType startType, TokenType stopType) {
        return mapEnterExit(antlrType, startType, stopType, it -> true);
    }

    /**
     * Creates a start mapping from antlrType to startType and a stop mapping from antlrType to stopType.
     * @param antlrType The antlr token type
     * @param startType The token type for the start mapping
     * @param stopType The token type for the stop mapping
     * @param condition The condition under which the mapping applies
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    protected <T extends ParserRuleContext> RangeBuilder<T> mapEnterExit(Class<T> antlrType, TokenType startType, TokenType stopType,
            Predicate<T> condition) {
        ContextTokenBuilder<T> start = this.mapEnter(antlrType, startType, condition);
        ContextTokenBuilder<T> end = this.mapExit(antlrType, stopType, condition);
        return new RangeBuilder<>(start, end);
    }

    /**
     * Creates a mapping for terminal tokens
     * @param terminalType The type of the terminal node
     * @param jplagType The jplag token type
     * @return The builder for the token
     */
    protected TerminalTokenBuilder mapTerminal(int terminalType, TokenType jplagType) {
        return this.mapTerminal(terminalType, jplagType, it -> true);
    }

    /**
     * Creates a mapping for terminal tokens
     * @param terminalType The type of the terminal node
     * @param jplagType The jplag token type
     * @param condition The condition under which the mapping applies
     * @return The builder for the token
     */
    protected TerminalTokenBuilder mapTerminal(int terminalType, TokenType jplagType, Predicate<org.antlr.v4.runtime.Token> condition) {
        TerminalTokenBuilder builder = new TerminalTokenBuilder(jplagType, token -> token.getType() == terminalType && condition.test(token),
                this.collector, this.currentFile);
        this.terminalMapping.add(builder);
        return builder;
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

    private <T extends ParserRuleContext> ContextTokenBuilder<T> initTypeBuilder(Class<T> antlrType, TokenType jplagType, Predicate<T> condition,
            ContextTokenBuilderType type) {
        return new ContextTokenBuilder<>(jplagType, rule -> rule.getClass() == antlrType && condition.test(antlrType.cast(rule)), this.collector,
                this.currentFile, type);
    }
}
