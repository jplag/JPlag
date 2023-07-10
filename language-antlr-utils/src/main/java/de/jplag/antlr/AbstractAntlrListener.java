package de.jplag.antlr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.TokenType;
import de.jplag.semantics.VariableRegistry;

/**
 * Base class for Antlr listeners. You can use the create*Mapping functions to map antlr tokens to jplag tokens.
 * <p>
 * You should create a constructor matching one of the constructors and create your mapping after calling super.
 */
@SuppressWarnings("unused")
public class AbstractAntlrListener implements ParseTreeListener {
    private final List<ContextTokenBuilder<ParserRuleContext>> startMappings;
    private final List<ContextTokenBuilder<ParserRuleContext>> endMappings;
    private final List<ContextTokenBuilder<ParserRuleContext>> rangeMappings;

    private final List<TerminalTokenBuilder> terminalMapping;

    private final TokenCollector collector;
    private final File currentFile;

    private VariableRegistry variableRegistry;

    /**
     * New instance
     * @param collector The token collector
     * @param currentFile The currently processed file
     * @param extractsSemantics If true, the listener will extract semantics along with every token
     */
    public AbstractAntlrListener(TokenCollector collector, File currentFile, boolean extractsSemantics) {
        this.collector = collector;
        this.currentFile = currentFile;

        this.startMappings = new ArrayList<>();
        this.endMappings = new ArrayList<>();
        this.rangeMappings = new ArrayList<>();

        this.terminalMapping = new ArrayList<>();

        if (extractsSemantics) {
            this.variableRegistry = new VariableRegistry();
        }
    }

    /**
     * Craetes a new AbstractAntlrListener, that does not collect semantics information
     * @param collector The collector, obtained by the parser
     * @param currentFile The current file, obtained by the parser
     */
    public AbstractAntlrListener(TokenCollector collector, File currentFile) {
        this(collector, currentFile, false);
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
        this.terminalMapping.stream().filter(mapping -> mapping.matches(terminalNode.getSymbol()))
                .forEach(mapping -> mapping.createToken(terminalNode.getSymbol(), variableRegistry));
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
        // does nothing, because we do not handle error nodes right now.
    }

    @Override
    public void enterEveryRule(ParserRuleContext rule) {
        this.startMappings.stream().filter(mapping -> mapping.matches(rule)).forEach(mapping -> mapping.createToken(rule, variableRegistry));

        this.rangeMappings.stream().filter(mapping -> mapping.matches(rule)).forEach(mapping -> mapping.createToken(rule, variableRegistry));
    }

    @Override
    public void exitEveryRule(ParserRuleContext rule) {
        this.endMappings.stream().filter(mapping -> mapping.matches(rule)).forEach(mapping -> mapping.createToken(rule, variableRegistry));
    }

