package de.jplag.java_cpg.evaluation;

import static de.jplag.java_cpg.AbstractJavaCpgLanguageTest.BASE_PATH;
import static de.jplag.java_cpg.evaluation.PmdTest.getPmdDeadLinesInFile;
import static de.jplag.java_cpg.evaluation.PmdTest.runPmdForFile;
import static de.jplag.options.JPlagOptions.DEFAULT_SHOWN_COMPARISONS;
import static de.jplag.options.JPlagOptions.DEFAULT_SIMILARITY_METRIC;
import static de.jplag.options.JPlagOptions.DEFAULT_SIMILARITY_THRESHOLD;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.exceptions.ExitException;
import de.jplag.highlightextraction.FrequencyAnalysisOptions;
import de.jplag.java_cpg.JavaCpgLanguage;
import de.jplag.java_cpg.ai.ArrayAiType;
import de.jplag.java_cpg.ai.CharAiType;
import de.jplag.java_cpg.ai.CpgErrorException;
import de.jplag.java_cpg.ai.FloatAiType;
import de.jplag.java_cpg.ai.IntAiType;
import de.jplag.java_cpg.ai.JavaLanguageFeatureNotSupportedException;
import de.jplag.java_cpg.ai.ProgpediaTests;
import de.jplag.java_cpg.ai.StringAiType;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.merging.MergingOptions;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.reportobject.ReportObjectFactory;

import kotlin.Pair;

@Disabled("Only for manual evaluation of dead code removal and plagiarism detection")
class EvaluationEngineTest {

    static @NotNull Stream<String> testFiles() {
        return Stream.of(
                // the first block is jvm warmup files
                "aiGenerated/claude/Project1.java", "aiGenerated/claude/Project2.java", "aiGenerated/claude/Project3.java",
                "aiGenerated/claude/Project4.java", "aiGenerated/claude/Project5.java", "aiGenerated/claude/Project6.java",
                "aiGenerated/claude/Project7.java", "aiGenerated/claude/Project8.java", "aiGenerated/claude/Project9.java",
                //
                "aiGenerated/gemini/ProjectA.java", "aiGenerated/gemini/ProjectB.java", "aiGenerated/gemini/ProjectC.java",
                "aiGenerated/gemini/ProjectD.java", "aiGenerated/gemini/ProjectE.java", "aiGenerated/gemini/ProjectF.java",
                "aiGenerated/gemini/ProjectG.java", "aiGenerated/gemini/ProjectH.java", "aiGenerated/gemini/ProjectI.java",
                "aiGenerated/gemini/ProjectJ.java", "aiGenerated/gemini/ProjectK.java", "aiGenerated/gemini/ProjectL.java",
                "aiGenerated/gemini/ProjectM.java", "aiGenerated/gemini/ProjectN.java", "aiGenerated/gemini/ProjectO.java",
                "aiGenerated/gemini/ProjectP.java", "aiGenerated/gemini/ProjectQ.java",
                // "aiGenerated/gemini/ProjectR.java", "aiGenerated/gemini/ProjectS.java", //anonymus class causes problems
                "aiGenerated/gemini/ProjectT.java", "aiGenerated/gemini/ProjectU.java", "aiGenerated/gemini/Project1.java",
                "aiGenerated/gemini/Project2.java", "aiGenerated/gemini/Project3.java", "aiGenerated/gemini/Project4.java",
                "aiGenerated/gemini/Project5.java", "aiGenerated/gemini/Project7.java", "aiGenerated/gemini/Project8.java",
                //
                "aiGenerated/claude/Project1.java", "aiGenerated/claude/Project2.java", "aiGenerated/claude/Project3.java",
                "aiGenerated/claude/Project4.java", "aiGenerated/claude/Project5.java", "aiGenerated/claude/Project6.java",
                "aiGenerated/claude/Project7.java", "aiGenerated/claude/Project8.java", "aiGenerated/claude/Project9.java",
                "aiGenerated/claude/Project10.java",
                //
                "aiGenerated/perplexityLabs/Project1.java", "aiGenerated/perplexityLabs/Project2.java", "aiGenerated/perplexityLabs/Project3.java",
                "aiGenerated/perplexityLabs/Project4.java",
                //
                "aiGenerated/geminiPlag/NetworkController.java", "aiGenerated/geminiPlag/ServerProcessManager.java",
                "aiGenerated/geminiPlag/GridOverseer.java",
                //
                "aiGenerated/grok/project1.java", "aiGenerated/grok/project2.java", "aiGenerated/grok/project3.java",
                "aiGenerated/grok/project4.java", "aiGenerated/grok/project5.java", "aiGenerated/grok/project6.java",
                "aiGenerated/grok/project7.java", "aiGenerated/grok/project8.java");
    }

