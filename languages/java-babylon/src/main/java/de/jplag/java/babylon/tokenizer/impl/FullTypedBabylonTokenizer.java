package de.jplag.java.babylon.tokenizer.impl;

import com.google.auto.service.AutoService;
import de.jplag.TokenType;
import de.jplag.java.babylon.ParserBabylon;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.semantics.CodeSemantics;
import jdk.incubator.code.Body;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.java.JavaType;

import java.io.File;

/**
 * {@link BabylonTokenizer} implementation that fully outputs all {@link Op}s as tokens without further interpretation.
 * Includes result types.
 */
@AutoService(BabylonTokenizer.class)
public class FullTypedBabylonTokenizer extends AbstractBabylonTokenizer {
    /**
     * Identifier of this tokenizer.
     */
    public static final String IDENTIFIER = "full-typed";

    protected FullTypedBabylonTokenizer(String identifier) {
        super(identifier);
    }

    /**
     * Create a new instance.
     */
    public FullTypedBabylonTokenizer() {
        this(IDENTIFIER);
    }

    @Override
    public BabylonTokenizer.AtFile atFile(ParserBabylon parser, File file) {
        return new AtFile(parser, file);
    }

    /**
     * Tokenizer bound to a particular file, defaulting to that file for output {@link de.jplag.Token}s.
     */
    public static class AtFile extends AbstractBabylonTokenizer.AtFile {
        /**
         * Create a new instance.
         *
         * @param parser the parser to output to
         * @param file   the current file
         */
        public AtFile(ParserBabylon parser, File file) {
            super(parser, file);
        }

        @Override
        public void handle(Op op) {
            addToken(getTokenType(op), op.location(), CodeSemantics.createControl());
            for (Body body : op.bodies()) handle(body);
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
    }
}
