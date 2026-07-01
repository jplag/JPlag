package de.jplag.java.babylon;

import java.io.File;
import java.util.Optional;

import javax.tools.JavaCompiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Token;
import de.jplag.java.Parser;
import de.jplag.java.TokenGeneratingTreeScanner;
import de.jplag.java.babylon.extractor.ExtractionFailedException;
import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.semantics.VariableRegistry;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * A {@link TreeScanner} implementation that passes a {@link Token} sequence on to the {@link Parser}.<br>
 * Performs transformations according to the attached {@link TransformationPipeline}.
 */
public class TokenGeneratingTreeScannerBabylon extends TokenGeneratingTreeScanner {
    private static final Logger logger = LoggerFactory.getLogger(TokenGeneratingTreeScannerBabylon.class);

    private final TransformationPipeline pipeline;
    private final TransformationPipeline.Context context;
    private final BabylonTokenizer tokenizer;

    /**
     * Constructs a new instance.
     * @param file the file being scanned
     * @param parser the parser to output tokens to
     * @param map the line map for the current file
     * @param positions the {@link SourcePositions} object for the current context
     * @param ast the AST to scan
     * @param codebaseAsts the ASTs comprising the codebase which is being scanned, including the ast to scan
     * @param variableRegistry the registry of current variables for token sequence normalization
     * @param tokenizer the tokenizer for converting code models to tokens
     * @param context context obtained from the prepass for this pipeline
     * @param task the compilation task
     */
    public TokenGeneratingTreeScannerBabylon(File file, ParserBabylon parser, LineMap map, SourcePositions positions, CompilationUnitTree ast,
            Iterable<? extends CompilationUnitTree> codebaseAsts, VariableRegistry variableRegistry, BabylonTokenizer.Provider tokenizer,
            TransformationPipeline.Context context, JavaCompiler.CompilationTask task) {
        super(file, parser, map, positions, ast, variableRegistry);
        this.pipeline = parser.getPipeline();
        this.context = context;
        this.tokenizer = tokenizer.getTokenizer(new BabylonTokenizer.TokenizerConstructionContext(parser, file, codebaseAsts, task));
    }

    @Override
    public Void visitMethod(MethodTree node, Void unused) {
        Optional<CoreOp.FuncOp> transform;
        try {
            transform = pipeline.transform(node, context);
        } catch (ExtractionFailedException e) {
            logger.atWarn().setCause(e).log("Failed to extract code model for method {} in file {}. Falling back to normal Java tokenization",
                    node.getName(), file);
            return super.visitMethod(node, unused);
        }

        variableRegistry.enterLocalScope();
        transform.ifPresent(tokenizer::handle);
        variableRegistry.addAllNonLocalVariablesAsReads();
        variableRegistry.exitLocalScope();

        // unfortunately, Babylon does not seem to handle inner classes (for now), so this fallback is needed.
        return new InnerClassScanner().visitMethod(node, unused);
    }

    private class InnerClassScanner extends TreeScanner<Void, Void> {
        @Override
        public Void visitClass(ClassTree node, Void unused) {
            return TokenGeneratingTreeScannerBabylon.this.visitClass(node, unused);
        }
    }
}
