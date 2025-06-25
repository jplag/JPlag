package de.jplag.highlight_extraction.frequencyDetermination;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.jplag.*;
import de.jplag.TestBase;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.highlight_extraction.*;
import de.jplag.options.JPlagOptions;

public class WeightOptimizationTest extends TestBase {

    private static JPlagResult result;
    private static Path baseOutputDir;
    private static SubmissionSet submissionSet;
    private static LongestCommonSubsequenceSearch strategy;
    public static JPlagOptions options;

    private static final String[] datasetPaths = {
            "C:\\Users\\Elisa\\Projekte\\JPlag\\core\\src\\test\\resources\\de\\jplag\\samples\\FrequencyDetermination\\00000019\\Java",
            "C:\\Users\\Elisa\\Projekte\\JPlag\\core\\src\\test\\resources\\de\\jplag\\samples\\FrequencyDetermination\\00000056\\Java",
            "C:\\Users\\Elisa\\Projekte\\JPlag\\core\\src\\test\\resources\\de\\jplag\\samples\\FrequencyDetermination\\Sheet3TaskA"};

    private static final FrequencyStrategy[] freqStrategies = {new CompleteMatchesStrategy(), new ContainedStrategy(), new SubMatchesStrategy(),
            new WindowOfMatchesStrategy()};

    @BeforeAll
    static void setupOutputDir() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        baseOutputDir = Paths.get("output", "weight_optimization_" + timestamp);
        try {
            Files.createDirectories(baseOutputDir);
        } catch (IOException e) {
            throw new RuntimeException("output probleme: " + baseOutputDir, e);
        }
    }

    @Test
    void runWeightOptimizationAllDatasetsAndStrategies() throws Exception {
        for (String datasetPath : datasetPaths) {
            System.out.println("=== Datensatz: " + datasetPath + " ===");
            options = getOptions(List.of(datasetPath), List.of(), o -> o); // getDefaultOptions(datasetPath);//getDefaultOptions(datasetPath);

            if (datasetPath.equals("FrequencyDetermination\\00000019\\Java")) {
                String baseCodeDir = "C:\\Data\\progpedia\\base\\main.java";

                options = options.withBaseCodeSubmissionName(baseCodeDir);
            }

            SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
            submissionSet = builder.buildSubmissionSet();
            strategy = new LongestCommonSubsequenceSearch(options);
            result = strategy.compareSubmissions(submissionSet);

            for (FrequencyStrategy freqStrategy : freqStrategies) {
                runWeightOptimizationForDatasetAndStrategy(datasetPath, freqStrategy);
            }
        }
    }

    private void runWeightOptimizationForDatasetAndStrategy(String datasetPath, FrequencyStrategy freqStrategy) throws IOException {
        int strategyNumber = 8;

        System.out.println("Starte WeightOptimization mit Strategie: " + freqStrategy.getClass().getSimpleName() + " für Datensatz: " + datasetPath);

        FrequencyDetermination fd = new FrequencyDetermination(freqStrategy, strategyNumber);
        fd.runAnalysis(result.getAllComparisons());

        MatchWeighting weighting = new MatchWeighting(freqStrategy, fd.getTokenFrequencyMap());
        for (var comparison : result.getAllComparisons()) {
            weighting.weightAllMatches(comparison.matches(),
                    comparison.firstSubmission().getTokenList().stream().map(t -> t.getType().toString()).toList());
        }

        FrequencySimilarity myFrequencySimilarity = new FrequencySimilarity(result.getAllComparisons());

        String datasetName = Paths.get(datasetPath).getFileName().toString();
        String strategyName = freqStrategy.getClass().getSimpleName();
        Path outputDir = baseOutputDir.resolve(datasetName).resolve(strategyName);
        Files.createDirectories(outputDir);

        for (double weight = 0.0; weight <= 1.0; weight += 0.1) {
            List<JPlagComparison> sorted = myFrequencySimilarity.calculateFrequencySimilarity(result.getAllComparisons(), weight);
            saveSimilarityCsv(sorted, weight, myFrequencySimilarity, outputDir);
        }
    }

    private void saveSimilarityCsv(List<JPlagComparison> comparisons, double weight, FrequencySimilarity freqSim, Path outputDir) throws IOException {
        String fileName = String.format("similarity_weight_%.2f.csv", weight).replace(",", ".");
        Path csvFile = outputDir.resolve(fileName);

        try (var writer = Files.newBufferedWriter(csvFile)) {
            writer.write("ComparisonID,Submission1,Submission2,Similarity\n");
            for (JPlagComparison comparison : comparisons) {
                String id = comparison.toString();
                String sub1 = comparison.firstSubmission().getName();
                String sub2 = comparison.secondSubmission().getName();
                double similarity = freqSim.frequencySimilarity(comparison, weight);
                writer.write(String.format("\"%s\",\"%s\",\"%s\",%.5f\n", id, sub1, sub2, similarity));
            }
        }

        System.out.println("CSV gespeichert: " + csvFile);
    }

