package de.jplag.antlr.treewalker;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;

public class TreeWalkerNode<T extends ParserRuleContext> {
    private TreeWalker treeWalker;
    private T antlrContext;
    private ParseTreeListener listener;

    TreeWalkerNode(TreeWalker treeWalker, ParseTreeListener listener, T antlrContext) {
        this.treeWalker = treeWalker;
        this.listener = listener;
        this.antlrContext = antlrContext;
    }

    public void executeEnter() {

    }

    public void executeExit() {

    }

    public void walkChild(ParseTree antlrContext) {
        this.treeWalker.walk(this.listener, antlrContext);
    }

    public T getAntlr() {
        return this.antlrContext;
    }
}
