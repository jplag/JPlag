package de.jplag.java.babylon;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.SourcePositions;
import de.jplag.java.JavacAdapter;
import de.jplag.java.Parser;
import de.jplag.java.babylon.transformer.TransformationPipeline;
import de.jplag.semantics.VariableRegistry;

import javax.tools.JavaCompiler;
import java.io.File;
import java.util.List;

class JavacAdapterBabylon extends JavacAdapter {
    private final TransformationPipeline pipeline;
    private final VariableRegistry variableRegistry;

    public JavacAdapterBabylon(TransformationPipeline pipeline, VariableRegistry variableRegistry) {
        this.pipeline = pipeline;
        this.variableRegistry = variableRegistry;
    }

    @Override
    protected boolean shouldAnalyze(JavaCompiler.CompilationTask task) {
        return true; // populates AST with references to Symbols - we need those!
    }

    @Override
    protected TreeVisitor<?, ?> createTreeScanner(File file, Parser parser, LineMap map, SourcePositions positions, CompilationUnitTree ast, JavaCompiler.CompilationTask task) {
        return MulticastTreeVisitor.create(List.of(
                pipeline.prepass(),
                new TokenGeneratingTreeScannerBabylon(file, (ParserBabylon) parser, map, positions, ast, task, variableRegistry)
        ));
    }
}
