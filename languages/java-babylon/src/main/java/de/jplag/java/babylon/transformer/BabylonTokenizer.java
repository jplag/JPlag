package de.jplag.java.babylon.transformer;

import de.jplag.TokenType;
import de.jplag.java.JavaTokenType;
import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.ParserBabylon;
import de.jplag.semantics.CodeSemantics;
import jdk.incubator.code.*;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.JavaOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class BabylonTokenizer implements BabylonDSL {
    private static final Logger logger = LoggerFactory.getLogger(BabylonTokenizer.class);

    private final ParserBabylon parser;
    private final File file;

    public BabylonTokenizer(ParserBabylon parser, File file) {
        this.parser = parser;
        this.file = file;
    }

    public void addToken(TokenType type, Op.Location location, CodeSemantics semantics) {
        parser.add(type, file, location, semantics);
    }

    public void addToken(TokenType type, CodeSemantics semantics) {
        parser.add(type, file, semantics);
    }

    public void handle(Body body) {
        for (Block block : body.blocks()) {
            handle(block);
        }
    }

    public void handle(Block block) {
        for (Op op : block.ops()) {
            handle(op);
        }
    }

    public void handle(Value value) {
        switch (value) {
            case null -> {}
            case Block.Parameter _ -> {}
            case Op.Result result -> handle(result.op());
        }
    }

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
                    throw new IllegalStateException("Try-with-resources should have been lowered out");
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
