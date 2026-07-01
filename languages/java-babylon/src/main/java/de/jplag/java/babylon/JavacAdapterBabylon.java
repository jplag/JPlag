package de.jplag.java.babylon;

import java.io.File;

import javax.tools.JavaCompiler;

import de.jplag.java.JavacAdapter;
import de.jplag.java.Parser;
import de.jplag.java.babylon.extractor.CodeModelExtractorImpl;
import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.java.babylon.transformer.TransformationStep;
import de.jplag.semantics.VariableRegistry;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.SourcePositions;

class JavacAdapterBabylon extends JavacAdapter {
    private final TransformationPipeline pipeline;
    private final VariableRegistry variableRegistry;
    private final BabylonTokenizer.Provider tokenizer;

    private static final ScopedValue<TreeScannerConstructionContext> PREPASS_CONTEXT = ScopedValue.newInstance();

    public JavacAdapterBabylon(TransformationPipeline pipeline, VariableRegistry variableRegistry, BabylonTokenizer.Provider tokenizer) {
        this.pipeline = pipeline;
        this.variableRegistry = variableRegistry;
        this.tokenizer = tokenizer;
    }

    @Override
    protected boolean shouldAnalyze(JavaCompiler.CompilationTask task) {
        return true; // populates AST with references to Symbols - we need those!
    }

    @Override
    protected void handle(Iterable<? extends CompilationUnitTree> trees, Parser parser, SourcePositions positions,
            JavaCompiler.CompilationTask task) {
        TransformationPipeline.Context prepassContext = pipeline.prepass(trees,
                new TransformationStep.PrepassConstructionContext(new CodeModelExtractorImpl(task), task));
        TreeScannerConstructionContext context = new TreeScannerConstructionContext(prepassContext, trees);
        ScopedValue.where(PREPASS_CONTEXT, context).run(() -> super.handle(trees, parser, positions, task));
    }

    @Override
    protected TreeVisitor<?, ?> createTreeScanner(File file, Parser parser, LineMap map, SourcePositions positions, CompilationUnitTree ast,
            JavaCompiler.CompilationTask task) {
        TreeScannerConstructionContext context = PREPASS_CONTEXT.get();
        return new TokenGeneratingTreeScannerBabylon(file, (ParserBabylon) parser, map, positions, ast, context.codebaseAsts(), variableRegistry,
                tokenizer, context.pipelineContext(), task);
    }

    private record TreeScannerConstructionContext(TransformationPipeline.Context pipelineContext,
            Iterable<? extends CompilationUnitTree> codebaseAsts) {
    }
}
