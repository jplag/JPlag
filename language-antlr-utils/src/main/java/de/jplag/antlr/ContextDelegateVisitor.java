package de.jplag.antlr;

import java.util.function.Function;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Delegates visiting a {@link ParserRuleContext} to a different {@link ContextVisitor} derived by the given mapper
 * function.
 * @param <T> The original antlr type visited
 * @param <V> The target {@link ParserRuleContext} to visit instead
 */
public class ContextDelegateVisitor<T, V extends ParserRuleContext> extends DelegateVisitor<T, V> {
    private final ContextVisitor<V> contextVisitor;

    /**
     * @param delegate The visitor to delegate to
     * @param mapper The mapper function used to derive the target antlr context
     */
    public ContextDelegateVisitor(ContextVisitor<V> delegate, Function<T, V> mapper) {
        super(delegate, mapper);
        this.contextVisitor = delegate;
    }

    @Override
    public void delegateExit(HandlerData<T> parentData) {
        this.contextVisitor.exit(parentData.derive(this.mapper));
    }
}
