package de.jplag.java.babylon.tokenizer.impl;

import com.google.auto.service.AutoService;
import de.jplag.java.JavaTokenType;
import de.jplag.java.babylon.ParserBabylon;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.semantics.CodeSemantics;
import jdk.incubator.code.Body;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.JavaOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * {@link BabylonTokenizer} implementation intended for high-level code models.
 * Deliberately ignores smaller {@link Op}s, aiming for a similar abstraction level to {@link de.jplag.java.TokenGeneratingTreeScanner}.
 */
@AutoService(BabylonTokenizer.class)
public class HighLevelBabylonTokenizer extends AbstractBabylonTokenizer {
    protected static final Logger logger = LoggerFactory.getLogger(HighLevelBabylonTokenizer.class);

    /**
     * Create a new instance.
     */
    public HighLevelBabylonTokenizer() {
        super("high-level");
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

        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(Op op) {
            switch (op) {
                case CoreOp.FuncOp func -> {
                    addToken(JavaTokenType.J_METHOD_BEGIN, func.location(), CodeSemantics.createControl());
                    handle(func.body());
                    addToken(JavaTokenType.J_METHOD_END, CodeSemantics.createControl());
                }
                case JavaOp.LambdaOp lambda -> {
                    addToken(JavaTokenType.J_METHOD_BEGIN, lambda.location(), CodeSemantics.createControl());
                    handle(lambda.body());
                    addToken(JavaTokenType.J_METHOD_END, CodeSemantics.createControl());
                }
                case JavaOp.SynchronizedOp sync -> {
                    addToken(JavaTokenType.J_SYNC_BEGIN, sync.location(), CodeSemantics.createControl());
                    handle(sync.blockBody());
                    addToken(JavaTokenType.J_SYNC_END, CodeSemantics.createControl());
                }
                case CoreOp.ReturnOp returnOp -> {
                    addToken(JavaTokenType.J_RETURN, returnOp.location(), CodeSemantics.createControl());
                    handle(returnOp.returnValue());
                }
                case JavaOp.BreakOp breakOp -> {
                    addToken(JavaTokenType.J_BREAK, breakOp.location(), CodeSemantics.createControl());
                }
                case JavaOp.TryOp tryOp -> {
                    addToken(JavaTokenType.J_TRY_BEGIN, tryOp.location(), CodeSemantics.createControl());
                    var rb = tryOp.resourcesBody();
                    if (rb != null) {
                        throw new IllegalArgumentException("Try-with-resources should have been lowered out");
                    }
                    handle(tryOp.body());
                    addToken(JavaTokenType.J_TRY_END, CodeSemantics.createControl());
                    for (Body catchBody : tryOp.catchBodies()) {
                        addToken(JavaTokenType.J_CATCH_BEGIN, location(catchBody), CodeSemantics.createControl());
                        handle(catchBody);
                        addToken(JavaTokenType.J_CATCH_END, CodeSemantics.createControl());
                    }
                    var fin = tryOp.finallyBody();
                    if (fin != null) {
                        addToken(JavaTokenType.J_FINALLY_BEGIN, location(fin), CodeSemantics.createControl());
                        handle(fin);
                        addToken(JavaTokenType.J_FINALLY_END, CodeSemantics.createControl());
                    }
                }
                default -> logger.warn("Unsupported op: {} with content: {}", op.getClass(), op.toText());
            }
        }
    }
}