    /**
     * Creates a mapping using the start token from antlr as the location
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> createStartMapping(Class<T> antlrType, TokenType jplagType) {
        return this.createStartMapping(antlrType, jplagType, it -> true);
    }

    /**
     * Creates a mapping using the start token from antlr as the location
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param condition The condition under which the mapping applies
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    @SuppressWarnings("unchecked")
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> createStartMapping(Class<T> antlrType, TokenType jplagType,
            Predicate<T> condition) {
        ContextTokenBuilder<T> builder = initTypeBuilder(antlrType, jplagType, condition, ContextTokenBuilderType.START);
        this.startMappings.add((ContextTokenBuilder<ParserRuleContext>) builder);
        return builder;
    }

    /**
     * Creates a mapping using the stop token from antlr as the location
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> createStopMapping(Class<T> antlrType, TokenType jplagType) {
        return this.createStopMapping(antlrType, jplagType, it -> true);
    }

    /**
     * Creates a mapping using the stop token from antlr as the location
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param condition The condition under which the mapping applies
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    @SuppressWarnings("unchecked")
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> createStopMapping(Class<T> antlrType, TokenType jplagType,
            Predicate<T> condition) {
        ContextTokenBuilder<T> builder = initTypeBuilder(antlrType, jplagType, condition, ContextTokenBuilderType.STOP);
        this.endMappings.add((ContextTokenBuilder<ParserRuleContext>) builder);
        return builder;
    }

    /**
     * Creates a mapping using the beginning of the start token as the start location and the distance from the start to the
     * stop token as the length
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> createRangeMapping(Class<T> antlrType, TokenType jplagType) {
        return this.createRangeMapping(antlrType, jplagType, it -> true);
    }

    /**
     * Creates a mapping using the beginning of the start token as the start location and the distance from the start to the
     * stop token as the length
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     * @param condition The condition under which the mapping applies
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    @SuppressWarnings("unchecked")
    protected <T extends ParserRuleContext> ContextTokenBuilder<T> createRangeMapping(Class<T> antlrType, TokenType jplagType,
            Predicate<T> condition) {
        ContextTokenBuilder<T> builder = initTypeBuilder(antlrType, jplagType, condition, ContextTokenBuilderType.RANGE);
        this.rangeMappings.add((ContextTokenBuilder<ParserRuleContext>) builder);
        return builder;
    }

    /**
     * Creates a start mapping from antlrType to startType and a stop mapping from antlrType to stopType.
     * @param antlrType The antlr token type
     * @param startType The token type for the start mapping
     * @param stopType The token type for the stop mapping
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    protected <T extends ParserRuleContext> RangeBuilder<T> createStartStopMapping(Class<T> antlrType, TokenType startType, TokenType stopType) {
        return createStartStopMapping(antlrType, startType, stopType, it -> true);
    }

    /**
     * Creates a start mapping from antlrType to startType and a stop mapping from antlrType to stopType.
     * @param antlrType The antlr token type
     * @param startType The token type for the start mapping
     * @param stopType The token type for the stop mapping
     * @param condition The condition under which the mapping applies
     * @param <T> The type of {@link ParserRuleContext}
     * @return The builder for the token
     */
    protected <T extends ParserRuleContext> RangeBuilder<T> createStartStopMapping(Class<T> antlrType, TokenType startType, TokenType stopType,
            Predicate<T> condition) {
        ContextTokenBuilder<T> start = this.createStartMapping(antlrType, startType, condition);
        ContextTokenBuilder<T> end = this.createStopMapping(antlrType, stopType, condition);
        return new RangeBuilder<>(start, end);
    }

    /**
     * Creates a mapping for terminal tokens
     * @param terminalType The type of the terminal node
     * @param jplagType The jplag token type
     * @return The builder for the token
     */
    protected TerminalTokenBuilder createTerminalMapping(int terminalType, TokenType jplagType) {
        return this.createTerminalMapping(terminalType, jplagType, it -> true);
    }

    /**
     * Creates a mapping for terminal tokens
     * @param terminalType The type of the terminal node
     * @param jplagType The jplag token type
     * @param condition The condition under which the mapping applies
     * @return The builder for the token
     */
    protected TerminalTokenBuilder createTerminalMapping(int terminalType, TokenType jplagType, Predicate<org.antlr.v4.runtime.Token> condition) {
        TerminalTokenBuilder builder = new TerminalTokenBuilder(jplagType, token -> token.getType() == terminalType && condition.test(token),
                this.collector, this.currentFile);
        this.terminalMapping.add(builder);
        return builder;
    }

    private <T extends ParserRuleContext> ContextTokenBuilder<T> initTypeBuilder(Class<T> antlrType, TokenType jplagType, Predicate<T> condition,
            ContextTokenBuilderType type) {
        return new ContextTokenBuilder<>(jplagType, rule -> rule.getClass() == antlrType && condition.test(antlrType.cast(rule)), this.collector,
                this.currentFile, type);
    }
}
