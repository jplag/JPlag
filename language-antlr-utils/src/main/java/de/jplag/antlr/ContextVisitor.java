package de.jplag.antlr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import de.jplag.TokenType;
import de.jplag.semantics.VariableRegistry;

/**
 * The visitor for nodes, or contexts.
 * @param <T> The antlr type of the node.
 */
public class ContextVisitor<T extends ParserRuleContext> extends AbstractVisitor<T> {
    private final List<Consumer<T>> exitHandlers;

    ContextVisitor(Predicate<T> condition, TokenCollector tokenCollector, VariableRegistry variableRegistry) {
        super(condition, tokenCollector, variableRegistry);
        this.exitHandlers = new ArrayList<>();
    }

    /**
     * Add an action the visitor runs upon exiting the entity.
     * @param handler The action, takes the entity as parameter.
     * @return Self
     */
    public AbstractVisitor<T> onExit(Consumer<T> handler) {
        this.exitHandlers.add(handler);
        return this;
    }

    /**
     * Tell the visitor that it should generate a token upon exiting the entity. Should only be invoked once per visitor.
     * @param tokenType The type of the token.
     * @return Self
     */
    public ContextVisitor<T> mapExit(TokenType tokenType) {
        map(exitHandlers, tokenType, ParserRuleContext::getStop);
        return this;
    }

    /**
     * Tell the visitor that it should generate a token upon entering and one upon exiting the entity. Should only be
     * invoked once per visitor.
     * @param enterTokenType The type of the token generated on enter.
     * @param exitTokenType The type of the token generated on exit.
     * @return Self
     */
    public ContextVisitor<T> mapEnterExit(TokenType enterTokenType, TokenType exitTokenType) {
        mapEnter(enterTokenType);
        mapExit(exitTokenType);
        return this;
    }

    /**
     * Tell the visitor that it should generate a token upon entering and one upon exiting the entity. Should only be
     * invoked once per visitor. Alias for {@link #mapEnterExit(TokenType, TokenType)}.
     * @param enterTokenType The type of the token generated on enter.
     * @param exitTokenType The type of the token generated on exit.
     * @return Self
     */
    public ContextVisitor<T> map(TokenType enterTokenType, TokenType exitTokenType) {
        mapEnterExit(enterTokenType, exitTokenType);
        return this;
    }

    /**
     * Tell the visitor that the entity represents a local scope.
     * @return Self
     */
    public ContextVisitor<T> addLocalScope() {
        onEnter(ignore -> variableRegistry.enterLocalScope());
        onExit(ignore -> variableRegistry.exitLocalScope());
        return this;
    }

    /**
     * Tell the visitor that the entity represents a class scope.
     * @return Self
     */
    public ContextVisitor<T> addClassScope() {
        onEnter(ignore -> variableRegistry.enterClass());
        onExit(ignore -> variableRegistry.exitClass());
        return this;
    }

    void exit(T entity) {
        exitHandlers.forEach(handler -> handler.accept(entity));
    }

    Token extractEnterToken(T entity) {
        return entity.getStart();
    }
}
