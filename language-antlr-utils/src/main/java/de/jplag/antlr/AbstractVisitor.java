package de.jplag.antlr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.antlr.v4.runtime.Token;

import de.jplag.TokenType;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableRegistry;

/**
 * The abstract visitor.
 * @param <T> The type of the visited entity.
 */
public abstract class AbstractVisitor<T> {
    private final Predicate<T> condition;
    private final List<Consumer<T>> entryHandlers;
    private final TokenCollector tokenCollector;
    private Function<T, CodeSemantics> semanticsSupplier;
    VariableRegistry variableRegistry;  // used in ContextVisitor

    /**
     * @param condition The condition for the visit.
     * @param tokenCollector The used token collector.
     * @param variableRegistry The used variable registry.
     */
    AbstractVisitor(Predicate<T> condition, TokenCollector tokenCollector, VariableRegistry variableRegistry) {
        this.condition = condition;
        this.tokenCollector = tokenCollector;
        this.entryHandlers = new ArrayList<>();
        this.variableRegistry = variableRegistry;
    }

    /**
     * Add an action the visitor runs upon entering the entity.
     * @param handler The action, takes the entity as parameter.
     * @return Self
     */
    public AbstractVisitor<T> onEnter(Consumer<T> handler) {
        this.entryHandlers.add(handler);
        return this;
    }

    /**
     * Tell the visitor that it should generate a token upon entering the entity. Should only be invoked once per visitor.
     * @param tokenType The type of the token.
     * @return Self
     */
    public AbstractVisitor<T> mapEnter(TokenType tokenType) {
        map(entryHandlers, tokenType, this::extractEnterToken);
        return this;
    }

    /**
     * Tell the visitor that it should generate a token upon entering the entity. Should only be invoked once per visitor.
     * Alias for {@link #mapEnter(TokenType)}.
     * @param tokenType The type of the token.
     * @return Self
     */
    public AbstractVisitor<T> map(TokenType tokenType) {
        mapEnter(tokenType);
        return this;
    }

    /**
     * Tell the visitor that if it generates a token upon entering the entity, it should have semantics. If it doesn't
     * generate a token, the semantics are discarded. This is not checked and does not lead to a warning.
     * @param semanticsSupplier A function that takes the entity and returns the semantics.
     * @return Self
     */
    public AbstractVisitor<T> withSemantics(Function<T, CodeSemantics> semanticsSupplier) {
        this.semanticsSupplier = semanticsSupplier;
        return this;
    }

    /**
     * Tell the visitor that if it generates a token upon entering the entity, it should have semantics. If it doesn't
     * generate a token, the semantics are discarded. This is not checked and does not lead to a warning.
     * @param semanticsSupplier A function that returns the semantics.
     * @return Self
     */
    public AbstractVisitor<T> withSemantics(Supplier<CodeSemantics> semanticsSupplier) {
        this.semanticsSupplier = ignore -> semanticsSupplier.get();
        return this;
    }

    /**
     * Tell the visitor that if it generates a token upon entering the entity, it should have semantics of type control. If
     * it doesn't generate a token, the semantics are discarded. This is not checked and does not lead to a warning.
     * @return Self
     */
    public AbstractVisitor<T> withControlSemantics() {
        this.semanticsSupplier = ignore -> CodeSemantics.createControl();
        return this;
    }

    /**
     * @param entity The entity to check.
     * @return Whether the visitor should be visited.
     */
    boolean matches(T entity) {
        return this.condition.test(entity);
    }

    void enter(T entity) {
        entryHandlers.forEach(handler -> handler.accept(entity));
    }

    void exit(T entity) {
    }

    void map(List<Consumer<T>> handlers, TokenType tokenType, Function<T, Token> extractToken) {
        handlers.add(0, value -> tokenCollector.addToken(tokenType, semanticsSupplier, value, extractToken, variableRegistry));
    }

    abstract Token extractEnterToken(T entity);
}