//    @Test
//    void runClassificationForAllDatasets() throws Exception {
//        for (String datasetPath : datasetPaths) {
//            System.out.println("=== Klassifiziere Datensatz: " + datasetPath + " ===");
//
//            options = getOptions(List.of(datasetPath), List.of(), o -> o);
//
//            if (datasetPath.equals("FrequencyDetermination\\00000019\\Java")) {
//                String baseCodeDir = "C:\\Data\\progpedia\\base\\main.java";
//                options = options.withBaseCodeSubmissionName(baseCodeDir);
//            }
//
//            SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
//            submissionSet = builder.buildSubmissionSet();
//            strategy = new LongestCommonSubsequenceSearch(options);
//            result = strategy.compareSubmissions(submissionSet);
//
//            FrequencySimilarity similarity = new FrequencySimilarity(result.getAllComparisons());
//            LabelledWeighting lw = new LabelledWeighting();
//            lw.classifyComparisons(result.getAllComparisons(), similarity);
//
//            String datasetName = Paths.get(datasetPath).getFileName().toString();
//            Path outputDir = baseOutputDir.resolve("classification").resolve(datasetName);
//            Files.createDirectories(outputDir);
//
//            saveComparisonListAsCsv(lw.getPlagiatComparisons(), "plagiat", similarity, outputDir);
//            saveComparisonListAsCsv(lw.getAuffaelligComparisons(), "auffaellig", similarity, outputDir);
//            saveComparisonListAsCsv(lw.getUnauffaelligComparisons(), "unauffaellig", similarity, outputDir);
//            saveComparisonListAsCsv(lw.getZwischenGruppenComparisons(), "zwischengruppen", similarity, outputDir);
//
//            System.out.printf("→ %s: %d Plagiat, %d Auffällig, %d Unauffällig, %d Zwischen-Gruppen%n",
//                    datasetName,
//                    lw.getPlagiatComparisons().size(),
//                    lw.getAuffaelligComparisons().size(),
//                    lw.getUnauffaelligComparisons().size(),
//                    lw.getZwischenGruppenComparisons().size());
//        }
//    }
//    private void saveComparisonListAsCsv(List<JPlagComparison> comparisons, String label, FrequencySimilarity freqSim, Path outputDir) throws IOException {
//        Path file = outputDir.resolve(label + "_comparisons.csv");
//        try (var writer = Files.newBufferedWriter(file)) {
//            writer.write("ComparisonID,Submission1,Submission2,Similarity\n");
//            for (JPlagComparison comp : comparisons) {
//                String id = comp.toString();
//                String s1 = comp.firstSubmission().getName();
//                String s2 = comp.secondSubmission().getName();
//                double sim = freqSim.frequencySimilarity(comp, 0.0);
//                writer.write(String.format("\"%s\",\"%s\",\"%s\",%.5f\n", id, s1, s2, sim));
//            }
//        }
//        System.out.println("Klassifizierte Comparisons gespeichert: " + file);
//    }
}
