package de.jplag.java.babylon;

import static de.jplag.testutils.LanguageModuleTest.DEFAULT_TEST_CODE_PATH_BASE;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.tools.JavaCompiler;

import de.jplag.ParsingException;
import de.jplag.java.JavacAdapter;
import de.jplag.java.Parser;
import de.jplag.java.babylon.extractor.CodeModelExtractorImpl;
import de.jplag.java.babylon.pipeline.TransformationPipeline;
import de.jplag.java.babylon.transformer.SimpleTransformation;
import de.jplag.java.babylon.transformer.TransformationStep;
import de.jplag.java.babylon.transformer.impl.util.DelegatePipelineStep;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Base class for writing unit tests that use resources.<br>
 * Handles loading and transforming simple source files.
 */
public abstract class TestWithResource {
    protected Path getTestFileLocation() {
        return DEFAULT_TEST_CODE_PATH_BASE.resolve("java/babylon");
    }

    protected final ParseResult parseFile(String fileName) throws ParsingException {
        return parseFile(getTestFileLocation().resolve(fileName));
    }

    protected final ParseResult parseFile(Path file) throws ParsingException {
        if (!Files.isRegularFile(file) || !file.getFileName().toString().endsWith(".java")) {
            throw new IllegalArgumentException("Not a valid Java source file: " + file);
        }
        JavacAdapterTest adapter = new JavacAdapterTest();
        adapter.parseFiles(Set.of(file.toFile()), null);
        return adapter.getResult();
    }

    protected final TransformationPipeline pipeline(TransformationStep<?>... steps) {
        return new TransformationPipeline(List.of(steps));
    }

    protected final DelegatePipelineStep step(SimpleTransformation transformation) {
        return new DelegatePipelineStep(transformation);
    }

    private static final class JavacAdapterTest extends JavacAdapter {
        private ParseResult result;

        @Override
        protected boolean shouldAnalyze(JavaCompiler.CompilationTask task) {
            return true;
        }

        @Override
        protected void handle(Iterable<? extends CompilationUnitTree> trees, Parser parser, SourcePositions positions,
                JavaCompiler.CompilationTask task) {
            if (result != null) {
                throw new IllegalStateException();
            }
            result = new ParseResult(trees, task);
        }

        public ParseResult getResult() {
            if (result == null) {
                throw new IllegalStateException();
            }
            return result;
        }
    }

    /**
     * Result of parsing a set of source files.
     * @param trees the trees from the source files
     * @param task the compilation task that was used
     */
    public record ParseResult(Iterable<? extends CompilationUnitTree> trees, JavaCompiler.CompilationTask task) {
        /**
         * Asserts that the input is a simple source file and parses the main method contained within into a code model.
         * @return the parsed code model
         * @throws IllegalArgumentException if the assertions fail
         */
        public CoreOp.FuncOp extractCodeModel() {
            return extractCodeModel(new TransformationPipeline(List.of()));
        }

        /**
         * Asserts that the input is a simple source file and parses the main method contained within into a code model.
         * @param pipeline the transformation pipeline to apply
         * @return the parsed code model
         * @throws IllegalArgumentException if the assertions fail
         */
        public CoreOp.FuncOp extractCodeModel(TransformationPipeline pipeline) {
            TransformationPipeline.Context context = pipeline.prepass(trees,
                    new TransformationStep.PrepassConstructionContext(new CodeModelExtractorImpl(task), task));

            CompilationUnitTree ast = requireSingle(trees);
            ClassTree clazz = (ClassTree) requireSingle(ast.getTypeDecls());

            Iterator<? extends Tree> members = clazz.getMembers().iterator();
            if (!((MethodTree) members.next()).getName().toString().equals("<init>"))
                throw new IllegalArgumentException();
            MethodTree method = (MethodTree) members.next();
            if (!method.getName().toString().equals("main"))
                throw new IllegalArgumentException();

            return pipeline.transform(method, context).orElseThrow();
        }

        private <T> T requireSingle(Iterable<? extends T> iterable) {
            Iterator<? extends T> iterator = iterable.iterator();
            if (!iterator.hasNext())
                throw new IllegalArgumentException();
            T result = iterator.next();
            if (iterator.hasNext())
                throw new IllegalArgumentException();
            return result;
        }
    }
}
