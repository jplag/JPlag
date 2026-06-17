package de.jplag.java.babylon;

import java.io.File;

import javax.tools.JavaCompiler;

import de.jplag.java.JavacAdapter;
import de.jplag.java.Parser;
import de.jplag.java.babylon.tokenizer.BabylonTokenizer;
import de.jplag.java.babylon.transformer.Prepass;
import de.jplag.java.babylon.transformer.TransformationPipeline;
import de.jplag.semantics.VariableRegistry;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.SourcePositions;

class JavacAdapterBabylon extends JavacAdapter {
    private final TransformationPipeline pipeline;
    private final VariableRegistry variableRegistry;
    private final BabylonTokenizer.Provider tokenizer;

    private static final ScopedValue<Prepass.Multicast.Context> PREPASS_CONTEXT = ScopedValue.newInstance();

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
        CodeModelCreator cmc = new CodeModelCreator(task, null);
        Prepass<Prepass.Multicast.Context> prepass = pipeline.prepass(new TransformationPipeline.PrepassConstructionContext(cmc));
        for (CompilationUnitTree ast : trees) {
            cmc.setAst(ast);
            ast.accept(prepass, null);
        }
        prepass.finalizeContext();
        ScopedValue.where(PREPASS_CONTEXT, prepass.finalizeContext()).run(() -> super.handle(trees, parser, positions, task));
    }

    @Override
    protected TreeVisitor<?, ?> createTreeScanner(File file, Parser parser, LineMap map, SourcePositions positions, CompilationUnitTree ast,
            JavaCompiler.CompilationTask task) {
        return new TokenGeneratingTreeScannerBabylon(file, (ParserBabylon) parser, map, positions, ast, new CodeModelCreator(task, ast),
                variableRegistry, tokenizer, PREPASS_CONTEXT.get());
    }
}
