package de.jplag.java;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;

import org.slf4j.Logger;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

import de.jplag.TokenConstants;

public class JavacAdapter {

    private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

    public int parseFiles(File directory, Iterable<File> pathedFiles, final Parser parser) {
        var listener = new DiagnosticCollector<>();

        try (final StandardJavaFileManager fileManager = javac.getStandardFileManager(listener, null, StandardCharsets.UTF_8)) {
            var javaFiles = fileManager.getJavaFileObjectsFromFiles(pathedFiles);

            // We need to disable annotation processing
            // See
            // https://stackoverflow.com/questions/72737445/system-java-compiler-behaves-different-depending-on-dependencies-defined-in-mave
            final CompilationTask task = javac.getTask(null, fileManager, listener, List.of("-proc:none"), null, javaFiles);
            final Trees trees = Trees.instance(task);
            final SourcePositions positions = trees.getSourcePositions();
            for (final CompilationUnitTree ast : executeCompilationTask(task)) {
                final String filename = fileNameOf(directory, ast);
                final LineMap map = ast.getLineMap();
                ast.accept(new TokenGeneratingTreeScanner(filename, parser, map, positions, ast), null);
                parser.add(TokenConstants.FILE_END, filename, 1, -1, -1);
            }
        } catch (IOException e) {
            parser.logger.error(e.getMessage(), e);
        }
        return processErrors(parser.logger, listener);
    }

    private Iterable<? extends CompilationUnitTree> executeCompilationTask(final CompilationTask task) {
        Iterable<? extends CompilationUnitTree> abstractSyntaxTrees = Collections.emptyList();
        try {
            abstractSyntaxTrees = ((JavacTask) task).parse();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return abstractSyntaxTrees;
    }

    private String fileNameOf(File directory, final CompilationUnitTree ast) {
        if (directory == null)
            return ast.getSourceFile().getName();
        else {
            return Paths.get(directory.toURI()).relativize(Paths.get(ast.getSourceFile().toUri())).toString();
        }
    }

    private int processErrors(Logger logger, DiagnosticCollector<Object> listener) {
        int errors = 0;
        for (Diagnostic<?> diagnosticItem : listener.getDiagnostics()) {
            if (diagnosticItem.getKind() == javax.tools.Diagnostic.Kind.ERROR) {
                logger.error("{}", diagnosticItem);
                errors++;
            }
        }
        return errors;
    }

}
