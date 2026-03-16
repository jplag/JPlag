package de.jplag.java_cpg.ai;

import static de.jplag.java_cpg.ai.DeadCodeDetectionTest.translate;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.fraunhofer.aisec.cpg.TranslationResult;
import de.fraunhofer.aisec.cpg.graph.Component;
import de.jplag.ParsingException;
import de.jplag.java_cpg.ai.variables.VariableStore;
import de.jplag.java_cpg.ai.variables.values.Value;

/**
 * Tests from the Progpedia data set.
 * <p>
 * José Carlos Paiva, José Paulo Leal, and Álvaro Figueira. PROGpedia. Dec. 2022. 10.5281/zenodo.7449056
 * <a href="https://zenodo.org/records/7449056">zenodo.org/records/7449056</a> (visited on 11/04/2025).
 * @author ujiqk
 * @version 1.0
 */
public class ProgpediaTests {

    @NotNull
    private static AbstractInterpretation interpretFromResource(String resourceDir) throws ParsingException, InterruptedException {
        ClassLoader classLoader = DeadCodeDetectionTest.class.getClassLoader();
        File submissionsRoot = new File(Objects.requireNonNull(classLoader.getResource(resourceDir)).getFile());
        Set<File> submissionDirectories = Set.of(submissionsRoot);
        TranslationResult result = translate(submissionDirectories);
        AbstractInterpretation interpretation = new AbstractInterpretation(new VisitedLinesRecorder(), true);

        assert result.getComponents().size() == 1;
        Component comp = result.getComponents().getFirst();
        assert comp.getTranslationUnits().size() == 1;
        interpretation.runMain(comp.getTranslationUnits().getFirst());
        return interpretation;
    }

    /**
     * The Progpedia data set contains 15 problems, each with two categories of submissions (ACCEPTED and WRONG_ANSWER), and
     * each category has multiple submissions. This method generates the resource directories for all these submissions.
     */
    @NotNull
    public static Stream<String> progpediaResourceDirs() {
        return Stream
                .of("00000006", "00000016", "00000018", "00000019", "00000021", "00000022", "00000023", "00000034", "00000035", "00000039",
                        "00000042", "00000043", "00000045", "00000048", "00000053", "00000056")
                .flatMap(problemId -> Stream.of("ACCEPTED", "WRONG_ANSWER").flatMap(category -> getResourceDirsForProblem(problemId, category)));
    }

    /**
     * Generates the resource paths for all Java files in the Progpedia data set. This is used for the parameterized test.
     */
    @NotNull
    public static Stream<String> progpediaFiles() {
        return progpediaResourceDirs().flatMap(dir -> {
            ClassLoader classLoader = DeadCodeDetectionTest.class.getClassLoader();
            java.net.URL url = classLoader.getResource(dir);
            if (url == null)
                return Stream.empty();
            File directory = new File(url.getFile());
            File[] javaFiles = directory.listFiles((d, name) -> name.endsWith(".java"));
            if (javaFiles == null)
                return Stream.empty();
            return Arrays.stream(javaFiles).map(f -> dir + f.getName());
        }).map(s -> s.substring(5));    // remove first "java/"
    }

    private static Stream<String> getResourceDirsForProblem(String problemId, String category) {
        ClassLoader classLoader = DeadCodeDetectionTest.class.getClassLoader();
        java.net.URL url = classLoader.getResource("java/progpedia/" + problemId + "/" + category);
        if (url == null)
            return Stream.empty();
        File base = new File(Objects.requireNonNull(url).getFile());
        File[] dirs = base.listFiles(File::isDirectory);
        if (dirs == null)
            return Stream.empty();
        return Arrays.stream(dirs).map(f -> "java/progpedia/" + problemId + "/" + category + "/" + f.getName() + "/");
    }

    @ParameterizedTest
    @Disabled("takes too long")
    @MethodSource("progpediaResourceDirs")
    void testProgpedia(String resourceDir) throws ParsingException, InterruptedException {
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        Value.setUsedFloatAiType(FloatAiType.DEFAULT);
        Value.setUsedStringAiType(StringAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource(resourceDir);
        VariableStore variableStore = interpretation.getVariables();
        assertNotNull(variableStore);
    }

    @Test
    @Disabled("only for debugging a single file, not a real test")
    void testSingle1() throws ParsingException, InterruptedException {
        String fileName = "java/progpedia/00000006/ACCEPTED/00130_00001";
        Value.setUsedIntAiType(IntAiType.DEFAULT);
        Value.setUsedFloatAiType(FloatAiType.DEFAULT);
        Value.setUsedStringAiType(StringAiType.DEFAULT);
        AbstractInterpretation interpretation = interpretFromResource(fileName);
        VariableStore variableStore = interpretation.getVariables();
        assertNotNull(variableStore);
    }

}