    private static @NotNull Stream<Pair<String, String>> testPlagFiles() {
        return Stream.of(
                // JVM Warmup:
                new Pair<>("aiGenerated/claude/Project4.java", "aiGenerated/claude/Project1.java"),
                new Pair<>("aiGenerated/claude/Project4.java", "aiGenerated/claude/Project3.java"),
                new Pair<>("aiGenerated/claude/Project5.java", "aiGenerated/claude/Project6.java"),
                new Pair<>("aiGenerated/claude/Project7.java", "aiGenerated/claude/Project8.java"),
                new Pair<>("aiGenerated/claude/Project9.java", "aiGenerated/claude/Project10.java"),
                //
                new Pair<>("aiGenerated/gemini/ProjectA.java", "aiGenerated/gemini/ProjectC.java"),
                new Pair<>("aiGenerated/gemini/ProjectB.java", "aiGenerated/gemini/ProjectE.java"),
                new Pair<>("aiGenerated/gemini/ProjectF.java", "aiGenerated/gemini/ProjectG.java"),
                new Pair<>("aiGenerated/gemini/ProjectH.java", "aiGenerated/gemini/ProjectI.java"),
                new Pair<>("aiGenerated/gemini/ProjectJ.java", "aiGenerated/gemini/ProjectK.java"),
                new Pair<>("aiGenerated/gemini/ProjectL.java", "aiGenerated/gemini/ProjectM.java"),
                new Pair<>("aiGenerated/gemini/ProjectN.java", "aiGenerated/gemini/ProjectO.java"),
                new Pair<>("aiGenerated/gemini/ProjectP.java", "aiGenerated/gemini/ProjectQ.java"),
                // new Pair<>("aiGenerated/gemini/ProjectR.java", "aiGenerated/gemini/ProjectS.java"), //anonymus class causes problems
                new Pair<>("aiGenerated/gemini/ProjectT.java", "aiGenerated/gemini/ProjectU.java"),
                new Pair<>("aiGenerated/gemini/Project1.java", "aiGenerated/gemini/Project2.java"),
                new Pair<>("aiGenerated/gemini/Project1.java", "aiGenerated/gemini/Project3.java"),
                // new Pair<>("aiGenerated/gemini/Project4.java", "aiGenerated/gemini/Project5.java"), //bug in GraphTransformations
                new Pair<>("aiGenerated/gemini/Project7.java", "aiGenerated/gemini/Project8.java"),
                new Pair<>("aiGenerated/gemini/Project4.java", "aiGenerated/gemini/Project7.java"),
                new Pair<>("aiGenerated/gemini/Project4.java", "aiGenerated/gemini/Project8.java"),
                //
                new Pair<>("aiGenerated/gemini/ProjectH.java", "aiGenerated/geminiPlag/NetworkController.java"),
                new Pair<>("aiGenerated/gemini/ProjectJ.java", "aiGenerated/geminiPlag/ServerProcessManager.java"),   // break
                new Pair<>("aiGenerated/geminiPlag/NetworkController.java", "aiGenerated/geminiPlag/GridOverseer.java"), // bug in my code
                //
                new Pair<>("aiGenerated/claude/Project1.java", "aiGenerated/claude/Project2.java"),
                new Pair<>("aiGenerated/claude/Project4.java", "aiGenerated/claude/Project1.java"),
                new Pair<>("aiGenerated/claude/Project4.java", "aiGenerated/claude/Project3.java"),
                new Pair<>("aiGenerated/claude/Project5.java", "aiGenerated/claude/Project6.java"),
                new Pair<>("aiGenerated/claude/Project7.java", "aiGenerated/claude/Project8.java"),
                new Pair<>("aiGenerated/claude/Project9.java", "aiGenerated/claude/Project10.java"),
                //
                new Pair<>("aiGenerated/perplexityLabs/Project1.java", "aiGenerated/perplexityLabs/Project3.java"),
                new Pair<>("aiGenerated/perplexityLabs/Project1.java", "aiGenerated/perplexityLabs/Project4.java"),
                //
                new Pair<>("aiGenerated/grok/project1.java", "aiGenerated/grok/project2.java"),
                new Pair<>("aiGenerated/grok/project3.java", "aiGenerated/grok/project4.java"),
                new Pair<>("aiGenerated/grok/project3.java", "aiGenerated/grok/project5.java"),
                new Pair<>("aiGenerated/grok/project4.java", "aiGenerated/grok/project5.java"),
                new Pair<>("aiGenerated/grok/project6.java", "aiGenerated/grok/project7.java"),   // break
                new Pair<>("aiGenerated/grok/project6.java", "aiGenerated/grok/project8.java"),
                new Pair<>("aiGenerated/grok/project7.java", "aiGenerated/grok/project8.java")    // break
        );
    }

    private static @NotNull Stream<Pair<String, String>> testPlagFilesUnrelated() {
        return Stream.of(
                // JVM Warmup:
                new Pair<>("aiGenerated/claude/Project4.java", "aiGenerated/claude/Project1.java"),
                new Pair<>("aiGenerated/claude/Project4.java", "aiGenerated/claude/Project3.java"),
                new Pair<>("aiGenerated/claude/Project5.java", "aiGenerated/claude/Project6.java"),
                new Pair<>("aiGenerated/claude/Project7.java", "aiGenerated/claude/Project8.java"),
                new Pair<>("aiGenerated/claude/Project9.java", "aiGenerated/claude/Project10.java"),
                //
                new Pair<>("aiGenerated/gemini/ProjectA.java", "aiGenerated/gemini/Project3.java"),
                new Pair<>("aiGenerated/gemini/ProjectB.java", "aiGenerated/gemini/ProjectI.java"),
                new Pair<>("aiGenerated/gemini/ProjectF.java", "aiGenerated/grok/project4.java"),
                new Pair<>("aiGenerated/gemini/ProjectH.java", "aiGenerated/claude/Project1.java"),
                new Pair<>("aiGenerated/gemini/ProjectJ.java", "aiGenerated/grok/project5.java"),
                new Pair<>("aiGenerated/gemini/ProjectL.java", "aiGenerated/gemini/Project2.java"),
                new Pair<>("aiGenerated/gemini/ProjectN.java", "aiGenerated/gemini/ProjectU.java"),
                new Pair<>("aiGenerated/gemini/ProjectP.java", "aiGenerated/gemini/Project7.java"),
                new Pair<>("aiGenerated/gemini/ProjectT.java", "aiGenerated/perplexityLabs/Project4.java"),
                new Pair<>("aiGenerated/gemini/Project1.java", "aiGenerated/geminiPlag/NetworkController.java"),
                new Pair<>("aiGenerated/gemini/Project1.java", "aiGenerated/gemini/Project8.java"),
                new Pair<>("aiGenerated/gemini/Project7.java", "aiGenerated/gemini/ProjectG.java"),
                new Pair<>("aiGenerated/gemini/Project4.java", "aiGenerated/gemini/ProjectC.java"),
                new Pair<>("aiGenerated/gemini/Project4.java", "aiGenerated/gemini/ProjectQ.java"),
                new Pair<>("aiGenerated/gemini/ProjectH.java", "aiGenerated/perplexityLabs/Project3.java"),
                new Pair<>("aiGenerated/geminiPlag/NetworkController.java", "aiGenerated/grok/project2.java"),
                new Pair<>("aiGenerated/claude/Project1.java", "aiGenerated/gemini/ProjectO.java"),
                new Pair<>("aiGenerated/claude/Project4.java", "aiGenerated/claude/Project8.java"),
                new Pair<>("aiGenerated/claude/Project4.java", "aiGenerated/grok/project8.java"),
                new Pair<>("aiGenerated/claude/Project5.java", "aiGenerated/claude/Project2.java"),
                new Pair<>("aiGenerated/claude/Project7.java", "aiGenerated/claude/Project3.java"),
                new Pair<>("aiGenerated/claude/Project9.java", "aiGenerated/geminiPlag/GridOverseer.java"),
                new Pair<>("aiGenerated/perplexityLabs/Project1.java", "aiGenerated/gemini/Project8.java"),
                new Pair<>("aiGenerated/perplexityLabs/Project1.java", "aiGenerated/gemini/ProjectE.java"),
                new Pair<>("aiGenerated/grok/project1.java", "aiGenerated/claude/Project10.java"),
                new Pair<>("aiGenerated/grok/project3.java", "aiGenerated/gemini/ProjectM.java"),
                new Pair<>("aiGenerated/grok/project3.java", "aiGenerated/geminiPlag/NetworkController.java"),
                new Pair<>("aiGenerated/grok/project4.java", "aiGenerated/gemini/ProjectK.java"),
                new Pair<>("aiGenerated/grok/project6.java", "aiGenerated/claude/Project6.java"));
    }

