package de.jplag.java_cpg;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.TranslationResult;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.jplag.ParsingException;
import de.jplag.java_cpg.transformation.matching.CpgIsomorphismDetector;
import de.jplag.java_cpg.transformation.matching.PatternRepository;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.SimpleGraphPattern;

public class MatchingTest extends AbstractJavaCpgLanguageTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(MatchingTest.class);

    public static Stream<Arguments> providePairs() {
        return Stream.of(Arguments.of("IfElseWithNegatedCondition.java", IfStatement.class, PatternRepository.ifElseWithNegatedCondition().build()),
                Arguments.of("GetterSetter.java", MethodDeclaration.class, PatternRepository.setterMethod().build()));
    }

    @ParameterizedTest
    @MethodSource("providePairs")
    <T extends Node> void testMatch(String filename, Class<T> rootType, SimpleGraphPattern<T> pattern) throws InterruptedException {
        File file = new File(baseDirectory, filename);
        try {
            CpgAdapter cpgAdapter = new CpgAdapter();
            cpgAdapter.clearTransformations();
            TranslationResult graph = cpgAdapter.translate(Set.of(file));
            CpgIsomorphismDetector detector = new CpgIsomorphismDetector();
            detector.loadGraph(graph);
            List<T> rootCandidates = detector.getNodesOfType(rootType);

            assertTrue(rootCandidates.stream().anyMatch(candidate -> {
                List<Match> matches = pattern.recursiveMatch(candidate);
                if (matches.isEmpty()) {
                    return false;
                }
                LOGGER.info("Mapping contained %d nodes.".formatted(matches.getFirst().getSize()));
                return true;
            }));
        } catch (ParsingException e) {
            throw new RuntimeException(e);
        }
    }
}
