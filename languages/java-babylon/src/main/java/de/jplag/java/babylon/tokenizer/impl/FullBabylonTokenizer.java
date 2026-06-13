package de.jplag.java.babylon.tokenizer.impl;

import java.io.File;

import de.jplag.TokenType;
import de.jplag.java.babylon.ParserBabylon;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Op;

/**
 * {@link BabylonTokenizer} implementation that fully outputs all {@link Op}s as tokens without further interpretation.
 */
@AutoService(BabylonTokenizer.class)
public class FullBabylonTokenizer extends FullTypedBabylonTokenizer {
    /**
     * Identifier of this tokenizer.
     */
    public static final String IDENTIFIER = "full";

    /**
     * Create a new instance.
     */
    public FullBabylonTokenizer() {
        super(IDENTIFIER);
    }

    @Override
    public BabylonTokenizer.AtFile atFile(ParserBabylon parser, File file) {
        return new AtFile(parser, file);
    }

    /**
     * Tokenizer bound to a particular file, defaulting to that file for output {@link de.jplag.Token}s.
     */
    public static class AtFile extends FullTypedBabylonTokenizer.AtFile {
        /**
         * Create a new instance.
         * @param parser the parser to output to
         * @param file the current file
         */
        public AtFile(ParserBabylon parser, File file) {
            super(parser, file);
        }

        @Override
        protected TokenType getTokenType(Op op) {
            return new UnknownTokenType(op.externalizeOpName());
        }
    }
}
