package de.jplag.antlr;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
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
    private final Map<Class<? extends ParserRuleContext>, TokenType> startMapping;
    private final Map<Class<? extends ParserRuleContext>, TokenType> endMapping;
    private final Map<Class<? extends ParserRuleContext>, TokenType> rangeMapping;

    private final Map<String, TokenType> terminalMapping;

    private final TokenCollector collector;
    private final File currentFile;

    /**
     * New instance
     * @param collector The token collector
     * @param currentFile The currently processed file
     */
    public AbstractAntlrListener(TokenCollector collector, File currentFile) {
        this.collector = collector;
        this.currentFile = currentFile;

        this.startMapping = new HashMap<>();
        this.endMapping = new HashMap<>();
        this.rangeMapping = new HashMap<>();

        this.terminalMapping = new HashMap<>();
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
        this.terminalMapping.forEach((regex, type) -> {
            if (terminalNode.getSymbol().getText().matches(regex)) {
                transformToken(type, terminalNode.getSymbol());
            }
        });
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {
        if (this.startMapping.containsKey(parserRuleContext.getClass())) {
            TokenType type = this.startMapping.get(parserRuleContext.getClass());
            transformToken(type, parserRuleContext.getStart());
        }

        if (this.rangeMapping.containsKey(parserRuleContext.getClass())) {
            TokenType type = this.rangeMapping.get(parserRuleContext.getClass());
            transformToken(type, parserRuleContext.getStart(), parserRuleContext.getStop());
        }
    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {
        if (this.endMapping.containsKey(parserRuleContext.getClass())) {
            TokenType type = this.endMapping.get(parserRuleContext.getClass());
            transformToken(type, parserRuleContext.getStop());
        }
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
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     */
    protected void createStartMapping(Class<? extends ParserRuleContext> antlrType, TokenType jplagType) {
        this.startMapping.put(antlrType, jplagType);
    }

    /**
     * Creates a mapping using the stop token from antlr as the location
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     */
    protected void createStopMapping(Class<? extends ParserRuleContext> antlrType, TokenType jplagType) {
        this.endMapping.put(antlrType, jplagType);
    }

    /**
     * Creates a mapping using the beginning of the start token as the start location and the distance from the start to the
     * stop token as the length
     * @param antlrType The antlr context type
     * @param jplagType The Jplag token type
     */
    protected void createRangeMapping(Class<? extends ParserRuleContext> antlrType, TokenType jplagType) {
        this.rangeMapping.put(antlrType, jplagType);
    }

    /**
     * Creates a start mapping from antlrType to startType and a stop mapping from antlrType to stopType.
     * @param antlrType The antlr token type
     * @param startType The token type for the start mapping
     * @param stopType The token type for the stop mapping
     */
    protected void createStartStopMapping(Class<? extends ParserRuleContext> antlrType, TokenType startType, TokenType stopType) {
        this.createStartMapping(antlrType, startType);
        this.createStopMapping(antlrType, stopType);
    }

    /**
     * Creates a mapping for terminal tokens
     * @param terminalRegex The regex identifying the correct tokens
     * @param jplagType The jplag token type
     */
    protected void createTerminalMapping(String terminalRegex, TokenType jplagType) {
        this.terminalMapping.put(terminalRegex, jplagType);
    }
}