    public static <T> double similarity(@NotNull List<T> s1, @NotNull List<T> s2) {
        // stolen from https://stackoverflow.com/questions/955110/similarity-string-comparison-in-java
        List<T> longer = s1, shorter = s2;
        if (s1.size() < s2.size()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.size();
        if (longerLength == 0) {
            return 1.0; /* both lists are zero length */
        }
        double result = (longerLength - editDistance(longer, shorter)) / (double) longerLength;
        result = Math.round(result * 10000.0) / 10000.0;
        return result * 100;
    }

    private static <T> int editDistance(@NotNull List<T> s1, @NotNull List<T> s2) {
        // stolen from https://stackoverflow.com/questions/955110/similarity-string-comparison-in-java
        int[] costs = new int[s2.size() + 1];
        for (int i = 0; i <= s1.size(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.size(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (!(s1.get(i - 1).equals(s2.get(j - 1)))) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.size()] = lastValue;
            }
        }
        return costs[s2.size()];
    }

    static @NotNull List<Token> getTokensFromFile(@NotNull String fileName, boolean removeDeadCode, boolean detectDeadCode, boolean reorder,
            boolean normalize, boolean removeSimpleDeadCode) throws ParsingException {
        return getTokensFromFilePair(fileName, removeDeadCode, detectDeadCode, reorder, normalize, removeSimpleDeadCode).getFirst();
    }

    static @NotNull Pair<List<Token>, Integer> getTokensFromFilePair(@NotNull String fileName, boolean removeDeadCode, boolean detectDeadCode,
            boolean reorder, boolean normalize, boolean removeSimpleDeadCode) throws ParsingException {
        assert normalize || !reorder;
        GraphTransformation[] transformations = JavaCpgLanguage.deadCodeRemovalTransformations();
        JavaCpgLanguage language = new JavaCpgLanguage(removeDeadCode, detectDeadCode, reorder, removeSimpleDeadCode, transformations,
                IntAiType.DEFAULT, FloatAiType.DEFAULT, StringAiType.DEFAULT, CharAiType.DEFAULT, ArrayAiType.DEFAULT);
        // IntAiType.INTERVALS, FloatAiType.DEFAULT, StringAiType.CHAR_INCLUSION, CharAiType.DEFAULT, ArrayAiType.LENGTH);
        // IntAiType.SET, FloatAiType.SET, StringAiType.REGEX, CharAiType.SET, ArrayAiType.DEFAULT);
        File file = new File(BASE_PATH.toFile().getAbsolutePath(), fileName);
        Set<File> files = Set.of(file);
        Pair<List<Token>, Integer> result = language.parse2(files, normalize);
        result.getFirst().removeLast(); // remove EOF token
        return result;
    }

    public static @NotNull List<Token> getTokensFromFileWithoutDeadCode(@NotNull String fileName, boolean reorder, boolean removeSimpleDeadCode)
            throws ParsingException {
        try {
            File originalFile = new File(BASE_PATH.toFile().getAbsolutePath(), fileName);
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

    private static @NotNull List<Token> getTokensFromFileWithoutPmdDeadCode(@NotNull String fileName) throws ParsingException, IOException {
        File tempFile = runPmdForFile(new File(BASE_PATH.toFile(), fileName));
        JavaCpgLanguage language = new JavaCpgLanguage(false, false, false, false);
        List<Token> result = language.parse(Set.of(tempFile), false);
        result.removeLast(); // remove EOF token
        return result;
    }

    private static double getJPlagCpgPlagScore(@NotNull String fileNameA, @NotNull String fileNameB, boolean removeDeadCode, boolean detectDeadCode,
            boolean reorder, boolean normalize, boolean removeSimpleDeadCode) throws ExitException, IOException {
        JavaCpgLanguage language = new JavaCpgLanguage(removeDeadCode, detectDeadCode, reorder, removeSimpleDeadCode,
                JavaCpgLanguage.allTransformations(), IntAiType.DEFAULT, FloatAiType.DEFAULT, StringAiType.DEFAULT, CharAiType.DEFAULT,
                ArrayAiType.DEFAULT);
        // IntAiType.INTERVALS, FloatAiType.DEFAULT, StringAiType.CHAR_INCLUSION, CharAiType.DEFAULT, ArrayAiType.LENGTH);
        // IntAiType.SET, FloatAiType.SET, StringAiType.REGEX, CharAiType.SET, ArrayAiType.DEFAULT);
        return getJPlagScore(fileNameA, fileNameB, normalize, language);
    }

    private static @NotNull JPlagResult getJPlagCpgPlagScore(@NotNull Set<File> files, boolean removeDeadCode, boolean detectDeadCode,
            boolean reorder, boolean normalize, boolean removeSimpleDeadCode) throws ExitException {
        JavaCpgLanguage language = new JavaCpgLanguage(removeDeadCode, detectDeadCode, reorder, removeSimpleDeadCode,
                JavaCpgLanguage.allTransformations(), IntAiType.DEFAULT, FloatAiType.DEFAULT, StringAiType.DEFAULT, CharAiType.DEFAULT,
                ArrayAiType.DEFAULT);
        // IntAiType.INTERVALS, FloatAiType.DEFAULT, StringAiType.CHAR_INCLUSION, CharAiType.DEFAULT, ArrayAiType.LENGTH);
        // IntAiType.SET, FloatAiType.SET, StringAiType.REGEX, CharAiType.SET, ArrayAiType.DEFAULT);
        return getJPlagScore(files, normalize, language);
    }

    private static double getJPlagPlagScore(@NotNull String fileNameA, @NotNull String fileNameB, boolean normalize)
            throws ExitException, IOException {
        de.jplag.java.JavaLanguage language = new de.jplag.java.JavaLanguage();
        return getJPlagScore(fileNameA, fileNameB, normalize, language);
    }

    private static @NotNull JPlagResult getJPlagPlagScore(@NotNull Set<File> files, boolean normalize) throws ExitException {
        de.jplag.java.JavaLanguage language = new de.jplag.java.JavaLanguage();
        return getJPlagScore(files, normalize, language);
    }

    private static double getJPlagScore(@NotNull String fileNameA, @NotNull String fileNameB, boolean normalize, Language language)
            throws ExitException, IOException {
        File fileA = new File(BASE_PATH.toFile().getAbsolutePath(), fileNameA);
        File fileB = new File(BASE_PATH.toFile().getAbsolutePath(), fileNameB);
        // Create temporary directories for submissions
        File tempDirA = createTempDir();
        File tempDirB = createTempDir();
        // Copy files to temp directories
        File targetA = new File(tempDirA, "SubmissionA.java");
        File targetB = new File(tempDirB, "SubmissionB.java");
        java.nio.file.Files.copy(fileA.toPath(), targetA.toPath());
        java.nio.file.Files.copy(fileB.toPath(), targetB.toPath());
        Set<File> submissionDirectories = Set.of(tempDirA, tempDirB);
        JPlagOptions options = new JPlagOptions(language, null, submissionDirectories, Set.of(), null, null, null, null, DEFAULT_SIMILARITY_METRIC,
                DEFAULT_SIMILARITY_THRESHOLD, DEFAULT_SHOWN_COMPARISONS, new ClusteringOptions(), false, new MergingOptions(), normalize, false,
                new FrequencyAnalysisOptions());
        JPlagResult result = JPlag.run(options);
        assert result.getAllComparisons().size() == 1;
        JPlagComparison comparison = result.getAllComparisons().getFirst();
        double similarity = comparison.similarity();
        return similarity;
    }

    private static @NotNull JPlagResult getJPlagScore(@NotNull Set<File> submissionDirectories, boolean normalize, Language language)
            throws ExitException {
        JPlagOptions options = new JPlagOptions(language, null, submissionDirectories, Set.of(), null, null, null, null, DEFAULT_SIMILARITY_METRIC,
                DEFAULT_SIMILARITY_THRESHOLD, DEFAULT_SHOWN_COMPARISONS, new ClusteringOptions(), false, new MergingOptions(), normalize, false,
                new FrequencyAnalysisOptions());
        return JPlag.run(options);
    }

    private static @NotNull File createTempDir() throws IOException {
        File tempDir = File.createTempFile("jplag_submission_", "");
        tempDir.delete();
        tempDir.mkdir();
        tempDir.deleteOnExit();
        return tempDir;
    }

    public static boolean checkNonDeadCodeNotRemoved(@NotNull List<Token> verifiedDeadCode, @NotNull List<Token> detectedDeadCode) {
        // we check that all tokens in verifiedDeadCode are also in detectedDeadCode
        // if (detectedDeadCode.size() < verifiedDeadCode.size()) {
        // System.out.println("Detected dead code size: " + detectedDeadCode.size() + ", Verified dead code size: " +
        // verifiedDeadCode.size());
        // return false; // Detected dead code is smaller than verified dead code
        // }
        int detectedIndex = 0;
        for (Token verifiedToken : verifiedDeadCode) {
            boolean found = false;
            while (detectedIndex < detectedDeadCode.size()) {
                if (Objects.equals(detectedDeadCode.get(detectedIndex).toString(), verifiedToken.toString())) {
                    detectedIndex++;
                    found = true;
                    break;
                }
                detectedIndex++;
            }
            if (!found) {
                System.out.println("Token not found: " + verifiedToken);
                return false;    // Token from verified dead code isn't found in detected dead code
            }
        }
        return true;
    }

    public static @NotNull Stream<String> progpediaFiles() {
        // warm up with 6 files
        List<String> warmups = List.of("progpedia/00000006/ACCEPTED/00218_00001/CigarrasTontas.java",
                "progpedia/00000006/ACCEPTED/00076_00001/Main.java", "progpedia/00000006/ACCEPTED/00005_00001/CigarrasTontas.java",
                "progpedia/00000006/ACCEPTED/00118_00002/Cigarras.java", "progpedia/00000006/ACCEPTED/00046_00002/cigarras.java",
                "progpedia/00000006/ACCEPTED/00133_00001/Cigarras.java", "progpedia/00000021/WRONG_ANSWER/00168_00002/encomenda.java");
        return Stream.concat(warmups.stream(), ProgpediaTests.progpediaFiles());
    }

    public static @NotNull Stream<String> kitGenFiles() {
        List<String> baseDirs = List.of("kit_DONT_COMMIT/BoardGame/insert", "kit_DONT_COMMIT/BoardGame/refactor", "kit_DONT_COMMIT/TicTacToe/insert",
                "kit_DONT_COMMIT/TicTacToe/refactor", "kit_DONT_COMMIT/TicTacToe/gpt", "kit_DONT_COMMIT/TicTacToe/gptobf");
        return baseDirs.stream().map(baseDir -> new File(BASE_PATH.toFile(), baseDir)).filter(File::exists).filter(File::isDirectory)
                .flatMap(dir -> Stream.ofNullable(dir.listFiles(File::isDirectory))).flatMap(Stream::of)
                .map(file -> BASE_PATH.toFile().toURI().relativize(file.toURI()).getPath());
    }

    public static @NotNull Stream<String> kitHumanFiles() {
        List<String> baseDirs = List.of("kit_DONT_COMMIT/BoardGame/human", "kit_DONT_COMMIT/TicTacToe/human"
        // "kit_DONT_COMMIT/ws2223-Sheet4TaskA-perseverance", "kit_DONT_COMMIT/ws2425-Sheet3TaskA-dotsandboxes"
        );
        return baseDirs.stream().map(baseDir -> new File(BASE_PATH.toFile(), baseDir)).filter(File::exists).filter(File::isDirectory)
                .flatMap(dir -> Stream.ofNullable(dir.listFiles(File::isDirectory))).flatMap(Stream::of)
                .map(file -> BASE_PATH.toFile().toURI().relativize(file.toURI()).getPath());
    }

    public static @NotNull Set<String> kitBoardGamePlag() {
        List<String> baseDirs = List.of("kit_DONT_COMMIT/BoardGame/human", "kit_DONT_COMMIT/BoardGame/insert", "kit_DONT_COMMIT/BoardGame/refactor");
        return baseDirs.stream().map(baseDir -> new File(BASE_PATH.toFile(), baseDir)).filter(File::exists).filter(File::isDirectory)
                .flatMap(dir -> Stream.ofNullable(dir.listFiles(File::isDirectory))).flatMap(Stream::of)
                .map(file -> BASE_PATH.toFile().toURI().relativize(file.toURI()).getPath()).collect(Collectors.toSet());
    }

    public static @NotNull Set<String> kitTicTocToePlag() {
        List<String> baseDirs = List.of("kit_DONT_COMMIT/TicTacToe/human", "kit_DONT_COMMIT/TicTacToe/insert", "kit_DONT_COMMIT/TicTacToe/refactor",
                "kit_DONT_COMMIT/TicTacToe/gpt", "kit_DONT_COMMIT/TicTacToe/gptobf");
        return baseDirs.stream().map(baseDir -> new File(BASE_PATH.toFile(), baseDir)).filter(File::exists).filter(File::isDirectory)
                .flatMap(dir -> Stream.ofNullable(dir.listFiles(File::isDirectory))).flatMap(Stream::of)
                .map(file -> BASE_PATH.toFile().toURI().relativize(file.toURI()).getPath()).collect(Collectors.toSet());
    }

    @ParameterizedTest
    @Disabled("Only for evaluation purposes, not a real test")
    @MethodSource("testFiles")
    void AiGeneratedTestDataDeadCodeEvaluation(String fileName) throws ParsingException, IOException {
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
        Pair<List<Token>, Integer> result;
        List<Token> tokensWithoutDeadCode;
        int removedDeadLines = 0;
        try {
            result = getTokensFromFilePair(fileName, true, true, false, true, false);
            tokensWithoutDeadCode = result.getFirst();
            removedDeadLines = result.getSecond();
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
                // throw new RuntimeException(e);
            }
        }
        long timeFullRemoval = System.nanoTime() - startTime;

        List<Token> tokensWithoutDeadCodeManual = getTokensFromFileWithoutDeadCode(fileName, false, false);
        startTime = System.nanoTime();
        int pmdRemovedLines = getPmdDeadLinesInFile(new File(BASE_PATH.toFile(), fileName));
        long timePmd = System.nanoTime() - startTime;

        boolean tokensSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokens);
        boolean simpleRemovalSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode);
        boolean removalSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutDeadCode);
        if (javaLanguageFeatureNotSupported || cpgErrorException || runtimeError) {
            tokensSound = true;
            simpleRemovalSound = true;
            removalSound = true;
        }
        if (!removalSound || !simpleRemovalSound || !tokensSound) {
            System.out.println("Non-dead code was removed in file: " + fileName);
        }

