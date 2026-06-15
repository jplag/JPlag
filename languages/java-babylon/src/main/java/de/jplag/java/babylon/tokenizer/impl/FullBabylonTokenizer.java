package de.jplag.java.babylon.tokenizer.impl;

import java.io.File;

import de.jplag.TokenType;
import de.jplag.java.babylon.ParserBabylon;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Op;

/**
 * {@link BabylonTokenizer.Provider} implementation that fully outputs all {@link Op}s as tokens without further
 * interpretation.
 */
public class FullBabylonTokenizer extends FullTypedBabylonTokenizer {
    /**
     * Identifier of this tokenizer.
     */
    public static final String IDENTIFIER = "full";

    /**
     * Create a new instance.
     * @param parser the parser to output to
     * @param file the current file
     */
    public FullBabylonTokenizer(ParserBabylon parser, File file) {
        super(parser, file);
    }

    @Override
    protected TokenType getTokenType(Op op) {
        return new UnknownTokenType(op.externalizeOpName());
    }

    /**
     * {@link BabylonTokenizer.Provider} for {@link FullBabylonTokenizer}.
     */
    @AutoService(BabylonTokenizer.Provider.class)
    public static class Provider extends FullTypedBabylonTokenizer.Provider {
        /**
         * Create a new instance.
         */
        public Provider() {
            super(IDENTIFIER);
        }

        @Override
        public BabylonTokenizer getTokenizer(ParserBabylon parser, File file) {
            return new FullBabylonTokenizer(parser, file);
        }
    }
}
