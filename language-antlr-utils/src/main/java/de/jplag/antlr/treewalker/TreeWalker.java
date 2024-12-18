package de.jplag.antlr.treewalker;

import java.util.Map;
import java.util.function.Consumer;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class TreeWalker extends ParseTreeWalker {
    private final Map<Class<?>, Consumer<TreeWalkerNode<?>>> customRules;

    public TreeWalker(TreeWalkerRuleBuilder builder) {
        this.customRules = builder.getCustomRules();
    }

    @Override
    public void walk(ParseTreeListener listener, ParseTree t) {
        if (t instanceof ErrorNode) {
            listener.visitErrorNode((ErrorNode) t);
        } else if (t instanceof TerminalNode) {
            listener.visitTerminal((TerminalNode) t);
        } else {
            if (customRules.containsKey(t.getClass())) {
                TreeWalkerNode<ParserRuleContext> node = new TreeWalkerNode<>(this, listener, (ParserRuleContext) t);
                customRules.get(t.getClass()).accept(node);
            } else {
                super.walk(listener, t);
            }
        }
    }

    public void enterEvent(ParseTreeListener listener, RuleNode node) {
        this.enterRule(listener, node);
    }

    public void exitEvent(ParseTreeListener listener, RuleNode node) {
        this.exitRule(listener, node);
    }
}
