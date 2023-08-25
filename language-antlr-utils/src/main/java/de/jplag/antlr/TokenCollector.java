package de.jplag.antlr;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableRegistry;

/**
 * Collects the tokens during parsing.
 */
public class TokenCollector {
    private static final Logger logger = Logger.getLogger(TokenCollector.class.getName());
    private final List<Token> collected;
    private final boolean extractsSemantics;
    private File file;

    /**
     * @param extractsSemantics If semantics are extracted
     */
    TokenCollector(boolean extractsSemantics, File initialFile) {
        this.collected = new ArrayList<>();
        this.extractsSemantics = extractsSemantics;
        this.file = initialFile;
    }

    /**
     * @return All collected tokens
     */
    List<Token> getTokens() {
        return Collections.unmodifiableList(this.collected);
    }

    <T> void addToken(TokenType jplagType, Function<T, CodeSemantics> semanticsSupplier, T entity,
            Function<T, org.antlr.v4.runtime.Token> extractToken, VariableRegistry variableRegistry) {
        org.antlr.v4.runtime.Token antlrToken = extractToken.apply(entity);
        int line = antlrToken.getLine();
        int column = antlrToken.getCharPositionInLine() + 1;
        int length = antlrToken.getText().length();
        Token token;
        if (extractsSemantics) {
            if (semanticsSupplier == null)
                throw new IllegalStateException(String.format("Expected semantics bud did not receive any for token %s", jplagType.getDescription()));
            CodeSemantics semantics = semanticsSupplier.apply(entity);
            token = new Token(jplagType, this.file, line, column, length, semantics);
            variableRegistry.updateSemantics(semantics);
        } else {
            if (semanticsSupplier != null)
                logger.warning(() -> String.format("Received semantics for token %s despite not expecting any", jplagType.getDescription()));
            token = new Token(jplagType, this.file, line, column, length);
        }
        addToken(token);
    }

    /**
     * Add a file end token.
     * @param newFile The next file, null if there isn't one.
     */
    void addFileEndToken(File newFile) {
        addToken(extractsSemantics ? Token.semanticFileEnd(file) : Token.fileEnd(file));
        // don't need to update semantics because variable registry is new for every file
        this.file = newFile;
    }

    private void addToken(Token token) {
        this.collected.add(token);
    }
}
