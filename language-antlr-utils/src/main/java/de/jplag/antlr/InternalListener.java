package de.jplag.antlr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.semantics.VariableRegistry;

/**
 * Internal listener that implements pre-existing antlr methods that are called automatically. This listener is created
 * for every file.
 */
class InternalListener implements ParseTreeListener {
    private final AbstractAntlrListener listener;
    private final TokenCollector collector;
    protected final VariableRegistry variableRegistry;

    InternalListener(AbstractAntlrListener listener, TokenCollector collector) {
        this.listener = listener;
        this.collector = collector;
        this.variableRegistry = new VariableRegistry();
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
        listener.visitTerminal(getHandlerData(terminalNode.getSymbol()));
    }

    @Override
    public void enterEveryRule(ParserRuleContext rule) {
        listener.enterEveryRule(getHandlerData(rule));
    }

    @Override
    public void exitEveryRule(ParserRuleContext rule) {
        listener.exitEveryRule(getHandlerData(rule));
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
        // does nothing, because we do not handle error nodes right now.
    }

    private <T> HandlerData<T> getHandlerData(T entity) {
        return new HandlerData<>(entity, variableRegistry, collector);
    }
}
