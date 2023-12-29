package de.jplag.java_cpg;

import de.fraunhofer.aisec.cpg.TranslationResult;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.fraunhofer.aisec.cpg_vis_neo4j.Application;
import de.jplag.ParsingException;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.TransformationRepository;
import de.jplag.java_cpg.transformation.matching.CpgIsomorphismDetector;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.net.ConnectException;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateTransformTest extends AbstractJavaCpgLanguageTest {

    private CpgIsomorphismDetector detector;

    public static Stream<Arguments> provideTuples() {
        return Stream.of(
            Arguments.of("UnusedVariableDeclaration.java", TransformationRepository.removeUnusedVariableDeclarations(), VariableDeclaration.class),
            Arguments.of("IfElseWithNegatedCondition.java", TransformationRepository.ifWithNegatedConditionResolution(), IfStatement.class)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTuples")
    public <T extends Node> void createTransformTest(String fileName, GraphTransformation<T> transformation) throws ParsingException, InterruptedException, ConnectException {

        Set<File> files = Set.of(new File(baseDirectory, fileName));
        TranslationResult graph = new CpgAdapter().translate(files);

        detector = new CpgIsomorphismDetector();
        detector.loadGraph(graph);

        instantiate(transformation);

        boolean pushToNeo4j = false;
        if (pushToNeo4j) {
            new Application().pushToNeo4j(graph);
        }
    }

    private <T extends Node> void instantiate(GraphTransformation<T> transformation) {
        GraphPattern<T> sourcePattern = transformation.getSourcePattern();
        Iterator<GraphPattern.Match<T>> maybeMatch = detector.getMatches(sourcePattern);

        assertTrue(maybeMatch.hasNext());
        GraphPattern.Match<T> match = maybeMatch.next();

        transformation.apply(match);
    }

}
