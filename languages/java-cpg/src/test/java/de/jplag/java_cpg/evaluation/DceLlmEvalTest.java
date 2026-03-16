package de.jplag.java_cpg.evaluation;

import static de.jplag.java_cpg.evaluation.EvaluationEngineTest.checkNonDeadCodeNotRemoved;
import static de.jplag.java_cpg.evaluation.EvaluationEngineTest.similarity;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.java_cpg.JavaCpgLanguage;
import de.jplag.java_cpg.ai.ArrayAiType;
import de.jplag.java_cpg.ai.CharAiType;
import de.jplag.java_cpg.ai.CpgErrorException;
import de.jplag.java_cpg.ai.FloatAiType;
import de.jplag.java_cpg.ai.IntAiType;
import de.jplag.java_cpg.ai.JavaLanguageFeatureNotSupportedException;
import de.jplag.java_cpg.ai.StringAiType;
import de.jplag.java_cpg.transformation.GraphTransformation;

/**
 * Evaluation on the DCE-LLM dataset, which contains Java files with manually annotated dead code. We compare the
 * results of our dead code removal with the manual annotations and measure the similarity and runtime.
 */
public class DceLlmEvalTest {

    private static @NotNull Stream<String> dceLlmFiles() {
        // String basePath = "/home/alpaka/PycharmProjects/baplots/testFiles";
        String basePath = "/home/alpaka/IdeaProjects/DCE-LLM/testFiles";
        Path baseDir = Path.of(basePath);
        if (!Files.exists(baseDir)) {
            return Stream.empty();
        }
        try (Stream<Path> paths = Files.walk(baseDir)) {
            List<String> files = paths.filter(Files::isRegularFile).map(Path::toString).filter(path -> path.endsWith(".java")).toList();
            return files.stream();
        } catch (IOException _) {
            return Stream.empty();
        }
    }

    @NotNull
    private static List<Token> getTokensFromFile(@NotNull String fileName, boolean removeDeadCode, boolean detectDeadCode, boolean reorder,
            boolean normalize, boolean removeSimpleDeadCode) throws ParsingException {
        assert normalize || !reorder;
        GraphTransformation[] transformations = JavaCpgLanguage.deadCodeRemovalTransformations();
        JavaCpgLanguage language = new JavaCpgLanguage(removeDeadCode, detectDeadCode, reorder, removeSimpleDeadCode, transformations,
                // IntAiType.DEFAULT, FloatAiType.DEFAULT, StringAiType.DEFAULT, CharAiType.DEFAULT, ArrayAiType.DEFAULT);
                // IntAiType.INTERVALS, FloatAiType.DEFAULT, StringAiType.CHAR_INCLUSION, CharAiType.DEFAULT, ArrayAiType.LENGTH);
                IntAiType.SET, FloatAiType.SET, StringAiType.REGEX, CharAiType.SET, ArrayAiType.DEFAULT);
        File file = new File(fileName);
        Set<File> files = Set.of(file);
        List<Token> result = language.parse(files, normalize);
        if (result.isEmpty()) {
            throw new CpgErrorException("CPG could not parse file " + fileName);
        }
        result.removeLast(); // remove EOF token
        return result;
    }

