package de.jplag.cpp;

import org.antlr.v4.runtime.tree.ParseTree;

import de.jplag.antlr.treewalker.TreeWalkerRuleBuilder;
import de.jplag.cpp.grammar.CPP14Parser;

public class CppTreeWalkerRules extends TreeWalkerRuleBuilder {
    public CppTreeWalkerRules() {
        addRule(CPP14Parser.IterationStatementContext.class, node -> {
            if (node.getAntlr().For() != null && node.getAntlr().forInitStatement() != null) {
                node.executeEnter();
                node.walkChild(node.getAntlr().For());
                node.walkChild(node.getAntlr().LeftParen());
                node.walkChild(node.getAntlr().forInitStatement());
                node.walkChild(node.getAntlr().condition());
                node.walkChild(node.getAntlr().Semi());
                node.walkChild(node.getAntlr().RightParen());
                node.walkChild(node.getAntlr().statement());
                node.walkChild(node.getAntlr().expression());
                node.executeExit();
            } else {
                node.executeEnter();
                for (ParseTree child : node.getAntlr().children) {
                    node.walkChild(child);
                }
                node.executeExit();
            }
        });
    }
}
