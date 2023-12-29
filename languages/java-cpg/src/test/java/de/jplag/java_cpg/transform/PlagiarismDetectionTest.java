package de.jplag.java_cpg.transform;


import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.exceptions.ExitException;
import de.jplag.java_cpg.Language;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.TransformationRepository;
import de.jplag.options.JPlagOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

/**
 * An integration test that checks whether pairs of submissions are accepted as equal after being subjected to different transformations.
 */
public class PlagiarismDetectionTest {

    protected static final Path BASE_PATH = Path.of("src", "test", "resources", "java");
    protected static File baseDirectory;
    private static Language language;

    @BeforeAll
    public static void setUpOnce() {
        language = new de.jplag.java_cpg.Language();
        baseDirectory = BASE_PATH.toFile();
    }

    @ParameterizedTest
    @MethodSource("getArguments")
    public void testPlagiarismPair(String submissionsPath, GraphTransformation<?>[] transformation) {
        Set<File> submissionSet = Set.of(new File(baseDirectory, submissionsPath));
        language.addTransformations(transformation);
        JPlagOptions options = new JPlagOptions(language, submissionSet, Set.of());

        JPlagResult result;
        try {
            result = new JPlag(options).run();
        } catch (ExitException e) {
            throw new RuntimeException(e);
        }

        JPlagComparison jPlagComparison = result.getAllComparisons().get(0);
        Assertions.assertEquals(jPlagComparison.similarity(), 1.0);
    }

    private static Stream<Arguments> getArguments() {
        return Stream.of(
            Arguments.of("unusedVariables", new GraphTransformation[] { TransformationRepository.removeUnusedVariableDeclarationStatements(), TransformationRepository.removeUnusedVariableDeclarations(), TransformationRepository.removeEmptyDeclarationStatement() })
        );
    }
}
