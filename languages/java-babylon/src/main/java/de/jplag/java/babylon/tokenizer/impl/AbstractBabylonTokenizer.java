package de.jplag.java.babylon.tokenizer.impl;

import java.io.File;

import de.jplag.TokenType;
import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.ParserBabylon;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.semantics.CodeSemantics;

import jdk.incubator.code.Op;

/**
 * Responsible for transforming a code model into a {@link de.jplag.Token} sequence by outputting it to a
 * {@link ParserBabylon}.
 */
public abstract class AbstractBabylonTokenizer implements BabylonTokenizer, BabylonDSL {
    protected final ParserBabylon parser;
    protected final File file;

    /**
     * Create a new instance.
     * @param parser the parser to output to
     * @param file the current file
     */
    public AbstractBabylonTokenizer(ParserBabylon parser, File file) {
        this.parser = parser;
        this.file = file;
    }

    protected void addToken(TokenType type, Op.Location location, CodeSemantics semantics) {
        parser.add(type, file, location, semantics);
    }

    protected void addToken(TokenType type, CodeSemantics semantics) {
        parser.add(type, file, semantics);
    }

    /**
     * Tokenize a single {@link Op}.
     * @param op the op to tokenize
     * @throws IllegalArgumentException if the op is invalid
     */
    public abstract void handle(Op op);

    /**
     * Responsible for creating instances of {@link AbstractBabylonTokenizer} subclasses.
     */
    public static abstract class Provider implements BabylonTokenizer.Provider {
        private final String identifier;

        /**
         * Create a new instance.
         * @param identifier the identifier of this tokenizer for use in the CLI
         */
        public Provider(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public final String getIdentifier() {
            return identifier;
        }
    }
}
