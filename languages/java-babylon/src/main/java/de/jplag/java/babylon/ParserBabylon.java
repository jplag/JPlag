package de.jplag.java.babylon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.java.JavacAdapter;
import de.jplag.java.Parser;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.java.babylon.transformer.TransformationPipeline;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableRegistry;

import jdk.incubator.code.Op;

/**
 * {@link Parser} with extensions for Babylon, including a Babylon-aligned API for adding tokens.
 */
public class ParserBabylon extends Parser {
    private static final Logger logger = LoggerFactory.getLogger(ParserBabylon.class);
    private final TransformationPipeline pipeline;
    private final VariableRegistry variableRegistry;
    private final BabylonTokenizer tokenizer;

    /**
     * Crease a new instance.
     * @param pipeline the pipeline to use for this parser
     * @param tokenizer the tokenizer to use for extracting tokens from the model
     */
    public ParserBabylon(TransformationPipeline pipeline, BabylonTokenizer tokenizer) {
        this.pipeline = pipeline;
        this.tokenizer = tokenizer;
        this.variableRegistry = new VariableRegistry();
    }

    /**
     * Returns the pipeline that is used for this parser.
     * @return the pipeline used by this parser
     */
    public TransformationPipeline getPipeline() {
        return pipeline;
    }

    /**
     * Returns the {@link VariableRegistry} that is used for this parser.
     * @return the variable registry used by this parser
     */
    public VariableRegistry getVariableRegistry() {
        return variableRegistry;
    }

    @Override
    protected JavacAdapter getJavacAdapter() {
        return new JavacAdapterBabylon(pipeline, variableRegistry, tokenizer);
    }

    @Override
    public List<Token> parse(Set<File> files) throws ParsingException {
        lastLocation = new Op.Location(1, 1);
        deferredTokens.clear();
        List<Token> tokens;
        try {
            tokens = super.parse(files);
        } catch (Error | RuntimeException e) {
            logger.error("Error while parsing", e);
            throw e;
        }
        drainDeferredTokens(lastLocation);
        return tokens;
    }

    private final List<DeferredToken> deferredTokens = new ArrayList<>();
    private Op.Location lastLocation;

    @Override
    public void add(Token token) {
        Op.Location location = token.getStartLine() == Token.NO_VALUE || token.getStartColumn() == Token.NO_VALUE ? null
                : new Op.Location(token.getStartLine(), token.getStartColumn());
        drainDeferredTokens(location);
        super.add(token);
        this.variableRegistry.updateSemantics(token.getSemantics());
        updateLastLocation(location);
    }

    private void updateLastLocation(@Nullable Op.Location location) {
        if (location == null)
            return;
        if (location.line() < lastLocation.line())
            return;
        if (location.line() == lastLocation.line() && location.column() < lastLocation.column())
            return;
        this.lastLocation = location;
    }

    private void drainDeferredTokens(@Nullable Op.Location nextLocation) {
        var copy = List.copyOf(deferredTokens);
        deferredTokens.clear();
        for (DeferredToken deferredToken : copy) {
            Op.Location startLocation = Objects.requireNonNullElse(deferredToken.location, lastLocation);
            Op.Location endLocation;
            int length;
            if (nextLocation == null || startLocation.line() != nextLocation.line() || startLocation.column() >= nextLocation.column()) {
                endLocation = new Op.Location(startLocation.line(), startLocation.column() + 1);
                length = 1;
            } else {
                endLocation = new Op.Location(nextLocation.line(), nextLocation.column() - 1);
                length = endLocation.column() - startLocation.column();
            }
            super.add(new Token(deferredToken.type, deferredToken.file, startLocation.line(), startLocation.column(), endLocation.line(),
                    endLocation.column(), length, deferredToken.semantics));
            variableRegistry.updateSemantics(deferredToken.semantics);
        }
    }

    /**
     * Add a new token to the current file. For internal use by the language module.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param startLine is the line index in the source code where the token starts. Index is 1-based.
     * @param startColumn is the column index, meaning where the token starts in the line. Index is 1-based.
     * @param endLine is the line index in the source code where the token ends. Index is 1-based.
     * @param endColumn is the column index, meaning where the token ends in the line. Index is 1-based.
     * @param length is the length of the token in the source code.
     * @param semantics is a record containing semantic information about the token.
     */
    public void add(TokenType type, File file, int startLine, int startColumn, int endLine, int endColumn, int length, CodeSemantics semantics) {
        add(new Token(type, file, startLine, startColumn, endLine, endColumn, length, semantics));
    }

    /**
     * Add a new token to the current file. For internal use by the language module.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param location the location in the source code where the token resides.
     * @param semantics is a record containing semantic information about the token.
     */
    public void add(TokenType type, File file, @Nullable Op.Location location, CodeSemantics semantics) {
        if (location == null) {
            location = lastLocation;
        } else {
            drainDeferredTokens(location);
        }
        deferredTokens.add(new DeferredToken(type, file, semantics, location));
        updateLastLocation(location);
    }

    /**
     * Add a new token to the current file. For internal use by the language module.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param semantics is a record containing semantic information about the token.
     */
    public void add(TokenType type, File file, CodeSemantics semantics) {
        add(type, file, null, semantics);
    }

    private record DeferredToken(TokenType type, File file, CodeSemantics semantics, @Nullable Op.Location location) {
    }
}
