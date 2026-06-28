package de.jplag.java.babylon.tokenizer.impl;

import java.io.File;

import de.jplag.TokenType;
import de.jplag.java.babylon.ParserBabylon;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.semantics.CodeSemantics;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Body;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.java.JavaType;

/**
 * {@link BabylonTokenizer} implementation that fully outputs all {@link Op}s as tokens without further interpretation.
 * Includes result types.
 */
public class FullTypedBabylonTokenizer extends AbstractBabylonTokenizer {
    /**
     * Identifier of this tokenizer.
     */
    public static final String IDENTIFIER = "full-typed";

    /**
     * Create a new instance.
     * @param parser the parser to output to
     * @param file the current file
     */
    public FullTypedBabylonTokenizer(ParserBabylon parser, File file) {
        super(parser, file);
    }

    @Override
    public void handle(Op op) {
        addToken(getTokenType(op), op.location(), CodeSemantics.createControl());
        for (Body body : op.bodies())
            handle(body);
    }

    protected TokenType getTokenType(Op op) {
        StringBuilder sb = new StringBuilder();
        if (op.parent() != null) {
            Op.Result opr = op.result();
            if (!opr.type().equals(JavaType.VOID)) {
                sb.append(opr.type().externalize()).append(" = ");
            }
        }
        sb.append(op.externalizeOpName());
        return new UnknownTokenType(sb.toString());
    }

    /**
     * {@link BabylonTokenizer.Provider} for {@link FullTypedBabylonTokenizer}.
     */
    @AutoService(BabylonTokenizer.Provider.class)
    public static class Provider extends AbstractBabylonTokenizer.Provider {
        /**
         * Create a new instance.
         */
        public Provider() {
            this(IDENTIFIER);
        }

        protected Provider(String identifier) {
            super(identifier);
        }

        @Override
        public BabylonTokenizer getTokenizer(TokenizerConstructionContext context) {
            return new FullTypedBabylonTokenizer(context.parser(), context.file());
        }
    }
}
