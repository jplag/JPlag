package de.jplag.antlr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.Token;
import de.jplag.TokenType;

/**
 * Base class for Antlr listeners. You can use the create*Mapping functions to map antlr tokens to jplag tokens.
 * <p>
 * You should create a constructor matching {@link AbstractAntlrListener#AbstractAntlrListener(TokenCollector, File)}
 * and create your mapping after calling super.
 */
public class AbstractAntlrListener implements ParseTreeListener {
    private final List<TypeBuilder<RuleContext>> startMappings;
    private final List<TypeBuilder<RuleContext>> endMappings;
    private final List<TypeBuilder<RuleContext>> rangeMappings;

    private final List<TypeBuilder<org.antlr.v4.runtime.Token>> terminalMapping;

    private final TokenCollector collector;
    private final File currentFile;

    /**
     * New instance
     *
     * @param collector   The token collector
     * @param currentFile The currently processed file
     */
    public AbstractAntlrListener(TokenCollector collector, File currentFile) {
        this.collector = collector;
        this.currentFile = currentFile;

        this.startMappings = new ArrayList<>();
        this.endMappings = new ArrayList<>();
        this.rangeMappings = new ArrayList<>();

        this.terminalMapping = new ArrayList<>();
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
        this.terminalMapping.stream().filter(mapping -> mapping.matches(terminalNode.getSymbol())).forEach(mapping ->
                transformToken(mapping.getTokenType(), terminalNode.getSymbol()));
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
        // does nothing, because we do not handle error nodes right now.
    }

    @Override
    public void enterEveryRule(ParserRuleContext rule) {
        this.startMappings.stream().filter(mapping -> mapping.matches(rule))
                .forEach(mapping -> transformToken(mapping.getTokenType(), rule.getStart()));

        this.rangeMappings.stream().filter(mapping -> mapping.matches(rule))
                .forEach(mapping -> transformToken(mapping.getTokenType(), rule.getStart(), rule.getStop()));
    }

    @Override
    public void exitEveryRule(ParserRuleContext rule) {
        this.endMappings.stream().filter(mapping -> mapping.matches(rule)).forEach(mapping -> transformToken(mapping.getTokenType(), rule.getStop()));
    }

    private void transformToken(TokenType tokenType, org.antlr.v4.runtime.Token token) {
        int column = token.getCharPositionInLine() + 1;
        int length = token.getText().length();

        Token jPlagToken = new Token(tokenType, this.currentFile, token.getLine(), column, length);
        this.collector.addToken(jPlagToken);
    }

    private void transformToken(TokenType tokenType, org.antlr.v4.runtime.Token start, org.antlr.v4.runtime.Token end) {
        int column = start.getCharPositionInLine() + 1;
        int length = end.getStopIndex() - start.getStartIndex() + 1;

        Token jPlagToken = new Token(tokenType, this.currentFile, start.getLine(), column, length);
        this.collector.addToken(jPlagToken);
    }

    /**
     * Creates a mapping using the start token from antlr as the location
     *
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     */
    protected void createStartMapping(Class<? extends ParserRuleContext> antlrType, TokenType jplagType) {
        this.createStartMapping(antlrType, jplagType, it -> true);
    }

    /**
     * Creates a mapping using the start token from antlr as the location
     *
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param condition The condition under which the mapping applies
     */
    protected <T extends ParserRuleContext> void createStartMapping(Class<T> antlrType, TokenType jplagType, Predicate<T> condition) {
        this.startMappings.add(this.initTypeBuilder(antlrType, jplagType, condition));
    }

    /**
     * Creates a mapping using the stop token from antlr as the location
     *
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     */
    protected void createStopMapping(Class<? extends ParserRuleContext> antlrType, TokenType jplagType) {
        this.createStopMapping(antlrType, jplagType, it -> true);
    }

    /**
     * Creates a mapping using the stop token from antlr as the location
     *
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param condition The condition under which the mapping applies
     */
    protected <T extends ParserRuleContext> void createStopMapping(Class<T> antlrType, TokenType jplagType, Predicate<T> condition) {
        this.endMappings.add(this.initTypeBuilder(antlrType, jplagType, condition));
    }

    /**
     * Creates a mapping using the beginning of the start token as the start location and the distance from the start to the
     * stop token as the length
     *
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     */
    protected void createRangeMapping(Class<? extends ParserRuleContext> antlrType, TokenType jplagType) {
        this.createRangeMapping(antlrType, jplagType, it -> true);
    }

    /**
     * Creates a mapping using the beginning of the start token as the start location and the distance from the start to the
     * stop token as the length
     *
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param condition The condition under which the mapping applies
     */
    protected <T extends ParserRuleContext> void createRangeMapping(Class<T> antlrType, TokenType jplagType, Predicate<T> condition) {
        this.rangeMappings.add(this.initTypeBuilder(antlrType, jplagType, condition));
    }

    /**
     * Creates a start mapping from antlrType to startType and a stop mapping from antlrType to stopType.
     *
     * @param antlrType The antlr token type
     * @param startType The token type for the start mapping
     * @param stopType  The token type for the stop mapping
     */
    protected void createStartStopMapping(Class<? extends ParserRuleContext> antlrType, TokenType startType, TokenType stopType) {
        createStartStopMapping(antlrType, startType, stopType, it -> true);
    }

    /**
     * Creates a start mapping from antlrType to startType and a stop mapping from antlrType to stopType.
     *
     * @param antlrType The antlr token type
     * @param startType The token type for the start mapping
     * @param stopType  The token type for the stop mapping
     * @param condition The condition under which the mapping applies
     */
    protected <T extends ParserRuleContext> void createStartStopMapping(Class<T> antlrType, TokenType startType, TokenType stopType,
                                                                        Predicate<T> condition) {
        this.createStartMapping(antlrType, startType, condition);
        this.createStopMapping(antlrType, stopType, condition);
    }

    /**
     * Creates a mapping for terminal tokens
     *
     * @param terminalType The type of the terminal node
     * @param jplagType    The jplag token type
     */
    protected void createTerminalMapping(int terminalType, TokenType jplagType) {
        this.createTerminalMapping(terminalType, jplagType, it -> true);
    }

    /**
     * Creates a mapping for terminal tokens
     *
     * @param terminalType The type of the terminal node
     * @param jplagType    The jplag token type
     * @param condition    The condition under which the mapping applies
     */
    protected void createTerminalMapping(int terminalType, TokenType jplagType, Predicate<org.antlr.v4.runtime.Token> condition) {
        this.terminalMapping.add(new TypeBuilder<>(jplagType, token -> token.getType() == terminalType && condition.test(token)));
    }

    private <T extends ParserRuleContext> TypeBuilder<RuleContext> initTypeBuilder(Class<T> antlrType, TokenType jplagType, Predicate<T> condition) {
        return new TypeBuilder<>(jplagType, rule -> rule.getClass() == antlrType && condition.test(antlrType.cast(rule)));
    }
}
