package de.jplag.java;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

import de.jplag.ParsingException;
import de.jplag.Token;

public class JavacAdapter {

    private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

    public void parseFiles(Set<File> files, final Parser parser) throws ParsingException {
        var listener = new DiagnosticCollector<>();

        try (final StandardJavaFileManager fileManager = javac.getStandardFileManager(listener, null, StandardCharsets.UTF_8)) {
            var javaFiles = fileManager.getJavaFileObjectsFromFiles(files);

            // We need to disable annotation processing, see
            // https://stackoverflow.com/questions/72737445/system-java-compiler-behaves-different-depending-on-dependencies-defined-in-mave
            final CompilationTask task = javac.getTask(null, fileManager, listener, List.of("-proc:none"), null, javaFiles);
            final Trees trees = Trees.instance(task);
            final SourcePositions positions = trees.getSourcePositions();
            for (final CompilationUnitTree ast : executeCompilationTask(task, parser.logger)) {
                File file = new File(ast.getSourceFile().toUri());
                final LineMap map = ast.getLineMap();
                var scanner = new TokenGeneratingTreeScanner(file, parser, map, positions, ast);
                ast.accept(scanner, null);
                if (scanner.getParsingException() != null) {
                    throw scanner.getParsingException();
                }
                parser.add(Token.fileEnd(file));
            }
        } catch (IOException exception) {
            parser.logger.error(exception.getMessage(), exception);
            throw new ParsingException(null, exception.getMessage(), exception);
        }
        processErrors(parser.logger, listener);
    }

    private Iterable<? extends CompilationUnitTree> executeCompilationTask(final CompilationTask task, Logger logger) {
        Iterable<? extends CompilationUnitTree> abstractSyntaxTrees = Collections.emptyList();
        try {
            abstractSyntaxTrees = ((JavacTask) task).parse();
        } catch (IOException exception) {
            logger.error(exception.getMessage(), exception);
        }
        return abstractSyntaxTrees;
    }

    private void processErrors(Logger logger, DiagnosticCollector<Object> listener) throws ParsingException {
        for (Diagnostic<?> diagnosticItem : listener.getDiagnostics()) {
            if (diagnosticItem.getKind() == javax.tools.Diagnostic.Kind.ERROR) {
                File file = null;
                if (diagnosticItem.getSource() instanceof JavaFileObject) {
                    JavaFileObject fileObject = (JavaFileObject) diagnosticItem.getSource();
                    file = new File(fileObject.toUri());
                }
                logger.error("{}", diagnosticItem);
                throw new ParsingException(file, diagnosticItem.getMessage(Locale.getDefault()));
            }
        }
    }

}
