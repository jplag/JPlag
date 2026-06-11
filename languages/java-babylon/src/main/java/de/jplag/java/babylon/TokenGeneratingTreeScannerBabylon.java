package de.jplag.java.babylon;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.SourcePositions;
import de.jplag.java.TokenGeneratingTreeScanner;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.java.babylon.transformer.TransformationPipeline;
import de.jplag.semantics.VariableRegistry;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;

import javax.tools.JavaCompiler;
import java.io.File;

class TokenGeneratingTreeScannerBabylon extends TokenGeneratingTreeScanner {
    private final JavaCompiler.CompilationTask task;
    private final TransformationPipeline pipeline;
    private final BabylonTokenizer.AtFile tokenizer;

    public TokenGeneratingTreeScannerBabylon(
            File file,
            ParserBabylon parser,
            LineMap map,
            SourcePositions positions,
            CompilationUnitTree ast,
            JavaCompiler.CompilationTask task,
            VariableRegistry variableRegistry,
            BabylonTokenizer tokenizer
    ) {
        super(file, parser, map, positions, ast, variableRegistry);
        this.task = task;
        this.pipeline = parser.getPipeline();
        this.tokenizer = tokenizer.atFile(parser, file);
    }

    @Override
    public Void visitMethod(MethodTree node, Void unused) {
        variableRegistry.enterLocalScope();

        CoreOp.FuncOp op = Op.ofMethodTree(task, ast, node).orElseThrow(() -> new IllegalStateException("Can't resolve body of method: " + node.getName()));
        CoreOp.FuncOp transformed = pipeline.transform(op);
        tokenizer.handle(transformed);

        variableRegistry.addAllNonLocalVariablesAsReads();
        variableRegistry.exitLocalScope();
        return null;
    }
}
