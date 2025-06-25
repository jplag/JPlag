package de.jplag.highlight_extraction.frequencyDetermination;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.jplag.options.JPlagOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.jplag.*;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.highlight_extraction.*;

public class WeightTwoPointComparisonTest extends TestBase {

    private static JPlagResult result;
    private static Path baseOutputDir;
    private static SubmissionSet submissionSet;
    private static LongestCommonSubsequenceSearch strategy;
    public static JPlagOptions options;

    private static final String[] datasetPaths = {
            "C:\\Users\\Elisa\\Projekte\\JPlag\\core\\src\\test\\resources\\de\\jplag\\samples\\FrequencyDetermination\\00000019\\Java",
            "C:\\Users\\Elisa\\Projekte\\JPlag\\core\\src\\test\\resources\\de\\jplag\\samples\\FrequencyDetermination\\00000056\\Java",
            "C:\\Users\\Elisa\\Projekte\\JPlag\\core\\src\\test\\resources\\de\\jplag\\samples\\FrequencyDetermination\\Sheet3TaskA"
    };

    private static final FrequencyStrategy[] freqStrategies = {
            new CompleteMatchesStrategy(),
            new ContainedStrategy(),
            new SubMatchesStrategy(),
            new WindowOfMatchesStrategy()
    };

    @BeforeAll
    static void setupOutputDir() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        baseOutputDir = Paths.get("output", "two_point_comparison_" + timestamp);
        Files.createDirectories(baseOutputDir);
    }

    @Test
    void runTwoWeightComparisonExport() throws Exception {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Path outputRoot = baseOutputDir.resolve("two_weight_similarity_" + timestamp);

        for (String datasetPath : datasetPaths) {
            System.out.println("=== Zwei-Gewicht-Vergleich für Datensatz: " + datasetPath + " ===");

            options = getOptions(List.of(datasetPath), List.of(), o -> o);
            if (datasetPath.contains("00000019\\Java")) {
                String baseCodeDir = "C:\\Data\\progpedia\\base\\main.java";
                options = options.withBaseCodeSubmissionName(baseCodeDir);
            }

            SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
            submissionSet = builder.buildSubmissionSet();
            strategy = new LongestCommonSubsequenceSearch(options);
            result = strategy.compareSubmissions(submissionSet);
            System.out.println("Vergleiche: " + result.getAllComparisons().size());

            Path datasetPathObj = Paths.get(datasetPath);
            String parentName = datasetPathObj.getParent().getFileName().toString();
            String languageName = datasetPathObj.getFileName().toString();
            String datasetName = "progpedia_" + parentName + "_" + languageName;

            for (FrequencyStrategy freqStrategy : freqStrategies) {
                System.out.println("→ Strategie: " + freqStrategy.getClass().getSimpleName());

                //strategie
                FrequencyDetermination fd = new FrequencyDetermination(freqStrategy, 8);
                fd.runAnalysis(result.getAllComparisons());

                //Matching
                MatchWeighting weighting = new MatchWeighting(freqStrategy, fd.getTokenFrequencyMap());
                for (var comparison : result.getAllComparisons()) {
                    weighting.weightAllMatches(
                            comparison.matches(),
                            comparison.firstSubmission().getTokenList().stream().map(t -> t.getType().toString()).toList()
                    );
                }

                FrequencySimilarity similarity = new FrequencySimilarity(result.getAllComparisons());
                LabelledWeighting lw = new LabelledWeighting();
                lw.classifyComparisons0(result.getAllComparisons(), similarity);

                String strategyName = freqStrategy.getClass().getSimpleName();
                Path outputDir = outputRoot.resolve(datasetName).resolve(strategyName);
                Files.createDirectories(outputDir);

                System.out.println("\nplagiat: " + lw.getPlagiatComparisons().size());
                System.out.println("auffaellig: " + lw.getAuffaelligComparisons().size());
                System.out.println("unauffaellig: " + lw.getUnauffaelligComparisons().size());
                runAndSaveTwoWeightSimilarity(lw.getPlagiatComparisons(), "plagiat", similarity, outputDir);
                runAndSaveTwoWeightSimilarity(lw.getAuffaelligComparisons(), "auffaellig", similarity, outputDir);
                runAndSaveTwoWeightSimilarity(lw.getUnauffaelligComparisons(), "unauffaellig", similarity, outputDir);
            }
        }
    }

    private void runAndSaveTwoWeightSimilarity(List<JPlagComparison> comparisons, String label,
                                               FrequencySimilarity similarity, Path outputDir) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Path file = outputDir.resolve(label + "_two_weight_" + timestamp + ".csv");

        try (var writer = Files.newBufferedWriter(file)) {
            writer.write("ComparisonID,Submission1,Submission2,Weight_0.0,Weight_0.5,Weight_1.0\n");
            for (JPlagComparison comp : comparisons) {
                String id = comp.toString();
                String s1 = comp.firstSubmission().getName();
                String s2 = comp.secondSubmission().getName();
                double sim0 = comp.similarity();
                double sim1 = similarity.frequencySimilarity(comp, 1.0);
                double sim2 = similarity.frequencySimilarity(comp, 0.5);
                writer.write(String.format("\"%s\",\"%s\",\"%s\",%.5f,%.5f,%.5f\n", id, s1, s2, sim0, sim2,  sim1));
            }
        }

        System.out.println("Vergleich mit zwei Gewichtungen gespeichert: " + file);
    }
}