    @NotNull
    private static List<Token> getTokensFromFileWithoutDeadCode(@NotNull String fileName, boolean reorder, boolean removeSimpleDeadCode)
            throws ParsingException {
        try {
            File originalFile = new File(fileName);
            File tempFile = File.createTempFile("jplag_temp_", ".java");
            tempFile.deleteOnExit();
            if (originalFile.isDirectory()) {
                // If it's a directory, find and process Java files inside
                File[] javaFiles = originalFile.listFiles((_, name) -> name.endsWith(".java"));
                if (javaFiles == null || javaFiles.length == 0) {
                    throw new ParsingException(originalFile, "No Java files found in directory");
                }
                originalFile = javaFiles[0]; // Use the first Java file
            }
            BufferedReader reader = new BufferedReader(new FileReader(originalFile));
            java.io.PrintWriter writer = new java.io.PrintWriter(tempFile);
            String line;
            boolean inDeadCode = false;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.contains("//DeadCodeStart")) {
                    inDeadCode = true;
                } else if (trimmed.contains("//DeadCodeEnd")) {
                    inDeadCode = false;
                } else if (!inDeadCode) {
                    writer.println(line);
                }
            }
            reader.close();
            writer.close();
            JavaCpgLanguage language = new JavaCpgLanguage(false, false, reorder, removeSimpleDeadCode);
            List<Token> result = language.parse(Set.of(tempFile), false);
            if (!result.isEmpty()) {
                result.removeLast(); // remove EOF token
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @Disabled("Only for evaluation purposes, not a real test")
    @MethodSource("dceLlmFiles")
    void DceLlmDeadCodeEvaluation(String fileName) throws ParsingException {
        System.out.println(fileName);
        long startTime = System.nanoTime();
        List<Token> tokens = getTokensFromFile(fileName, false, false, false, false, false);
        long timeNoRemoval = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        List<Token> tokensWithoutSimpleDeadCode = getTokensFromFile(fileName, false, false, false, true, false);
        long timeSimpleRemoval = System.nanoTime() - startTime;

        boolean javaLanguageFeatureNotSupported = false;
        boolean cpgErrorException = false;
        boolean runtimeError = false;
        startTime = System.nanoTime();
        List<Token> tokensWithoutDeadCode;
        try {
            tokensWithoutDeadCode = getTokensFromFile(fileName, true, true, false, true, false);
        } catch (JavaLanguageFeatureNotSupportedException _) {
            tokensWithoutDeadCode = new ArrayList<>(tokens);
            javaLanguageFeatureNotSupported = true;
        } catch (CpgErrorException _) {
            tokensWithoutDeadCode = new ArrayList<>(tokens);
            cpgErrorException = true;
        } catch (Exception e) {
            Throwable one = e.getCause();
            Throwable two = one.getCause();
            if (two instanceof CpgErrorException) {
                tokensWithoutDeadCode = new ArrayList<>(tokens);
                cpgErrorException = true;
            } else if (two instanceof JavaLanguageFeatureNotSupportedException) {
                tokensWithoutDeadCode = new ArrayList<>(tokens);
                javaLanguageFeatureNotSupported = true;
            } else {
                runtimeError = true;
                tokensWithoutDeadCode = new ArrayList<>(tokens);
                throw new RuntimeException(e);
            }
        }
        long timeFullRemoval = System.nanoTime() - startTime;

        List<Token> tokensWithoutDeadCodeManual = getTokensFromFileWithoutDeadCode(fileName, false, false);

        boolean tokensSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokens);
        boolean simpleRemovalSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode);
        boolean removalSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutDeadCode);
        if (javaLanguageFeatureNotSupported || cpgErrorException || runtimeError) {
            tokensSound = true;
            simpleRemovalSound = true;
            removalSound = true;
        }
        double simNoRemoval = similarity(tokensWithoutDeadCodeManual, tokens);
        double simSimpleRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode);
        double simFullRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutDeadCode);
        double removedSimpleTokens = tokens.size() - tokensWithoutSimpleDeadCode.size();
        double removedFullTokens = tokens.size() - tokensWithoutDeadCode.size();
        double removedManualTokens = tokens.size() - tokensWithoutDeadCodeManual.size();

        File csvFile = new File("DceLlm_deadcode_results.csv");
        boolean fileExists = csvFile.exists();
        try (java.io.FileWriter writer = new java.io.FileWriter(csvFile, true)) {
            if (!fileExists) {
                writer.write(
                        "FileName,NoRemoval,SimpleRemoval,FullRemoval,RemovedSimpleTokens,RemovedFullTokens,RemovedManualTokens,TimeNoRemoval(ms),TimeSimpleRemoval(ms),TimeFullRemoval(ms),JavaLanguageFeatureNotSupported,CpgErrorException,RuntimeException,tokensSound,simpleRemovalSound,removalSound\n");
            }
            writer.write(String.format("%s,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.2f,%.2f,%.2f,%b,%b,%b,%b,%b,%b%n", fileName, simNoRemoval,
                    simSimpleRemoval, simFullRemoval, removedSimpleTokens, removedFullTokens, removedManualTokens, timeNoRemoval / 1_000_000.0,
                    timeSimpleRemoval / 1_000_000.0, timeFullRemoval / 1_000_000.0, javaLanguageFeatureNotSupported, cpgErrorException, runtimeError,
                    tokensSound, simpleRemovalSound, removalSound));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Similarity between manual and no dead code removal: " + simNoRemoval + "% (took " + timeNoRemoval / 1_000_000.0 + " ms)");
        System.out.println("Similarity between manual and automatic simple dead code removal: " + simSimpleRemoval + "% (took "
                + timeSimpleRemoval / 1_000_000.0 + " ms)");
        System.out.println(
                "Similarity between manual and automatic dead code removal: " + simFullRemoval + "% (took " + timeFullRemoval / 1_000_000.0 + " ms)");
        assertTrue(true);
    }

    @Test
    @Disabled("Only for evaluation purposes, not a real test")
    void DceLlmDeadCodeEvaluationSingle() throws ParsingException {
        // String fileName = "/home/alpaka/PycharmProjects/baplots/testFiles/p02723/s919988520.java";
        String fileName = "/home/alpaka/PycharmProjects/baplots/testFiles/p02766/s036883984.java";

        long startTime = System.nanoTime();
        List<Token> tokens = getTokensFromFile(fileName, false, false, false, false, false);
        long timeNoRemoval = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        List<Token> tokensWithoutSimpleDeadCode = getTokensFromFile(fileName, false, false, false, true, false);
        long timeSimpleRemoval = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        List<Token> tokensWithoutDeadCode = getTokensFromFile(fileName, true, true, false, true, false);
        long timeFullRemoval = System.nanoTime() - startTime;

        List<Token> tokensWithoutDeadCodeManual = getTokensFromFileWithoutDeadCode(fileName, false, false);

        double simNoRemoval = similarity(tokensWithoutDeadCodeManual, tokens);
        double simSimpleRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode);
        double simFullRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutDeadCode);
        // Assert we don't remove non-dead code
        assertTrue(checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokens));
        assertTrue(checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode));
        assertTrue(checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutDeadCode));

        System.out.println("Similarity between manual and no dead code removal: " + simNoRemoval + "% (took " + timeNoRemoval / 1_000_000.0 + " ms)");
        System.out.println("Similarity between manual and automatic simple dead code removal: " + simSimpleRemoval + "% (took "
                + timeSimpleRemoval / 1_000_000.0 + " ms)");
        System.out.println(
                "Similarity between manual and automatic dead code removal: " + simFullRemoval + "% (took " + timeFullRemoval / 1_000_000.0 + " ms)");
        assertTrue(true);
    }

}
