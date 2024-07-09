package de.jplag.java;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.util.FileUtils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

public class JavacAdapter {

    private static final Logger logger = LoggerFactory.getLogger(JavacAdapter.class);

    private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    private static final boolean USE_PREVIEW_FEATURES = usePreviewFeatures();

    private static boolean usePreviewFeatures() {
        // We only enable preview features, if Java versions are matching. See https://github.com/jplag/JPlag/discussions/1851
        boolean previewFeatures = JavaLanguage.JAVA_VERSION == Integer.parseInt(System.getProperty("java.specification.version"));
        if (!previewFeatures) {
            logger.info("Preview features are disabled for Java. Please switch to Java {} to enable preview features.", JavaLanguage.JAVA_VERSION);
        }
        return previewFeatures;
    }

    public void parseFiles(Set<File> files, final Parser parser) throws ParsingException {
        var listener = new DiagnosticCollector<>();

        final Charset guessedCharset = FileUtils.detectCharsetFromMultiple(files);
        try (final StandardJavaFileManager fileManager = javac.getStandardFileManager(listener, null, guessedCharset)) {
            var javaFiles = fileManager.getJavaFileObjectsFromFiles(files);

            // We need to disable annotation processing, see
            // https://stackoverflow.com/questions/72737445/system-java-compiler-behaves-different-depending-on-dependencies-defined-in-mave
            List<String> options = new ArrayList<>();
            options.add("-proc:none");

            if (USE_PREVIEW_FEATURES) {
                options.add("--enable-preview");
                options.add("--release=" + JavaLanguage.JAVA_VERSION);
            }

            final CompilationTask task = javac.getTask(null, fileManager, listener, options, null, javaFiles);
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
        List<ParsingException> parsingExceptions = new ArrayList<>(processErrors(listener));
        if (!parsingExceptions.isEmpty()) {
            throw Objects.requireNonNull(ParsingException.wrappingExceptions(parsingExceptions));
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
