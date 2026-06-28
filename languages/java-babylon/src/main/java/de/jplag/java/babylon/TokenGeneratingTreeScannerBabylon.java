package de.jplag.java.babylon;

import java.io.File;

import de.jplag.Token;
import de.jplag.java.Parser;
import de.jplag.java.TokenGeneratingTreeScanner;
import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.semantics.VariableRegistry;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;

/**
 * A {@link TreeScanner} implementation that passes a {@link Token} sequence on to the {@link Parser}.<br>
 * Performs transformations according to the attached {@link TransformationPipeline}.
 */
public class TokenGeneratingTreeScannerBabylon extends TokenGeneratingTreeScanner {
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
     * @param variableRegistry the registry of current variables for token sequence normalization
     * @param tokenizer the tokenizer for converting code models to tokens
     * @param context context obtained from the prepass for this pipeline
     */
    public TokenGeneratingTreeScannerBabylon(File file, ParserBabylon parser, LineMap map, SourcePositions positions, CompilationUnitTree ast,
            VariableRegistry variableRegistry, BabylonTokenizer.Provider tokenizer, TransformationPipeline.Context context) {
        super(file, parser, map, positions, ast, variableRegistry);
        this.pipeline = parser.getPipeline();
        this.context = context;
        this.tokenizer = tokenizer.getTokenizer(new BabylonTokenizer.TokenizerConstructionContext(parser, file));
    }

    @Override
    public Void visitMethod(MethodTree node, Void unused) {
        variableRegistry.enterLocalScope();

        pipeline.transform(node, ast, context).ifPresent(tokenizer::handle);

        variableRegistry.addAllNonLocalVariablesAsReads();
        variableRegistry.exitLocalScope();
        return null;
    }
}
