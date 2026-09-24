package de.jplag.java.babylon.tokenizer.impl;

import java.io.File;

import de.jplag.TokenType;
import de.jplag.java.babylon.ParserBabylon;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.semantics.CodeSemantics;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Body;
import jdk.incubator.code.Op;

/**
 * {@link BabylonTokenizer.Provider} implementation that fully outputs all {@link Op}s as tokens without further
 * interpretation.
 */
public class FullBabylonTokenizer extends AbstractBabylonTokenizer {
    /**
     * Identifier of this tokenizer.
     */
    public static final String IDENTIFIER = "full";

    protected FullBabylonTokenizer(ParserBabylon parser, File file) {
        super(parser, file);
    }

    @Override
    public void tokenize(Op op) {
        addToken(getTokenType(op), op.location(), CodeSemantics.createControl());
        for (Body body : op.bodies())
            tokenize(body);
    }

    protected TokenType getTokenType(Op op) {
        return new UnknownTokenType(op.externalizeOpName());
    }

    /**
     * {@link BabylonTokenizer.Provider} for {@link FullBabylonTokenizer}.
     */
    @AutoService(BabylonTokenizer.Provider.class)
    public static class Provider extends AbstractBabylonTokenizer.Provider {
        /**
         * Create a new instance.
         */
        public Provider() {
            super(IDENTIFIER);
        }

        protected Provider(String identifier) {
            super(identifier);
        }

        @Override
        public BabylonTokenizer getTokenizer(TokenizerConstructionContext context) {
            return new FullBabylonTokenizer(context.parser(), context.file());
        }
    }
}
