package de.jplag.java;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.jplag.inputs.SubmissionFile;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
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

/**
 * The {@code JavacAdapter} class provides an adapter to the Java compiler for JPlag. It is responsible for compiling
 * Java source files and handling any compiler-specific configuration or errors. This adapter abstracts the interaction
 * with the underlying compiler, enabling JPlag to analyze Java code submissions.
 */
public class JavacAdapter {

    private static final Logger logger = LoggerFactory.getLogger(JavacAdapter.class);

    private static final String NO_ANNOTATION_PROCESSING = "-proc:none";
    private static final String PREVIEW_FLAG = "--enable-preview";
    private static final String RELEASE_VERSION_OPTION = "--release=";

    private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    /**
     * Parses the given set of Java source files using JavaC.
     * @param files is the Java source files to parse.
     * @param parser is the parser to receive the tokens.
     * @throws ParsingException if an error occurs during parsing.
     */
    public void parseFiles(List<SubmissionFile> files, final Parser parser) throws ParsingException {
        var listener = new DiagnosticCollector<>();

        List<ParsingException> parsingExceptions = new ArrayList<>();
        final Charset charset = FileUtils.detectCharsetFromSubmissionFiles(files);
        try {
            Map<String, SubmissionFile> nameMap = new HashMap<>();
            List<JavaFileObject> javaFileObjects = new ArrayList<>();
            for (SubmissionFile file : files) {
                String source = FileUtils.readFileContent(file, charset);
                javaFileObjects.add(new StringSourceFile(file.name(), source));
                nameMap.put(file.name(), file);
            }

            // We need to disable annotation processing, see https://stackoverflow.com/q/72737445
            String releaseVersion = RELEASE_VERSION_OPTION + Runtime.version().feature(); // required for preview flag
            List<String> options = List.of(NO_ANNOTATION_PROCESSING, PREVIEW_FLAG, releaseVersion);
            final CompilationTask task = compiler.getTask(null, null, listener, options, null, javaFileObjects);
            final Trees trees = Trees.instance(task);
            final SourcePositions positions = new FixedSourcePositions(trees.getSourcePositions());
            for (final CompilationUnitTree ast : executeCompilationTask(task)) {
                SubmissionFile file = nameMap.get(ast.getSourceFile().getName());
                final LineMap map = ast.getLineMap();
                var scanner = new TokenGeneratingTreeScanner(file, parser, map, positions, ast);
                ast.accept(scanner, null);
                parser.add(Token.semanticFileEnd(file));
            }
            parsingExceptions.addAll(processErrors(listener, javaFileObject -> files.get(javaFileObjects.indexOf(javaFileObject))));
        } catch (Exception exception) {
            throw new ParsingException(null, exception.getMessage(), exception);
        }
        if (!parsingExceptions.isEmpty()) {
            throw ParsingException.wrappingExceptions(parsingExceptions);
        }
    }

    private Iterable<? extends CompilationUnitTree> executeCompilationTask(final CompilationTask task) {
        Iterable<? extends CompilationUnitTree> abstractSyntaxTrees = Collections.emptyList();
        try {
            abstractSyntaxTrees = ((JavacTask) task).parse();
        } catch (IOException exception) {
            logger.error(exception.getMessage(), exception);
        }
        return abstractSyntaxTrees;
    }

    private List<ParsingException> processErrors(DiagnosticCollector<Object> listener, Function<JavaFileObject, SubmissionFile> fileMapper) {
        return listener.getDiagnostics().stream().filter(it -> it.getKind() == javax.tools.Diagnostic.Kind.ERROR).map(diagnosticItem -> {
            SubmissionFile file = null;
            if (diagnosticItem.getSource() instanceof JavaFileObject fileObject) {
                file = fileMapper.apply(fileObject);
            }
            return new ParsingException(file, diagnosticItem.toString());
        }).toList();
    }

}
