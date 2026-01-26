package de.jplag.antlr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.TokenType;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableRegistry;

/**
 * The visitor for nodes, or contexts.
 * @param <T> The antlr type of the node.
 */
public class ContextVisitor<T extends ParserRuleContext> extends AbstractVisitor<T> {
    private final List<Consumer<HandlerData<T>>> exitHandlers;
    private TokenType exitTokenType;
    private Function<T, CodeSemantics> exitSemantics;
    private boolean lengthAsRange;

    private DelegateVisitor<T, ?> delegate;

    ContextVisitor(Predicate<T> condition) {
        super(condition);
        this.exitHandlers = new ArrayList<>();
        this.lengthAsRange = false;
        this.delegate = null;
    }

    /**
     * Add an action the visitor runs upon exiting the entity.
     * @param handler The action, takes the entity and the variable registry as parameter.
     * @return Self
     */
    public AbstractVisitor<T> onExit(BiConsumer<T, VariableRegistry> handler) {
        exitHandlers.add(handlerData -> handler.accept(handlerData.entity(), handlerData.variableRegistry()));
        return this;
    }

    /**
     * Add an action the visitor runs upon exiting the entity.
     * @param handler The action, takes the entity as parameter.
     * @return Self
     */
    public AbstractVisitor<T> onExit(Consumer<T> handler) {
        exitHandlers.add(handlerData -> handler.accept(handlerData.entity()));
        return this;
    }

    /**
     * Tell the visitor that it should generate a token upon exiting the entity. Should only be invoked once per visitor.
     * @param tokenType The type of the token.
     * @return Self
     */
    public ContextVisitor<T> mapExit(TokenType tokenType) {
        exitTokenType = tokenType;
        return this;
    }

    /**
     * Behaves like mapEnter, but the created token will range from the beginning of this context to the end instead of only
     * marking the beginning.
     * @param tokenType The type of token to crate
     * @return Self
     */
    public AbstractVisitor<T> mapRange(TokenType tokenType) {
        this.entryTokenType = tokenType;
        this.lengthAsRange = true;
        return this;
    }

    /**
     * Delegates calls to this visitor to a derived visitor. The mapper function is used to determine the delegated token.
     * This invalidated all mapping happening inside this visitor. You need to configure the new visitor to do so.
     * @param mapper The mapper function
     * @return the delegation visitor.
     */
    public TerminalVisitor delegateTerminal(Function<T, TerminalNode> mapper) {
        TerminalVisitor delegateVisitor = new TerminalVisitor(ignore -> true);
        this.delegate = new DelegateVisitor<>(delegateVisitor, parentData -> mapper.apply(parentData).getSymbol());
        return delegateVisitor;
    }

    /**
     * Delegates calls to this visitor to a derived visitor. The mapper function is used to determine the delegated token.
     * This invalidated all mapping happening inside this visitor. You need to configure the new visitor to do so. Visits
     * the terminal upon exiting this context.
     * @param mapper The mapper function
     * @return the delegation visitor.
     */
    public TerminalVisitor delegateTerminalExit(Function<T, TerminalNode> mapper) {
        TerminalVisitor delegateVisitor = new TerminalVisitor(ignore -> true);
        this.delegate = new DelegateVisitor<>(delegateVisitor, parentData -> mapper.apply(parentData).getSymbol());
        this.delegate.mapOnExit();
        return delegateVisitor;
    }

    /**
     * Delegates calls to this visitor to a derived visitor. The mapper function is used to determine the delegated token.
     * This invalidated all mapping happening inside this visitor. You need to configure the new visitor to do so.
     * @param <V> the type of {@link ParserRuleContext} to visit.
     * @param mapper The mapper function
     * @return the delegation visitor.
     */
    public <V extends ParserRuleContext> ContextVisitor<V> delegateContext(Function<T, V> mapper) {
        ContextVisitor<V> visitor = new ContextVisitor<>(ignore -> true);
        this.delegate = new ContextDelegateVisitor<>(visitor, mapper);
        return visitor;
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

    @Override
    public ContextVisitor<T> withSemantics(Function<T, CodeSemantics> semantics) {
        super.withSemantics(semantics);
        this.exitSemantics = semantics;
        return this;
    }

    @Override
    public ContextVisitor<T> withSemantics(Supplier<CodeSemantics> semantics) {
        withSemantics(ignore -> semantics.get());
        return this;
    }

    /**
     * Tell the visitor that if it generates a token upon entering the entity, it should have semantics of type loop begin,
     * same for the exit and loop end.
     * @return Self
     */
    public ContextVisitor<T> withLoopSemantics() {
        super.withSemantics(CodeSemantics::createLoopBegin);
        this.exitSemantics = ignore -> CodeSemantics.createLoopEnd();
        return this;
    }

    /**
     * Tell the visitor that the entity represents a local scope.
     * @return Self
     */
    public ContextVisitor<T> addLocalScope() {
        onEnter((ignore, variableRegistry) -> variableRegistry.enterLocalScope());
        onExit((ignore, variableRegistry) -> variableRegistry.exitLocalScope());
        return this;
    }

    /**
     * Tell the visitor that the entity represents a class scope.
     * @return Self
     */
    public ContextVisitor<T> addClassScope() {
        onEnter((ignore, variableRegistry) -> variableRegistry.enterClass());
        onExit((ignore, variableRegistry) -> variableRegistry.exitClass());
        return this;
    }

    /**
     * Exit a given entity, injecting the needed dependencies.
     * @param data is the data of the original visitor
     */
    void exit(HandlerData<T> data) {
        if (this.delegate != null) {
            this.delegate.delegateExit(data);
            return;
        }

        addToken(data, exitTokenType, exitSemantics, ParserRuleContext::getStop);
        exitHandlers.forEach(handler -> handler.accept(data));
    }

    @Override
    void enter(HandlerData<T> data) {
        if (this.delegate != null) {
            this.delegate.delegateEnter(data);
            return;
        }

        if (this.lengthAsRange) {
            this.handleEnter(data, this::extractEnterToken, ParserRuleContext::getStop);
        } else {
            super.enter(data);
        }
    }

    @Override
    Token extractEnterToken(T entity) {
        return entity.getStart();
    }

    @Override
    boolean matches(T entity) {
        if (this.delegate != null && !this.delegate.isPresent(entity)) {
            return false;
        }

        return super.matches(entity);
    }
}
