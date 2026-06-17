package de.jplag.java.babylon;

import java.io.File;

import javax.tools.JavaCompiler;

import com.sun.source.util.TreeScanner;
import de.jplag.Token;
import de.jplag.java.Parser;
import de.jplag.java.TokenGeneratingTreeScanner;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.java.babylon.transformer.Prepass;
import de.jplag.java.babylon.transformer.TransformationPipeline;
import de.jplag.semantics.VariableRegistry;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.SourcePositions;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * A {@link TreeScanner} implementation that passes a {@link Token} sequence on to the {@link Parser}.<br>
 * Performs transformations according to the attached {@link TransformationPipeline}.
 */
public class TokenGeneratingTreeScannerBabylon extends TokenGeneratingTreeScanner {
    private final JavaCompiler.CompilationTask task;
    private final TransformationPipeline pipeline;
    private final Prepass.Multicast.Context context;
    private final BabylonTokenizer tokenizer;

    /**
     * Constructs a new instance.
     * @param file the file being scanned
     * @param parser the parser to output tokens to
     * @param map the line map for the current file
     * @param positions the {@link SourcePositions} object for the current context
     * @param ast the AST to scan
     * @param task the current compilation task
     * @param variableRegistry the registry of current variables for token sequence normalization
     * @param tokenizer the tokenizer for converting code models to tokens
     * @param context context obtained from the prepass for this pipeline
     */
    public TokenGeneratingTreeScannerBabylon(File file, ParserBabylon parser, LineMap map, SourcePositions positions, CompilationUnitTree ast,
            JavaCompiler.CompilationTask task, VariableRegistry variableRegistry, BabylonTokenizer.Provider tokenizer,
            Prepass.Multicast.Context context) {
        super(file, parser, map, positions, ast, variableRegistry);
        this.task = task;
        this.pipeline = parser.getPipeline();
        this.context = context;
        this.tokenizer = tokenizer.getTokenizer(parser, file);
    }

    @Override
    public Void visitMethod(MethodTree node, Void unused) {
        variableRegistry.enterLocalScope();

        CoreOp.FuncOp op = Op.ofMethodTree(task, ast, node)
                .orElseThrow(() -> new IllegalStateException("Can't resolve body of method: " + node.getName()));
        CoreOp.FuncOp transformed = pipeline.transform(op, context);
        tokenizer.handle(transformed);

        variableRegistry.addAllNonLocalVariablesAsReads();
        variableRegistry.exitLocalScope();
        return null;
    }
}
