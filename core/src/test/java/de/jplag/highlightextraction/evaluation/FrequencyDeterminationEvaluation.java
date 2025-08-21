package de.jplag.highlightextraction.evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.*;

import de.jplag.*;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.highlightextraction.*;
import de.jplag.options.JPlagOptions;

/**
 * Class for frequency Determination evaluate creates csv Data for evaluation.
 */
public class FrequencyDeterminationEvaluation extends TestBase {
    private static LongestCommonSubsequenceSearch strategy;
    private static SubmissionSet submissionSet;
    private static JPlagResult result;

    private static final String[] datasetPaths = {"FrequencyDetermination\\00000019\\Java", "FrequencyDetermination\\00000056\\Java",
            "FrequencyDetermination\\Sheet3TaskA"};

    private static Path baseOutputDir;

    @BeforeAll
    static void setupOutputDir() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        baseOutputDir = Paths.get("output", timestamp);
        try {
            Files.createDirectories(baseOutputDir);
        } catch (IOException e) {
            throw new RuntimeException("output: " + baseOutputDir, e);
        }
    }

    @Test
    void runAllTests() throws Exception {
        for (String datasetPath : datasetPaths) {
            System.out.println("Dataset: " + datasetPath);
            JPlagOptions options = getDefaultOptions(datasetPath);
            SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
            submissionSet = builder.buildSubmissionSet();
            strategy = new LongestCommonSubsequenceSearch(options);
            result = strategy.compareSubmissions(submissionSet);

            runTestWithStrategy(new CompleteMatchesStrategy(), "CompleteMatches", datasetPath);
            runTestWithStrategy(new ContainedMatchesStrategy(), "ContainedMatches", datasetPath);
            runTestWithStrategy(new SubMatchesStrategy(), "SubMatches", datasetPath);
            runTestWithStrategy(new WindowOfMatchesStrategy(), "WindowOfMatches", datasetPath);
        }
    }

    private void runTestWithStrategy(FrequencyStrategy strategy, String testName, String datasetPath) throws IOException {
        System.out.println("Test: " + testName + " Dataset: " + datasetPath);
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 100);
        fd.buildFrequencyMap(result.getAllComparisons());
        System.out.println(fd);

        Map<List<TokenType>, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();
        printTestResult(tokenFrequencyMap);
        saveCsv(tokenFrequencyMap, datasetPath, testName);
    }

    void printTestResult(Map<List<TokenType>, Integer> tokenFrequencyMap) {
        System.out.println("\nToken-Frequency-analysis:");
        for (Map.Entry<List<TokenType>, Integer> myEntry : tokenFrequencyMap.entrySet()) {
            List<TokenType> key = myEntry.getKey();
            int count = myEntry.getValue();
            String id = myEntry.getValue().toString();
            System.out.printf("Tokens: [%.30s...] | Frequency: %2d | %s%n | %s \n", key, count, "*".repeat(Math.min(count, 50)), id);
        }
    }

    private void saveCsv(Map<List<TokenType>, Integer> tokenFrequencyMap, String datasetPath, String testName) throws IOException {
        String datasetName = Paths.get(datasetPath).getName(1).toString();
        Path testOutputDir = baseOutputDir.resolve(datasetName).resolve(testName);
        Files.createDirectories(testOutputDir);

        Path csvFile = testOutputDir.resolve("token_frequency_" + datasetName + ".csv");
        Path mapFile = testOutputDir.resolve("token_frequency_" + datasetName + "_id_map.csv");

        ArrayList<List<TokenType>> keys = new ArrayList<>(tokenFrequencyMap.keySet());
        keys.sort(Comparator.comparingInt(k -> tokenFrequencyMap.get(k)));

        try (FileWriter csvWriter = new FileWriter(csvFile.toFile()); FileWriter mapWriter = new FileWriter(mapFile.toFile())) {

            csvWriter.write("ID,Haeufigkeit\n");
            mapWriter.write("ID,Token\n");

            int id = 1;
            for (List<TokenType> key : keys) {
                int count = tokenFrequencyMap.get(key);
                csvWriter.write(String.format("%d,%d\n", id, count));
                String keyString = key.stream().map(TokenType::toString)   // TokenType zu String
                        .collect(Collectors.joining(","));
                keyString = keyString.replace("\"", "\"\"");
                mapWriter.write(String.format("%d,\"%s\"\n", id, keyString));
                id++;
            }
        }
    }
}