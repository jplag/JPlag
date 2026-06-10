package de.jplag.java.babylon;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.SourcePositions;
import de.jplag.java.JavacAdapter;
import de.jplag.java.Parser;

import javax.tools.JavaCompiler;
import java.io.File;

public class JavacAdapterBabylon extends JavacAdapter {
    @Override
    protected boolean shouldAnalyze(JavaCompiler.CompilationTask task) {
        return true; // populates AST with references to Symbols - we need those!
    }

    @Override
    protected TreeVisitor<?, ?> createTreeScanner(File file, Parser parser, LineMap map, SourcePositions positions, CompilationUnitTree ast, JavaCompiler.CompilationTask task) {
        return super.createTreeScanner(file, parser, map, positions, ast, task);
    }
}
