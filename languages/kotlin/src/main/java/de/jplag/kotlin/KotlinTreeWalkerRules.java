package de.jplag.kotlin;

import org.antlr.v4.runtime.tree.ParseTree;

import de.jplag.antlr.treewalker.TreeWalkerRuleBuilder;
import de.jplag.kotlin.grammar.KotlinParser;

public class KotlinTreeWalkerRules extends TreeWalkerRuleBuilder {
    public KotlinTreeWalkerRules() {
        addRule(KotlinParser.PrimaryConstructorContext.class, node -> {
            node.executeEnter();
            node.walkChild(node.getAntlr().modifierList());
            node.walkChild(node.getAntlr().CONSTRUCTOR());
            node.walkChild(node.getAntlr().classParameters());
            ParseTree parent = node.getAntlr().getParent();
            KotlinParser.ClassBodyContext body = null;

            if (parent instanceof KotlinParser.ClassDeclarationContext) {
                body = ((KotlinParser.ClassDeclarationContext) parent).classBody();
            }
            if (parent instanceof KotlinParser.ObjectDeclarationContext) {
                body = ((KotlinParser.ObjectDeclarationContext) parent).classBody();
            }

            if (body != null) {
                for (KotlinParser.ClassMemberDeclarationContext member : body.classMemberDeclaration()) {
                    if (member.anonymousInitializer() != null) {
                        node.walkChildren(member.anonymousInitializer().children);
                    }
                }
            }

            node.executeExit();
        });

        addRule(KotlinParser.ClassMemberDeclarationContext.class, node -> {
            node.executeEnter();
            ParseTree parent = node.getAntlr().getParent().getParent();
            if (parent instanceof KotlinParser.ClassDeclarationContext
                    && ((KotlinParser.ClassDeclarationContext) parent).primaryConstructor() != null) {
                node.walkChildren(node.getAntlr().children.stream().filter(it -> !(it instanceof KotlinParser.AnonymousInitializerContext)).toList());
            } else {
                node.walkChildren(node.getAntlr().children);
            }
            node.executeExit();
        });
    }
}
