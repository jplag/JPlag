package de.jplag.cli.antlrtesttool;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.antlr.ContextVisitor;
import de.jplag.antlr.HandlerData;
import de.jplag.antlr.TerminalVisitor;
import de.jplag.antlr.TokenCollector;
import de.jplag.semantics.VariableRegistry;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Runs the antlr grammar and identifies generated tokens
 */
public class TestRunner {
    private Method createLexer;
    private Method createParser;
    private Method getContext;

    private AbstractAntlrParserAdapter<?> adapter;
    private AbstractAntlrListener listener;

    private List<ContextVisitor<ParserRuleContext>> contextVisitors;
    private List<TerminalVisitor> terminalVisitors;

    /**
     * New instance
     * @param adapter The adapter to test
     * @throws NoSuchMethodException -
     * @throws InvocationTargetException -
     * @throws IllegalAccessException -
     * @throws NoSuchFieldException -
     */
    public TestRunner(AbstractAntlrParserAdapter<?> adapter) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        createLexer = adapter.getClass().getDeclaredMethod("createLexer", CharStream.class);
        createParser = adapter.getClass().getDeclaredMethod("createParser", CommonTokenStream.class);
        getContext = adapter.getClass().getDeclaredMethod("getEntryContext", Parser.class);
        Method getListener = adapter.getClass().getDeclaredMethod("getListener");


        createLexer.setAccessible(true);
        createParser.setAccessible(true);
        getContext.setAccessible(true);
        getListener.setAccessible(true);

        this.adapter = adapter;
        listener = (AbstractAntlrListener) getListener.invoke(adapter);

        Field contextVisitorsField = AbstractAntlrListener.class.getDeclaredField("contextVisitors");
        Field terminalVisitorsFiled = AbstractAntlrListener.class.getDeclaredField("terminalVisitors");

        contextVisitorsField.setAccessible(true);
        terminalVisitorsFiled.setAccessible(true);

        contextVisitors = (List<ContextVisitor<ParserRuleContext>>) contextVisitorsField.get(listener);
        terminalVisitors = (List<TerminalVisitor>) terminalVisitorsFiled.get(listener);
    }

    /**
     * Runs the test on the given input
     * @param input The source to parse
     * @return The tree node to display
     * @throws InvocationTargetException -
     * @throws IllegalAccessException -
     */
    public TreeNode runTest(String input) throws InvocationTargetException, IllegalAccessException {
        Lexer lexer = (Lexer) createLexer.invoke(adapter, CharStreams.fromString(input));
        Parser parser = (Parser) createParser.invoke(adapter, new CommonTokenStream(lexer));
        ParserRuleContext context = (ParserRuleContext) getContext.invoke(adapter, parser);

        return buildForContext(context);
    }

    private MutableTreeNode buildForContext(ParserRuleContext context) {
        TreeEntry entry;
        String text = context.getClass().getSimpleName() + " " + getTokenGenerationInformation(context);
        if(context.getStop() == null || context.getStop().getStartIndex() < context.getStart().getStartIndex()) {
            entry = new TreeEntry(-1, -1, text);
        } else {
            entry = new TreeEntry(context.getStart().getStartIndex(), context.getStop().getStopIndex() + 1, text);
        }

        DefaultMutableTreeNode mutableTreeNode = new DefaultMutableTreeNode(entry);

        for (int i = 0; i < context.getChildCount(); i++) {
            ParseTree child = context.getChild(i);
            if(child instanceof ParserRuleContext childContext) {
                mutableTreeNode.add(buildForContext(childContext));
            }

            if(child instanceof TerminalNode terminal) {
                String childText = terminal.getSymbol().getText() + " " + getTokenGenerationInformation(terminal);
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new TreeEntry(terminal.getSymbol().getStartIndex(), terminal.getSymbol().getStopIndex() + 1, childText));
                mutableTreeNode.add(childNode);
            }
        }
        return mutableTreeNode;
    }

    private String getTokenGenerationInformation(ParserRuleContext context) {
        AtomicInteger entryCount = new AtomicInteger();
        AtomicInteger exitCount = new AtomicInteger();

        String entry = contextVisitors.stream().filter(it -> it.matches(context)).flatMap(it -> {
            TokenCollector collector = new TokenCollector(false);
            HandlerData<ParserRuleContext> data = new HandlerData<>(context, new VariableRegistry(), collector);
            it.enter(data);
            entryCount.addAndGet(collector.getTokens().size());
            return collector.getTokens().stream();
        }).map(it -> "\"" + it.getType().getDescription() + "\"").collect(Collectors.joining(", ", "entry -> [", "]"));

        String exit = contextVisitors.stream().filter(it -> it.matches(context)).flatMap(it -> {
            TokenCollector collector = new TokenCollector(false);
            HandlerData<ParserRuleContext> data = new HandlerData<>(context, new VariableRegistry(), collector);
            it.exit(data);
            exitCount.addAndGet(collector.getTokens().size());
            return collector.getTokens().stream();
        }).map(it -> "\"" + it.getType().getDescription() + "\"").collect(Collectors.joining(", ", "exit -> [", "]"));

        if(entryCount.get() + exitCount.get() > 0) {
            String result = "{";
            if(entryCount.get() > 0) {
                result += entry;
            }
            if(entryCount.get() > 0 && exitCount.get() > 0) {
                result += "; ";
            }
            if(exitCount.get() > 0) {
                result += exit;
            }
            return result + "}";
        } else {
            return "";
        }
    }

    private String getTokenGenerationInformation(TerminalNode terminalNode) {
        AtomicInteger count = new AtomicInteger();

        String text = terminalVisitors.stream().filter(it -> it.matches(terminalNode)).flatMap(it -> {
            TokenCollector collector = new TokenCollector(false);
            HandlerData<TerminalNode> data = new HandlerData<>(terminalNode, new VariableRegistry(), collector);
            it.enter(data);
            count.addAndGet(collector.getTokens().size());
            return collector.getTokens().stream();
        }).map(it -> "\"" + it.getType().getDescription() + "\"").collect(Collectors.joining(", ", "visit -> [", "]"));

        if(count.get() > 0) {
            return text;
        } else {
            return "";
        }
    }
}
