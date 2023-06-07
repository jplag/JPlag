package de.jplag.antlr;

import de.jplag.Token;
import de.jplag.TokenType;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AbstractAntlrListener implements ParseTreeListener {
    private final Map<Class<? extends ParserRuleContext>, TokenType> startMapping;
    private final Map<Class<? extends ParserRuleContext>, TokenType> endMapping;
    private final Map<Class<? extends ParserRuleContext>, TokenType> rangeMapping;

    private final Map<String, TokenType> terminalMapping;

    private final TokenCollector collector;
    private final File currentFile;

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

    protected void createStartMapping(Class<? extends ParserRuleContext> antlrType, TokenType jplagType) {
        this.startMapping.put(antlrType, jplagType);
    }

    protected void createStopMapping(Class<? extends ParserRuleContext> antlrType, TokenType jplagType) {
        this.endMapping.put(antlrType, jplagType);
    }

    protected void createRangeMapping(Class<? extends ParserRuleContext> antlrType, TokenType jplagType) {
        this.rangeMapping.put(antlrType, jplagType);
    }

    protected void createStartStopMapping(Class<? extends ParserRuleContext> antlrType, TokenType startType, TokenType stopType) {
        this.createStartMapping(antlrType, startType);
        this.createStopMapping(antlrType, stopType);
    }

    protected void createTerminalMapping(String terminalRegex, TokenType jplagType) {
        this.terminalMapping.put(terminalRegex, jplagType);
    }
}
