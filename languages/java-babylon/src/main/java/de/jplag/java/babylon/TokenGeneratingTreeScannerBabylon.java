package de.jplag.java.babylon;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.SourcePositions;
import de.jplag.java.Parser;
import de.jplag.java.TokenGeneratingTreeScanner;

import javax.tools.JavaCompiler;
import java.io.File;

public class TokenGeneratingTreeScannerBabylon extends TokenGeneratingTreeScanner {
    private final JavaCompiler.CompilationTask task;
    private final Experiment experiment;

    public TokenGeneratingTreeScannerBabylon(File file, Parser parser, LineMap map, SourcePositions positions, CompilationUnitTree ast, JavaCompiler.CompilationTask task) {
        super(file, parser, map, positions, ast);
        this.task = task;
        this.experiment = new Experiment(parser, file);
    }

    @Override
    public Void visitMethod(MethodTree node, Void unused) {
        variableRegistry.enterLocalScope();

        experiment.handle(task, ast, node);

        variableRegistry.addAllNonLocalVariablesAsReads();
        variableRegistry.exitLocalScope();
        return null;
    }
}
