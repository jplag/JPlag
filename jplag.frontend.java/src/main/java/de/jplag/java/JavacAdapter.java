package de.jplag.java;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

public class JavacAdapter {

    private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

    public int parseFiles(File directory, File[] pathedFiles, final Parser parser) {
        final StandardJavaFileManager fileManager = javac.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        DiagnosticCollector<? super JavaFileObject> diagListen = new DiagnosticCollector<>();
        final JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, diagListen, null, null,
                fileManager.getJavaFileObjects(pathedFiles));
        Iterable<? extends CompilationUnitTree> asts = Collections.emptyList();
        try {
            asts = ((JavacTask) task).parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Trees trees = Trees.instance(task);
        final SourcePositions positions = trees.getSourcePositions();
        for (final CompilationUnitTree ast : asts) {
            final String filename;
            if (directory == null)
                filename = ast.getSourceFile().getName();
            else {
                filename = Paths.get(directory.toURI()).relativize(Paths.get(ast.getSourceFile().toUri())).toString();
            }
            final LineMap map = ast.getLineMap();
            ast.accept(new TokenGeneratingTreeScanner(filename, parser, map, positions, ast), null);
            parser.add(JavaTokenConstants.FILE_END, filename, 1, -1, -1);
        }
        int errors = 0;
        for (Diagnostic<?> diagItem : diagListen.getDiagnostics()) {
            if (diagItem.getKind() == javax.tools.Diagnostic.Kind.ERROR) {
                parser.getErrorConsumer().addError(diagItem.toString());
                errors++;
            }
        }
        return errors;
    }

}
