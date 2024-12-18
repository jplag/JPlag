package de.jplag.antlr.treewalker;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.antlr.v4.runtime.ParserRuleContext;

public class TreeWalkerRuleBuilder {
    private Map<Class<?>, Consumer<TreeWalkerNode<? extends ParserRuleContext>>> customRules;

    public TreeWalkerRuleBuilder() {
        this.customRules = new HashMap<>();
    }

    protected <T extends ParserRuleContext> void addRule(Class<T> antlrContext, Consumer<TreeWalkerNode<T>> nodeHandler) {
        this.customRules.put(antlrContext, node -> nodeHandler.accept((TreeWalkerNode<T>) node));
    }

    Map<Class<?>, Consumer<TreeWalkerNode<?>>> getCustomRules() {
        return customRules;
    }
}
