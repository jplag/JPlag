package de.jplag.antlr;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableRegistry;

/**
 * Collects the tokens during parsing.
 */
public class TokenCollector {
    private static final Logger logger = LoggerFactory.getLogger(TokenCollector.class);

    private final List<Token> collected;
    private final boolean extractsSemantics;
    private File file;

    /**
     * @param extractsSemantics If semantics are extracted
     */
    TokenCollector(boolean extractsSemantics) {
        this.collected = new ArrayList<>();
        this.extractsSemantics = extractsSemantics;
    }

    /**
     * @return All collected tokens
     */
    List<Token> getTokens() {
        return Collections.unmodifiableList(this.collected);
    }

    <T> void addToken(TokenType jplagType, Function<T, CodeSemantics> semanticsSupplier, T entity,
            Function<T, org.antlr.v4.runtime.Token> extractStartToken, Function<T, org.antlr.v4.runtime.Token> extractEndToken,
            VariableRegistry variableRegistry) {
        if (jplagType == null) {
            return;
        }
        org.antlr.v4.runtime.Token antlrToken = extractStartToken.apply(entity);
        org.antlr.v4.runtime.Token antlrEndToken = extractEndToken.apply(entity);
        int startLine = antlrToken.getLine();
        int startColumn = antlrToken.getCharPositionInLine() + 1;
        int endLine = antlrEndToken.getLine();
        int endColumn = antlrEndToken.getCharPositionInLine() + 1;
        int length = antlrEndToken.getStopIndex() - antlrToken.getStartIndex() + 1;
        Token token;
        if (extractsSemantics) {
            if (semanticsSupplier == null) {
                throw new IllegalStateException(String.format("Expected semantics bud did not receive any for token %s", jplagType.getDescription()));
            }
            CodeSemantics semantics = semanticsSupplier.apply(entity);
            token = new Token(jplagType, this.file, startLine, startColumn, endLine, endColumn, length, semantics);
            variableRegistry.updateSemantics(semantics);
        } else {
            if (semanticsSupplier != null) {
                logger.warn("Received semantics for token {} despite not expecting any", jplagType.getDescription());
            }
            token = new Token(jplagType, this.file, startLine, startColumn, endLine, endColumn, length);
        }
        addToken(token);
    }

    void enterFile(File newFile) {
        this.file = newFile;
    }

    void addFileEndToken() {
        addToken(extractsSemantics ? Token.semanticFileEnd(file) : Token.fileEnd(file));
        // don't need to update semantics because variable registry is new for every file
    }

    private void addToken(Token token) {
        this.collected.add(token);
    }
}
