package de.jplag.antlr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.logging.Logger;

import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.semantics.CodeSemantics;

/**
 * Handles the extraction of tokens. Contains information on the appropriate antlr types, the conditions under which the
 * token should be extracted and semantics information.
 * @param <T> The antlr type being mapped
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class TokenBuilder<T> {
    private static final Logger LOG = Logger.getLogger(TokenBuilder.class.getName());
    private static final String UNEXPECTED_SEMANTICS = "The listener %s indicates, it does not extract semantics. But the token (%s) has semantics information";
    private static final String MISSING_SEMANTICS = "Tokens should contain semantics, but none were supplied";

    private final Predicate<T> condition;
    protected final TokenType tokenType;

    private Function<T, CodeSemantics> semanticsSupplier = null;
    private final List<BiConsumer<SemanticsDataHolder, T>> semanticsHandler;

    protected final TokenCollector tokenCollector;
    protected final File file;

    /**
     * New instance
     * @param tokenType The token type
     * @param condition The condition
     * @param collector The token collector for the listener
     * @param file The file the listener is for
     */
    TokenBuilder(TokenType tokenType, Predicate<T> condition, TokenCollector collector, File file) {
        this.condition = condition;
        this.tokenType = tokenType;
        this.tokenCollector = collector;
        this.file = file;

        this.semanticsHandler = new ArrayList<>();
    }

    /**
     * Checks if the token should be extracted for this node.
     * @param value The node to check
     * @return true, if the token should be extracted
     */
    boolean matches(T value) {
        return this.condition.test(value);
    }

    /**
     * Sets the given semantics for the token
     * @param semantics The semantics
     * @return Self
     */
    public TokenBuilder<T> withSemantics(CodeSemantics semantics) {
        this.semanticsSupplier = ignore -> semantics;
        return this;
    }

    /**
     * Uses the given function to build the token semantics from the antlr node
     * @param function The function
     * @return Self
     */
    public TokenBuilder<T> withSemantics(Function<T, CodeSemantics> function) {
        this.semanticsSupplier = function;
        return this;
    }

    /**
     * Sets control semantics for the token
     * @return Self
     */
    public TokenBuilder<T> withControlSemantics() {
        withSemantics(CodeSemantics.createControl());
        return this;
    }

    /**
     * Adds a semantics handler to this builder. This can be used to perform additional operation like calling methods in
     * the {@link de.jplag.semantics.VariableRegistry}.
     * @param handler The handler function
     * @return Self
     */
    public TokenBuilder<T> addSemanticsHandler(Consumer<SemanticsDataHolder> handler) {
        this.semanticsHandler.add((semantics, rule) -> handler.accept(semantics));
        return this;
    }

    /**
     * Adds a semantics handler, that can perform additional operations required for semantics using the Semantics context
     * objects and the antlr node.
     * @param handler The handler
     * @return Self
     */
    public TokenBuilder<T> addSemanticsHandler(BiConsumer<SemanticsDataHolder, T> handler) {
        this.semanticsHandler.add(handler);
        return this;
    }

    void createToken(T antlrContent, SemanticsDataHolder semantics) {
        org.antlr.v4.runtime.Token antlrToken = getAntlrToken(antlrContent);

        int line = antlrToken.getLine();
        int column = antlrToken.getCharPositionInLine() + 1;
        int length = antlrToken.getText().length();

        Token token;
        if (semantics != null) {
            if (semanticsSupplier == null) {
                throw new InternalListenerException(MISSING_SEMANTICS);
            }

            this.semanticsHandler.forEach(it -> it.accept(semantics, antlrContent));
            token = new Token(this.tokenType, this.file, line, column, length, semanticsSupplier.apply(antlrContent));
        } else {
            if (semanticsSupplier != null) {
                LOG.warning(() -> String.format(UNEXPECTED_SEMANTICS, this.getClass().getName(), this.tokenType.getDescription()));
            }

            token = new Token(this.tokenType, this.file, line, column, length);
        }

        this.tokenCollector.addToken(token);
    }

    protected abstract org.antlr.v4.runtime.Token getAntlrToken(T antlrContent);

    protected int getLength(T antlrContent) {
        return getAntlrToken(antlrContent).getText().length();
    }
}
