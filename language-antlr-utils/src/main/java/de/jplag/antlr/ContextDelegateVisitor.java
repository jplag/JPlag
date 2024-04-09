package de.jplag.antlr;

import java.util.function.Function;

import org.antlr.v4.runtime.ParserRuleContext;

public class ContextDelegateVisitor<T, V extends ParserRuleContext> extends DelegateVisitor<T, V> {
    private final ContextVisitor<V> contextVisitor;

    public ContextDelegateVisitor(ContextVisitor<V> delegate, Function<T, V> mapper) {
        super(delegate, mapper);
        this.contextVisitor = delegate;
    }

    @Override
    public void delegateExit(HandlerData<T> parentData) {
        this.contextVisitor.exit(parentData.derive(this.mapper));
    }
}
