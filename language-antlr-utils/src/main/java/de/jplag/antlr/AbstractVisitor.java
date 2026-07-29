package de.jplag.antlr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.antlr.v4.runtime.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.TokenType;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableRegistry;

/**
 * The abstract visitor.
 * @param <T> The type of the visited entity.
 */
public abstract class AbstractVisitor<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractVisitor.class);

    private final Predicate<T> condition;
    private final List<Consumer<HandlerData<T>>> entryHandlers;
    private Function<T, CodeSemantics> entrySemantics;
    protected TokenType entryTokenType;

    /**
     * @param condition The condition for the visit.
     */
    AbstractVisitor(Predicate<T> condition) {
        this.condition = condition;
        this.entryHandlers = new ArrayList<>();
    }

    /**
     * Add an action the visitor runs upon entering the entity.
     * @param handler The action, takes the entity and the variable registry as parameter.
     * @return Self
     */
    public AbstractVisitor<T> onEnter(BiConsumer<T, VariableRegistry> handler) {
        entryHandlers.add(handlerData -> handler.accept(handlerData.entity(), handlerData.variableRegistry()));
        return this;
    }

    /**
     * Add an action the visitor runs upon entering the entity.
     * @param handler The action, takes the entity as parameter.
     * @return Self
     */
    public AbstractVisitor<T> onEnter(Consumer<T> handler) {
        entryHandlers.add(handlerData -> handler.accept(handlerData.entity()));
        return this;
    }

    /**
     * Tell the visitor that it should generate a token upon entering the entity. Should only be invoked once per visitor.
     * @param tokenType The type of the token.
     * @return Self
     */
    public AbstractVisitor<T> mapEnter(TokenType tokenType) {
        entryTokenType = tokenType;
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
     * Tell the visitor that if it generates a token upon entering the entity, it should have semantics.
     * @param semanticsSupplier A function that takes the entity and returns the semantics.
     * @return Self
     */
    public AbstractVisitor<T> withSemantics(Function<T, CodeSemantics> semanticsSupplier) {
        this.entrySemantics = semanticsSupplier;
        return this;
    }

    /**
     * Tell the visitor that if it generates a token upon entering the entity, it should have semantics.
     * @param semanticsSupplier A function that returns the semantics.
     * @return a self reference.
     */
    public AbstractVisitor<T> withSemantics(Supplier<CodeSemantics> semanticsSupplier) {
        withSemantics(ignore -> semanticsSupplier.get());
        return this;
    }

    /**
     * Tell the visitor that if it generates a token upon entering the entity, it should have semantics of type control.
     * @return Self
     */
    public AbstractVisitor<T> withControlSemantics() {
        withSemantics(CodeSemantics::createControl);
        return this;
    }

    /**
     * @param entity The entity to check.
     * @return Whether to visit the entity.
     */
    boolean matches(T entity) {
        return this.condition.test(entity);
    }

    /**
     * Enter a given entity, injecting the needed dependencies.
     * @param data is the data passed to the visitors.
     */
    void enter(HandlerData<T> data) {
        handleEnter(data, this::extractEnterToken, this::extractEnterToken);
    }

    protected void handleEnter(HandlerData<T> data, Function<T, Token> extractStartToken, Function<T, Token> extractEndToken) {
        if (entryTokenType == null && entrySemantics != null) {
            logger.warn("Received semantics, but no token type, so no token was generated and the semantics discarded");
        }
        addToken(data, entryTokenType, entrySemantics, extractStartToken, extractEndToken);  // addToken takes null token types
        entryHandlers.forEach(handler -> handler.accept(data));
    }

    void addToken(HandlerData<T> data, TokenType tokenType, Function<T, CodeSemantics> semantics, Function<T, Token> extractToken) {
        addToken(data, tokenType, semantics, extractToken, extractToken);
    }

    void addToken(HandlerData<T> data, TokenType tokenType, Function<T, CodeSemantics> semantics, Function<T, Token> extractStartToken,
            Function<T, Token> extractEndToken) {
        data.collector().addToken(tokenType, semantics, data.entity(), extractStartToken, extractEndToken, data.variableRegistry());
    }

    abstract Token extractEnterToken(T entity);
}