        double simNoRemoval = similarity(tokensWithoutDeadCodeManual, tokens);
        double simSimpleRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode);
        double simFullRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutDeadCode);
        double removedSimpleTokens = tokens.size() - tokensWithoutSimpleDeadCode.size();
        double removedFullTokens = tokens.size() - tokensWithoutDeadCode.size();
        double removedManualTokens = tokens.size() - tokensWithoutDeadCodeManual.size();

        File csvFile = new File("Ai_deadcode_results.csv");
        boolean fileExists = csvFile.exists();
        try (java.io.FileWriter writer = new java.io.FileWriter(csvFile, true)) {
            if (!fileExists) {
                writer.write(
                        "FileName,NoRemoval,SimpleRemoval,FullRemoval,RemovedSimpleTokens,RemovedFullTokens,RemovedManualTokens,TimeNoRemoval(ms),TimeSimpleRemoval(ms),TimeFullRemoval(ms),JavaLanguageFeatureNotSupported,CpgErrorException,RuntimeException,tokensSound,simpleRemovalSound,removalSound,removedDeadLines,pmdRemovedLines,timePmd\n");
            }
            writer.write(String.format("%s,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.2f,%.2f,%.2f,%b,%b,%b,%b,%b,%b,%d,%d,%.2f%n", fileName, simNoRemoval,
                    simSimpleRemoval, simFullRemoval, removedSimpleTokens, removedFullTokens, removedManualTokens, timeNoRemoval / 1_000_000.0,
                    timeSimpleRemoval / 1_000_000.0, timeFullRemoval / 1_000_000.0, javaLanguageFeatureNotSupported, cpgErrorException, runtimeError,
                    tokensSound, simpleRemovalSound, removalSound, removedDeadLines, pmdRemovedLines, timePmd / 1_000_000.0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Similarity between manual and no dead code removal: " + simNoRemoval + "% (took " + timeNoRemoval / 1_000_000.0 + " ms)");
        System.out.println("Similarity between manual and automatic simple dead code removal: " + simSimpleRemoval + "% (took "
                + timeSimpleRemoval / 1_000_000.0 + " ms)");
        System.out.println(
                "Similarity between manual and automatic dead code removal: " + simFullRemoval + "% (took " + timeFullRemoval / 1_000_000.0 + " ms)");
        System.out.println("Pmd removed lines: " + pmdRemovedLines + " (took " + timePmd / 1_000_000.0 + " ms)");
        assertTrue(true);
    }

    @Test
    @Disabled("Only for evaluation purposes, not a real test")
    void AiGeneratedTestDataDeadCodeEvaluationSingle() throws ParsingException {
        String fileName = "aiGenerated/claude/Project1.java";

        List<Token> tokensWithoutDeadCode = getTokensFromFile(fileName, true, true, false, true, false);

        List<Token> tokens = getTokensFromFile(fileName, false, false, false, false, false);
        List<Token> tokensWithoutSimpleDeadCode = getTokensFromFile(fileName, false, false, false, true, false);
        List<Token> tokensWithoutDeadCodeManual = getTokensFromFileWithoutDeadCode(fileName, false, false);
        // Assert we don't remove non-dead code
        assertTrue(checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokens));
        assertTrue(checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode));
        assertTrue(checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutDeadCode));

        System.out.println("Similarity between manual and no dead code removal: " + similarity(tokensWithoutDeadCodeManual, tokens) + "%");
        System.out.println("Similarity between manual and simple dead code removal: "
                + similarity(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode) + "%");
        System.out
                .println("Similarity between manual and dead code removal: " + similarity(tokensWithoutDeadCodeManual, tokensWithoutDeadCode) + "%");
        assertTrue(true);
    }

    @ParameterizedTest
    @Disabled("Only for evaluation purposes, not a real test")
    @MethodSource("kitHumanFiles")
    // @MethodSource("kitGenFiles")
    void KitDeadCodeEvaluation(String fileName) throws ParsingException {
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
                // throw new RuntimeException(e);
            }
        }
        long timeFullRemoval = System.nanoTime() - startTime;

        double simSimpleRemoval = similarity(tokens, tokensWithoutSimpleDeadCode);
        double simFullRemoval = similarity(tokens, tokensWithoutDeadCode);
        double removedSimpleTokens = tokens.size() - tokensWithoutSimpleDeadCode.size();
        double removedFullTokens = tokens.size() - tokensWithoutDeadCode.size();

        File csvFile = new File("Kit_deadcode_results.csv");
        boolean fileExists = csvFile.exists();
        try (java.io.FileWriter writer = new java.io.FileWriter(csvFile, true)) {
            if (!fileExists) {
                writer.write(
                        "FileName,NoRemoval,SimpleRemoval,FullRemoval,RemovedSimpleTokens,RemovedFullTokens,RemovedManualTokens,TimeNoRemoval(ms),TimeSimpleRemoval(ms),TimeFullRemoval(ms),JavaLanguageFeatureNotSupported,CpgErrorException,RuntimeException\n");
            }
            writer.write(String.format("%s,%.4f,%.4f,%.4f,%.4f,%.2f,%.2f,%.2f,%b,%b,%b%n", fileName, simSimpleRemoval, simFullRemoval,
                    removedSimpleTokens, removedFullTokens, timeNoRemoval / 1_000_000.0, timeSimpleRemoval / 1_000_000.0,
                    timeFullRemoval / 1_000_000.0, javaLanguageFeatureNotSupported, cpgErrorException, runtimeError));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Similarity between manual and automatic simple dead code removal: " + simSimpleRemoval + "% (took "
                + timeSimpleRemoval / 1_000_000.0 + " ms)");
        System.out.println(
                "Similarity between manual and automatic dead code removal: " + simFullRemoval + "% (took " + timeFullRemoval / 1_000_000.0 + " ms)");
        assertTrue(true);
    }

    @Test
    @Disabled("Only for evaluation purposes, not a real test")
    void KitDeadCodeEvaluationSingle() throws ParsingException {
        String fileName = "kit_DONT_COMMIT/BoardGame/human/subm208/";
        long startTime = System.nanoTime();
        List<Token> tokens = getTokensFromFile(fileName, false, false, false, false, false);
        long timeNoRemoval = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        List<Token> tokensWithoutSimpleDeadCode = getTokensFromFile(fileName, false, false, false, true, true);
        long timeSimpleRemoval = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        List<Token> tokensWithoutDeadCode = getTokensFromFile(fileName, true, true, false, true, true);
        long timeFullRemoval = System.nanoTime() - startTime;
        double simSimpleRemoval = similarity(tokens, tokensWithoutSimpleDeadCode);
        double simFullRemoval = similarity(tokens, tokensWithoutDeadCode);
        System.out.println("Time no removal: " + timeNoRemoval / 1_000_000.0 + " ms");
        System.out.println("Similarity between no and automatic simple dead code removal: " + simSimpleRemoval + "% (took "
                + timeSimpleRemoval / 1_000_000.0 + " ms)");
        System.out.println(
                "Similarity between no and automatic dead code removal: " + simFullRemoval + "% (took " + timeFullRemoval / 1_000_000.0 + " ms)");
        assertTrue(true);
    }

    @ParameterizedTest
    @Disabled("Only for evaluation purposes, not a real test")
    @MethodSource("progpediaFiles")
    void ProgpediaDeadCodeEvaluation(String fileName) throws ParsingException, IOException { // the first 6 lines are warmup
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
        Pair<List<Token>, Integer> result;
        List<Token> tokensWithoutDeadCode;
        int removedDeadLines = 0;
        try {
            result = getTokensFromFilePair(fileName, true, true, false, true, false);
            tokensWithoutDeadCode = result.getFirst();
            removedDeadLines = result.getSecond();
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
                // throw new RuntimeException(e);
            }
        }
        long timeFullRemoval = System.nanoTime() - startTime;

        List<Token> tokensWithoutDeadCodeManual = getTokensFromFileWithoutDeadCode(fileName, false, false);
        startTime = System.nanoTime();
        int pmdRemovedLines = getPmdDeadLinesInFile(new File(BASE_PATH.toFile(), fileName));
        long timePmd = System.nanoTime() - startTime;

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

        File csvFile = new File("Progpedia_deadcode_results.csv");
        boolean fileExists = csvFile.exists();
        try (java.io.FileWriter writer = new java.io.FileWriter(csvFile, true)) {
            if (!fileExists) {
                writer.write(
                        "FileName,NoRemoval,SimpleRemoval,FullRemoval,RemovedSimpleTokens,RemovedFullTokens,RemovedManualTokens,TimeNoRemoval(ms),TimeSimpleRemoval(ms),TimeFullRemoval(ms),JavaLanguageFeatureNotSupported,CpgErrorException,RuntimeException,tokensSound,simpleRemovalSound,removalSound,removedDeadLines,pmdRemovedLines,timePmd\n");
            }
            writer.write(String.format("%s,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.2f,%.2f,%.2f,%b,%b,%b,%b,%b,%b,%d,%d,%.2f%n", fileName, simNoRemoval,
                    simSimpleRemoval, simFullRemoval, removedSimpleTokens, removedFullTokens, removedManualTokens, timeNoRemoval / 1_000_000.0,
                    timeSimpleRemoval / 1_000_000.0, timeFullRemoval / 1_000_000.0, javaLanguageFeatureNotSupported, cpgErrorException, runtimeError,
                    tokensSound, simpleRemovalSound, removalSound, removedDeadLines, pmdRemovedLines, timePmd / 1_000_000.0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Similarity between manual and no dead code removal: " + simNoRemoval + "% (took " + timeNoRemoval / 1_000_000.0 + " ms)");
        System.out.println("Similarity between manual and automatic simple dead code removal: " + simSimpleRemoval + "% (took "
                + timeSimpleRemoval / 1_000_000.0 + " ms)");
        System.out.println(
                "Similarity between manual and automatic dead code removal: " + simFullRemoval + "% (took " + timeFullRemoval / 1_000_000.0 + " ms)");
        System.out.println("Pmd removed lines: " + pmdRemovedLines + " (took " + timePmd / 1_000_000.0 + " ms)");
        assertTrue(true);
    }

    @Test
    @Disabled("Only for evaluation purposes, not a real test")
    void ProgpediaDeadCodeEvaluationSingle() throws ParsingException {
        String fileName = "progpedia/00000019/WRONG_ANSWER/00189_00001";

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

    @ParameterizedTest
    @Disabled("Only for evaluation purposes, not a real test")
    @MethodSource("testPlagFiles")
    // @MethodSource("testPlagFilesUnrelated")
    void AiGeneratedTestDataPlagEvaluation(@NotNull Pair<String, String> fileNames) throws ExitException, IOException {
        String fileA = fileNames.getFirst();
        String fileB = fileNames.getSecond();

        long startTime = System.nanoTime();
        double similarityJPlag = getJPlagPlagScore(fileA, fileB, false);
        long timeJPlag = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        double similarityMinimalCpg = getJPlagCpgPlagScore(fileA, fileB, false, false, false, false, false);
        long timeMinimalCpg = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        double similarityStandardCpg = getJPlagCpgPlagScore(fileA, fileB, false, false, false, true, false);
        long timeStandardCpg = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        double similarityAi = getJPlagCpgPlagScore(fileA, fileB, true, true, false, true, false);
        long timeAi = System.nanoTime() - startTime;

        File csvFile = new File("AI_plagiarism_results.csv");
        boolean fileExists = csvFile.exists();

        try (java.io.FileWriter writer = new java.io.FileWriter(csvFile, true)) {
            if (!fileExists) {
                writer.write("FileA,FileB,JPlag,CpgMinimal,CpgStandard,CpgAI,TimeJPlag(ms),TimeMinimalCpg(ms),TimeStandardCpg(ms),TimeAi(ms)\n");
            }
            writer.write(String.format("%s,%s,%.4f,%.4f,%.4f,%.4f,%.2f,%.2f,%.2f,%.2f%n", fileA, fileB, similarityJPlag, similarityMinimalCpg,
                    similarityStandardCpg, similarityAi, timeJPlag / 1_000_000.0, timeMinimalCpg / 1_000_000.0, timeStandardCpg / 1_000_000.0,
                    timeAi / 1_000_000.0));
        }

        System.out.println("Plagiarism scores for " + fileA + " and " + fileB + ":");
        System.out.println("JPlag standard: " + similarityJPlag + " (took " + timeJPlag / 1_000_000.0 + " ms)");
        System.out.println("Cpg minimal transformations: " + similarityMinimalCpg + " (took " + timeMinimalCpg / 1_000_000.0 + " ms)");
        System.out.println("Cpg standard transformations: " + similarityStandardCpg + " (took " + timeStandardCpg / 1_000_000.0 + " ms)");
        System.out.println("Cpg with AI dead code removal: " + similarityAi + " (took " + timeAi / 1_000_000.0 + " ms)");
        assertTrue(true);
    }

    @Test
    @Disabled("Only for evaluation purposes, not a real test")
    void AiGeneratedTestDataPlagEvaluationSingle() throws ExitException, IOException {
        // new Pair<>("aiGenerated/gemini/ProjectT.java", "aiGenerated/perplexityLabs/Project4.java"),
        String fileA = "aiGenerated/gemini/ProjectT.java";
        String fileB = "aiGenerated/perplexityLabs/Project4.java";
        double similarityJPlag = getJPlagPlagScore(fileA, fileB, false);
        double similarityMinimalCpg = getJPlagCpgPlagScore(fileA, fileB, false, false, false, false, false);
        double similarityStandardCpg = getJPlagCpgPlagScore(fileA, fileB, false, false, false, true, false);
        double similarityAi = getJPlagCpgPlagScore(fileA, fileB, true, true, false, true, false);

        System.out.println("Plagiarism scores for " + fileA + " and " + fileB + ":");
        System.out.println("JPlag standard: " + similarityJPlag);
        System.out.println("Cpg minimal transformations: " + similarityMinimalCpg);
        System.out.println("Cpg standard transformations: " + similarityStandardCpg);
        System.out.println("Cpg with AI dead code removal: " + similarityAi);
        assertTrue(true);
    }

    @Test
    void KitPlagTicTacToeEval() throws IOException, ExitException {
        Set<String> files = kitTicTocToePlag();
        Set<File> fileSet = files.stream().skip(0).map(file -> new File(BASE_PATH.toFile().getAbsolutePath(), file)).collect(Collectors.toSet());

        JPlagResult resultJPlag = getJPlagPlagScore(fileSet, false);
        File outDir = new File("outputTicTacToe.jplag.zip");
        ReportObjectFactory reportObjectFactory = new ReportObjectFactory(outDir);
        reportObjectFactory.createAndSaveReport(resultJPlag);

        JPlagResult resultCPG = getJPlagCpgPlagScore(fileSet, false, false, false, true, true);
        File outDir2 = new File("outputTicTacToe.cpg.zip");
        ReportObjectFactory reportObjectFactory2 = new ReportObjectFactory(outDir2);
        reportObjectFactory2.createAndSaveReport(resultCPG);

        JPlagResult resultAI = getJPlagCpgPlagScore(fileSet, true, true, false, true, true);
        File outDir3 = new File("outputTicTacToe.ai.zip");
        ReportObjectFactory reportObjectFactory3 = new ReportObjectFactory(outDir3);
        reportObjectFactory3.createAndSaveReport(resultAI);

        assertTrue(true);
    }

    @Test
    void KitPlagBoardGameEval() throws IOException, ExitException {
        Set<String> files = kitBoardGamePlag();
        Set<File> fileSet = files.stream().sorted().skip(0).map(file -> new File(BASE_PATH.toFile().getAbsolutePath(), file))
                .collect(Collectors.toSet());

        JPlagResult resultJPlag = getJPlagPlagScore(fileSet, false);
        File outDir = new File("outputBoardGame.jplag.zip");
        ReportObjectFactory reportObjectFactory = new ReportObjectFactory(outDir);
        reportObjectFactory.createAndSaveReport(resultJPlag);

        JPlagResult resultCPG = getJPlagCpgPlagScore(fileSet, false, false, false, true, true);
        File outDir2 = new File("outputBoardGame.cpg.zip");
        ReportObjectFactory reportObjectFactory2 = new ReportObjectFactory(outDir2);
        reportObjectFactory2.createAndSaveReport(resultCPG);

        JPlagResult resultAI = getJPlagCpgPlagScore(fileSet, true, true, false, true, true);
        File outDir3 = new File("outputBoardGame.ai.zip");
        ReportObjectFactory reportObjectFactory3 = new ReportObjectFactory(outDir3);
        reportObjectFactory3.createAndSaveReport(resultAI);

        assertTrue(true);
    }

    @Test
    @Disabled("Only for evaluation purposes, not a real test")
    void KitPlagEvaluationSingle() throws ExitException, IOException {
        String folderA = "kit_DONT_COMMIT/BoardGame/human/subm208/";
        String folderB = "kit_DONT_COMMIT/TicTacToe/human/24229";
        Set<File> fileSet = Stream.of(folderA, folderB).map(file -> new File(BASE_PATH.toFile().getAbsolutePath(), file)).collect(Collectors.toSet());

        JPlagResult resultJPlag = getJPlagPlagScore(fileSet, false);
        double similarityJPlag = resultJPlag.getAllComparisons().getFirst().similarity();
        System.out.println("JPlag standard: " + similarityJPlag);

        JPlagResult minimalCpg = getJPlagCpgPlagScore(fileSet, false, false, false, false, false);
        double similarityMinimalCpg = minimalCpg.getAllComparisons().getFirst().similarity();
        System.out.println("Cpg minimal transformations: " + similarityMinimalCpg);

        JPlagResult StandardCpg = getJPlagCpgPlagScore(fileSet, false, false, false, true, false);
        double similarityStandardCpg = StandardCpg.getAllComparisons().getFirst().similarity();
        System.out.println("Cpg standard transformations: " + similarityStandardCpg);

        JPlagResult ai = getJPlagCpgPlagScore(fileSet, true, true, false, false, false);
        double similarityAi = ai.getAllComparisons().getFirst().similarity();
        System.out.println("Cpg with AI dead code removal: " + similarityAi);

        System.out.println("Plagiarism scores for " + folderA + " and " + folderB + ":");
        assertTrue(true);
    }

}
