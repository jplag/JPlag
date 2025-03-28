package de.jplag.java;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.util.FileUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

public class JavacAdapter {

    private static final String NO_ANNOTATION_PROCESSING = "-proc:none";
    private static final String PREVIEW_FLAG = "--enable-preview";
    private static final String RELEASE_VERSION_OPTION = "--release=";

    private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    public void parseFiles(Set<File> files, final Parser parser) throws ParsingException {
        var listener = new DiagnosticCollector<>();

        List<ParsingException> parsingExceptions = new ArrayList<>();
        final Charset guessedCharset = FileUtils.detectCharsetFromMultiple(files);
        try (final StandardJavaFileManager fileManager = compiler.getStandardFileManager(listener, null, guessedCharset)) {
            var javaFiles = fileManager.getJavaFileObjectsFromFiles(files);

            // We need to disable annotation processing, see https://stackoverflow.com/q/72737445
            String releaseVersion = RELEASE_VERSION_OPTION + Runtime.version().feature(); // required for preview flag
            List<String> options = List.of(NO_ANNOTATION_PROCESSING, PREVIEW_FLAG, releaseVersion);
            final CompilationTask task = compiler.getTask(null, fileManager, listener, options, null, javaFiles);
            final Trees trees = Trees.instance(task);
            final SourcePositions positions = new FixedSourcePositions(trees.getSourcePositions());
            for (final CompilationUnitTree ast : executeCompilationTask(task, parser.logger)) {
                File file = new File(ast.getSourceFile().toUri());
                final LineMap map = ast.getLineMap();
                var scanner = new TokenGeneratingTreeScanner(file, parser, map, positions, ast);
                ast.accept(scanner, null);
                parser.add(Token.semanticFileEnd(file));
            }
        } catch (Exception exception) {
            throw new ParsingException(null, exception.getMessage(), exception);
        }
        parsingExceptions.addAll(processErrors(listener));
        if (!parsingExceptions.isEmpty()) {
            throw ParsingException.wrappingExceptions(parsingExceptions);
        }
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

    private List<ParsingException> processErrors(DiagnosticCollector<Object> listener) {
        return listener.getDiagnostics().stream().filter(it -> it.getKind() == javax.tools.Diagnostic.Kind.ERROR).map(diagnosticItem -> {
            File file = null;
            if (diagnosticItem.getSource() instanceof JavaFileObject fileObject) {
                file = new File(fileObject.toUri());
            }
            return new ParsingException(file, diagnosticItem.toString());
        }).toList();
    }

}
