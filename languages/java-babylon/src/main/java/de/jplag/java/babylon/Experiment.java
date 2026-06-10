package de.jplag.java.babylon;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import de.jplag.*;
import de.jplag.java.JavaTokenType;
import de.jplag.java.babylon.transformer.TryWithoutResourcesTransformer;
import de.jplag.semantics.CodeSemantics;
import jdk.incubator.code.*;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.JavaOp;

import javax.tools.JavaCompiler;
import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static de.jplag.Token.NO_VALUE;

public class Experiment implements BabylonDSL {
    private static final Path SOURCES = Path.of(
            "languages", "java", "src", "test", "resources", "de", "jplag", "java"
    );

    private final ParserBabylon parser;
    private final File file;

    public Experiment(ParserBabylon parser, File file) {
        this.parser = parser;
        this.file = file;
    }

    public static void main(String[] args) throws NoSuchMethodException, ParsingException {
        var files = Set.of(SOURCES.resolve("TryWithResource.java").toFile());
        var tokens = new JavaBabylonLanguage().parse(files, false);
        IO.println(TokenPrinterUtils.printTokensByFile(tokens));
    }

    public void addToken(TokenType type, File file, long startLine, long startColumn, long endLine, long endColumn, long length,
                         CodeSemantics semantics) {
        parser.add(new Token(type, file, Math.toIntExact(startLine), Math.toIntExact(startColumn), Math.toIntExact(endLine),
                Math.toIntExact(endColumn), Math.toIntExact(length), semantics));
    }

    public void addToken(TokenType type, File file, Op.Location location, CodeSemantics semantics) {
        addToken(type, file, location.line(), location.column(), NO_VALUE, NO_VALUE, NO_VALUE, semantics);
    }

    public void addToken(TokenType type, Op.Location location, CodeSemantics semantics) {
        addToken(type, file, location, semantics);
    }

    public void addToken(TokenType type, CodeSemantics semantics) {
        addToken(type, file, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, semantics);
    }

    public static <T> T requireSingle(SequencedCollection<T> list) {
        if (list.size() != 1) {
            throw new IllegalStateException("Expected exactly one element, but found: " + list.size());
        }
        return list.getFirst();
    }

    public void handle(
            JavaCompiler.CompilationTask task,
            CompilationUnitTree cu,
            MethodTree method
    ) {
        var op = Op.ofMethodTree(task, cu, method).orElseThrow();
        var lowered = op
                .transform(new TryWithoutResourcesTransformer())
//                .transform(CodeTransformer.LOWERING_TRANSFORMER)
                ;
//        var ssa = SSA.transform(lowered);
        var ssa = lowered;

        handle(ssa);
        IO.println(ssa.toText());
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
                addToken(JavaTokenType.J_METHOD_END, func.location(), CodeSemantics.createControl());
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
            default -> IO.println("Unsupported op: " + op.getClass() + " with content: " + op.toText());
        }
    }
}
